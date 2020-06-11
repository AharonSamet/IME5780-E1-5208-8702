package elements;

import primitives.Color;
import primitives.Point3D;
import primitives.Vector;

/**
 * class point light
 */
public class PointLight extends Light implements LightSource {
    protected Point3D _position;
    protected double _kC, _kL, _kQ;

    // ****************************** Constructors *****************************//

    /**
     * constructor point light
     *
     * @param intensity intensity
     * @param position  position
     * @param kC        kc
     * @param kL        kl
     * @param kQ        kq
     */
    public PointLight(Color intensity, Point3D position, double kC, double kL, double kQ) {
        super(intensity);
        this._position = position;
        this._kC = kC;
        this._kL = kL;
        this._kQ = kQ;
    }

    // ****************************** Overrides *****************************//

    @Override
    public double getDistance(Point3D point) {
        return _position.distance(point);
    }

    @Override
    public Color getIntensity(Point3D p) {
        double dSqr = p.distanceSquared(_position);
        double d = Math.sqrt(dSqr);
        return _intensity.reduce(_kC + _kL * d + _kQ * dSqr);
    }

    @Override
    public Vector getL(Point3D p) {
        return p.subtract(_position).normalize();
    }
}
