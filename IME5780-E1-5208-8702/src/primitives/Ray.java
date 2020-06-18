package primitives;

import static primitives.Util.isZero;

/**
 * class Ray for describe a vector when the begin point is not 0,0,0
 *
 * @author AhronS, IsraelN
 */
public class Ray {
    private static final double DELTA = 0.1;
    private final Point3D _p0;
    private final Vector _dir;

    // ****************************** Constructors *****************************/

    /**
     * Ray constructor with light dir
     *
     * @param p0  point
     * @param dir light dir
     * @param n   normal
     */
    public Ray(Point3D p0, Vector dir, Vector n) {
        _dir = dir.normalized();
        double nv = n.dotProduct(dir);
        Vector delta = n.scale((nv > 0 ? DELTA : -DELTA));
        _p0 = p0.add(delta);
    }

    /**
     * Ray constructor receiving a 2 value
     *
     * @param p0  as Point3D
     * @param dir as Vector
     */
    public Ray(Point3D p0, Vector dir) {
        //ask the Doctor!!!!!
        //this(p0, dir, null);
        this._p0 = p0;
        this._dir = dir.normalized();
    }

    /**
     * Ray copy constructor
     *
     * @param other Ray
     */
    public Ray(Ray other) {
        this._p0 = other._p0;
        this._dir = other._dir.normalized();
    }

    /****************************** Getters *****************************/

    /**
     * Ray getter value
     *
     * @return Point3D
     */
    public Point3D getP0() {
        return _p0;
    }

    /**
     * Ray getter value
     *
     * @return Vector
     */
    public Vector getDir() {
        return _dir;
    }

    /**
     * the target point for refactor
     *
     * @return the target
     * @ length to scale
     */
    public Point3D getPoint(double length) {
        return isZero(length) ? _p0 : _p0.add(_dir.scale(length));
    }

    // ****************************** Overrides *****************************/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof Ray)) return false;
        Ray ray = (Ray) o;
        return _dir.equals(ray._dir) && _p0.equals(ray._p0);
    }

    @Override
    public String toString() {
        return "Ray{" +
                "_p0=" + _p0 +
                ", _dir=" + _dir +
                '}';
    }
}
