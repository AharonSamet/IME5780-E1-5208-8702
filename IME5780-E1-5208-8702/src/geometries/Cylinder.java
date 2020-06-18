package geometries;

import primitives.*;

import java.util.LinkedList;
import java.util.List;

import static primitives.Util.isZero;

/**
 * class Cylinder for describe Cylinder object
 * this class extends from RadialGeometry
 *
 * @author AhronS, IsraelN
 */
public class Cylinder extends Tube {
    private Double _height;


    //****************************** Constructors *****************************/
    /**
     * Cylinder constructor with color and material
     *
     * @param material material
     * @param emission emission
     * @param radius    radius
     * @param ray       ray
     * @param height   the height
     */
    public Cylinder(Material material, Color emission, double radius, Ray ray, double height) {
       super(material,emission,radius,ray);
       _height=height;
    }

    /**
     * Cylinder constructor with color
     *
     * @param emission emission
     * @param radius    radius
     * @param ray       ray
     * @param height   the height
     */
    public Cylinder(Color emission, double radius, Ray ray, double height) {
       this(Material.DEFAULT,emission,radius,ray,height);
    }

    /**
     * Cylinder constructor
     *
     * @param radius radius
     * @param ray    ray
     * @par9am height the height
     */
    public Cylinder(Double radius, Ray ray, Double height) {
       this(Color.BLACK,radius,ray,height);
    }


    //****************************** Getters *****************************/

    /**
     * Cylinder getter
     *
     * @return height
     */
    public Double getHeight() {
        return _height;
    }

    // ****************************** Overrides *****************************/
    @Override
    public Vector getNormal(Point3D point3D) {
        // check if its same points p0==p
        Vector n;
        Double t;
        try {
            n = new Vector(point3D.subtract(this.getAxisRay().getP0())); //red vec
            t = n.dotProduct(this.getAxisRay().getDir());
        } catch (IllegalArgumentException e) {
            return this.getAxisRay().getDir();
        }
        // check if p at the base, orthogonal, so the normal is the vec of the ray
        if (isZero(t) || isZero(this._height - t))
            return this.getAxisRay().getDir();
        // else, it's like Tube
        Vector shadow = this.getAxisRay().getDir().scale(t);
        return n.subtract(shadow).normalize();
    }

    @Override
    public String toString() {
        return super.toString() +
                "_height=" + _height +
                '}';
    }

    @Override
    public List<GeoPoint> findIntersections(Ray ray, double maxDistance) {
        List<GeoPoint> intersections = super.findIntersections(ray, maxDistance);
        List<GeoPoint> result = new LinkedList<>();
        if (intersections != null) {
            for (GeoPoint geoPoint : intersections)
                result.add(new GeoPoint(this, geoPoint.point));

            return result;
        }
        return null;
    }
}