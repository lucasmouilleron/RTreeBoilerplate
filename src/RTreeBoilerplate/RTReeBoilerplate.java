package RTreeBoilerplate;

import com.infomatiq.jsi.Point;
import java.util.ArrayList;

public class RTReeBoilerplate
{

    ////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args)
    {
        disableLog4J();
        test();
    }

    ////////////////////////////////////////////////////////////////////////////////
    private static void test()
    {
        int nbPointsToAdd = 300000;
        int nbPointsToFind = 10000;
        int nbFindPoints = 3;
        float fromLat = -2.261862278F;
        float toLat = 7.6356048584F;
        float fromLng = 43.2105023957F;
        float toLng = 50.6289054699F;

        long start, end;
        SpatialIndexPlus si = new SpatialIndexPlus();

        System.out.println("Indexing " + nbPointsToAdd + " points");
        start = System.currentTimeMillis();
        for(int i = 0; i < nbPointsToAdd; i++)
        {
            float lat = fromLat + (float) (Math.random() * ((toLat - fromLat) + 1));
            float lng = fromLng + (float) (Math.random() * ((toLng - fromLng) + 1));
            Double data = Math.random();
            si.addGeoData(lat, lng, data);
        }
        end = System.currentTimeMillis();
        System.out.println("Average time to add " + nbPointsToAdd + " points : " + (end - start) / (nbPointsToAdd / 1000.0) + " us");

        System.out.println("Finding " + nbPointsToFind + " points");
        start = System.currentTimeMillis();
        for(int i = 0; i < nbPointsToFind; i++)
        {
            float lat = fromLat + (float) (Math.random() * ((toLat - fromLat) + 1));
            float lng = fromLng + (float) (Math.random() * ((toLng - fromLng) + 1));
            Point pointToFind = new Point(lat, lng);
            ArrayList<SpatialIndexPlus.GeoData> pointsFound = si.finNearestGeoDatas(pointToFind, nbFindPoints);
            /*for(Rectangle pointFound : pointsFound)
            {
                System.out.println("Found " + pointFound.toString() + " at distance " + pointFound.distance(pointToFind));
            }*/
        }
        end = System.currentTimeMillis();
        System.out.println("Average time to find "+nbFindPoints+" neighbours of point : " + (end - start) / (nbPointsToFind / 1000.0) + " us");

        System.out.println("Adding and removing points");
        float lat = fromLat + (float) (Math.random() * ((toLat - fromLat) + 1));
        float lng = fromLng + (float) (Math.random() * ((toLng - fromLng) + 1));
        Double data = Math.random();
        SpatialIndexPlus.GeoData added = si.addGeoData(lat, lng, data);
        System.out.println(added);
        boolean removed = si.removePoint(lat, lng);
        System.out.println(removed);
    }

    ////////////////////////////////////////////////////////////////////////////////
    private static void disableLog4J()
    {
        /*Logger root = (Logger) LoggerFactory.getLogger(Logger.GLOBAL_LOGGER_NAME);
         root.setLevel(Level.OFF);*/
    }
}
