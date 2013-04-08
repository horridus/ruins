package cek.ruins.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import cek.ruins.Point;
import cek.ruins.map.Map;
import cek.ruins.map.Region;
import cek.ruins.map.RegionCorner;
import cek.ruins.map.RegionEdge;
import cek.ruins.map.voronoi.VoronoiCorner;
import cek.ruins.map.voronoi.VoronoiEdge;
import cek.ruins.map.voronoi.VoronoiGraph;
import cek.ruins.map.voronoi.VoronoiSite;

public class Utils {
	public static Color COAST_COLOR = new Color(218, 189, 71);
	public static Color COAST_COLOR_EDGE = new Color(218, 189, 71);
	public static Color LAKE_COLOR = new Color(2, 152, 232);
	public static Color LAKE_COLOR_EDGE = new Color(2, 152, 232);
	public static Color RIVER_COLOR = new Color(2, 152, 232);
	public static Color OCEAN_COLOR = new Color(49, 54, 88, 255);
	public static Color OCEAN_COLOR_EDGE = new Color(49, 54, 88, 255);
	public static Color LAND_COLOR = new Color(18, 80, 4, 255);
	public static Color LAND_COLOR_EDGE = new Color(18, 80, 4, 255);
	
	public static void floodFillBoundary(BufferedImage img, int x, int y, Color boundary, Color color) {
		Queue<Point> queue = new LinkedList<Point>();
		queue.add(new Point(x, y));
		
		while (!queue.isEmpty()) {
			Point point = queue.remove();
			if (point.x < 0 || point.x >= img.getWidth() || point.y < 0 || point.y >= img.getHeight())
				continue;
			
			int currentColor = img.getRGB((int)point.x, (int)point.y); 
			if (currentColor == boundary.getRGB() || currentColor == color.getRGB())
				continue;
			
			img.setRGB((int)point.x, (int)point.y, color.getRGB());
			for (int i = (int)point.x + 1; i < img.getWidth(); i++) {
				currentColor = img.getRGB(i, (int)point.y); 
				if (currentColor == boundary.getRGB() || currentColor == color.getRGB())
					break;
				
				img.setRGB(i, (int)point.y, color.getRGB());
				queue.add(new Point(i, (int)point.y - 1));
				queue.add(new Point(i, (int)point.y + 1));
			}
			
			for (int i = (int)point.x - 1; i >= 0; i--) {
				currentColor = img.getRGB(i, (int)point.y); 
				if (currentColor == boundary.getRGB() || currentColor == color.getRGB())
					break;
				
				img.setRGB(i, (int)point.y, color.getRGB());
				queue.add(new Point(i, (int)point.y - 1));
				queue.add(new Point(i, (int)point.y + 1));
			}
			
			queue.add(new Point((int)point.x, (int)point.y - 1));
			queue.add(new Point((int)point.x, (int)point.y + 1));
		}
	}
	
	public static void floodFillBackgroundWNoise(BufferedImage img, int x, int y, Color background, Color color, double samplingFactor, float noiseOpacity, double seed) {
		NoiseGenerator nGenerator = new NoiseGenerator();
		
		Queue<Point> queue = new LinkedList<Point>();
		queue.add(new Point(x, y));
		
		while (!queue.isEmpty()) {
			Point point = queue.remove();
			if (point.x < 0 || point.x >= img.getWidth() || point.y < 0 || point.y >= img.getHeight())
				continue;
			
			int currentColor = img.getRGB((int)point.x, (int)point.y); 
			if (currentColor != background.getRGB())
				continue;
			
			float noise = (float) nGenerator.noise01(point.x * samplingFactor, point.y * samplingFactor, seed);
			img.setRGB((int)point.x, (int)point.y, blendSrcAtop(color, new Color(noise, noise, noise, noiseOpacity)).getRGB());
			for (int i = (int)point.x + 1; i < img.getWidth(); i++) {
				currentColor = img.getRGB(i, (int)point.y); 
				if (currentColor != background.getRGB())
					break;
				
				noise = (float) nGenerator.noise01(i * samplingFactor, point.y * samplingFactor, seed);
				img.setRGB(i, (int)point.y, blendSrcAtop(color, new Color(noise, noise, noise, noiseOpacity)).getRGB());
				queue.add(new Point(i, (int)point.y - 1));
				queue.add(new Point(i, (int)point.y + 1));
			}
			
			for (int i = (int)point.x - 1; i >= 0; i--) {
				currentColor = img.getRGB(i, (int)point.y); 
				if (currentColor != background.getRGB())
					break;
				
				noise = (float) nGenerator.noise01(i * samplingFactor, point.y * samplingFactor, seed);
				img.setRGB(i, (int)point.y, blendSrcAtop(color, new Color(noise, noise, noise, noiseOpacity)).getRGB());
				queue.add(new Point(i, (int)point.y - 1));
				queue.add(new Point(i, (int)point.y + 1));
			}
			
			queue.add(new Point((int)point.x, (int)point.y - 1));
			queue.add(new Point((int)point.x, (int)point.y + 1));
		}
	}
	
	public static void floodFillBackgroundFineGraine(BufferedImage img, int x, int y, Color background, Color color, double seed) {
		NoiseGenerator nGenerator = new NoiseGenerator();
		
		Queue<Point> queue = new LinkedList<Point>();
		queue.add(new Point(x, y));
		
		while (!queue.isEmpty()) {
			Point point = queue.remove();
			if (point.x < 0 || point.x >= img.getWidth() || point.y < 0 || point.y >= img.getHeight())
				continue;
			
			int currentColor = img.getRGB((int)point.x, (int)point.y); 
			if (currentColor != background.getRGB())
				continue;
			
			float noise0 = (float) nGenerator.noise01(point.x * 0.01, point.y * 0.01, seed);
			float noise1 = (float) nGenerator.noise01(point.x * 1, point.y * 1, seed);
			Color finalColor = Utils.blendSrcAtop(Utils.blendSrcAtop(color, new Color(noise0, noise0, noise0, 0.05f)), new Color(noise1, noise1, noise1, 0.10f));
			img.setRGB((int)point.x, (int)point.y, finalColor.getRGB());
			for (int i = (int)point.x + 1; i < img.getWidth(); i++) {
				currentColor = img.getRGB(i, (int)point.y); 
				if (currentColor != background.getRGB())
					break;
				
				noise0 = (float) nGenerator.noise01(point.x * 0.01, point.y * 0.01, seed);
				noise1 = (float) nGenerator.noise01(point.x * 1, point.y * 1, seed);
				finalColor = Utils.blendSrcAtop(Utils.blendSrcAtop(color, new Color(noise0, noise0, noise0, 0.05f)), new Color(noise1, noise1, noise1, 0.10f));
				img.setRGB(i, (int)point.y, finalColor.getRGB());
				queue.add(new Point(i, (int)point.y - 1));
				queue.add(new Point(i, (int)point.y + 1));
			}
			
			for (int i = (int)point.x - 1; i >= 0; i--) {
				currentColor = img.getRGB(i, (int)point.y); 
				if (currentColor != background.getRGB())
					break;
				
				noise0 = (float) nGenerator.noise01(point.x * 0.01, point.y * 0.01, seed);
				noise1 = (float) nGenerator.noise01(point.x * 1, point.y * 1, seed);
				finalColor = Utils.blendSrcAtop(Utils.blendSrcAtop(color, new Color(noise0, noise0, noise0, 0.05f)), new Color(noise1, noise1, noise1, 0.10f));
				img.setRGB(i, (int)point.y, finalColor.getRGB());
				queue.add(new Point(i, (int)point.y - 1));
				queue.add(new Point(i, (int)point.y + 1));
			}
			
			queue.add(new Point((int)point.x, (int)point.y - 1));
			queue.add(new Point((int)point.x, (int)point.y + 1));
		}
	}
	
	private static Color blendSrcAtop(Color dst, Color src) {
		float rd = dst.getRed()   / 255.0f;
		float rs = src.getRed()   / 255.0f;
		float gd = dst.getGreen() / 255.0f;
		float gs = src.getGreen() / 255.0f;
		float bd = dst.getBlue()  / 255.0f;
		float bs = src.getBlue()  / 255.0f;
		float ad = dst.getAlpha() / 255.0f;
		float as = src.getAlpha() / 255.0f;
		
		float r = rs * ad + rd * (1 - as);
		float g = gs * ad + gd * (1 - as);
		float b = bs * ad + bd * (1 - as);
		float a = ad;
		
		r = (float) Math.max(Math.min(r, 1.0), 0.0);
		g = (float) Math.max(Math.min(g, 1.0), 0.0);
		b = (float) Math.max(Math.min(b, 1.0), 0.0);
		a = (float) Math.max(Math.min(a, 1.0), 0.0);
		
		return new Color(r, g, b, a);
	}
	
	//FIXME sembra scazzare quando si gioca con l'alpha (alpha sull'immagine già applicata mentre in getRGB no)
	public static void floodFillBackground(BufferedImage img, int x, int y, Color background, Color color) {
		Queue<Point> queue = new LinkedList<Point>();
		queue.add(new Point(x, y));
		
		while (!queue.isEmpty()) {
			Point point = queue.remove();
			if (point.x < 0 || point.x >= img.getWidth() || point.y < 0 || point.y >= img.getHeight())
				continue;
			
			int currentColor = img.getRGB((int)point.x, (int)point.y); 
			if (currentColor != background.getRGB())
				continue;
			
			img.setRGB((int)point.x, (int)point.y, color.getRGB());
			for (int i = (int)point.x + 1; i < img.getWidth(); i++) {
				currentColor = img.getRGB(i, (int)point.y); 
				if (currentColor != background.getRGB())
					break;
				
				img.setRGB(i, (int)point.y, color.getRGB());
				queue.add(new Point(i, (int)point.y - 1));
				queue.add(new Point(i, (int)point.y + 1));
			}
			
			for (int i = (int)point.x - 1; i >= 0; i--) {
				currentColor = img.getRGB(i, (int)point.y); 
				if (currentColor != background.getRGB())
					break;
				
				img.setRGB(i, (int)point.y, color.getRGB());
				queue.add(new Point(i, (int)point.y - 1));
				queue.add(new Point(i, (int)point.y + 1));
			}
			
			queue.add(new Point((int)point.x, (int)point.y - 1));
			queue.add(new Point((int)point.x, (int)point.y + 1));
		}
	}
	
	public static BufferedImage createMoistureMapImage(Map map, List<GeneralPath> noisyEdges) {
		BufferedImage image = new BufferedImage(map.size(), map.size(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		g.setColor(Color.cyan);
		g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
		g.setColor(new Color(255,255,255,255));
		
		VoronoiGraph regionsGraph = map.regionsGraph();
		
		g.setColor(Color.black);
		Iterator<VoronoiEdge> edgesIt = regionsGraph.edges().iterator();
		while (edgesIt.hasNext()) {
			VoronoiEdge edge = edgesIt.next();
			VoronoiCorner corner0 = regionsGraph.corners().get(edge.corner0);
			VoronoiCorner corner1 = regionsGraph.corners().get(edge.corner1);
			
			g.drawLine((int)Math.floor(corner0.point.x), (int)Math.floor(corner0.point.y), (int)Math.floor(corner1.point.x), (int)Math.floor(corner1.point.y));
		}
		
		Iterator<VoronoiSite> sitesIt = regionsGraph.sites().iterator();
		while (sitesIt.hasNext()) {
			VoronoiSite site = sitesIt.next();
			int x = (int) Math.floor(site.center.x);
			int y = (int) Math.floor(site.center.y);
			
			Region region = map.regions().get(site.index);
			
			Color moistureColor = new Color(100, 100, 100, 255);
			
			if (region.isOcean()) {
				moistureColor = Color.blue;
			}
			else if (region.isWater()) {
				moistureColor = Utils.LAKE_COLOR;
			}
			else {
				moistureColor = interpolateColor(Utils.LAND_COLOR, new Color(255, 225, 225, 255), region.moisture());
			}
			
			Utils.floodFillBoundary(image, x, y, Color.black, moistureColor);
			
			g.drawLine(x, y, x, y);
		}
		
		drawRiver(g, map, noisyEdges);
		
		return image;
	}
	
	protected static void drawRiver(Graphics2D g, Map map, List<GeneralPath> paths) {
		VoronoiGraph regionsGraph = map.regionsGraph();
		
		g.setColor(Utils.LAKE_COLOR);
		Iterator<RegionEdge> rEdgesIt = map.regionsEdges().iterator();
		while (rEdgesIt.hasNext()) {
			RegionEdge rEdge = rEdgesIt.next();
			VoronoiEdge vEdge = regionsGraph.edges().get(rEdge.index()); 
			RegionCorner rCorner0 = map.regionsCorners().get(vEdge.corner0);
			RegionCorner rCorner1 = map.regionsCorners().get(vEdge.corner1);
			
			if (rEdge.isRiver() && !(rCorner0.isWater() && rCorner1.isWater())) {
				g.draw(paths.get(rEdge.index()));
			}
		}
	}
	
	public static Color interpolateColor(Color c0, Color c1, double interpolation) {
		int r = (int) Math.round((((1.0 - interpolation) * c0.getRed()) + (interpolation * c1.getRed())));
		int g = (int) Math.round((((1.0 - interpolation) * c0.getGreen()) + (interpolation * c1.getGreen())));
		int b = (int) Math.round((((1.0 - interpolation) * c0.getBlue()) + (interpolation * c1.getBlue())));
		int a = (int) Math.round((((1.0 - interpolation) * c0.getAlpha())) + (interpolation * c1.getAlpha()));
		
		r = (r > 255)? 255 : r;
		g = (g > 255)? 255 : g;
		b = (b > 255)? 255 : b;
		a = (a > 255)? 255 : a;
		
		return new Color(r, g, b, a); 
	}
	
	public static List<GeneralPath> generateNoisyPaths(Map map, NoisyEdges edges) {
		Vector<GeneralPath> paths = new Vector<GeneralPath>();
		paths.setSize(map.regionsEdges().size());
		
		Iterator<Region> regionsIt = map.regions().iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = map.regionsGraph().sites().get(region.index());
			
			for (Integer regionIdx : site.neighbors) {
				Integer edgeIdx = map.regionsGraph().lookupEdgeFromSites(site, region.index(), regionIdx);
				
				List<Point> hpath0 = edges.halfPath0(edgeIdx);
				List<Point> hpath1 = edges.halfPath1(edgeIdx);
				
				GeneralPath path = new GeneralPath();
				path.moveTo(hpath0.get(0).x, hpath0.get(0).y);
				
				//draw forward
				for (int i = 0; i < hpath0.size(); i++) {
					path.lineTo(hpath0.get(i).x, hpath0.get(i).y);
				}
				
				//draw backward
				for (int i = hpath1.size() - 1; i >= 0; i--) {
					path.lineTo(hpath1.get(i).x, hpath1.get(i).y);
				}
				
				if (paths.get(edgeIdx) == null)
					paths.set(edgeIdx, path);
			}
		}
		
		return paths;
	}
	
	public static BufferedImage noiseMap(int size, double seed, double samplingFactor, float opacity) {
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		
		NoiseGenerator nGenerator = new NoiseGenerator();
		
		for (int y = 0; y < image.getWidth(); y++)
			for (int x = 0; x < image.getWidth(); x++) {
				float noise = (float) nGenerator.noise01(x * samplingFactor, y * samplingFactor, seed);
				image.setRGB(x, y, new Color(noise, noise, noise, opacity).getRGB());
			}
		
		return image;
	}
}
