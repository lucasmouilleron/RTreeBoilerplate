package RTreeBoilerplate;

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
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
        int nbPoints = 300000;
        float fromLat = -2.261862278F;
        float toLat = 7.6356048584F;
        float fromLng = 43.2105023957F;
        float toLng = 50.6289054699F;

        float findLat = 0.5945830345F;
        float findLng = 45.579743061F;
        int nbFindPoints = 10;

        long start, end;
        SpatialIndexPlus si = new SpatialIndexPlus();

        System.out.println("Indexing " + nbPoints + " points");
        start = System.currentTimeMillis();
        for(int i = 0; i < nbPoints; i++)
        {
            float lat = fromLat + (float) (Math.random() * ((toLat - fromLat) + 1));
            float lng = fromLng + (float) (Math.random() * ((toLng - fromLng) + 1));
            si.addPoint(lat, lng);
        }
        end = System.currentTimeMillis();
        System.out.println("Average time to add " + nbPoints + " points : " + (end - start) / (nbPoints / 1000.0) + " us");

        start = System.currentTimeMillis();
        Point pointToFind = new Point(findLat, findLng);
        ArrayList<SpatialIndexPlus.RectangleIndexed> pointsFound = si.finNearestRectangles(pointToFind, nbFindPoints);
        for(Rectangle pointFound : pointsFound)
        {
            System.out.println("Found " + pointFound.toString() + " at distance " + pointFound.distance(pointToFind));
        }
        end = System.currentTimeMillis();
        System.out.println("Found in  " + (end - start) / 1000.0 + " us");

        float lat = fromLat + (float) (Math.random() * ((toLat - fromLat) + 1));
        float lng = fromLng + (float) (Math.random() * ((toLng - fromLng) + 1));
        SpatialIndexPlus.RectangleIndexed added = si.addPoint(lat, lng);
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
