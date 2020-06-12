package geometries;

import primitives.*;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * class Plane for describe plane object
 * this class implements Geometry interface
 *
 * @author AhronS, IsraelN
 */
public class Plane extends Geometry {
    private Point3D _p;
    private Vector _normal;

    // ****************************** Constructors *****************************/


    /**
     * Plane constructor by 3 Points with color and material
     *
     * @param material material
     * @param emission emission
     * @param p1       point3d
     * @param p2       point3d
     * @param p3       point3d
     */
    public Plane(Material material, Color emission, Point3D p1, Point3D p2, Point3D p3) {
        super(emission, material);
        Vector vec1 = new Vector(p2.subtract(p1));
        Vector vec2 = new Vector(p3.subtract(p1));
        this._normal = vec1.crossProduct(vec2).normalize();
        this._p = p1;
    }

    /**
     * Plane constructor by 3 Points and color
     *
     * @param emission emission
     * @param p1       point3d
     * @param p2       point3d
     * @param p3       point3d
     */
    public Plane(Color emission, Point3D p1, Point3D p2, Point3D p3) {
        this(Material.DEFAULT, emission, p1, p2, p3);
    }

    /**
     * Plane constructor by 3 Points
     *
     * @param p1 point3d
     * @param p2 point3d
     * @param p3 point3d
     */
    public Plane(Point3D p1, Point3D p2, Point3D p3) {
        this(Color.BLACK, p1, p2, p3);
    }

//    =============================================================================================

    /**
     * Plane constructor by normal and point with color and material
     *
     * @param material material
     * @param emission emission
     * @param point3D  point
     * @param normal   normal
     */
    public Plane(Material material, Color emission, Point3D point3D, Vector normal) {
        super(emission, material);
        _p = point3D;
        _normal = normal;
    }

    /**
     * Plane constructor by normal and point and color
     *
     * @param emission emission
     * @param point3D  point
     * @param normal   normal
     */
    public Plane(Color emission, Point3D point3D, Vector normal) {
        this(Material.DEFAULT, emission, point3D, normal);
    }

    /**
     * Plane constructor by normal and point
     *
     * @param point3D point
     * @param normal  normal
     */
    public Plane(Point3D point3D, Vector normal) {
        this(Color.BLACK, point3D, normal);
    }

    //****************************** Getters *****************************/

    /**
     * Plane getter
     *
     * @return point
     */
    public Point3D getP() {
        return _p;
    }

    /**
     * Plane getter
     *
     * @return plane normal
     */
    public Vector getNormal() {
        return _normal;
    }

    // ****************************** Overrides *****************************/
    @Override
    public Vector getNormal(Point3D other) {
        Vector p2p = new Vector(this._p.subtract(other));
        if ((p2p.dotProduct(this._normal)) != 0)
            throw new IllegalArgumentException("your point not exist on the plane");
        return _normal;
    }

    @Override
    public String toString() {
        return "Plane{" +
                "_p=" + _p +
                ", _normal=" + _normal +
                '}';
    }

    @Override
    public List<GeoPoint> findIntersections(Ray ray, double maxDistance) {
        Vector p0Q;
        Point3D p0 = ray.getP0();
        Vector v = ray.getDir();
        //check if p0 == q0 (0 point)
        try {
            p0Q = _p.subtract(p0);
        } catch (IllegalArgumentException e) {
            return null;
        }
        // check if ray parallel or include the plane (0 point)
        double nv = _normal.dotProduct(v);
        if (isZero(nv))
            return null;
        double t = alignZero(_normal.dotProduct(p0Q) / nv);
        if (t <= 0)
            return null;
        if (alignZero(maxDistance-t)<0)
            return null;
        return List.of(new GeoPoint(this, p0.add(v.scale(t))));
    }
}