/*
** Author:  Massimo Angelillo
*/

package raytracing.geometry;
import raytracing.util.Vec3;
import scala.math.{abs,max,min,cos,sin};

trait Surface {
	val equation: Vec3 => Double;
	/* 
	** intersectDistance returns the distance between the closest
	** intersection point of a geometric object and a ray, and the ray origin.
	** The intersection point must lie along the ray's path, and not be negative.
	** A return of -1 indicates no intersection
	*/
	def intersectDistance(r: Ray): Double;
	def gradient(pt: Vec3): Vec3 = {
		val grad_x = (equation(pt)-equation(pt - Vec3(x=0.01)))*100;
		val grad_y = (equation(pt)-equation(pt - Vec3(y=0.01)))*100;
		val grad_z = (equation(pt)-equation(pt - Vec3(z=0.01)))*100;
		return Vec3(grad_x, grad_y, grad_z);
	}
	/*
	** intersectPoint returns the actual 3D point, within the world space,
	** where a ray intersects an object.
	*/
	def intersectPoint(ray: Ray): Vec3 = {
		val d = intersectDistance(ray);
		if(d == -1) return null;
		return ray.equation(d);
	}
	def getNormal(pt: Vec3): Vec3 = {
		return gradient(pt).normalize
	}
}