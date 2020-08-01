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
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.001;

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
        Ray lightRay = new Ray(geoPoint.point, lightDirection, n);
        Point3D point = geoPoint.point;
        List<GeoPoint> intersections = _scene.getGeometries().findIntersections(lightRay);
        if (intersections == null)
            return true;
        double lightDistance = light.getDistance(point);
        for (GeoPoint gp : intersections) {
            double t = gp.point.distance(point) - lightDistance;
            if (alignZero(t) <= 0 && gp.geometry.getMaterial().getKT() == 0)
                return false;
        }
        return true;
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
     * Calc the color intensity in a intersection point
     *
     * @param ray    ray
     * @return the color
     */
    private Color calcColor(Ray ray) {
        GeoPoint geoPoint = findClosestIntersection(ray);
        // if no have intersection on this ray so paint background
        if (geoPoint == null)
            return _scene.getBackground();
        else {
            return calcColor(geoPoint, ray, MAX_CALC_COLOR_LEVEL, 1.0).add(
                    _scene.getAmbientLight().getIntensity());
        }

    }

    private Color calcColor(List<Ray> rays){
        Color color = Color.BLACK;
        for(Ray ray:rays){
            color = color.add(calcColor(ray));
        }
        return color.reduce(rays.size());
    }

    /**
     * recursive func
     *
     * @param geoPoint geo point
     * @param inRay    ray
     * @param level    parameter of limit times recursive
     * @param k        stop condition
     * @return the color
     */
    private Color calcColor(GeoPoint geoPoint, Ray inRay, int level, double k) {
        if (level == 1 || k < MIN_CALC_COLOR_K) {
            return Color.BLACK;
        }
        Color result = geoPoint.geometry.getEmission();
        Point3D pointGeo = geoPoint.point;
        Vector v = pointGeo.subtract(_scene.getCamera().getPlace()).normalize();
        // the normal vector of geometric object in the geoPoint
        Vector n = geoPoint.geometry.getNormal(pointGeo);

        double nv = alignZero(n.dotProduct(v));
        if (nv == 0)
            return result;

        //the material of the geometric object
        Material material = geoPoint.geometry.getMaterial();
        //the shininess of the geometric object
        int nShininess = material.getNShininess();
        double kkr = k * material.getKR();
        double kkt = k * material.getKT();
        result = result.add(getLightSourcesColors(geoPoint, k, result, v, n, nv, nShininess, material.getKD(), material.getKS()));
        // if the reflection factor after multiplication to the 'k' is bigger than MIN_CALC_COLOR_K
        if (kkr > MIN_CALC_COLOR_K) {
            Ray reflectedRay = constructReflectedRay(pointGeo, inRay, n);
            GeoPoint reflectedPoint = findClosestIntersection(reflectedRay);
            if (reflectedPoint != null) {
                result = result.add(calcColor(reflectedPoint, reflectedRay, level - 1, kkr).scale(material.getKR()));
            }
        }
        // if the transparency factor after multiplication to the 'k' is bigger than MIN_CALC_COLOR_K
        if (kkt > MIN_CALC_COLOR_K) {
            Ray refractedRay = constructRefractedRay(pointGeo, inRay, n);
            GeoPoint refractedPoint = findClosestIntersection(refractedRay);
            if (refractedPoint != null) {
                result = result.add(calcColor(refractedPoint, refractedRay, level - 1, kkt).scale(material.getKT()));
            }
        }
        return result;
    }

    /**
     * func calc the level of transparency
     *
     * @param ls ls
     * @param l  l
     * @param n  n
     * @param gp gp
     * @return the level
     */
    private double transparency(LightSource ls, Vector l, Vector n, GeoPoint gp) {
        Vector lightDirection = l.scale(-1); // from point to light source
        Ray lightRay = new Ray(gp.point, lightDirection, n);
        List<GeoPoint> intersections = _scene.getGeometries().findIntersections(lightRay);
        if (intersections == null)
            return 1d;
        double lightDistance = ls.getDistance(gp.point);
        double ktr = 1d;
        for (GeoPoint geo : intersections) {
            double temp = geo.point.distance(gp.point) - lightDistance;
            if (alignZero(temp) <= 0) {
                ktr *= geo.geometry.getMaterial().getKT();
                if (ktr < MIN_CALC_COLOR_K) {
                    return 0.0;
                }
            }
        }
        return ktr;
    }

    /**
     * func calc the reflection ray
     *
     * @param point point
     * @param inRay ray
     * @param n     normal
     * @return the ray
     */
    private Ray constructReflectedRay(Point3D point, Ray inRay, Vector n) {
        Vector v = inRay.getDir();
        double vn = v.dotProduct(n);
        if (vn == 0)
            return null;
        Vector r = v.subtract(n.scale(2 * vn));
        return new Ray(point, r, n);
    }

    /**
     * this func calc the refracted ray
     *
     * @param point point
     * @param inRay ray
     * @param n     normal
     * @return the ref ray
     */
    private Ray constructRefractedRay(Point3D point, Ray inRay, Vector n) {
        return new Ray(point, inRay.getDir(), n);
    }

    /**
     * this func Calc the intensity in a intersection point
     * with the light sources rays
     *
     * @param geoPoint   geo point
     * @param k          k
     * @param result     the color of lighted point
     * @param v          from camera to lighted point
     * @param n          normal
     * @param nv         nv
     * @param nShininess shininess
     * @param kd         kd
     * @param ks         ks
     * @return get light sources color
     */
    private Color getLightSourcesColors(GeoPoint geoPoint, double k, Color result, Vector v, Vector n, double nv, int nShininess, double kd, double ks) {
        Point3D pointGeo = geoPoint.point;
        List<LightSource> lightSources = _scene.getLights();
        // if the light source collection did not empty
        if (lightSources != null) {
            for (LightSource lightSource : lightSources) {
                Vector l = lightSource.getL(pointGeo);
                double nl = alignZero(n.dotProduct(l));
                if (nl * nv > 0) {
                    double ktr = transparency(lightSource, l, n, geoPoint);
                    if (ktr * k > MIN_CALC_COLOR_K) {
                        Color lightIntensity = lightSource.getIntensity(pointGeo).scale(ktr);
                        result = result.add(calcDiffusive(kd, nl, lightIntensity),
                                calcSpecular(ks, l, n, nl, v, nShininess, lightIntensity));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Prints a grid on the background of our image
     *
     * @param interval The interval between line to line
     * @param color    the color for the grid
     */
    public void printGrid(int interval, java.awt.Color color) {
        //i = row, j = columns on the image (Nx X Ny)
        for (int i = 0; i < this._imageWriter.getNy(); ++i)
            for (int j = 0; j < this._imageWriter.getNx(); ++j) {
                if (j % interval == 0 || i % interval == 0)
                    _imageWriter.writePixel(j, i, color);
            }
    }

    /**
     * Create the image color matrix from the scene
     * And where there are points that are in the geometric body - then paint a special color
     */
    public void renderImage() {
        Camera camera = _scene.getCamera();
        Intersectable geometries = _scene.getGeometries();


        int nX = _imageWriter.getNx();
        int nY = _imageWriter.getNy();

        double width = _imageWriter.getWidth();
        double height = _imageWriter.getHeight();
        double distance = _scene.getDistance();

        Ray ray;
        for (int i = 0; i < nY; ++i)
            for (int j = 0; j < nX; ++j) {
                //creating a new ray for every pixel
                List<Ray> rays = camera.constructRaysThroughPixel(nX, nY, j, i, distance, width, height);
                //GeoPoint closestPoint = getClosestPoint(intersectionPoints);
                _imageWriter.writePixel(j, i, calcColor(rays).getColor());
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