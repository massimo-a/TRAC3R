/*
** Author:  Massimo Angelillo
*/

package invision.geometry

import invision.util.Vec3

import scala.math.{max, min}

trait Bounded {
	def hit(r: Ray): Boolean
	def merge(b: Bounded): Bounded
	def translate(x: Double, y: Double, z: Double): Bounded
	def rotateWith(ro: Vec3=>Vec3): Bounded
	def intersections(r: Ray): (Double, Double)
	def stretch(a: Double, b: Double, c: Double): Bounded
}

object Bounded {
	private final case object NoBounds extends Bounded {
		def hit(r: Ray): Boolean = true
		def merge(b: Bounded): Bounded = NoBounds
		def translate(x: Double, y: Double, z: Double): Bounded = NoBounds
		def rotateWith(ro: Vec3=>Vec3): Bounded = NoBounds
		def intersections(r: Ray): (Double, Double) = (1, 1e12)
		def stretch(a: Double, b: Double, c: Double): Bounded = NoBounds
	}
	private final case class BoundingBox(pos: Vec3, right: Vec3, up: Vec3, forward: Vec3) extends Bounded {
		private val vertices = Array(
			pos, pos + right, pos + up, pos + forward,
			pos + right + up, pos + right + forward, pos + up + forward,
			pos + right + up + forward
		)
		private val minimum: Vec3 = vertices.reduceLeft((a: Vec3, b: Vec3) => Vec3(a.x min b.x, a.y min b.y, a.z min b.z))
		private val maximum: Vec3 = vertices.reduceLeft((a: Vec3, b: Vec3) => Vec3(a.x max b.x, a.y max b.y, a.z max b.z))
		
		def hit(r: Ray): Boolean = {
			val t = intersections(r)
			t._2 >= t._1 || (t._2 > 0 ^ t._1 > 0)
		}
		
		def merge(b: Bounded): Bounded = {
			b match {
				case bb: BoundingBox =>
					val _min = Vec3(bb.minimum.x min minimum.x, bb.minimum.y min minimum.y, bb.minimum.z min minimum.z)
					val _max = Vec3(bb.maximum.x max maximum.x, bb.maximum.y max maximum.y, bb.maximum.z max maximum.z)
					val diff = _max - _min
					BoundingBox(_min, Vec3(diff.x), Vec3(0, diff.y), Vec3(0, 0, diff.z))
				case _: Bounded => NoBounds
			}
		}
		
		def translate(x: Double, y: Double, z: Double): Bounded = {
			BoundingBox(pos+Vec3(x, y, z), right, up, forward)
		}
		
		def rotateWith(ro: Vec3 => Vec3): Bounded = {
			BoundingBox(ro(pos), ro(right), ro(up), ro(forward))
		}
		
		def stretch(a: Double, b: Double, c: Double): Bounded = {
			BoundingBox(pos, right*a, up*b, forward*c)
		}
		
		def intersections(r: Ray): (Double, Double) = {
			val inverseDir = r.direction.map(x => 1/x)
			val mins = (minimum - r.origin)**inverseDir
			val maxs = (maximum - r.origin)**inverseDir
			(max(max(max(Double.NegativeInfinity, min(mins.x, maxs.x)), min(mins.y, maxs.y)), min(mins.z, maxs.z)),
			 min(min(min(Double.PositiveInfinity, max(mins.x, maxs.x)), max(mins.y, maxs.y)), max(mins.z, maxs.z)))
		}
	}
	def apply(p: Vec3, width: Double, height: Double, depth: Double): Bounded = {
		BoundingBox(p, Vec3(width),Vec3(0,height),Vec3(0,0,depth))
	}
	def apply(min: Vec3, max: Vec3): Bounded = {
		BoundingBox(min, Vec3(max.x-min.x),Vec3(0,max.y-min.y),Vec3(0,0,max.z-min.z))
	}
	def apply(): Bounded = {
		NoBounds
	}
}
