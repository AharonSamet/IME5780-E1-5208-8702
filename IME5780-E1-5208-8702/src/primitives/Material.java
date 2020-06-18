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
     * constructor for all fields
     *
     * @param kT         transparency
     * @param kR         reflection
     * @param kD         dist
     * @param kS         s
     * @param nShininess shininess
     */
    public Material(double kT, double kR, double kD, double kS, int nShininess) {
        this._kT = kT;
        this._kR = kR;
        this._kD = kD;
        this._kS = kS;
        this._nShininess = nShininess;
    }

    /**
     * Material constructor
     *
     * @param kD         kd
     * @param kS         ks
     * @param nShininess shininess
     */
    public Material(double kD, double kS, int nShininess) {
        this(0,0,kD,kS,nShininess);
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
