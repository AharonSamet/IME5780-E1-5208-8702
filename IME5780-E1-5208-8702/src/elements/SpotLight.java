package elements;

import primitives.Color;
import primitives.Point3D;
import primitives.Util;
import primitives.Vector;

import static primitives.Util.isZero;

/**
 * class spot light
 */
public class SpotLight extends PointLight {
    protected Vector _direction;
    protected double _concentration;

    // ****************************** Constructors *****************************//

    /**
     * constructor spot light with concentration
     *
     * @param intensity     intensity
     * @param position      position
     * @param direction     direction
     * @param kC            kc
     * @param kL            kl
     * @param kQ            kq
     * @param concentration concentration
     */
    public SpotLight(Color intensity, Point3D position, Vector direction, double kC, double kL, double kQ, double concentration) {
        super(intensity, position, kC, kL, kQ);
        this._direction = new Vector(direction).normalized();
        this._concentration = concentration;
    }

    /**
     * constructor spot light
     *
     * @param intensity intensity
     * @param position  position
     * @param direction direction
     * @param kC        kc
     * @param kL        kl
     * @param kQ        kq
     */
    public SpotLight(Color intensity, Point3D position, Vector direction, double kC, double kL, double kQ) {
        this(intensity, position, direction, kC, kL, kQ, 1);
    }

    // ****************************** Overrides *****************************//

    @Override
    public Color getIntensity(Point3D p) {
        double projection = _direction.dotProduct(getL(p));
        if (Util.isZero(projection))
            return Color.BLACK;
        double factor = Math.max(0, projection);
        Color pointLightIntensity = super.getIntensity(p);
        if (_concentration != 1)
            factor = Math.pow(factor, _concentration);
        return (pointLightIntensity.scale(factor));
    }

    @Override
    public Vector getL(Point3D p) {
        return super.getL(p);
    }
}