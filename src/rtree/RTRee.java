package rtree;

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.SpatialIndex;
import com.infomatiq.jsi.rtree.RTree;
import gnu.trove.TIntProcedure;
import java.util.ArrayList;

public class RTRee
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
    private static class SpatialIndexPlus
    {

        ////////////////////////////////////////////////////////////////////////////////
        public SpatialIndex spatialIndex;
        public ArrayList<Rectangle> rectangles;

        ////////////////////////////////////////////////////////////////////////////////
        public SpatialIndexPlus()
        {
            spatialIndex = new RTree();
            spatialIndex.init(null);
            rectangles = new ArrayList<>();
        }

        ////////////////////////////////////////////////////////////////////////////////
        public RectangleIndexed addPoint(Point point)
        {
            return addRectangle(point.x, point.y, point.x, point.y);
        }

        ////////////////////////////////////////////////////////////////////////////////
        public RectangleIndexed addPoint(float x, float y)
        {
            return addPoint(new Point(x, y));
        }

        ////////////////////////////////////////////////////////////////////////////////
        public boolean removePoint(Point point)
        {
            ArrayList<RectangleIndexed> founds = finNearestRectangles(point, 1);
            if(founds.size() == 1)
            {
                spatialIndex.delete(founds.get(0), founds.get(0).id);
                rectangles.remove(founds.get(0).id);
                return true;
            }
            else
            {
                return false;
            }
        }

        ////////////////////////////////////////////////////////////////////////////////
        public boolean removePoint(float x, float y)
        {
            return removePoint(new Point(x, y));
        }

        ////////////////////////////////////////////////////////////////////////////////
        public RectangleIndexed addRectangle(Rectangle rectangle)
        {
            int index = rectangles.size();
            RectangleIndexed rectangleIndexed = new RectangleIndexed(rectangle, index);
            rectangles.add(rectangles.size(), rectangleIndexed);
            spatialIndex.add(rectangles.get(index), index);
            return rectangleIndexed;
        }

        ////////////////////////////////////////////////////////////////////////////////
        public RectangleIndexed addRectangle(float x1, float y1, float x2, float y2)
        {
            return addRectangle(new Rectangle(x1, y1, x2, y2));
        }

        ////////////////////////////////////////////////////////////////////////////////
        public ArrayList<RectangleIndexed> finNearestRectangles(Point pointToFind, int nbPoints)
        {
            ArrayList<RectangleIndexed> foundRectangles = new ArrayList<>();
            spatialIndex.nearestN(pointToFind, new TIntProcedure()
            {
                public boolean execute(int i)
                {
                    foundRectangles.add(new RectangleIndexed(rectangles.get(i), i));
                    return true;
                }
            }, nbPoints, Float.MAX_VALUE);
            return foundRectangles;
        }

        ////////////////////////////////////////////////////////////////////////////////
        public class RectangleIndexed extends Rectangle
        {

            int id;

            public RectangleIndexed(float x1, float y1, float x2, float y2, int id)
            {
                super(x1, y1, x2, y2);
                this.id = id;
            }

            public RectangleIndexed(Rectangle rectangle, int id)
            {
                this.minX = rectangle.minX;
                this.maxX = rectangle.maxX;
                this.minY = rectangle.minY;
                this.maxY = rectangle.maxY;
                this.id = id;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    private static void disableLog4J()
    {
        /*Logger root = (Logger) LoggerFactory.getLogger(Logger.GLOBAL_LOGGER_NAME);
         root.setLevel(Level.OFF);*/
    }

    ////////////////////////////////////////////////////////////////////////////////
    /*@Deprecated
     private static void defaultTest()
     {
     int rowCount = 1000;
     int columnCount = 1000;
     int count = rowCount * columnCount;
     long start, end;

     System.out.println("Creating " + count + " rectangles");
     final Rectangle[] rects = new Rectangle[count];
     int id = 0;
     for(int row = 0; row < rowCount; row++)
     {
     for(int column = 0; column < rowCount; column++)
     {
     rects[id++] = new Rectangle(row, column, row + 0.5f, column + 0.5f);
     }
     }

     System.out.println("Indexing " + count + " rectangles");
     start = System.currentTimeMillis();
     SpatialIndex si = new RTree();
     si.init(null);
     for(id = 0; id < count; id++)
     {
     si.add(rects[id], id);
     }
     end = System.currentTimeMillis();
     System.out.println("Average time to index rectangle = " + ((end - start) / (count / 1000.0)) + " us");

     final Point p = new Point(36.3f, 84.3f);
     System.out.println("Querying for the nearest 3 rectangles to " + p);
     si.nearestN(p, new TIntProcedure()
     {
     public boolean execute(int i)
     {
     System.out.println("Rectangle " + i + " " + rects[i] + ", distance=" + rects[i].distance(p));
     return true;
     }
     }, 3, Float.MAX_VALUE);

     // Run a performance test, find the 3 nearest rectangles
     final int[] ret = new int[1];
     System.out.println("Running 10000 queries for the nearest 3 rectangles");
     start = System.currentTimeMillis();
     for(int row = 0; row < 100; row++)
     {
     for(int column = 0; column < 100; column++)
     {
     p.x = row + 0.6f;
     p.y = column + 0.7f;
     si.nearestN(p, new TIntProcedure()
     {
     public boolean execute(int i)
     {
     ret[0]++;
     return true; // don't do anything with the results, for a performance test.
     }
     }, 3, Float.MAX_VALUE);
     }
     }
     end = System.currentTimeMillis();
     System.out.println("Average time to find nearest 3 rectangles = " + ((end - start) / (10000 / 1000.0)) + " us");
     System.out.println("total time = " + (end - start) + "ms");
     System.out.println("total returned = " + ret[0]);

     // Run a performance test, find the 3 nearest rectangles
     System.out.println("Running 30000 queries for the nearest 3 rectangles");

     TIntProcedure proc = new NullProc();
     start = System.currentTimeMillis();
     for(int row = 0; row < 300; row++)
     {
     for(int column = 0; column < 100; column++)
     {
     p.x = row + 0.6f;
     p.y = column + 0.7f;
     si.nearestN(p, proc, 3, Float.MAX_VALUE);
     }
     }
     end = System.currentTimeMillis();
     System.out.println("Average time to find nearest 3 rectangles = " + ((end - start) / (30000 / 1000.0)) + " us");
     System.out.println("total time = " + (end - start) + "ms");
     System.out.println("total returned = " + ret[0]);
     }

     ////////////////////////////////////////////////////////////////////////////////
     private static class NullProc implements TIntProcedure
     {

     public boolean execute(int i)
     {
     return true;
     }
     }
     */
}
