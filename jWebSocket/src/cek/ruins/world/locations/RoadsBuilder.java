package cek.ruins.world.locations;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cek.ruins.Point;
import cek.ruins.map.Map;
import cek.ruins.map.Region;
import cek.ruins.map.voronoi.VoronoiSite;
import cek.ruins.utils.AStarFuck;

public class RoadsBuilder extends AStarFuck<VoronoiSite> {
	private Map map;
	private Integer destination;
	private Integer origin;
	private VoronoiSite destinationSite;
	private VoronoiSite originSite;

	private double meanRegionLenght;

	public RoadsBuilder(Map map) {
		this.map = map;

		float totalArea = 0;
		//we suppose that regions are squares for huristic distance calculations
		Iterator<VoronoiSite> sitesIt = this.map.regionsGraph().sites().iterator();
		while (sitesIt.hasNext()) {
			totalArea += sitesIt.next().area;
		}

		this.meanRegionLenght = Math.sqrt(totalArea/this.map.regionsGraph().sites().size());
	}

	public Road buildNewRoad(Location locationOrigin, Location locationDestination) {
		this.destination = locationDestination.regionIndex();
		this.origin = locationOrigin.regionIndex();
		this.destinationSite = this.map.regionsGraph().sites().get(this.destination);
		this.originSite = this.map.regionsGraph().sites().get(this.origin);

		List<Integer> path = new LinkedList<Integer>();
		List<Point> pathCoords = new LinkedList<Point>();

		pathCoords.add(new Point(locationOrigin.x, locationOrigin.y));

		List<VoronoiSite> vsPath = this.compute(this.originSite);

		Iterator<VoronoiSite> vsPathIt = vsPath.iterator();
		while (vsPathIt.hasNext()) {
			VoronoiSite site = vsPathIt.next();
			path.add(site.index);
			pathCoords.add(site.center);
		}

		pathCoords.add(new Point(locationDestination.x, locationDestination.y));

		return new Road(path, pathCoords);
	}

	@Override
	protected boolean isGoal(VoronoiSite node) {
		if (node.index == this.destination)
			return true;
		else
			return false;
	}

	@Override
	protected Double g(VoronoiSite from, VoronoiSite to) {
		if (from.index == to.index)
			return 0.0;
		else {
			Double weight = 1.0;
			Region region = this.map.regions().get(to.index);

			switch (region.biome()) {
			case badlands:
				weight = 15.0;
				break;
			case coast:
				weight = 1.0;
				break;
			case coniferous_forest:
				weight = 3.0;
				break;
			case deciduous_forest:
				weight = 3.0;
				break;
			case glacier:
				weight = 35.0;
				break;
			case grassland:
				weight = 1.0;
				break;
			case high_mountains:
				weight = 40.0;
				break;
			case ice:
				weight = 35.0;
				break;
			case lake:
				weight = Double.MAX_VALUE;
				break;
			case marsh:
				weight = 10.0;
				break;
			case mountains:
				weight = 20.0;
				break;
			case ocean:
				weight = Double.MAX_VALUE;
				break;
			case rain_forest:
				weight = 5.0;
				break;
			case rock_desert:
				weight = 25.0;
				break;
			case sand_desert:
				weight = 25.0;
				break;
			case savanna:
				weight = 10.0;
				break;
			case shrubland:
				weight = 1.0;
				break;
			case swamp:
				weight = 20.0;
				break;
			case taiga:
				weight = 1.0;
				break;
			case tundra:
				weight = 1.0;
				break;
			case volcanos:
				weight = Double.MAX_VALUE;
				break;
			default:
				break;
			}
			return weight;
		}
	}

	@Override
	protected Double h(VoronoiSite from, VoronoiSite to) {
		double distance = Math.sqrt(Math.pow(from.center.x - to.center.x, 2.0) + Math.pow(from.center.y - to.center.y, 2.0));

		return distance/this.meanRegionLenght;
	}

	@Override
	protected List<VoronoiSite> generateSuccessors(VoronoiSite node) {
		List<VoronoiSite> successors = new LinkedList<VoronoiSite>();

		Iterator<Integer> neighborsIt = node.neighbors.iterator();
		while (neighborsIt.hasNext()) {
			VoronoiSite neighbor = this.map.regionsGraph().sites().get(neighborsIt.next());
			successors.add(neighbor);
		}

		return successors;
	}
}
