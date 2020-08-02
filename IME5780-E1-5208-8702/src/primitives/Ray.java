package primitives;

import java.util.LinkedList;
import java.util.List;

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

    /**
     * this func create set of rays
     *
     * @param apertureSize aperture Size
     * @param numOfDOFRays num Of DOF Rays
     * @param focalPoint focalPoint
     * @param vUp vec up
     * @param vRight vec right
     * @return a list of rays
     */
    public List<Ray> focalRays(double apertureSize, int numOfDOFRays, Point3D focalPoint, Vector vUp, Vector vRight) {
        List<Ray> raysList = new LinkedList<>();
        raysList.add(this);
        if (Util.isZero(apertureSize) || numOfDOFRays <= 1)
            return raysList;
        int loopLength = (int) Math.sqrt(numOfDOFRays);
        double factor = apertureSize / loopLength;
        //first location
        Point3D p0 = this._p0.add(vUp.scale(-apertureSize / 2)).add(vRight.scale(-apertureSize / 2));
        for (int i = 1; i <= loopLength; i++) {
            for (int j = 1; j <= loopLength; j++) {
                Vector vUpAxis = vUp.scale(i * factor);
                Vector vRightAxis = vRight.scale(j * factor);
                Point3D currentPoint = p0.add(vUpAxis).add(vRightAxis);
                raysList.add(new Ray(currentPoint, focalPoint.subtract(currentPoint)));
            }
        }
        return raysList;
    }

    /****************************** Overrides *****************************/
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
