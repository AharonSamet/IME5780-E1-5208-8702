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
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.001;
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
     * @param light    light source
     * @param l        light vector
     * @param n        normal vector
     * @param geoPoint geoPoint
     * @return pos/neg
     */
    private boolean unshaded(LightSource light, Vector l, Vector n, GeoPoint geoPoint) {
        Vector lightDirection = l.scale(-1); // from point to light source
        Vector delta = n.scale(n.dotProduct(lightDirection) > 0 ? DELTA : -DELTA);
        Point3D point = geoPoint.point.add(delta);
        Ray lightRay = new Ray(point, lightDirection);
        List<GeoPoint> intersections = _scene.getGeometries().findIntersections(lightRay,
                light.getDistance(geoPoint.point));
        return intersections == null;
    }


    /**
     * from all intersections we need the closest one
     *
     * @param ray ray
     * @return the closest intersection
     */
    private GeoPoint findClosestIntersection(Ray ray) {
        GeoPoint closestPoint = null;
        double closestDistance = Double.MAX_VALUE;
        Point3D ray_p0 = ray.getP0();

        List<GeoPoint> intersections = _scene.getGeometries().findIntersections(ray);
        if (intersections == null)
            return null;

        for (GeoPoint gp : intersections) {
            double distance = ray_p0.distance(gp.point);

            if (distance < closestDistance) {
                closestPoint = gp;
                closestDistance = distance;
            }
        }
        return closestPoint;
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
     * @param gp
     * @param inRay
     * @return
     */
    private Color calcColor(GeoPoint gp, Ray inRay) {
        return calcColor(gp, inRay, MAX_CALC_COLOR_LEVEL, 1.0).add(
                _scene.getAmbientLight().getIntensity());
    }

    /**
     * @param gp
     * @param inRay
     * @param level
     * @param k
     * @return
     */
    private Color calcColor(GeoPoint gp, Ray inRay, int level, double k) {
        if (level == 0 || k < MIN_CALC_COLOR_K) {
            return Color.BLACK;
        }
        Color color = gp.geometry.getEmission();
        Vector v = gp.point.subtract(_scene.getCamera().getPlace()).normalize(); //direction from point of view to point
        Vector n = gp.geometry.getNormal(gp.point); //normal ray to the surface at the point
        Material material = gp.geometry.getMaterial();
        int nShininess = material.getNShininess(); //degree of light shining of the material
        double kd = material.getKD(); //degree of diffusion of the material
        double ks = material.getKS(); //degree of light return shining of the material


        if (_scene.getLights() != null) {
            for (LightSource lightSource : _scene.getLights()) {

                Vector l = lightSource.getL(gp.point); //the ray of the light
                double nl = alignZero(n.dotProduct(l)); //dot-product n*l
                double nv = alignZero(n.dotProduct(v));

                if (nl * nv < 0) { // Check that 𝒔𝒊𝒈𝒏(𝒍∙𝒏) == 𝒔𝒊𝒈𝒏(𝒗∙𝒏) according to Phong reflectance model
                    double ktr = transparency(lightSource, l, n, gp);
                    if (ktr * k > MIN_CALC_COLOR_K) {
                        Color lightIntensity = lightSource.getIntensity(gp.point).scale(ktr);
                        color = color.add(
                                calcDiffusive(kd, nl, lightIntensity),
                                calcSpecular(ks, l, n, nl, v, nShininess, lightIntensity));
                    }
                }
            }
        }

        if (level == 1)
            return Color.BLACK;
        double kr = material.getKR(); //degree of material reflection
        double kkr = k * kr;
        if (kkr > MIN_CALC_COLOR_K) {
            Ray reflectedRay = constructReflectedRay(gp.point, inRay, n);
            GeoPoint reflectedPoint = findClosestIntersection(reflectedRay);
            if (reflectedPoint != null) {
                color = color.add(calcColor(reflectedPoint, reflectedRay, level - 1, kkr).scale(kr));
            }
        }

        double kt = material.getKT(); //degree of material transparency
        double kkt = k * kt;
        if (kkt > MIN_CALC_COLOR_K) {
            Ray refractedRay = constructRefractedRay(gp.point, inRay, n);
            GeoPoint refractedPoint = findClosestIntersection(refractedRay);
            if (refractedPoint != null) {
                color = color.add(calcColor(refractedPoint, refractedRay, level - 1, kkt).scale(kt));
            }
        }
        return color;
    }

    private double transparency(LightSource ls, Vector l, Vector n, GeoPoint gp) {
        Vector lightDirection = l.scale(-1); // from point to light source
        Ray lightRay = new Ray(gp.point, lightDirection, n);
        List<GeoPoint> intersections = _scene.getGeometries().findIntersections(lightRay);
        if (intersections == null)
            return 1.0;
        double lightDistance = ls.getDistance(gp.point);
        double ktr = 1.0;
        for (GeoPoint geo : intersections) {
            if (alignZero(geo.point.distance(geo.point) - lightDistance) <= 0) {
                ktr *= geo.geometry.getMaterial().getKT();
                if (ktr < MIN_CALC_COLOR_K)
                    return 0.0;
            }
        }
        return ktr;
    }

    private Ray constructReflectedRay(Point3D point, Ray inRay, Vector n) {
        return null;
    }

    private Ray constructRefractedRay(Point3D point, Ray inRay, Vector n) {
        return null;
    }


    /*
     * calculator the color
     *
     * @param _intersection the closest point
     * @return - The result from the extended calcColor function
     *//*
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
                if (nl * nv > 0 && unshaded(lightSource, l, n, _intersection)) {
                    Color ip = lightSource.getIntensity(_intersection.point);
                    result = result.add(
                            calcDiffusive(kd, nl, ip),
                            calcSpecular(ks, l, n, nl, v, nShininess, ip));
                }
            }
        }
        return result;
    }
*/

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
                    _imageWriter.writePixel(j, i, calcColor(closestPoint, ray).getColor());
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