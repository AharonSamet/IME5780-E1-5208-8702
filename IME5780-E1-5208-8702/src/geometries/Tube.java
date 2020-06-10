package geometries;

import primitives.*;

import java.util.List;

import static primitives.Util.isZero;

/**
 * class Tube for describe tube object
 * this class extends from RadialGeometry
 *
 * @author AhronS, IsraelN
 */
public class Tube extends RadialGeometry {
    private Ray _axisRay;

    //****************************** Constructors *****************************/

    /**
     * tube constructor with color and material
     *
     * @param material material
     * @param emission emission
     * @param radius   radius
     * @param axisRay  axis ray
     */
    public Tube(Material material, Color emission, double radius, Ray axisRay) {
        super(material,emission,radius);
        _axisRay=axisRay;
    }

    /**
     * tube constructor with color
     *
     * @param emission emission
     * @param radius   radius
     * @param axisRay  axis ray
     */
    public Tube(Color emission, double radius, Ray axisRay) {
        this(Material.DEFAULT,emission,radius,axisRay);
    }

    /**
     * Tube constructor
     *
     * @param radius  radius
     * @param axisRay Ray
     */
    public Tube(Double radius, Ray axisRay) {
        this(Color.BLACK,radius,axisRay);
    }

    // ****************************** Getters *****************************/

    /**
     * Tube getter
     *
     * @return Ray
     */
    public Ray getAxisRay() {
        return _axisRay;
    }

    // ****************************** Overrides *****************************/
    @Override
    public Vector getNormal(Point3D point3D) {
        Vector n = point3D.subtract(this._axisRay.getP0());
        double t = n.dotProduct(this._axisRay.getDir());
        Vector shadow = this._axisRay.getDir();
        if (!isZero(t))
            shadow = this._axisRay.getDir().scale(t);
        return n.subtract(shadow).normalize();
    }

    @Override
    public String toString() {
        return super.toString() +
                "_axisRay=" + _axisRay +
                '}';
    }

    @Override
    public List<GeoPoint> findIntersections(Ray ray) {
        return null;
    }
}
