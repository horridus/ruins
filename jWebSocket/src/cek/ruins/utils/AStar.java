package cek.ruins.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public abstract class AStar<T> {
	
	private class Path implements Comparable<Object> {
		public T point;
		public Double f;
		public Double g;
		public Path parent;

		/**
		 * Default c'tor.
		 */
		public Path(){
			this.parent = null;
			this.point = null;
			this.g = f = 0.0;
		}
		
		/**
		 * Another c'tor.
		 */
		public Path(T point){
			this.parent = null;
			this.point = point;
			this.g = this.f = 0.0;
		}

		/**
		 * C'tor by copy another object.
		 *
		 * @param p The path object to clone.
		 */
		@SuppressWarnings("unused")
		public Path(Path p){
			this();
			this.parent = p;
			this.g = p.g;
			this.f = p.f;
		}

		/**
		 * Compare to another object using the total cost f.
		 *
		 * @param o The object to compare to.
		 * @see       Comparable#compareTo()
		 * @return <code>less than 0</code> This object is smaller
		 * than <code>0</code>;
		 *        <code>0</code> Object are the same.
		 *        <code>bigger than 0</code> This object is bigger
		 * than o.
		 */
		public int compareTo(Object o){
			@SuppressWarnings("unchecked")
			Path p = (Path)o;
			return (int)(f - p.f);
		}

		/**
		 * Get the last point on the path.
		 *
		 * @return The last point visited by the path.
		 */
		public T point(){
			return point;
		}

		/**
		 * Set the
		 */
		@SuppressWarnings("unused")
		public void setPoint(T p){
			point = p;
		}
		
		@SuppressWarnings("unused")
		public Path parent() {
			return parent;
		}

		public void setParent(Path parent) {
			this.parent = parent;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof AStar.Path) {
				@SuppressWarnings("unchecked")
				Path p = (Path) obj;
				
				if (p.point() != null && this.point() != null) {
					if (p.point().equals(this.point()))
						return true;
				}
			}
			
			return false;
		}
	}
	
	/**
	 * Check if the current node is a goal for the problem.
	 *
	 * @param node The node to check.
	 * @return <code>true</code> if it is a goal, <code>false</else> otherwise.
	 */
	protected abstract boolean isGoal(T node);

	/**
	 * Cost for the operation to go to <code>to</code> from
	 * <code>from</from>.
	 *
	 * @param from The node we are leaving.
	 * @param to The node we are reaching.
	 * @return The cost of the operation.
	 */
	protected abstract Double g(T from, T to);

	/**
	 * Estimated cost to reach a goal node.
	 * An admissible heuristic never gives a cost bigger than the real
	 * one.
	 * <code>from</from>.
	 *
	 * @param from The node we are leaving.
	 * @param to The node we are reaching.
	 * @return The estimated cost to reach an object.
	 */
	protected abstract Double h(T from, T to);


	/**
	 * Generate the successors for a given node.
	 *
	 * @param node The node we want to expand.
	 * @return A list of possible next steps.
	 */
	protected abstract List<T> generateSuccessors(T node);
	
	private PriorityQueue<Path> open;
	private List<Path> closed;
	
	public AStar() {
		this.open = new PriorityQueue<Path>();
		this.closed = new ArrayList<Path>();
	}
	
	/**
	 * Find the shortest path to a goal starting from
	 * <code>start</code>.
	 *
	 * @param start The initial node.
	 * @return A list of nodes from the initial point to a goal,
	 * <code>null</code> if a path doesn't exist.
	 */
	public List<T> compute(T start) {
		this.open.clear();
		
		Path path = new Path(start);
		f(path, path.point(), path.point());
		
		while (!isGoal(path.point())) {
			this.closed.add(path);
			
			List<T> successors = generateSuccessors(path.point());
			for (T successor : successors) {
				Path successorPath = new Path(successor);
				
				if (!closed.contains(successorPath)) {
					successorPath.setParent(path);
					f(successorPath, path.point(), successorPath.point());
					
					if (!open.contains(successorPath))
						open.offer(successorPath);
					else {
						for (Path p : open) {
							if (p.equals(successorPath) && successorPath.g < p.g ) {
								open.remove(p);
								open.offer(successorPath);
								break;
							}
						}
					}
				}
			}
			
			path = open.poll();
			
			if (path == null)
				return null;
		}
		
		LinkedList<T> retPath = new LinkedList<T>();

		for(Path i = path; i != null; i = i.parent){
				retPath.addFirst(i.point());
		}

		return retPath;
	}
	
	/**
	 * Total cost function to reach the node <code>to</code> from
	 * <code>from</code>.
	 *
	 * The total cost is defined as: f(x) = g(x) + h(x).
	 * @param from The node we are leaving.
	 * @param to The node we are reaching.
	 * @return The total cost.
	 */
	protected Double f(Path p, T from, T to) {
			Double g =  g(from, to) + ((p.parent != null) ? p.parent.g : 0.0);
			Double h = h(from, to);

			p.g = g;
			p.f = g + h;

			return p.f;
	}
}
