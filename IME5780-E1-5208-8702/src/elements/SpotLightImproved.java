package elements;

import primitives.Color;
import primitives.Point3D;
import primitives.Vector;

/**
 * class for improve the spot light (sharp spot beam)
 * bonus class:)
 */
public class SpotLightImproved extends SpotLight {
    /**
     * constructor
     *
     * @param colorIntensity colorIntensity
     * @param position       position
     * @param direction      direction
     * @param kC             kc
     * @param kL             kl
     * @param kQ             kq
     */
    public SpotLightImproved(Color colorIntensity, Point3D position, Vector direction, double kC, double kL, double kQ, double _concentration) {
        super(colorIntensity, position, direction, kC, kL, kQ,_concentration);
    }

    @Override
    public Color getIntensity(Point3D p) {
        double dSquared = p.distanceSquared(_position);
        double d = p.distance(_position);
        Vector vector;
        if(p.subtract(_position).normalized() == null)
            vector = new Vector(_direction);
        else
            vector = p.subtract(_position).normalized();
        return (_intensity.scale(Math.max(0,Math.pow(_direction.dotProduct(vector),_concentration)))
                .reduce(_kC + _kL * d + _kQ * dSquared));
    }
}