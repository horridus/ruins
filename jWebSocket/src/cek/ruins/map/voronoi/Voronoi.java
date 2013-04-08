package cek.ruins.map.voronoi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import cek.ruins.Point;

import be.humphreys.simplevoronoi.GraphEdge;

public class Voronoi extends be.humphreys.simplevoronoi.SimpleVoronoi {
	private List<Point> startingPoints;
	private double boundingBoxSize;
	private int numLloydIteration;
	private Vector<List<VoronoiCorner>> buckets; //needed as sorting container to avoid duplicated corners
	
	private VoronoiGraph graph;
	
	public Voronoi(double minDistanceBetweenSites, double boundingBoxSize, int numLloydIteration) {
		this(minDistanceBetweenSites, boundingBoxSize, numLloydIteration, new VoronoiGraph());
	}
	
	public Voronoi(double minDistanceBetweenSites, double boundingBoxSize, int numLloydIteration, VoronoiGraph graph) {
		super(minDistanceBetweenSites);
		
		this.startingPoints = new ArrayList<Point>();
		this.boundingBoxSize = boundingBoxSize;
		this.numLloydIteration = numLloydIteration;
		
		this.graph = graph;
	}
	
	//final site position could be different because of
	//algorithm cosmetic correction (Lloyd Relaxation, edges avaraging) 
	//on polygons centers and edges length.
	public boolean addPotentialSite(double x, double y) {
		if (x > 0 && x < this.boundingBoxSize && y > 0 && y < this.boundingBoxSize) {
			this.startingPoints.add(new Point(x, y));
			return true;
		}
		else
			return false;
	}

	 public VoronoiGraph generate() {
		 double[] xs = new double[this.startingPoints.size()];
		 double[] ys = new double[this.startingPoints.size()];
		 
		 for (int i = 0; i < this.startingPoints.size(); i++) {
			 Point p = this.startingPoints.get(i);
			 xs[i] = p.x;
			 ys[i] = p.y;
		 }
		 
		 List<GraphEdge> edges = super.generateVoronoi(xs, ys, 0, this.boundingBoxSize, 0, this.boundingBoxSize);
		 makeGraph(edges);
		 
		 for (int i = 0; i < this.numLloydIteration; i++) {
			 lloydRelaxation();
		 
		 for (int n = 0; n < this.graph.sites().size(); n++) {
			 VoronoiSite site = this.graph.sites().get(n);
			 xs[n] = site.center.x;
			 ys[n] = site.center.y;
			 
			 this.startingPoints.set(n, site.center);
		 }
		 
		 edges = super.generateVoronoi(xs, ys, 0, this.boundingBoxSize, 0, this.boundingBoxSize);
		 makeGraph(edges);
		 }
		 
		 uniformEdges();
		 computeEdgesMidpoint();
		 
		 return this.graph;
	 }
	 
	 public void reset() {
		 this.startingPoints.clear();
		 
		 this.graph.sites().clear();
		 this.graph.corners().clear();
		 this.graph.edges().clear();
	 }
	 
	 private void makeGraph(List<GraphEdge> edges) {
		 makeEdges(edges);
		 makeSites(edges);
		 makeCorners(edges);
	 }
	 
	 private void makeEdges(List<GraphEdge> edges) {
		 this.graph.edges().clear();
		 
		 Iterator<GraphEdge> edgesIt = edges.iterator();
		 while (edgesIt.hasNext()) {
			 GraphEdge edge = edgesIt.next();
			 
			 Point c0 = new Point(edge.x1, edge.y1);
			 Point c1 = new Point(edge.x2, edge.y2);
			 
			 //remove zero length edges
			 if (c0.equals(c1)) {
				 edgesIt.remove();
				 continue;
			 }
			 
			 VoronoiEdge vEdge = new VoronoiEdge();
			 vEdge.site0 = edge.site1;
			 vEdge.site1 = edge.site2;
			 
			 this.graph.edges().add(vEdge);
		 }
	 }
	 
	 private void makeSites(List<GraphEdge> edges) {
		 this.graph.sites().clear();
		 
		 ((Vector<VoronoiSite>)this.graph.sites()).setSize(this.startingPoints.size());
		 
		 int edgeIdx = 0;
		 
		 Iterator<GraphEdge> edgesIt = edges.iterator();
		 while (edgesIt.hasNext()) {
			 GraphEdge edge = edgesIt.next();
			 VoronoiSite site;
			 
			 Point oldSite = this.startingPoints.get(edge.site1);
			 
			 if (this.graph.sites().get(edge.site1) != null) {
				 site = this.graph.sites().get(edge.site1);
			 }
			 else {
				 site = new VoronoiSite(edge.site1);
				 this.graph.sites().set(edge.site1, site);
			 }

			 site.center.x = oldSite.x;
			 site.center.y = oldSite.y;
			 site.neighbors.add(edge.site2);
			 site.borders.add(edgeIdx);
			 
			 oldSite = this.startingPoints.get(edge.site2);
			 
			 if (this.graph.sites().get(edge.site2) != null) {
				 site = this.graph.sites().get(edge.site2);
			 }
			 else {
				 site = new VoronoiSite(edge.site2);
				 this.graph.sites().set(edge.site2, site);
			 }

			 site.center.x = oldSite.x;
			 site.center.y = oldSite.y;
			 site.neighbors.add(edge.site1);
			 site.borders.add(edgeIdx);
			 
			 edgeIdx++;
		 }
	 }
	 
	 private void makeCorners(List<GraphEdge> edges) {
		 this.graph.corners().clear();
		 
		 this.buckets = new Vector<List<VoronoiCorner>>();
		 
		 int edgeIdx = 0;
		 
		 Iterator<GraphEdge> edgesIt = edges.iterator();
		 while (edgesIt.hasNext()) {
			 GraphEdge edge = edgesIt.next();
			 Point c0 = new Point(edge.x1, edge.y1);
			 Point c1 = new Point(edge.x2, edge.y2);
			 
			 int cornerIdx0 = makeCorner(c0);
			 //add it to its sites()...
			 this.graph.sites().get(edge.site1).corners.add(cornerIdx0);
			 this.graph.sites().get(edge.site2).corners.add(cornerIdx0);
			 
			 //...and to edges
			 this.graph.edges().get(edgeIdx).corner0 = cornerIdx0;
			 
			 //set site as a touching site for this corner
			 VoronoiCorner corner0 = this.graph.corners().get(cornerIdx0);
			 if (!corner0.touches.contains(edge.site1))
				 corner0.touches.add(edge.site1);
			 
			 if (!corner0.touches.contains(edge.site2))
				 corner0.touches.add(edge.site2);
			 
			 //link corner to this edge
			 corner0.protudes.add(edgeIdx);
			 			 
			 int cornerIdx1 = makeCorner(c1);
				 
			 //add it to its sites()...
			 this.graph.sites().get(edge.site1).corners.add(cornerIdx1);
			 this.graph.sites().get(edge.site2).corners.add(cornerIdx1);
			 
			 //...and to edges
			 this.graph.edges().get(edgeIdx).corner1 = cornerIdx1;
			 
			 //set site as a touching site for this corner
			 VoronoiCorner corner1 = this.graph.corners().get(cornerIdx1);
			 if (!corner1.touches.contains(edge.site1))
				 corner1.touches.add(edge.site1);
			 
			 if (!corner1.touches.contains(edge.site2))
				 corner1.touches.add(edge.site2);
			 
			 //link corner to this edge
			 corner1.protudes.add(edgeIdx);  //FIXME ci finiscono anche degli edge che non centrano un cazzo
			 
			 //update adjacents vectors of both corners
			 corner0.adjacent.add(cornerIdx1);
			 corner1.adjacent.add(cornerIdx0);
			 
			 edgeIdx++;
		 }
		 
		 buckets = null;
	 }
	 
	 //Check if we already made this corner else make a new one and
	 //add to corners collection
	 private int makeCorner(Point point) {
		 if (point == null)
			 return -1;
		 
		 int floorX = (int) Math.floor(point.x);
		 for(int bucketIdx = floorX - 1; bucketIdx < floorX + 1; bucketIdx++) {
			 if (bucketIdx < 0)
				 continue;
			 
			 if (bucketIdx < this.buckets.size() && this.buckets.get(bucketIdx) != null) {
				 for (VoronoiCorner corner : this.buckets.get(bucketIdx)) {
					 if (point.equals(corner.point)) {
						 //corner already exists, stop looking
						 return this.graph.corners().indexOf(corner);
					 }
				 }
			 }
		 }
		 
		 //this corner is new, so... 
		 List<VoronoiCorner> bucket;
		 if (floorX < this.buckets.size() && this.buckets.get(floorX) != null)
			 bucket = this.buckets.get(floorX);
		 else
			 bucket = new ArrayList<VoronoiCorner>();
		 
		 VoronoiCorner corner = new VoronoiCorner();
		 corner.point = point;
		 corner.border = (point.x == 0 || point.x == boundingBoxSize || point.y == 0 || point.y == boundingBoxSize);
		 
		 //TODO altre prorpietà da riempire
		 
		 
		 //...fill bucket and... 
		 bucket.add(corner);
		 
		 //...expand number of buckets if needed and...
		 if (floorX + 1 > this.buckets.size())
			 this.buckets.setSize(floorX + 1);
		 
		 this.buckets.set(floorX, bucket);

		 //...fill graph's corners().
		 this.graph.corners().add(corner);
		 
		 //return the  corner index.
		 return this.graph.corners().size() - 1;
	 }
	 
	 private void computeEdgesMidpoint() {
		 Iterator<VoronoiEdge> edgesIt = this.graph.edges().iterator();
		 while (edgesIt.hasNext()) {
			 VoronoiEdge edge = edgesIt.next();
			 VoronoiCorner corner0 = this.graph.corners().get(edge.corner0);
			 VoronoiCorner corner1 = this.graph.corners().get(edge.corner1);
			 edge.midpoint.x = (corner0.point.x + corner1.point.x) / 2.0;
			 edge.midpoint.y = (corner0.point.y + corner1.point.y) / 2.0;
		 }
	 }
	 
	 private void lloydRelaxation() {
		 Iterator<VoronoiSite> sitesIt = this.graph.sites().iterator();
		 while (sitesIt.hasNext()) {
			 VoronoiSite site = sitesIt.next();
			 Point relaxedCenter = new Point(0.0, 0.0);
			 
			 int numCorners = site.corners.size();
			 for (Integer cornerIdx : site.corners) {
				 VoronoiCorner corner = this.graph.corners().get(cornerIdx);
				 
				 relaxedCenter.x += corner.point.x;
				 relaxedCenter.y += corner.point.y;
			 }
			 
			 relaxedCenter.x /= numCorners;
			 relaxedCenter.y /= numCorners;
			 
			 site.center = relaxedCenter;
		 }
	 }
	 
	 private void uniformEdges() {
		 List<Point> uCorners = new Vector<Point>();
		 
		 Iterator<VoronoiCorner> cornersIt = this.graph.corners().iterator();
		 while (cornersIt.hasNext()) {
			 VoronoiCorner corner = cornersIt.next();
			 
			 if (corner.border) {
				 uCorners.add(corner.point);
			 }
			 else {
				 Point point = new Point();
				 for (int neighborIdx : corner.touches) {
					 point.x += this.graph.sites().get(neighborIdx).center.x;
					 point.y += this.graph.sites().get(neighborIdx).center.y;
				 }
				 
				 point.x /= corner.touches.size();
				 point.y /= corner.touches.size();
				 uCorners.add(point);
			 }
		 }
		 
		 for (int i = 0; i < this.graph.corners().size(); i++) {
			 VoronoiCorner corner = this.graph.corners().get(i);
			 corner.point = uCorners.get(i);
		 }
	 }
}
