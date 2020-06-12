package geometries;

import primitives.Ray;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * class Geometries all geometries in the collect
 */
public class Geometries implements Intersectable {
    // hold the collect of the geometries
    private List<Intersectable> listOfGeometries= new LinkedList<>();

    // ****************************** Constructors *****************************/

    /**
     * Geometries constructor
     *
     * @param geometries geometries
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    // ****************************** Functions *****************************//

    /**
     * add geometry to list
     *
     * @param geometries
     */
    public void add(Intersectable... geometries) {
        this.listOfGeometries.addAll(Arrays.asList(geometries));
    }

    // ****************************** Overrides *****************************//

    @Override
    public List<GeoPoint> findIntersections(Ray ray, double maxDistance) {
        List<GeoPoint> intersections = null;

        for (Intersectable geo : listOfGeometries) {
            List<GeoPoint> tempIntersections = geo.findIntersections(ray, maxDistance);
            if (tempIntersections != null) {
                if (intersections == null)
                    intersections = new LinkedList<>();
                intersections.addAll(tempIntersections);
            }
        }
        return intersections;
    }
}
