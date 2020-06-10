package geometries;

import primitives.Color;
import primitives.Material;

/**
 * Class RadialGeometries implements Geometry
 *
 * @author AhronS and IsraelN
 */
public abstract class RadialGeometry extends Geometry {
    private double _radius;

    //****************************** Constructors *****************************/

    /**
     * radial geometry constructor with color and material
     *
     * @param material material
     * @param emission emission
     * @param radius   radius
     */
    public RadialGeometry(Material material, Color emission, double radius) {
        super(emission, material);
        _radius = radius;
    }

    /**
     * radial geometry constructor with color
     *
     * @param emission emission
     * @param radius   radius
     */
    public RadialGeometry(Color emission, double radius) {
        this(Material.DEFAULT, emission, radius);
    }

    /**
     * RadialGeometry constructor
     *
     * @param radius radius
     */
    public RadialGeometry(double radius) {
        this(Color.BLACK,radius);
    }


    /**
     * RadialGeometry copy constructor
     *
     * @param other radialGeometry instance
     */
    public RadialGeometry(RadialGeometry other) {
        super(other._emission, other._material);
        this._radius = other._radius;
    }

    // ****************************** Getters *****************************/

    /**
     * RadialGeometry getter
     *
     * @return _radius
     */
    public double getRadius() {
        return _radius;
    }

}
