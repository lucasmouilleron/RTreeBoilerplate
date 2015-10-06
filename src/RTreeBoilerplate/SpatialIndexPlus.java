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
    public ArrayList<GeoData> geoDatas;

    ////////////////////////////////////////////////////////////////////////////////
    public SpatialIndexPlus()
    {
        spatialIndex = new RTree();
        spatialIndex.init(null);
        geoDatas = new ArrayList<>();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public GeoData addGeoData(Point point, Object data)
    {
        return addRectangle(point.x, point.y, point.x, point.y, data);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public GeoData addGeoData(float x, float y, Object data)
    {
        return addGeoData(new Point(x, y), data);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public boolean removePoint(Point point)
    {
        ArrayList<GeoData> founds = finNearestGeoDatas(point, 1);
        if(founds.size() == 1)
        {
            spatialIndex.delete(founds.get(0), founds.get(0).id);
            geoDatas.remove(founds.get(0).id);
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
    protected GeoData addRectangle(Rectangle rectangle, Object data)
    {
        int index = geoDatas.size();
        GeoData geoData = new GeoData(rectangle, index, data);
        geoDatas.add(geoDatas.size(), geoData);
        spatialIndex.add(geoDatas.get(index), index);
        return geoData;
    }

    ////////////////////////////////////////////////////////////////////////////////
    protected GeoData addRectangle(float x1, float y1, float x2, float y2, Object data)
    {
        return addRectangle(new Rectangle(x1, y1, x2, y2), data);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ArrayList<GeoData> finNearestGeoDatas(Point pointToFind, int nbPoints)
    {
        ArrayList<GeoData> foundGeoDatas = new ArrayList<>();
        spatialIndex.nearestN(pointToFind, new TIntProcedure()
        {
            @Override
            public boolean execute(int i)
            {
                foundGeoDatas.add(geoDatas.get(i));
                return true;
            }
        }, nbPoints, Float.MAX_VALUE);
        return foundGeoDatas;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public class GeoData extends Rectangle
    {

        int id;
        Object data;

        public GeoData(float x1, float y1, float x2, float y2, int id, Object data)
        {
            super(x1, y1, x2, y2);
            this.id = id;
            this.data = data;
        }

        public GeoData(Rectangle rectangle, int id, Object data)
        {
            this.minX = rectangle.minX;
            this.maxX = rectangle.maxX;
            this.minY = rectangle.minY;
            this.maxY = rectangle.maxY;
            this.id = id;
            this.data = data;
        }

        @Override
        public String toString()
        {
            return minX + " / " + minY + " // " + maxX + " / " + maxY + " : " + id + " // " + data;
        }
    }
}
