package geometries;

import primitives.Color;
import primitives.Material;
import primitives.Point3D;
import primitives.Vector;

/**
 * interface for geometric objects
 */
public abstract class Geometry implements Intersectable {
    protected Color _emission;
    protected Material _material;

    //****************************** Constructors *****************************/

    /**
     * constructor for Geometry with color and material
     *
     * @param emission emission
     * @param material material
     */
    public Geometry(Color emission, Material material) {
        _emission = emission;
        _material = material;
    }

    /**
     * constructor for Geometry
     *
     * @param emission emission
     */
    public Geometry(Color emission) {
        this(emission, Material.DEFAULT);
    }

    /**
     * default constructor for Geometry
     */
    public Geometry() {
        this(Color.BLACK);
    }

    // ****************************** Getters *****************************//

    /**
     * _emission getter
     *
     * @return emission
     */
    public Color getEmission() {
        return _emission;
    }

    /**
     * Geometry getter
     *
     * @return material
     */
    public Material getMaterial() {
        return _material;
    }

    /**
     * this func receiving a point and providing a normal
     *
     * @param point3D the point
     */
    public abstract Vector getNormal(Point3D point3D);
}
