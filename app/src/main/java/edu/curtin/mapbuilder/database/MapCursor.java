/*****************************************************************************
* AUTH: William Payne
* FILE: MapCursor.java
* LAST MOD: 5/10/19
* PURPOSE: Allows you to extract MapElements from Map database queries.
           Extending CursorWrapper provides methods which abstract the use of
           the Cursor object.
           This class in adition to extending the CursorWrapper also implements
           Iterator to allow foreach loop accessing.
           Note that although this implements the iterable interface, a single
           instance of this class may only iterated once due to the cursor.
*****************************************************************************/
package edu.curtin.mapbuilder.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Iterator;

import edu.curtin.mapbuilder.model.MapElement;
import edu.curtin.mapbuilder.model.Structure;
import edu.curtin.mapbuilder.database.MapSchema.Table.Cols;

public class MapCursor extends CursorWrapper implements Iterator<MapElement>, 
                                                        Iterable<MapElement>
{
    //CONSTRUCTOR-------------------------------------------------------------
    public MapCursor(Cursor cursor)
    {
        super(cursor);
        cursor.moveToFirst();
    }

    /**
     * Constructs and returns a new MapElement using the data from the cursor
     * @param
     * @return MapElement
     */
    public MapElement getMapElement()
    {
        //EXTRACT FIELDS FROM THE CURSOR AT THE REQUIRED COLUMNS

        int x = getInt(getColumnIndex(Cols.X_COORD));
        int y = getInt(getColumnIndex(Cols.Y_COORD));
        String id = getString(getColumnIndex(Cols.ID));
        boolean buildable = getInt(getColumnIndex(Cols.BUILDABLE)) > 0;
        int terrainNorthWest = getInt(getColumnIndex(Cols.NW));
        int terrainSouthWest = getInt(getColumnIndex(Cols.SW));
        int terrainNorthEast = getInt(getColumnIndex(Cols.NE));
        int terrainSouthEast = getInt(getColumnIndex(Cols.SE));

        int structure_id = getInt(getColumnIndex(Cols.STRUCTURE_ID));
        String structure_label = getString(getColumnIndex(Cols.STRUCTURE_LABEL));
        int structure_type = getInt(getColumnIndex(Cols.STRUCTURE_TYPE));

        //ATTEMPT TO CREATE STRUCTURE
        Structure structure = null;
        try
        {
            structure = new Structure(structure_id, structure_label, structure_type);
        }
        catch (IllegalArgumentException e)
        {
            structure = null;
        }

        //ATTEMPT TO CONSTRUCT THE MAPELEMENT OBJECT
        MapElement element;
        try
        {
            element = new MapElement(
                    x,
                    y,
                    id,
                    buildable,
                    terrainNorthWest,
                    terrainNorthEast,
                    terrainSouthWest,
                    terrainSouthEast,
                    structure,
                    new int[]{x,y});
        }
        catch(IllegalArgumentException e)
        {
            element = null;
        }

        return element;
    }

    @Override
    /**
     * Do i have a next???
     * @return !isAfterLast() 
     */
    public boolean hasNext()
    {
        return !isAfterLast();
    }

    /**
     * Iterator method for returning MapElement
     * @return MapElement out
     */
    @Override
    public MapElement next()
    {
        MapElement out = null;
        if(hasNext())
        {
            out = getMapElement();
            moveToNext();
        }
        else
        {
            close();
        }

        return out;
    }

    /**
     * Create and return an instance of an iterator. Note this can only
     * be iterated once
     * @return 
     */
    @Override
    public Iterator<MapElement> iterator()
    {
        return this;
    }
}
