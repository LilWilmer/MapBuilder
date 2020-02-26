package edu.curtin.mapbuilder.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import edu.curtin.mapbuilder.database.MapSchema.Table;

import edu.curtin.mapbuilder.database.MapSchema;

/**
 * Represents a single grid square in the map. Each map element has both terrain and an optional
 * structure.
 *
 * The terrain comes in four pieces, as if each grid square was further divided into its own tiny
 * 2x2 grid (north-west, north-east, south-west and south-east). Each piece of the terrain is
 * represented as an int, which is actually a drawable reference. That is, if you have both an
 * ImageView and a MapElement, you can do this:
 *
 * ImageView iv = ...;
 * MapElement me = ...;
 * iv.setImageResource(me.getNorthWest());
 *
 * This will cause the ImageView to display the grid square's north-western terrain image,
 * whatever it is.
 *
 * (The terrain is broken up like this because there are a lot of possible combinations of terrain
 * images for each grid square. If we had a single terrain image for each square, we'd need to
 * manually combine all the possible combinations of images, and we'd get a small explosion of
 * image files.)
 *
 * Meanwhile, the structure is something we want to display over the top of the terrain. Each
 * MapElement has either zero or one Structure} objects. For each grid square, we can also change
 * which structure is built on it.
 */
public class MapElement
{
    //FIELDS----------------------------------------------------------------------------------------
    private Bitmap thumbnail;
    private final int x;
    private final int y;
    private final String id;
    private final boolean buildable;
    private String label;
    private final int[] coord;
    private final int terrainNorthWest;
    private final int terrainSouthWest;
    private final int terrainNorthEast;
    private final int terrainSouthEast;
    private Structure structure;

    /*Added observer field*/
    private LinkedList<WeakReference<Observer>> observers;
    private LinkedList<Observer> obs;

    /**
     * @param id uniquely defines the element
     * @param buildable whether or not structures will be accepted
     * @param northWest Northwest image tile
     * @param northEast Northeast image tile
     * @param southWest Southwest image tile
     * @param southEast Southeast image tile
     * @param structure The structure image
     * @param coord Its position in the game map as a grid coordinate
     */
    public MapElement(int x, int y, String id, boolean buildable, int northWest, int northEast,
                      int southWest, int southEast, Structure structure, int[] coord)
    {
        this.x = x;
        this.y = y;
        this.id = id;
        this.buildable = buildable;
        this.terrainNorthWest = northWest;
        this.terrainNorthEast = northEast;
        this.terrainSouthWest = southWest;
        this.terrainSouthEast = southEast;
        this.structure = structure;
        if(structure != null) this.label = structure.getLabel();
        else                  this.label = "-";
        this.coord = coord;
        this.thumbnail = null;

        this.observers = new LinkedList<>();
        this.obs = new LinkedList<>();
    }

    public int[] getCoord()
    {
        return coord;
    }

    public boolean isBuildable()
    {
        return buildable&&(!isOccupied());
    }

    public boolean isOccupied(){return structure!=null;}

    public int getNorthWest()
    {
        return terrainNorthWest;
    }

    public int getSouthWest()
    {
        return terrainSouthWest;
    }

    public int getNorthEast()
    {
        return terrainNorthEast;
    }

    public int getSouthEast()
    {
        return terrainSouthEast;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    /**
     * Retrieves the structure built on this map element.
     * @return The structure, or null if one is not present.
     */
    public Structure getStructure()
    {
        return structure;
    }

    public void setStructure(Structure structure)
    {
        this.structure = structure;
        if(structure!= null)
        {
            if(label.equals("-"))
            {
                this.label = structure.getLabel();
            }
        }
        notifyObservers();
    }

    /**
     * Saves the MapElement data to the imported database
     * @param db SQLite database used for saving data to
     */
    public void saveElement(SQLiteDatabase db)
    {
        //Setting up content values for insertion
        ContentValues cv = new ContentValues();
        cv.put(Table.Cols.X_COORD,             this.x);
        cv.put(Table.Cols.Y_COORD,             this.y);
        cv.put(MapSchema.Table.Cols.ID,        this.id);
        cv.put(MapSchema.Table.Cols.BUILDABLE, this.buildable);
        cv.put(MapSchema.Table.Cols.NW,        this.terrainNorthWest);
        cv.put(MapSchema.Table.Cols.NE,        this.terrainNorthEast);
        cv.put(MapSchema.Table.Cols.SW,        this.terrainSouthWest);
        cv.put(MapSchema.Table.Cols.SE,        this.terrainSouthEast);
        cv.put(MapSchema.Table.Cols.STRUCTURE_LABEL, this.label);

        //Adding structure to the content values if not null
        if(structure != null)
        {
            cv.put(MapSchema.Table.Cols.STRUCTURE_ID, this.structure.getDrawableId());
            cv.put(Table.Cols.STRUCTURE_TYPE,      Structure.getType(structure.getType()));
        }
        else
        {
            cv.putNull(MapSchema.Table.Cols.STRUCTURE_ID);
            cv.putNull(Table.Cols.STRUCTURE_TYPE);
        }

        //add or replace the map element in the database (avoids the primary key exception)
        db.replace(Table.NAME,null,cv);
    }

    public String getID()
    {
        return this.id;
    }


    public String getType()
    {
        if(structure!= null)
            switch (structure.getType())
            {
                case ROAD:
                    return "Road";

                case COMMERCIAL:
                    return "Commercial";

                case RESIDENTIAL:
                    return "Residential";

                case COSMETIC:
                    return "Cosmetic";

                case UNDEFINED:
                    return null;
            }
        return null;
    }

    public String getLabel()
    {
        return this.label;
    }

    public Bitmap getThumbnail(){return this.thumbnail;}

    public void setThumbnail(Bitmap thumbnail)
    {
        this.thumbnail = thumbnail;
        notifyObservers();
    }

    public void setLabel(String label){this.label = label;}

    public void clear()
    {
        this.structure = null;
        this.label = "-";
        this.thumbnail = null;
        notifyObservers();
    }
    /*OBSERVER METHODS----------------------------------------------------------------------------*/
    public void addObserver(Observer ob)
    {
        //remove the all the lingering observers
        WeakReference<Observer> obs = new WeakReference<>(ob);
        this.observers.add(obs);
    }

    public void addObs(Observer ob)
    {
        this.obs.add(ob);
    }

    public void removeObserver(Observer ob)
    {
        //removeGC();
        for(WeakReference<Observer> wr : observers)
        {
            if(wr.get() == ob)
            {
                this.observers.remove(wr);
                break;
            }
        }
    }
    public void notifyObservers()
    {
        //removeGC();
        for(WeakReference<Observer> ob : observers)
        {
            //still need to check whether the listeners have been garbage collected
            if (ob.get() != null)
            {
                ob.get().update(this);
            }
        }
        for(Observer ob : obs)
        {
            ob.update(this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeGC()
    {
        observers.removeIf((observer)-> (observer.get()==null));
    }


    public interface Observer
    {
        void update(MapElement element);
    }
}
