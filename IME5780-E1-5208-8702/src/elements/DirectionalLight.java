package elements;

import primitives.Color;
import primitives.Point3D;
import primitives.Vector;

/**
 * class Directional light
 */
public class DirectionalLight extends Light implements LightSource {
    private Vector _direction;

    // ****************************** Constructors *****************************//

    /**
     * constructor directional light
     *
     * @param intensity intensity
     * @param direction direction
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        this._direction = direction.normalized();
    }

    // ****************************** Overrides *****************************//

    @Override
    public double getDistance(Point3D point) {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public Color getIntensity(Point3D p) {
        return super.getIntensity();
    }

    @Override
    public Vector getL(Point3D p) {
        return _direction;
    }
}