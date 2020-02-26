package edu.curtin.mapbuilder.model;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashSet;
import java.util.Random;

import edu.curtin.mapbuilder.R;
import edu.curtin.mapbuilder.database.MapCursor;
import edu.curtin.mapbuilder.database.MapSchema;

/**
 * Represents the overall map, and contains a grid of MapElement objects (accessible using the
 * get(row, col) method). The two static constants width and height indicate the size of the map.
 *
 * There is a static get() method to be used to obtain an instance (rather than calling the
 * constructor directly).
 *
 * There is also a regenerate() method. The map is randomly-generated, and this method will invoke
 * the algorithm again to replace all the map data with a new randomly-generated grid.
 */
public class MapData implements MapElement.Observer
{
    //MapData class FIELDS:-------------------------------------------------------------------------
    private static final int WATER = R.drawable.ic_water;
    private static final int[] GRASS = {R.drawable.ic_grass1, R.drawable.ic_grass2,
            R.drawable.ic_grass3, R.drawable.ic_grass4};
    private static final Random rng = new Random();

    private static MapData instance = null;

    //MapData instance FIELDS:
    private int width;
    private int height;
    private MapElement[][] grid;
    private SQLiteDatabase db;

    private MapElement.Observer ob;

    //CONSTRUCTOR:----------------------------------------------------------------------------------

    private MapData(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.grid = null;
    }

    //SINGLETON GETTER:
    public static MapData get()
    {
        if(instance == null)
        {
            instance = new MapData(GameSettings.get().getMapWidth(),
                                   GameSettings.get().getMapHeight());
        }
        return instance;
    }

    public static MapData newInstance()
    {
        instance = new MapData(GameSettings.get().getMapWidth()
                                ,GameSettings.get().getMapHeight());
        return instance;
    }
    //DATABASE METHODS------------------------------------------------------------------------------
    /**
     * Attempt to load in data from the database. If it fails a new map is generated
     */
    public void load()
    {
        if(this.db == null || !loadGrid())
        {
            //if loading fails, generate a new grid (deletes old database in the process)
            regenerate();
        }
    }

    /**
     * Queries the database for all the map elements within the Map table. If the number of elements
     * extracted match the dimensions of the current MapData and each element is constructed
     * successfully, the grid is initialize with loaded elements.
     * @return Returns true if load was successful and false otherwise
     */
    //DATABASE INIT
    public boolean loadGrid()
    {
        boolean loaded = false;
        int count = 0;
        MapElement[][] grid = new MapElement[height][width];

        try(MapCursor mc = new MapCursor(db.query(MapSchema.Table.NAME,
                null,
                null,
                null,
                null,
                null,
                null)))
        {
            //LOAD MAP IF CURRENT MAP SIZE IS EQUAL TO THE SAVED MAP
            if(mc.getCount() == height * width)
            {
                MapElement me = null;
                for (int ii = 0; ii < height; ii++)
                {
                    for (int jj = 0; jj < width; jj++)
                    {
                        count++;
                        me = mc.next();
                        if (me == null)
                        {
                            //NOT ENOUGH DATA THE CREATE THE MAP
                            //TODO: SET UP SOME CUSTOM EXCEPTIONS FOR THIS EVENT (to replace the flag)
                            return loaded;
                        }
                        try
                        {
                            grid[me.getX()][me.getY()] = me;
                        }
                        catch(ArrayIndexOutOfBoundsException e)
                        {
                            return loaded;
                        }
                        me.addObserver(this);

                    }
                }
                this.grid = grid;
                loaded = true;
            }
            else
            {
                System.out.println("Size of grid "+height*width);
                System.out.println(mc.getCount()+" num elements out");
            }
        }

        return loaded;
    }

    public void setDatabase(SQLiteDatabase db){this.db = db;}
    public SQLiteDatabase getDatabase(){return db;}

    //GETTERS AND SETTERS---------------------------------------------------------------------------

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public MapElement[][] getGrid(){return grid;}

    //OTHER JASH------------------------------------------------------------------------------------
    private void generateGrid()
    {
        final int HEIGHT_RANGE = 256;
        final int WATER_LEVEL = 112;
        final int INLAND_BIAS = 24;
        final int AREA_SIZE = 1;
        final int SMOOTHING_ITERATIONS = 2;

        int[][] heightField = new int[height][width];
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                heightField[i][j] =
                    rng.nextInt(HEIGHT_RANGE)
                    + INLAND_BIAS * (
                        Math.min(Math.min(i, j), Math.min(height - i - 1, width - j - 1)) -
                        Math.min(height, width) / 4);
            }
        }

        int[][] newHf = new int[height][width];
        for(int s = 0; s < SMOOTHING_ITERATIONS; s++)
        {
            for(int i = 0; i < height; i++)
            {
                for(int j = 0; j < width; j++)
                {
                    int areaSize = 0;
                    int heightSum = 0;

                    for(int areaI = Math.max(0, i - AREA_SIZE);
                            areaI < Math.min(height, i + AREA_SIZE + 1);
                            areaI++)
                    {
                        for(int areaJ = Math.max(0, j - AREA_SIZE);
                                areaJ < Math.min(width, j + AREA_SIZE + 1);
                                areaJ++)
                        {
                            areaSize++;
                            heightSum += heightField[areaI][areaJ];
                        }
                    }

                    newHf[i][j] = heightSum / areaSize;
                }
            }

            int[][] tmpHf = heightField;
            heightField = newHf;
            newHf = tmpHf;
        }

        MapElement[][] grid = new MapElement[height][width];
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                MapElement element;

                if(heightField[i][j] >= WATER_LEVEL)
                {
                    boolean waterN = (i == 0)          || (heightField[i - 1][j] < WATER_LEVEL);
                    boolean waterE = (j == width - 1)  || (heightField[i][j + 1] < WATER_LEVEL);
                    boolean waterS = (i == height - 1) || (heightField[i + 1][j] < WATER_LEVEL);
                    boolean waterW = (j == 0)          || (heightField[i][j - 1] < WATER_LEVEL);

                    boolean waterNW = (i == 0) ||          (j == 0) ||         (heightField[i - 1][j - 1] < WATER_LEVEL);
                    boolean waterNE = (i == 0) ||          (j == width - 1) || (heightField[i - 1][j + 1] < WATER_LEVEL);
                    boolean waterSW = (i == height - 1) || (j == 0) ||         (heightField[i + 1][j - 1] < WATER_LEVEL);
                    boolean waterSE = (i == height - 1) || (j == width - 1) || (heightField[i + 1][j + 1] < WATER_LEVEL);

                    boolean coast = waterN || waterE || waterS || waterW ||
                                    waterNW || waterNE || waterSW || waterSE;

                    grid[i][j] = new MapElement(
                            i,j,
                        i+"_"+j,
                        !coast,
                        choose(waterN, waterW, waterNW,
                            R.drawable.ic_coast_north, R.drawable.ic_coast_west,
                            R.drawable.ic_coast_northwest, R.drawable.ic_coast_northwest_concave),
                        choose(waterN, waterE, waterNE,
                            R.drawable.ic_coast_north, R.drawable.ic_coast_east,
                            R.drawable.ic_coast_northeast, R.drawable.ic_coast_northeast_concave),
                        choose(waterS, waterW, waterSW,
                            R.drawable.ic_coast_south, R.drawable.ic_coast_west,
                            R.drawable.ic_coast_southwest, R.drawable.ic_coast_southwest_concave),
                        choose(waterS, waterE, waterSE,
                            R.drawable.ic_coast_south, R.drawable.ic_coast_east,
                            R.drawable.ic_coast_southeast, R.drawable.ic_coast_southeast_concave),
                        null,
                            new int[]{i,j});
                    grid[i][j].saveElement(db);

                }
                else
                {
                    grid[i][j] = new MapElement(
                            i,j,i+"_"+j,false, WATER, WATER, WATER, WATER, null
                            , new int[]{i,j});
                    grid[i][j].saveElement(db);

                }
                grid[i][j].addObserver(this);
            }
        }
        this.grid = grid;
    }

    private static int choose(boolean nsWater, boolean ewWater, boolean diagWater,
        int nsCoastId, int ewCoastId, int convexCoastId, int concaveCoastId)
    {
        int id;
        if(nsWater)
        {
            if(ewWater)
            {
                id = convexCoastId;
            }
            else
            {
                id = nsCoastId;
            }
        }
        else
        {
            if(ewWater)
            {
                id = ewCoastId;
            }
            else if(diagWater)
            {
                id = concaveCoastId;
            }
            else
            {
                id = GRASS[rng.nextInt(GRASS.length)];
            }
        }
        return id;
    }


    /**
     * This method is called to recreate the map which includes dropping the old one
     * from the data base.
     * This method is generally invoked after the settings change or when gameOver occurs.
     */
    public void regenerate()
    {
        if(db != null)
        {
            db.delete(MapSchema.Table.NAME, null, null);
        }
        instance.setWidth(GameSettings.get().getMapWidth());
        instance.setHeight(GameSettings.get().getMapHeight());
        instance.generateGrid();
    }

    public MapElement get(int i, int j)
    {
        return grid[i][j];
    }

    /**
     * Checks for roads near the specified grid coordinate
     * Im making the assumption that all 8 tiles around the centre tile are adjacent
     * @param coord the position to check for adjacent roads
     * @return Returns true if there is a block within a 1 block radius
     */
    public boolean adjacentRoad(int[] coord)
    {
        for(int ii = Math.max(0,coord[0]-1); ii < Math.min(grid.length,coord[0]+2); ii++)
        {
            for(int jj = Math.max(0,coord[1]-1); jj < Math.min(grid[0].length,coord[1]+2); jj++)
            {
                if(grid[ii][jj].getStructure()!= null
                && grid[ii][jj].getStructure().getType() == Structure.Type.ROAD)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void update(MapElement me)
    {
        System.out.println("HELLO - Will");
        this.ob.update(me);
    }

    public void addObserver(MapElement.Observer ob)
    {
        this.ob = ob;
    }
}
