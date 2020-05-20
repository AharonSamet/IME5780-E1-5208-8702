package renderer;

import elements.Camera;
import geometries.Intersectable;
import primitives.Color;
import primitives.Point3D;
import primitives.Ray;
import scene.Scene;

import java.util.List;

/**
 * class for create color matrix by scene
 *
 * @author AhronS, IsraelN
 */
public class Render {
    private ImageWriter _imageWriter;
    private Scene _scene;

    /**
     * constructor render
     *
     * @param _imageWriter imageWriter object
     * @param _scene       the scene to rend
     */
    public Render(ImageWriter _imageWriter, Scene _scene) {
        this._imageWriter = _imageWriter;
        this._scene = _scene;
    }

    /**
     * Finding the closest point to the camera
     * that the ray intersection any geometries.
     *
     * @param points list of points intersection
     * @return the closest point to the camera
     */
    private Point3D getClosestPoint(List<Point3D> points) {
        //initialization closesDistance to be the largest number of a double type
        double minDistance = Double.MAX_VALUE;
        Point3D closesPoint = null;
        Point3D place = new Point3D(_scene.getCamera().getPlace());
        for (Point3D p : points) {
            double distance = place.distance(p);
            if (place.distance(p) < minDistance) {
                closesPoint = p;
                minDistance = distance;
            }
        }
        return closesPoint;
    }

    /**
     * calculator the color
     *
     * @param point the closest point
     * @return - The result from the extended calcColor function
     */
    private Color calcColor(Point3D point) {
        return _scene.getAmbientLight().getIntensity();
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

        Ray ray; for (int i = 0; i < nX; ++i)
            for (int j = 0; j < nY; ++j) {
                //creating a new ray for every pixel
                ray = camera.constructRayThroughPixel(nX, nY, j, i, distance, width, height);
                List<Point3D> intersectionPoints = geometries.findIntersections(ray);
                // if no have intersection on this ray so paint background
                if (intersectionPoints == null)
                    _imageWriter.writePixel(j, i, background);
                else {
                    Point3D closestPoint = getClosestPoint(intersectionPoints);
                    _imageWriter.writePixel(j, i, calcColor(closestPoint).getColor());
                }
            }
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