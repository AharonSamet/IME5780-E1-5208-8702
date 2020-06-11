package elements;

import primitives.Color;
import primitives.Point3D;
import primitives.Util;
import primitives.Vector;

import static primitives.Util.alignZero;
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
        Vector projection = getL(p);
        if (projection == null)
            projection = _direction;
        double cos = alignZero(projection.dotProduct(_direction));
        if (cos <= 0)
            return Color.BLACK;
        return super.getIntensity(p).scale(cos);
    }
}