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

                if (sign(nl) == sign(nv)) {
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
        for (int i = 0; i < this._imageWriter.getNx(); ++i)
            for (int j = 0; j < this._imageWriter.getNy(); ++j) {
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
        for (int i = 0; i < nX; ++i)
            for (int j = 0; j < nY; ++j) {
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
        Vector R = l.add(n.scale(-2 * nl));
        double minusVR = -alignZero(R.dotProduct(v));
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

    private boolean sign(double val) {
        return (val > 0d);
    }

}