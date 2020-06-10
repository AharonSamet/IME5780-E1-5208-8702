package primitives;

/**
 * class Material for material of geometries
 */
public class Material {
    private final double _kD;
    private final double _kS;
    private final int _nShininess;
    public final static Material DEFAULT = new Material(0d,0d,0);

    // ****************************** Constructors *****************************//

    /**
     * Material constructor
     *
     * @param kD         kd
     * @param kS         ks
     * @param nShininess shininess
     */
    public Material(double kD, double kS, int nShininess) {
        this._kD = kD;
        this._kS = kS;
        this._nShininess = nShininess;
    }

    // ****************************** Getters *****************************//

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
