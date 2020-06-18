package primitives;

/**
 * class Material for material of geometries
 */
public class Material {
    private double _kT;
    private double _kR;
    private final double _kD;
    private final double _kS;
    private final int _nShininess;
    public final static Material DEFAULT = new Material(0d, 0d, 0);

    // ****************************** Constructors *****************************//


    /**
     * constructor
     *
     * @param kD         kd
     * @param kS         ks
     * @param nShininess number of shininess
     * @param kT         transparency
     * @param kR         reflection
     */
    public Material(double kD, double kS, int nShininess, double kT, double kR) {
        _kD = kD;
        _kS = kS;
        _nShininess = nShininess;
        _kT = kT;
        _kR = kR;
    }

    /**
     * Material constructor
     *
     * @param kD         kd
     * @param kS         ks
     * @param nShininess shininess
     */
    public Material(double kD, double kS, int nShininess) {
        this( kD, kS, nShininess, 0.0, 0.0);
    }

    /**
     * constructor for new material
     * @param material the material of object
     */
    public Material(Material material) {
        this(material._kD, material._kS, material._nShininess, material._kT, material._kR);
    }

    // ****************************** Getters *****************************//

    /**
     * getter for transparency/ refraction
     *
     * @return kt
     */
    public double getKT() {
        return _kT;
    }

    /**
     * getter for reflection
     *
     * @return kr
     */
    public double getKR() {
        return _kR;
    }

    /**
     * material getter
     *
     * @return kD
     */
    public double getKD() {
        return _kD;
    }

    /**
     * material getter
     *
     * @return kS
     */
    public double getKS() {
        return _kS;
    }

    /**
     * material getter
     *
     * @return nShininess
     */
    public int getNShininess() {
        return _nShininess;
    }
}
