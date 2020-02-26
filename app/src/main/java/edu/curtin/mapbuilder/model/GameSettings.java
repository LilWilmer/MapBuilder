package edu.curtin.mapbuilder.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Set;

import edu.curtin.mapbuilder.GameManager;
import edu.curtin.mapbuilder.database.GameSettingsSchema.Table;
import edu.curtin.mapbuilder.database.PlayerCursor;
import edu.curtin.mapbuilder.database.SettingsCursor;

/**
 * Model class for storing all the game settings.
 * @author Will
 */
public class GameSettings
{
    //FIELDS----------------------------------------------------------------------------------------
    //SETTINGS:
    private int PID = 0;
    private int mapWidth = 30;
    private int mapHeight = 20;
    private int initialMoney = 1000;
    private int familySize = 4;
    private int shopSize = 6;
    private int salary = 10;
    private int serviceCost = 2;
    private int houseBuildingCost = 100;
    private int commBuildingCost = 500;
    private int roadBuildingCost = 20;
    private float taxRate = 0.3f;

    //DATABASE:
    SQLiteDatabase db; public void setDatabase(SQLiteDatabase db){this.db = db;}

    //SINGLETON:
    private static GameSettings instance;

    /**
     * private singleton constructor
     */
    private GameSettings()
    {
        //ADD DATABASE INIT HERE
    }

    /**
     * private singleton constructor
     * @param mapWidth
     * @param mapHeight
     * @param initialMoney
     */
    private GameSettings(int mapWidth, int mapHeight, int initialMoney)
    {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.initialMoney = initialMoney;
    }

    /**
     * Singleton accessor
     * @return
     */
    public static GameSettings get()
    {
        if(instance == null)
        {
            instance = new GameSettings();
        }
        return instance;
    }

    //DATABASE METHODS------------------------------------------------------------------------------

    /**
     * attempts to initialize the settings object using the database reference
     */
    public void load()
    {
        if(db == null || !loadSettings())
        {
            //TODO: possibly throw exception or initialize with default values
        }
    }

    /**
     * Makes a query to the database and stores the data into its class fields if successful.
     * @return Returns true if loading the settings was successful
     */
    public boolean loadSettings()
    {
        /*automatically close cursor after use*/
        try(SettingsCursor cursor = new SettingsCursor(db.query(Table.NAME,
                null,
                null,
                null,
                null,
                null,
                null)))
        {
            /*if a single entry is found use it to initialize*/
            //note there should on be single settings entry in the database
            if(cursor.getCount() > 0)
            {
                cursor.loadSettings(this);
                return true;
            }
        }

        return false;
    }

    /**
     * Stores all the current settings into the database
     */
    private void save()
    {
        //preparing the content values
        ContentValues cv = new ContentValues();
        cv.put(Table.Cols.PLAYER, this.PID);
        cv.put(Table.Cols.WIDTH, this.mapWidth);
        cv.put(Table.Cols.HEIGHT, this.mapHeight);
        cv.put(Table.Cols.INITIAL_MONEY, this.initialMoney);
        cv.put(Table.Cols.FAMILY_SIZE, this.familySize);
        cv.put(Table.Cols.SHOP_SIZE, this.shopSize);
        cv.put(Table.Cols.SALARY, this.salary);
        cv.put(Table.Cols.SERVICE_COST, this.serviceCost);
        cv.put(Table.Cols.HOUSE_BUILDING_COST, this.houseBuildingCost);
        cv.put(Table.Cols.COMM_BUILDING_COST, this.commBuildingCost);
        cv.put(Table.Cols.ROAD_BUILDING_COST, this.roadBuildingCost);
        cv.put(Table.Cols.TAX_RATE, this.taxRate);

        //IN CASE THE FIELD ALREADY EXISTS, THIS AVOIDS A PRIMARY KEY EXCEPTION
        db.replace(Table.NAME, null, cv);
    }


    //GETTERS AND SETTERS---------------------------------------------------------------------------
    public int getPID()
    {
        return PID;
    }

    public void setPID(int PID)
    {
        this.PID = PID;
        save();
    }

    public int getMapWidth()
    {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth)
    {
        this.mapWidth = mapWidth;
        save();
    }

    public int getMapHeight()
    {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight)
    {
        this.mapHeight = mapHeight;
        save();
    }

    public int getInitialMoney()
    {
        return initialMoney;
    }

    public void setInitialMoney(int initialMoney)
    {
        this.initialMoney = initialMoney;
        save();
    }

    public int getFamilySize()
    {
        return familySize;
    }

    public void setFamilySize(int familySize)
    {
        this.familySize = familySize;
        save();
    }

    public int getShopSize()
    {
        return shopSize;
    }

    public void setShopSize(int shopSize)
    {
        this.shopSize = shopSize;
        save();
    }

    public int getSalary()
    {
        return salary;
    }

    public void setSalary(int salary)
    {
        this.salary = salary;
        save();
    }

    public float getTaxRate()
    {
        return taxRate;
    }

    public void setTaxRate(float taxRate)
    {
        this.taxRate = taxRate;
        save();
    }

    public int getServiceCost()
    {
        return serviceCost;
    }

    public void setServiceCost(int serviceCost)
    {
        this.serviceCost = serviceCost;
        save();
    }

    public int getHouseBuildingCost()
    {
        return houseBuildingCost;
    }

    public void setHouseBuildingCost(int houseBuildingCost)
    {
        this.houseBuildingCost = houseBuildingCost;
        save();
    }

    public int getCommBuildingCost()
    {
        return commBuildingCost;
    }

    public void setCommBuildingCost(int commBuildingCost)
    {
        this.commBuildingCost = commBuildingCost;
        save();
    }

    public int getRoadBuildingCost()
    {
        return roadBuildingCost;
    }

    public void setRoadBuildingCost(int roadBuildingCost)
    {
        this.roadBuildingCost = roadBuildingCost;
        save();
    }
}
