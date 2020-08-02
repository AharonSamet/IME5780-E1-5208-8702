package elements;

import primitives.Point3D;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.isZero;

/**
 * Camera class for scene
 *
 * @author AhronS, IsraelN
 */
public class Camera {
    private Point3D place;
    private Vector vto;
    private Vector vup;
    private Vector vright;
    private int _numOfDOFRays;
    private double _distDOF;
    private double _apertureSize;


    // ****************************** Constructors *****************************//

    /**
     * constructor for camera with params
     *
     * @param place place of camera
     * @param vto   forward vector
     * @param vup   up vector
     */
    public Camera(Point3D place, Vector vto, Vector vup) {
        this(place, vto, vup, 1, 0, 0);
    }

    /**
     * constructor for camera with params and the params for DOF
     *
     * @param place        place of camera
     * @param vto          forward vector
     * @param vup          up vector
     * @param numOfDOFRays num of rays
     * @param distDOF      distance from camera to focal plane
     * @param apertureSize size of the aperture
     */
    public Camera(Point3D place, Vector vto, Vector vup, int numOfDOFRays, double distDOF, double apertureSize) {
        if (isZero(vto.dotProduct(vup))) {
            this.place = place;
            this.vto = vto.normalized();
            this.vup = vup.normalized();
            vright = new Vector(vto.crossProduct(vup));
        } else
            throw new IllegalArgumentException("Illegal args");
        _numOfDOFRays = numOfDOFRays;
        _distDOF = distDOF;
        _apertureSize = apertureSize;
    }

    // ****************************** Getters *****************************//

    /**
     * Camera getter
     *
     * @return place
     */
    public Point3D getPlace() {
        return place;
    }

    /**
     * Camera getter
     *
     * @return vto
     */
    public Vector getVto() {
        return vto;
    }

    /**
     * Camera getter
     *
     * @return vup
     */
    public Vector getVup() {
        return vup;
    }

    /**
     * Camera getter
     *
     * @return vright
     */
    public Vector getVright() {
        return vright;
    }

    // ****************************** Functions *****************************//

    /**
     * func constructRaysThroughPixel
     * to create set of rays through pixel (by data and pitagoras)
     *
     * @param nX             pixels on width
     * @param nY             pixels on height
     * @param j              Pixel column
     * @param i              pixel row
     * @param screenDistance dst from view plane
     * @param screenWidth    screen width
     * @param screenHeight   screen height
     * @return ray list
     */
    public List<Ray> constructRaysThroughPixel(int nX, int nY, int j, int i,
                                               double screenDistance, double screenWidth, double screenHeight) {
        Ray centerRay = constructRayThroughPixel(nX, nY, j, i, screenDistance, screenWidth, screenHeight);
        double focalFullDist = Math.sqrt((_distDOF) * (_distDOF) + _apertureSize * _apertureSize);
        Point3D focalPoint = centerRay.getPoint(focalFullDist);
        return centerRay.focalRays(_apertureSize, _numOfDOFRays, focalPoint, vup, vright);
    }

    /**
     * func constructRayThroughPixel
     *
     * @param nX             pixels on width
     * @param nY             pixels on height
     * @param j              Pixel column
     * @param i              pixel row
     * @param screenDistance dst from view plane
     * @param screenWidth    screen width
     * @param screenHeight   screen height
     * @return ray
     */
    public Ray constructRayThroughPixel(int nX, int nY, int j, int i,
                                        double screenDistance, double screenWidth, double screenHeight) {
        if (isZero(screenDistance)) {
            throw new IllegalArgumentException("distance from cam cannot be 0");
        }
        // pixel of image center
        Point3D Pc = place.add(vto.scale(screenDistance));
        double Ry = screenHeight / nY;
        double Rx = screenWidth / nX;
        double yi = ((i - nY / 2d) * Ry + Ry / 2d);
        double xj = ((j - nX / 2d) * Rx + Rx / 2d);
        Point3D Pij = Pc;
        if (!isZero(xj))
            Pij = Pij.add(vright.scale(xj));
        if (!isZero(yi))
            Pij = Pij.add(vup.scale(-yi));
        Vector Vij = Pij.subtract(place);
        return new Ray(place, Vij.normalize());
    }
}