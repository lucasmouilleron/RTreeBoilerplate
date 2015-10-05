package RTreeBoilerplate;

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.SpatialIndex;
import com.infomatiq.jsi.rtree.RTree;
import gnu.trove.TIntProcedure;
import java.util.ArrayList;

////////////////////////////////////////////////////////////////////////////////
public class SpatialIndexPlus
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
