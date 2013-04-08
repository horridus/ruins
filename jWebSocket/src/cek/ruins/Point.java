package cek.ruins;

import java.util.UUID;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;


public class Point implements Marshallable {
	private static double TOLERANCE = 1e-10;
	
	public double x,y;
	
	public Point() {
		this.x = this.y = 0.0;
	}
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double magnitudo() {
		return Math.sqrt(x*x + y*y);
	}
	
	public Point subtract(Point p) {
		return new Point(this.x - p.x, this.y - p .y);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Point))
			return false;
		
		Point point = (Point) o;
		return (Math.pow((this.x - point.x), 2) + Math.pow((this.y - point.y), 2)) < Point.TOLERANCE;
	}
	
	public static Point interpolate(Point p0, Point p1, double interpolation) {
		double x = (1.0 - interpolation) * p0.x + interpolation * p1.x;
		double y = (1.0 - interpolation) * p0.y + interpolation * p1.y;
		
		return new Point(x, y);
	}
	
	@Override
	public int hashCode() {
		int result = 42;
		long fieldx = Double.doubleToLongBits(this.x);
		long fieldy = Double.doubleToLongBits(this.y);
		
		int c = (int) (fieldx ^ (fieldx>>>32));
		c += (int) (fieldy ^ (fieldy>>>32));
		
		return 31 * result + c;
	}
	
	@Override
	public DBObject toJSON() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
		
		builder.add("x", this.x);
		builder.add("y", this.y);
		
		return builder.get();
	}

	@Override
	public UUID id() {
		// TODO Auto-generated method stub
		return null;
	}
}
