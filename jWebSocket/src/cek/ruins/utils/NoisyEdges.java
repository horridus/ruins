package cek.ruins.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cek.ruins.Point;
import cek.ruins.map.Region;
import cek.ruins.map.voronoi.VoronoiCorner;
import cek.ruins.map.voronoi.VoronoiEdge;
import cek.ruins.map.voronoi.VoronoiSite;

public class NoisyEdges {
	private Map<Integer, List<Point>> paths0;
	private Map<Integer, List<Point>> paths1;
	
	public NoisyEdges() {
		this.paths0 = new HashMap<Integer, List<Point>>();
		this.paths1 = new HashMap<Integer, List<Point>>();
	}
	
	public List<Point> halfPath0(Integer index) {
		return this.paths0.get(index);
	}
	
	public List<Point> halfPath1(Integer index) {
		return this.paths1.get(index);
	}
	
	public void buildNoisyEdges(cek.ruins.map.Map map, double noiseFactor, Random generator) {
		Iterator<Region> regionsIt = map.regions().iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = map.regionsGraph().sites().get(region.index());
			for (Integer edgeIdx : site.borders) {
				if (!this.paths0.containsKey(edgeIdx)) {
					VoronoiEdge edge = map.regionsGraph().edges().get(edgeIdx);
					if (edge.corner0 != -1 && edge.corner1 != -1 && edge.site0 != -1 && edge.site1 != -1) {
						VoronoiSite site0 = map.regionsGraph().sites().get(edge.site0);
						VoronoiSite site1 = map.regionsGraph().sites().get(edge.site1);
						VoronoiCorner corner0 = map.regionsGraph().corners().get(edge.corner0);
						VoronoiCorner corner1 = map.regionsGraph().corners().get(edge.corner1);
						
						Point t = Point.interpolate(corner0.point, site0.center, noiseFactor);
						Point q = Point.interpolate(corner0.point, site1.center, noiseFactor);
						Point r = Point.interpolate(corner1.point, site0.center, noiseFactor);
						Point s = Point.interpolate(corner1.point, site1.center, noiseFactor);
						
						this.paths0.put(edgeIdx, buildNoisyLineSegments(corner0.point, t, edge.midpoint, q, (int)Math.round(map.size() * 0.004), generator));
						this.paths1.put(edgeIdx, buildNoisyLineSegments(corner1.point, s, edge.midpoint, r, (int)Math.round(map.size() * 0.004), generator));
					}
				}
			}
		}
	}
	
	protected List<Point> buildNoisyLineSegments(Point A, Point B, Point C, Point D, int minLength, Random generator) {
		List<Point> points = new LinkedList<Point>();
		
		points.add(A);
		subdivide(points, A, B, C, D, minLength, generator);
		points.add(C);
		
		return points;
	}
	
	protected void subdivide(List<Point> points, Point A, Point B, Point C, Point D, int minLength, Random generator) {
		
		if (A.subtract(C).magnitudo() < minLength || B.subtract(D).magnitudo() < minLength)
			return;
		
		//subdivide the quadrilateral
		double p = generator.nextDouble() * 0.6 + 0.2; //vertical (along A-D and B-C)
		double q = generator.nextDouble() * 0.6 + 0.2; //horizontal (along A-B and D-C)
		
		//midpoints
		Point E = Point.interpolate(A, D, p);
		Point F = Point.interpolate(B, C, p);
		Point G = Point.interpolate(A, B, q);
		Point I = Point.interpolate(D, C, q);
		
		//central point
		Point H = Point.interpolate(E, F, q);
		
		//divide the quad into subquads, but meet at H
		double s = 1.0 - generator.nextDouble() * 0.8 - 0.4;
		double t = 1.0 - generator.nextDouble() * 0.8 - 0.4;
		
		subdivide(points, A, Point.interpolate(G, B, s), H, Point.interpolate(E, D, t), minLength, generator);
		points.add(H);
		subdivide(points, H, Point.interpolate(F, C, s), C, Point.interpolate(I, D, t), minLength, generator);
	}
}
