package cek.ruins.utils;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import cek.ruins.Point;
import cek.ruins.map.Map;
import cek.ruins.map.Region;
import cek.ruins.map.RegionEdge;
import cek.ruins.map.voronoi.VoronoiEdge;
import cek.ruins.map.voronoi.VoronoiSite;

public class MapPainter {
	public static Color TRANSPARENT_BLACK_COLOR = new Color(0, 0, 0, 0);
	public static Color PAPER_BASE_COLOR = new Color(205, 160, 64, 255);
	public static Color LAND_BASE_COLOR = new Color(236, 203, 131, 255);
	public static Color COAST_COLOR = new Color(122, 75, 42, 255);
	public static Color BORDER_COLOR = new Color(122, 75, 42, 255);
	public static Color INNER_BORDER_COLOR = new Color(160, 160, 160, 255);
	public static Color LAKE_COLOR = new Color(2, 60, 159, 255);
	public static Color LAKE_EDGE_COLOR = new Color(3, 56, 148, 255);
	public static Color ELEVATION_MIN_COLOR = new Color(45, 200, 0, 255);
	public static Color ELEVATION_MAX_COLOR = new Color(245, 255, 242, 255);
	public static Color MOISTURE_MIN_COLOR = new Color(250, 253, 254, 255);
	public static Color MOISTURE_MAX_COLOR = new Color(31, 136, 167, 255);
	public static Color TEMPERATURE_MIN_COLOR = new Color(0, 204, 204, 255);
	public static Color TEMPERATURE_MEAN_COLOR = new Color(153, 255, 0, 255);
	public static Color TEMPERATURE_MAX_COLOR = new Color(204, 0, 0, 255);
    
	public static Color ICE_BIOME_COLOR = new Color(113, 166, 210, 255);
	public static Color LAKE_BIOME_COLOR = new Color(21, 96, 189, 255);
	public static Color COAST_BIOME_COLOR = new Color(255, 169, 95, 255);
	public static Color GRASSLAND_BIOME_COLOR = new Color(0, 139, 0, 255);
	public static Color MOUNTAINS_BIOME_COLOR = new Color(83, 75, 79, 255);
	public static Color HIGHMOUNTAINS_BIOME_COLOR = new Color(255, 255, 255, 255);
	
	public static Color SAND_DESERT_BIOME_COLOR = new Color(237, 201, 175, 255);
	public static Color ROCK_DESERT_BIOME_COLOR = new Color(186, 135, 89, 255);
	public static Color BADLANDS_BIOME_COLOR = new Color(102, 66, 77, 255);
	public static Color SAVANNA_BIOME_COLOR =  new Color(209, 226, 49, 255);
	public static Color SHRUBLAND_BIOME_COLOR = new Color(79, 121, 66, 255);
	public static Color TUNDRA_BIOME_COLOR = new Color(170, 240, 209, 255);
	public static Color TAIGA_BIOME_COLOR = new Color(11, 218, 81, 255);
	public static Color MARSH_BIOME_COLOR = new Color(11, 218, 81, 255);
	public static Color SWAMP_BIOME_COLOR = new Color(11, 218, 81, 255);
	public static Color GLACIER_BIOME_COLOR = new Color(11, 218, 81, 255);
	public static Color RAIN_FOREST_BIOME_COLOR = new Color(11, 218, 81, 255);
	public static Color DECIDUOUS_FOREST_BIOME_COLOR = new Color(11, 218, 81, 255);
	public static Color CONIFEROUS_FOREST_BIOME_COLOR = new Color(11, 218, 81, 255);
	
	
	private Map map;
	private Random generator;
	private Vector<GeneralPath> paths;
	
	private double noiseFactor;
	
	public MapPainter(Map map, Random generator) {
		this.map = map;
		this.generator = generator;
		
		this.noiseFactor = 0.5;
		
		NoisyEdges nEdges = new NoisyEdges();
		nEdges.buildNoisyEdges(map, noiseFactor, this.generator);
		generateNoisyPaths(nEdges);
	}
	
	private void generateNoisyPaths(NoisyEdges nEdges) {
		this.paths = new Vector<GeneralPath>();
		paths.setSize(map.regionsEdges().size());
		
		Iterator<Region> regionsIt = map.regions().iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = map.regionsGraph().sites().get(region.index());
			
			for (Integer regionIdx : site.neighbors) {
				Integer edgeIdx = map.regionsGraph().lookupEdgeFromSites(site, region.index(), regionIdx);
				
				List<Point> hpath0 = nEdges.halfPath0(edgeIdx);
				List<Point> hpath1 = nEdges.halfPath1(edgeIdx);
				
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
	}
	
	public BufferedImage createPaperMap() {
		BufferedImage image = new BufferedImage(map.size(), map.size(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		g.setColor(MapPainter.TRANSPARENT_BLACK_COLOR);
		g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
		
		GeneralPath coastPath = new GeneralPath();
		GeneralPath lakesPath = new GeneralPath();
		GeneralPath riversPath = new GeneralPath();
		GeneralPath regionsPath = new GeneralPath();
		GeneralPath regionsBordersPath = new GeneralPath();
		
		Iterator<RegionEdge> edgesIt = map.regionsEdges().iterator();
		while (edgesIt.hasNext()) {
			RegionEdge rEdge = edgesIt.next();
			VoronoiEdge vEdge = map.regionsGraph().edges().get(rEdge.index());
			
			Region region0 = map.regions().get(vEdge.site0);
			Region region1 = map.regions().get(vEdge.site1);

			if (region0.isOcean() != region1.isOcean()) { //one side is ocean and the other side is land -- coastline
				coastPath.append(paths.get(rEdge.index()), false);
			}
			else if (region0.isOcean() && region1.isOcean()) { //one side is ocean and the other side is ocean -- ocean
				continue;
			}
			else if (region0.isWater() != region1.isWater()) { //lake boundary
				lakesPath.append(paths.get(rEdge.index()), false);
			}
			else if (region0.isWater() || region1.isWater()) { //lake interior
				continue;
			}
			else if (rEdge.isRiver()) { //river
				riversPath.append(paths.get(rEdge.index()), false);
			}
			else if (rEdge.isBorder()) {
				regionsBordersPath.append(paths.get(rEdge.index()), false);
			}
			else { //land
				regionsPath.append(paths.get(rEdge.index()), false);
				/*
				g.setColor(Utils.LAND_COLOR_EDGE);
				g.draw(paths.get(rEdge.index()));
				
				VoronoiCorner corner0 = map.regionsGraph().corners().get(vEdge.corner0);
				VoronoiCorner corner1 = map.regionsGraph().corners().get(vEdge.corner1);
				
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(Color.darkGray);
				g.drawLine((int)Math.floor(corner0.point.x), (int)Math.floor(corner0.point.y), (int)Math.floor(corner1.point.x), (int)Math.floor(corner1.point.y));
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				*/
			}
			
		}
		
		g.setColor(MapPainter.COAST_COLOR);
		g.setStroke(new BasicStroke(2));
		g.draw(coastPath);
		
		g.setColor(MapPainter.COAST_COLOR);
		g.setStroke(new BasicStroke(2));
		g.draw(lakesPath);
		
		Iterator<Region> regionsIt = map.regions().iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = map.regionsGraph().sites().get(region.index());
			if (!region.isWater() && image.getRGB((int)site.center.x, (int)site.center.y) ==  MapPainter.TRANSPARENT_BLACK_COLOR.getRGB())
				Utils.floodFillBackgroundWNoise(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.LAND_BASE_COLOR, 0.01, 0.10f, generator.nextDouble());
			else if (region.isWater() && !region.isOcean() && image.getRGB((int)site.center.x, (int)site.center.y) ==  MapPainter.TRANSPARENT_BLACK_COLOR.getRGB())
				Utils.floodFillBackgroundWNoise(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.LAKE_COLOR, 1, 0.10f, generator.nextDouble());
		}
		
		g.setColor(MapPainter.INNER_BORDER_COLOR);
		g.setStroke(new BasicStroke(1.0f, // line width
			      /* cap style */BasicStroke.CAP_BUTT,
			      /* join style, miter limit */BasicStroke.JOIN_BEVEL, 1.0f,
			      /* the dash pattern */new float[] { 2.0f, 1.0f, 2.0f,1.0f },
			      /* the dash phase */0.0f));
		g.draw(regionsPath);
		
		g.setColor(MapPainter.BORDER_COLOR);
		g.setStroke(new BasicStroke(1.0f, // line width
				  /* cap style */BasicStroke.CAP_BUTT,
			      /* join style, miter limit */BasicStroke.JOIN_BEVEL, 1.0f,
			      /* the dash pattern */new float[] { 2.0f, 1.0f, 2.0f,1.0f },
			      /* the dash phase */0.0f));
		g.draw(regionsBordersPath);
		
		g.setColor(MapPainter.LAKE_EDGE_COLOR);
		g.setStroke(new BasicStroke(1));
		g.draw(riversPath);
		
		BufferedImage background = preparePaperMapBackground();
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_ATOP));
		
		g.drawImage(background, null, 0, 0);
		
		return image;
	}
	
	public BufferedImage createElevationMap() {
		BufferedImage image = new BufferedImage(map.size(), map.size(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		g.setColor(MapPainter.TRANSPARENT_BLACK_COLOR);
		g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
		
		GeneralPath coastPath = new GeneralPath();
		GeneralPath lakesPath = new GeneralPath();
		GeneralPath riversPath = new GeneralPath();
		GeneralPath regionsPath = new GeneralPath();
		
		Iterator<RegionEdge> edgesIt = map.regionsEdges().iterator();
		while (edgesIt.hasNext()) {
			RegionEdge rEdge = edgesIt.next();
			VoronoiEdge vEdge = map.regionsGraph().edges().get(rEdge.index());
			
			Region region0 = map.regions().get(vEdge.site0);
			Region region1 = map.regions().get(vEdge.site1);
			
			if (region0.isOcean() != region1.isOcean()) { //one side is ocean and the other side is land -- coastline
				coastPath.append(paths.get(rEdge.index()), false);
			}
			else if (region0.isOcean() && region1.isOcean()) { //one side is ocean and the other side is ocean -- ocean
				continue;
			}
			else if (region0.isWater() != region1.isWater()) { //lake boundary
				lakesPath.append(paths.get(rEdge.index()), false);
			}
			else if (region0.isWater() || region1.isWater()) { //lake interior
				continue;
			}
			else if (rEdge.isRiver()) { //river
				riversPath.append(paths.get(rEdge.index()), false);
			}
			else { //land
				regionsPath.append(paths.get(rEdge.index()), false);
				/*
				g.setColor(Utils.LAND_COLOR_EDGE);
				g.draw(paths.get(rEdge.index()));
				
				VoronoiCorner corner0 = map.regionsGraph().corners().get(vEdge.corner0);
				VoronoiCorner corner1 = map.regionsGraph().corners().get(vEdge.corner1);
				
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(Color.darkGray);
				g.drawLine((int)Math.floor(corner0.point.x), (int)Math.floor(corner0.point.y), (int)Math.floor(corner1.point.x), (int)Math.floor(corner1.point.y));
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				*/
			}
			
		}
		
		g.setColor(MapPainter.COAST_COLOR);
		g.setStroke(new BasicStroke(2));
		g.draw(coastPath);
		
		g.setColor(MapPainter.COAST_COLOR);
		g.setStroke(new BasicStroke(2));
		g.draw(lakesPath);
		
		g.setColor(MapPainter.COAST_COLOR);
		g.setStroke(new BasicStroke(1));
		g.draw(regionsPath);
		
		g.setColor(MapPainter.LAKE_EDGE_COLOR);
		g.setStroke(new BasicStroke(1));
		g.draw(riversPath);
		
		Iterator<Region> regionsIt = map.regions().iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = map.regionsGraph().sites().get(region.index());
			if (!region.isWater())
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, Utils.interpolateColor(MapPainter.ELEVATION_MIN_COLOR, MapPainter.ELEVATION_MAX_COLOR, region.elevation()));
			else if (region.isWater() && !region.isOcean())
				Utils.floodFillBackgroundWNoise(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.LAKE_COLOR, 1, 0.10f, generator.nextDouble());
		}
		
		BufferedImage background = preparePaperMapBackground();
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_ATOP));
		
		g.drawImage(background, null, 0, 0);
		
		return image;
	}
	
	public BufferedImage createMoistureMap() {
		BufferedImage image = new BufferedImage(map.size(), map.size(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		g.setColor(MapPainter.TRANSPARENT_BLACK_COLOR);
		g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
		
		GeneralPath coastPath = new GeneralPath();
		GeneralPath lakesPath = new GeneralPath();
		GeneralPath riversPath = new GeneralPath();
		GeneralPath regionsPath = new GeneralPath();
		
		Iterator<RegionEdge> edgesIt = map.regionsEdges().iterator();
		while (edgesIt.hasNext()) {
			RegionEdge rEdge = edgesIt.next();
			VoronoiEdge vEdge = map.regionsGraph().edges().get(rEdge.index());
			
			Region region0 = map.regions().get(vEdge.site0);
			Region region1 = map.regions().get(vEdge.site1);
			
			if (region0.isOcean() != region1.isOcean()) { //one side is ocean and the other side is land -- coastline
				coastPath.append(paths.get(rEdge.index()), false);
			}
			else if (region0.isOcean() && region1.isOcean()) { //one side is ocean and the other side is ocean -- ocean
				continue;
			}
			else if (region0.isWater() != region1.isWater()) { //lake boundary
				lakesPath.append(paths.get(rEdge.index()), false);
			}
			else if (region0.isWater() || region1.isWater()) { //lake interior
				continue;
			}
			else if (rEdge.isRiver()) { //river
				riversPath.append(paths.get(rEdge.index()), false);
			}
			else { //land
				regionsPath.append(paths.get(rEdge.index()), false);
				/*
				g.setColor(Utils.LAND_COLOR_EDGE);
				g.draw(paths.get(rEdge.index()));
				
				VoronoiCorner corner0 = map.regionsGraph().corners().get(vEdge.corner0);
				VoronoiCorner corner1 = map.regionsGraph().corners().get(vEdge.corner1);
				
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(Color.darkGray);
				g.drawLine((int)Math.floor(corner0.point.x), (int)Math.floor(corner0.point.y), (int)Math.floor(corner1.point.x), (int)Math.floor(corner1.point.y));
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				*/
			}
			
		}
		
		g.setColor(MapPainter.COAST_COLOR);
		g.setStroke(new BasicStroke(2));
		g.draw(coastPath);
		
		g.setColor(MapPainter.COAST_COLOR);
		g.setStroke(new BasicStroke(2));
		g.draw(lakesPath);
		
		g.setColor(MapPainter.COAST_COLOR);
		g.setStroke(new BasicStroke(1));
		g.draw(regionsPath);
		
		g.setColor(MapPainter.LAKE_EDGE_COLOR);
		g.setStroke(new BasicStroke(1));
		g.draw(riversPath);
		
		Iterator<Region> regionsIt = map.regions().iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = map.regionsGraph().sites().get(region.index());
			if (!region.isWater())
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, Utils.interpolateColor(MapPainter.MOISTURE_MIN_COLOR, MapPainter.MOISTURE_MAX_COLOR, region.moisture()));
			else if (region.isWater() && !region.isOcean())
				Utils.floodFillBackgroundWNoise(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.LAKE_COLOR, 1, 0.10f, generator.nextDouble());
		}
		
		BufferedImage background = preparePaperMapBackground();
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_ATOP));
		
		g.drawImage(background, null, 0, 0);
		
		return image;
	}
	
	public BufferedImage createTemperatureMap() {
		BufferedImage image = new BufferedImage(map.size(), map.size(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		g.setColor(MapPainter.TRANSPARENT_BLACK_COLOR);
		g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
		
		GeneralPath coastPath = new GeneralPath();
		GeneralPath lakesPath = new GeneralPath();
		GeneralPath riversPath = new GeneralPath();
		GeneralPath regionsPath = new GeneralPath();
		
		Iterator<RegionEdge> edgesIt = map.regionsEdges().iterator();
		while (edgesIt.hasNext()) {
			RegionEdge rEdge = edgesIt.next();
			VoronoiEdge vEdge = map.regionsGraph().edges().get(rEdge.index());
			
			Region region0 = map.regions().get(vEdge.site0);
			Region region1 = map.regions().get(vEdge.site1);
			
			if (region0.isOcean() != region1.isOcean()) { //one side is ocean and the other side is land -- coastline
				coastPath.append(paths.get(rEdge.index()), false);
			}
			else if (region0.isOcean() && region1.isOcean()) { //one side is ocean and the other side is ocean -- ocean
				continue;
			}
			else if (region0.isWater() != region1.isWater()) { //lake boundary
				lakesPath.append(paths.get(rEdge.index()), false);
			}
			else if (region0.isWater() || region1.isWater()) { //lake interior
				continue;
			}
			else if (rEdge.isRiver()) { //river
				riversPath.append(paths.get(rEdge.index()), false);
			}
			else { //land
				regionsPath.append(paths.get(rEdge.index()), false);
				/*
				g.setColor(Utils.LAND_COLOR_EDGE);
				g.draw(paths.get(rEdge.index()));
				
				VoronoiCorner corner0 = map.regionsGraph().corners().get(vEdge.corner0);
				VoronoiCorner corner1 = map.regionsGraph().corners().get(vEdge.corner1);
				
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(Color.darkGray);
				g.drawLine((int)Math.floor(corner0.point.x), (int)Math.floor(corner0.point.y), (int)Math.floor(corner1.point.x), (int)Math.floor(corner1.point.y));
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				*/
			}
			
		}
		
		g.setColor(MapPainter.COAST_COLOR);
		g.setStroke(new BasicStroke(2));
		g.draw(coastPath);
		
		g.setColor(MapPainter.COAST_COLOR);
		g.setStroke(new BasicStroke(2));
		g.draw(lakesPath);
		
		g.setColor(MapPainter.COAST_COLOR);
		g.setStroke(new BasicStroke(1));
		g.draw(regionsPath);
		
		g.setColor(MapPainter.LAKE_EDGE_COLOR);
		g.setStroke(new BasicStroke(1));
		g.draw(riversPath);
		
		Iterator<Region> regionsIt = map.regions().iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = map.regionsGraph().sites().get(region.index());
			if (!region.isWater()) {
				if (region.temperature() <= 0.0) {
					Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.TEMPERATURE_MIN_COLOR);
				}
				else if (region.temperature() >= 40.0) {
					Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.TEMPERATURE_MAX_COLOR);
				}
				else if (region.temperature() < 20.0) {
					double temperature = region.temperature() / 20.0; 
					Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, Utils.interpolateColor(MapPainter.TEMPERATURE_MIN_COLOR, MapPainter.TEMPERATURE_MEAN_COLOR, temperature));
				}
				else {
					double temperature = (region.temperature() - 20.0) / 20.0;
					Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, Utils.interpolateColor(MapPainter.TEMPERATURE_MEAN_COLOR, MapPainter.TEMPERATURE_MAX_COLOR, temperature));
				}
			}
			else if (region.isWater() && !region.isOcean())
				Utils.floodFillBackgroundWNoise(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.LAKE_COLOR, 1, 0.10f, generator.nextDouble());
		}
		
		BufferedImage background = preparePaperMapBackground();
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_ATOP));
		
		g.drawImage(background, null, 0, 0);
		
		return image;
	}
	
	public BufferedImage createBiomesMap() {
		BufferedImage image = new BufferedImage(map.size(), map.size(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		g.setColor(MapPainter.TRANSPARENT_BLACK_COLOR);
		g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
		
		GeneralPath coastPath = new GeneralPath();
		GeneralPath lakesPath = new GeneralPath();
		GeneralPath riversPath = new GeneralPath();
		GeneralPath regionsPath = new GeneralPath();
		
		Iterator<RegionEdge> edgesIt = map.regionsEdges().iterator();
		while (edgesIt.hasNext()) {
			RegionEdge rEdge = edgesIt.next();
			VoronoiEdge vEdge = map.regionsGraph().edges().get(rEdge.index());
			
			Region region0 = map.regions().get(vEdge.site0);
			Region region1 = map.regions().get(vEdge.site1);
			
			if (region0.isOcean() != region1.isOcean()) { //one side is ocean and the other side is land -- coastline
				coastPath.append(paths.get(rEdge.index()), false);
			}
			else if (region0.isOcean() && region1.isOcean()) { //one side is ocean and the other side is ocean -- ocean
				continue;
			}
			else if (region0.isWater() != region1.isWater()) { //lake boundary
				lakesPath.append(paths.get(rEdge.index()), false);
			}
			else if (region0.isWater() || region1.isWater()) { //lake interior
				continue;
			}
			else if (rEdge.isRiver()) { //river
				riversPath.append(paths.get(rEdge.index()), false);
			}
			else { //land
				regionsPath.append(paths.get(rEdge.index()), false);
				/*
				g.setColor(Utils.LAND_COLOR_EDGE);
				g.draw(paths.get(rEdge.index()));
				
				VoronoiCorner corner0 = map.regionsGraph().corners().get(vEdge.corner0);
				VoronoiCorner corner1 = map.regionsGraph().corners().get(vEdge.corner1);
				
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(Color.darkGray);
				g.drawLine((int)Math.floor(corner0.point.x), (int)Math.floor(corner0.point.y), (int)Math.floor(corner1.point.x), (int)Math.floor(corner1.point.y));
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				*/
			}
			
		}
		
		g.setColor(MapPainter.COAST_COLOR);
		g.setStroke(new BasicStroke(2));
		g.draw(coastPath);
		
		g.setColor(MapPainter.COAST_COLOR);
		g.setStroke(new BasicStroke(2));
		g.draw(lakesPath);
		
		g.setColor(MapPainter.COAST_COLOR);
		g.setStroke(new BasicStroke(1));
		g.draw(regionsPath);
		
		g.setColor(MapPainter.LAKE_EDGE_COLOR);
		g.setStroke(new BasicStroke(1));
		g.draw(riversPath);
		
		//double seed = generator.nextDouble();
		
		Iterator<Region> regionsIt = map.regions().iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = map.regionsGraph().sites().get(region.index());
			
			switch (region.biome()) {
			case grassland:
				//Utils.floodFillBackgroundFineGraine(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.GRASSLAND_BIOME_COLOR, seed);
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.GRASSLAND_BIOME_COLOR);
				break;
			case mountains:
				//Utils.floodFillBackgroundFineGraine(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.MOUNTAINS_BIOME_COLOR, seed);
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.MOUNTAINS_BIOME_COLOR);
				break;
			case high_mountains:
				//Utils.floodFillBackgroundFineGraine(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.HIGHMOUNTAINS_BIOME_COLOR, seed);
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.HIGHMOUNTAINS_BIOME_COLOR);
				break;
			case coast:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.COAST_BIOME_COLOR);
				break;
			case lake:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.LAKE_BIOME_COLOR);
				break;
			case ice:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.ICE_BIOME_COLOR);
				break;
			case sand_desert:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.SAND_DESERT_BIOME_COLOR);
				break;
			case rock_desert:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.ROCK_DESERT_BIOME_COLOR);
				break;
			case badlands:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.BADLANDS_BIOME_COLOR);
				break;
			case shrubland:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.SHRUBLAND_BIOME_COLOR);
				break;
			case savanna:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.SAVANNA_BIOME_COLOR);
				break;
			case tundra:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.TUNDRA_BIOME_COLOR);
				break;
			case taiga:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.TAIGA_BIOME_COLOR);
				break;
			case glacier:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.GLACIER_BIOME_COLOR);
				break;
			case swamp:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.SWAMP_BIOME_COLOR);
				break;
			case marsh:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.MARSH_BIOME_COLOR);
				break;
			case deciduous_forest:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.DECIDUOUS_FOREST_BIOME_COLOR);
				break;
			case coniferous_forest:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.CONIFEROUS_FOREST_BIOME_COLOR);
				break;
			case rain_forest:
				Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, MapPainter.TRANSPARENT_BLACK_COLOR, MapPainter.RAIN_FOREST_BIOME_COLOR);
				break;
			default:
				break;
			}
		}
		
		BufferedImage background = preparePaperMapBackground();
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_ATOP));
		
		g.drawImage(background, null, 0, 0);
		
		return image;
	}
	
	public BufferedImage createRegionIndex() {
		BufferedImage image = new BufferedImage(map.size(), map.size(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		Color baseColor = new Color(255, 255, 255, 255);
		g.setColor(baseColor);
		g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
		
		Iterator<GeneralPath> pathsIt = paths.iterator();
		while (pathsIt.hasNext()) {
			GeneralPath path = pathsIt.next();
			g.setColor(new Color(0,0,0,255));
			g.setStroke(new BasicStroke(1));
			g.draw(path);
		}
		
		Iterator<Region> regionsIt = map.regions().iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = map.regionsGraph().sites().get(region.index());
			
			int index = region.index() + 1;
			int highPart = index >> 8;
			int lowPart = index & 0x000000FF;
			
			Utils.floodFillBackground(image, (int)site.center.x, (int)site.center.y, baseColor, new Color(highPart, lowPart, 0, 255));
		}
		
		/*
		BufferedImage background = preparePaperMapBackground();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_ATOP));
		g.drawImage(background, null, 0, 0);
		*/
		return image;
	}
	
	protected BufferedImage preparePaperMapBackground() {
		BufferedImage image = new BufferedImage(map.size(), map.size(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		g.setColor(MapPainter.PAPER_BASE_COLOR);
		g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
		
		BufferedImage clouds = Utils.noiseMap(map.size(), generator.nextDouble(), 0.01, 0.05f);
		g.drawImage(clouds, null, 0, 0);
		
		clouds = Utils.noiseMap(map.size(), generator.nextDouble(), 1, 0.10f);
		g.drawImage(clouds, null, 0, 0);
		
		return image;
	}
}
