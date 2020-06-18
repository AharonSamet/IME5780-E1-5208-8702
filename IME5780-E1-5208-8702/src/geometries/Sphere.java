package geometries;

import primitives.*;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * class Sphere for describe sphere object
 * this class extends from RadialGeometry
 *
 * @author AhronS, IsraelN
 */
public class Sphere extends RadialGeometry {

    private Point3D _center;

    //****************************** Constructors *****************************/

    /**
     * Sphere constructor with color and material
     *
     * @param material      material
     * @param emissionLight emission color
     * @param radius        radius
     * @param center        center
     */
    public Sphere(Color emissionLight, Material material, double radius, Point3D center) {
        super(material, emissionLight, radius);
        _center = center;
    }

    /**
     * Sphere constructor with color
     *
     * @param emission emission color
     * @param radius   radius
     * @param center   center
     */
    public Sphere(Color emission, double radius, Point3D center) {
        this(emission, Material.DEFAULT, radius, center);
    }

    /**
     * Sphere constructor
     *
     * @param radius radius value
     * @param center point
     */
    public Sphere(Double radius, Point3D center) {
        this(Color.BLACK, radius, center);
    }

//****************************** Getters *****************************/

    /**
     * Sphere getter
     *
     * @return center point
     */
    public Point3D getCenter() {
        return _center;
    }

    // ****************************** Overrides *****************************/
    @Override
    public Vector getNormal(Point3D point3D) {
        return point3D.subtract(_center).normalize();
    }

    @Override
    public String toString() {
        return super.toString() + "Sphere{" +
                "_center=" + _center +
                '}';
    }

    @Override
    public List<GeoPoint> findIntersections(Ray ray, double maxDistance) {
        Point3D p0 = ray.getP0();
        Vector v = ray.getDir();
        Vector u;
        //check if p0 same as _center
        try {
            u = _center.subtract(p0);
        } catch (IllegalArgumentException e) {
            return List.of(new GeoPoint(this, ray.getPoint(getRadius())));
        }
        double tm = alignZero(v.dotProduct(u));
        double dSquared = u.lengthSquared() - tm * tm;
        double thSquared = alignZero(getRadius() * getRadius() - dSquared);

        if (thSquared <= 0)
            return null;
        double th = alignZero(Math.sqrt(thSquared));
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);
        if (t1 <= 0 && t2 <= 0)
            return null;
        if (t1 > 0 && t2 > 0 && t1 - maxDistance <= 0 && t2 - maxDistance <= 0)
            return List.of(new GeoPoint(this, ray.getPoint(t1)), new GeoPoint(this, ray.getPoint(t2)));
        if (t1 > 0 && t1 - maxDistance <= 0)
            return List.of(new GeoPoint(this, ray.getPoint(t1)));
        if (t2 - maxDistance > 0)
            return null;
        return List.of(new GeoPoint(this, ray.getPoint(t2)));
    }
}