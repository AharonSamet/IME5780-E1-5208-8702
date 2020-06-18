package scene;

import elements.*;
import geometries.Geometries;
import geometries.Intersectable;
import primitives.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * class for scene for build scene
 *
 * @author AhronS, IsraelN
 */
public class Scene {
    private String _name;
    private Color _background;
    private AmbientLight _ambientLight;
    private Geometries _geometries;
    private Camera _camera;
    private double _distance;
    private List<LightSource> _lights;
    // ****************************** Constructors *****************************//

    /**
     * constructor for scene
     *
     * @param name of the scene
     */
    public Scene(String name) {
        _name = name;
        _geometries = new Geometries();
        _lights = new LinkedList<LightSource>();
    }

    // ****************************** Getters *****************************//

    /**
     * Scene getter
     *
     * @return _name value
     */
    public String getName() {
        return _name;
    }

    /**
     * Scene getter
     *
     * @return _background value
     */
    public Color getBackground() {
        return _background;
    }

    /**
     * Scene getter
     *
     * @return _ambientLight value
     */
    public AmbientLight getAmbientLight() {
        return _ambientLight;
    }

    /**
     * Scene getter
     *
     * @return _geometries value
     */
    public Geometries getGeometries() {
        return _geometries;
    }

    /**
     * Scene getter
     *
     * @return _camera value
     */
    public Camera getCamera() {
        return _camera;
    }

    /**
     * Scene getter
     *
     * @return _distance value
     */
    public double getDistance() {
        return _distance;
    }

    public List<LightSource> getLights() {
        return _lights;
    }
    // ****************************** Setters *****************************//

    /**
     * Scene setter for background
     */
    public void setBackground(Color _background) {
        this._background = _background;
    }

    /**
     * Scene setter for ambientLight
     */
    public void setAmbientLight(AmbientLight _ambientLight) {
        this._ambientLight = _ambientLight;
    }

    /**
     * Scene setter for camera
     */
    public void setCamera(Camera _camera) {
        this._camera = _camera;
    }

    /**
     * Scene setter for distance
     */
    public void setDistance(double _distance) {
        this._distance = _distance;
    }

    // ****************************** Functions *****************************//

    /**
     * add func for new geometries
     *
     * @param geometries geo
     */
    public void addGeometries(Intersectable... geometries) {
        for (Intersectable geometry : geometries) {
            _geometries.add(geometry);
        }
    }

    /**
     * func to add lights
     *
     * @param lights light source
     */

    public void addLights(LightSource... lights) {
        if (lights != null)
            _lights.addAll(Arrays.asList(lights));
    }
}

