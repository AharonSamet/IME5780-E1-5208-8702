package elements;

import primitives.Color;

/**
 * class Light base class for the Lights
 *
 */
abstract class Light {
    protected Color _intensity;

    // ****************************** Constructors *****************************//

    /**
     * light constructor
     *
     * @param intensity intensity
     */
    public Light(Color intensity) {
        _intensity = intensity;
    }

    /**
     * getter for intensity
     *
     * @return intensity
     */
    public Color getIntensity() {
        return _intensity;
    }

}
