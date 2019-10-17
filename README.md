# TRAC3R
A rendering engine that uses a path tracing algorithm to generate 3D images.
Shapes can be defined by signed distance functions (SDF), implicit functions, or explicitly as a case class.

## RUNNING THE PROGRAM
You need SBT installed, but simply navigating to the root folder and using sbt run in your preferred terminal works.

## REQUIRED INSTALLS
    1. Java JDK version 1.8._ or later
    2. Scala Compiler version 2.12._ or later
    3. SBT version 1.1._ or later

## CURRENT FEATURES
    1. Material Properties
        a. Reflective
        b. Glossy
        c. Transparent
        d. Diffuse
        e. Texture mapping

    2. Geometric Features
        a. Shapes not bounded by volumes
          i.   Plane
          ii.  Sphere
          iii. Triangle
          iv.  Square
          v.   Polygon
        b. Signed Distance Function (SDF) Shapes
          i.   Sphere
          ii.  Cylinder
          iii. Box
          iv.  Torus
          v.   Ellipsoid
          vi.  You can union, intersect, and subtract these shapes
          vii. You can translate and rotate these shapes about the x, y, and z axes

## PATH TRACING ALGORITHM
Path tracing falls under a general umbrella of rendering algorithms known as ray casting.
It is meant to create a hyperrealistic image based on predefined geometry.
The general format for any ray casting algorithm is:
'''
  for all pixels in the image
    shoot ray from camera to pixel (i, j)
      if it intersects any geometry
        do math to get pixel color
      else return background color
'''
## TODO
1. Fix lighting class
2. JSON to case class parser
3. Matrix class
4. Fix terrain class
5. Fix surface marcher classes/traits
6. Work on God rays, smoke and water rendering