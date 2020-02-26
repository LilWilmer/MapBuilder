package edu.curtin.mapbuilder.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.ref.WeakReference;
import java.util.HashSet;

import edu.curtin.mapbuilder.database.PlayerCursor;
import edu.curtin.mapbuilder.database.PlayerSchema;

/**
 * Represents a player within the game.
 *
 * Records all the game statistics for the player along with their current mode and currently help
 * structure.
 * @author Will
 */
public class Player
{
    //CONST:
    public enum Mode{
        BUILDING,
        INSPECTING,
        DELETING
    };
    //FIELDS:---------------------------------------------------------------------------------------
    //Time should really be stored in GameData class but I don't want to create a database table
    //for GameData
    private long time; public void incTime(){time++;}
    public void setTime(long time){this.time = time;}
    public long getTime(){return time;}

    private int PID = 0;
    private int money;
    private boolean isBuilding;
    private Structure buildingSelection;
    private int population;
    private int lastIncome;
    private float employmentRate;
    private int nResidential;
    private int nCommercial;
    private Mode mode;

    //DATABASE:
    private SQLiteDatabase db; public void setDatabase(SQLiteDatabase db){this.db = db;}

    //OBSERVER
    private HashSet<WeakReference<Observer>> observers;

    //CLASS FIELDS:
    private static Player instance;

    //CONSTRUCTOR:
    private Player()
    {
        this.time = 0;
        this.money = 0;
        this.isBuilding = false;
        this.buildingSelection = null;
        this.population = 0;
        this.lastIncome = 0;
        this.employmentRate = 0f;
        this.nResidential = 0;
        this.nCommercial = 0;
        this.observers = new HashSet<>();
        this.db = null;
    }

    private Player(int money)
    {
        this.mode = Mode.BUILDING;
        this.money = money;
        this.isBuilding = false;
        this.buildingSelection = null;
    }
    //SINGLETON
    public static Player get()
    {
        if(instance == null)
        {
            instance = new Player();
        }
        return instance;
    }

    /**
     * Attempt to initialize player data with a database query.
     */
    //DATABASE METHODS:-----------------------------------------------------------------------------
    public void load()
    {
        if(this.db == null || !loadPlayer())
        {
            setMoney(GameSettings.get().getInitialMoney());
        }
    }

    /**
     * Queries the database for the player data
     * @return Returns true if loading from the database was successful
     */
    public boolean loadPlayer()
    {
        /*auto close the cursor after the query*/
        try(PlayerCursor cursor =  new PlayerCursor(db.query(PlayerSchema.Table.NAME,
                null,
                null,
                null,
                null,
                null,
                null)))
        {
            if (cursor.getCount() > 0) //note the counter should never be greater than 1
            {
                /*cursor initializes the player object*/
                cursor.loadPlayer(this);
                return true;
            }
        }
        return false;
    }

    public void save()
    {
        ContentValues cv = new ContentValues();
        cv.put(PlayerSchema.Table.Cols.PID, this.PID);
        cv.put(PlayerSchema.Table.Cols.TIME, this.time);
        cv.put(PlayerSchema.Table.Cols.MONEY, this.money);
        cv.put(PlayerSchema.Table.Cols.IS_BUILDING, this.isBuilding);
        cv.put(PlayerSchema.Table.Cols.LAST_INCOME, this.lastIncome);
        cv.put(PlayerSchema.Table.Cols.EMPLOYMENT_RATE, this.employmentRate);
        cv.put(PlayerSchema.Table.Cols.N_RESIDENTIAL, this.nResidential);
        cv.put(PlayerSchema.Table.Cols.N_COMMERCIAL, this.nCommercial);

        if(buildingSelection != null)
        {
            cv.put(PlayerSchema.Table.Cols.STRUCTURE_ID,
                    this.buildingSelection.getDrawableId());

            cv.put(PlayerSchema.Table.Cols.STRUCTURE_LABEL,
                    this.buildingSelection.getLabel());
        }
        else
        {
            cv.putNull(PlayerSchema.Table.Cols.STRUCTURE_ID);
            cv.putNull(PlayerSchema.Table.Cols.STRUCTURE_LABEL);
        }
        db.replace(PlayerSchema.Table.NAME, null, cv);
    }

    //GETTERS AND SETTERS---------------------------------------------------------------------------

    public Mode getMode()
    {
        return mode;
    }

    public void setMode(Mode mode)
    {
        this.mode = mode;
        notifyObservers();
    }

    public int getMoney()
    {
        return money;
    }

    public void setMoney(int money)
    {
        this.money = money;
        save();
        notifyObservers();
    }

    public boolean isBuilding()
    {
        return isBuilding;
    }

    public void setBuilding(boolean building)
    {
        isBuilding = building;
        notifyObservers();
    }

    public Structure getBuildingSelection()
    {
        return buildingSelection;
    }

    public void setBuildingSelection(Structure buildingSelection)
    {
        this.buildingSelection = buildingSelection;
        notifyObservers();
    }

    public void removeBuilding(Structure.Type type)
    {
        switch(type)
        {
            case COMMERCIAL: this.nCommercial--; break;
            case RESIDENTIAL: this.nResidential--; break;
        }
    }

    public void setPID(int PID){this.PID = PID;}

    public int getPID(){return PID;}

    public int getPopulation()
    {
        return population;
    }

    public void setPopulation(int population)
    {
        this.population = population;
    }

    public int getLastIncome()
    {
        return lastIncome;
    }

    public void setLastIncome(int lastIncome)
    {
        this.lastIncome = lastIncome;
    }

    public float getEmploymentRate()
    {
        return employmentRate;
    }

    public void setEmploymentRate(float employmentRate)
    {
        this.employmentRate = employmentRate;
    }

    public int getnResidential()
    {
        return nResidential;
    }

    public void setnResidential(int nResidential)
    {
        this.nResidential = nResidential;
    }

    public int getnCommercial()
    {
        return nCommercial;
    }

    public void setnCommercial(int nCommercial)
    {
        this.nCommercial = nCommercial;
    }

    //OBSERVER METHODS------------------------------------------------------------------------------
    public interface Observer
    {
        void playerUpdated();
    }
    public void addObserver(Observer ob)
    {
        this.observers.add(new WeakReference<>(ob));
    }

    public void notifyObservers()
    {
        //remove the lingering listeners before notifying
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            observers.removeIf(obs->obs.get()==null);
        }
        for( WeakReference<Observer> ob : observers)
        {
            //still need to see if a listener was garbage collected before notifying it
            if(ob.get()!= null)
                ob.get().playerUpdated();
        }
    }

    public void removeObserver(Observer ob)
    {
        observers.removeIf(element->element.get()==ob);
    }
}
