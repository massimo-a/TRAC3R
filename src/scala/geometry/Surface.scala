/*
** Author:  Massimo Angelillo
*/

package raytracing.geometry;
import raytracing.util.Vec3;
import scala.math.{sqrt,abs,max,min,cos,sin};

trait Intersectable {
	/* 
	** intersectDistance returns the distance between the closest
	** intersection point of a geometric object and a ray, and the ray origin.
	** The intersection point must lie along the ray's path, and not be negative.
	** A return of -1 indicates no intersection
	*/
	def intersectDistance(r: Ray): Double;
	
	/*
	** intersectPoint returns the actual 3D point, within the world space,
	** where a ray intersects an object.
	*/
	def intersectPoint(ray: Ray): Vec3 = {
		val d = intersectDistance(ray);
		if(d == -1) return null;
		return ray.equation(d);
	}
	def getNormal(pt: Vec3): Vec3;
}

trait SurfaceMarcher extends Intersectable {
	val equation: Vec3 => Double;
	def intersectDistance(r: Ray): Double;
	def gradient(pt: Vec3): Vec3 = {
		val grad_x = (equation(pt)-equation(pt - Vec3(x=0.01)))*100;
		val grad_y = (equation(pt)-equation(pt - Vec3(y=0.01)))*100;
		val grad_z = (equation(pt)-equation(pt - Vec3(z=0.01)))*100;
		return Vec3(grad_x, grad_y, grad_z);
	}
	def getNormal(pt: Vec3): Vec3 = {
		return gradient(pt).normalize
	}
}

case class Plane(normal: Vec3, point: Vec3) extends Intersectable {
	def getNormal(pt: Vec3): Vec3 = {return normal}
	def intersectDistance(r: Ray): Double = {
		val check = r.direction*normal
		if(abs(check) < 1E-6) return -1
		return ((point - r.origin)*normal)/check
	}
}

case class Sphere(radius: Double, center: Vec3) extends Intersectable {
	def getNormal(pt: Vec3): Vec3 = {return (pt - center).normalize}
	def intersectDistance(r: Ray): Double = {
		val b = 2*(r.direction*(r.origin - center))
		val c = (r.origin - center)*(r.origin - center) - radius*radius
		val disc = b*b - 4*c
		if(disc < 0) return -1;
		val t1 = -b/2 - sqrt(disc)
		val t2 = -b/2 + sqrt(disc)
		if(t1 < 0 && t2 > 0) {
			return t2
		} else if(t1 > 0) {
			return t1
		} else return -1
	}
}