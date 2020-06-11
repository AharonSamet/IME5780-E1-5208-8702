package elements;

import primitives.Color;
import primitives.Point3D;
import primitives.Vector;

/**
 * light source Interface
 */
public interface LightSource {

    /**
     * get the distance between the point and light source
     *
     * @param point point
     * @return d from point to light source
     */
    public double getDistance(Point3D point);

    /**
     * get intensity func
     *
     * @param p point
     * @return color
     */
    public Color getIntensity(Point3D p);

    /**
     * get lights func
     * p can not be equals to _position
     *
     * @param p point
     * @return vector color
     */
    public Vector getL(Point3D p);


}
