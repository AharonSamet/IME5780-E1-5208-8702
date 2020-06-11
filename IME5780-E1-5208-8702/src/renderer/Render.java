package renderer;

import elements.LightSource;
import geometries.Intersectable.GeoPoint;
import elements.Camera;
import geometries.*;
import primitives.*;
import scene.Scene;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * class for create color matrix by scene
 *
 * @author AhronS, IsraelN
 */
public class Render {
    private ImageWriter _imageWriter;
    private Scene _scene;
    /**
     * const to use with move rays head for shadow rays
     */
    private static final double DELTA = 0.1;

    // ********************** Constructors ********************** //

    /**
     * constructor render
     *
     * @param imageWriter imageWriter object
     * @param scene       the scene to rend
     */
    public Render(ImageWriter imageWriter, Scene scene) {
        this._imageWriter = imageWriter;
        this._scene = scene;
    }

    /**
     * check if have shadow or not
     *
     * @param l  light vector
     * @param n  normal vector
     * @param gp geoPoint
     * @return pos/neg
     */
    private boolean unshaded(Vector l, Vector n, GeoPoint gp, LightSource lightSource) {
        Vector lightDirection = l.scale(-1); // d from point to light source
        Vector delta = n.scale(n.dotProduct(lightDirection) > 0 ? DELTA : -DELTA);
        Point3D point = gp.point.add(delta);
        Ray lightRay = new Ray(point, lightDirection);
        List<GeoPoint> intersections = _scene.getGeometries().findIntersections(lightRay);
        return intersections == null; //return true if not have shadow
    }

    /**
     * Finding the closest point to the camera
     * that the ray intersection any geometries.
     *
     * @param points list of points intersection
     * @return the closest point to the camera
     */
    private GeoPoint getClosestPoint(List<GeoPoint> points) {
        //initialization closesDistance to be the largest number of a double type
        GeoPoint rtn = null;
        double minDistance = Double.MAX_VALUE;
        Point3D closesPoint = null;
        Point3D place = new Point3D(_scene.getCamera().getPlace());
        for (GeoPoint geoPoint : points) {
            Point3D p = geoPoint.point;
            double distance = place.distance(p);
            if (distance < minDistance) {
                minDistance = distance;
                rtn = geoPoint;
            }
        }
        return rtn;
    }

    /**
     * calculator the color
     *
     * @param _intersection the closest point
     * @return - The result from the extended calcColor function
     */
    private Color calcColor(GeoPoint _intersection) {
        List<LightSource> lightSources = _scene.getLights();
        Color result = _scene.getAmbientLight().getIntensity();
        result = result.add(_intersection.geometry.getEmission());

        Vector v = _intersection.point.subtract(_scene.getCamera().getPlace()).normalize();
        Vector n = _intersection.geometry.getNormal(_intersection.point);

        Material material = _intersection.geometry.getMaterial();
        int nShininess = material.getNShininess();
        double kd = material.getKD();
        double ks = material.getKS();

        if (lightSources != null) {
            for (LightSource lightSource : lightSources) {
                Vector l = lightSource.getL(_intersection.point);
                double nl = alignZero(n.dotProduct(l));
                double nv = alignZero(n.dotProduct(v));
                if (nl * nv > 0 && unshaded(l, n, _intersection, lightSource)) {
                    Color ip = lightSource.getIntensity(_intersection.point);
                    result = result.add(
                            calcDiffusive(kd, nl, ip),
                            calcSpecular(ks, l, n, nl, v, nShininess, ip));
                }
            }
        }
        return result;
    }

    /**
     * Prints a grid on the background of our image
     *
     * @param _interval The interval between line to line
     * @param _color    the color for the grid
     */
    public void printGrid(int _interval, java.awt.Color _color) {
        //i = row, j = columns on the image (Nx X Ny)
        for (int i = 0; i < this._imageWriter.getNy(); ++i)
            for (int j = 0; j < this._imageWriter.getNx(); ++j) {
                if (j % _interval == 0 || i % _interval == 0)
                    _imageWriter.writePixel(j, i, _color);
            }
    }

    /**
     * Create the image color matrix from the scene
     * And where there are points that are in the geometric body - then paint a special color
     */
    public void renderImage() {
        Camera camera = _scene.getCamera();
        Intersectable geometries = _scene.getGeometries();
        java.awt.Color background = _scene.getBackground().getColor();

        int nX = _imageWriter.getNx();
        int nY = _imageWriter.getNy();

        double width = _imageWriter.getWidth();
        double height = _imageWriter.getHeight();
        double distance = _scene.getDistance();

        Ray ray;
        for (int i = 0; i < nY; ++i)
            for (int j = 0; j < nX; ++j) {
                //creating a new ray for every pixel
                ray = camera.constructRayThroughPixel(nX, nY, j, i, distance, width, height);
                List<GeoPoint> intersectionPoints = geometries.findIntersections(ray);
                // if no have intersection on this ray so paint background
                if (intersectionPoints == null)
                    _imageWriter.writePixel(j, i, background);
                else {
                    GeoPoint closestPoint = getClosestPoint(intersectionPoints);
                    _imageWriter.writePixel(j, i, calcColor(closestPoint).getColor());
                }
            }
    }

    /**
     * Calculate Specular
     *
     * @param ks         specular
     * @param l          direction light to point
     * @param n          normal
     * @param nl         dot product
     * @param v          direction from point of view to point
     * @param nShininess shininess
     * @param ip         intensity point
     * @return specular
     */
    private Color calcSpecular(double ks, Vector l, Vector n, double nl, Vector v, int nShininess, Color ip) {
        double p = nShininess;
        Vector r = l.add(n.scale(-2 * nl));
        double minusVR = -alignZero(r.dotProduct(v));
        if (minusVR <= 0)
            return Color.BLACK;
        return ip.scale(ks * Math.pow(minusVR, p));
    }

    /**
     * Calculate Diffusive component of light reflection.
     *
     * @param kd diffusive
     * @param nl dot product
     * @param ip intensity point
     * @return diffusive
     */
    private Color calcDiffusive(double kd, double nl, Color ip) {
        if (nl < 0)
            nl = -nl;
        return ip.scale(nl * kd);
    }

    /**
     * Function writeToImage produces unoptimized jpeg file of
     * the image according to pixel color matrix in the directory
     * of the project
     */
    public void writeToImage() {
        _imageWriter.writeToImage();
    }
}