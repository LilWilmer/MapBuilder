package edu.curtin.mapbuilder.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import edu.curtin.mapbuilder.database.GameDbHelper;

/**
 * Stores each major model object to localize the database initialization
 * <p>
 * Last Mod: 5/10/19
 * @author Will
 */
public class GameData
{
    //FIELDS----------------------------------------------------------------------------------------
    //GAME DATA:
    private GameSettings settings;
    private MapData mapData;
    private StructureData structureData;
    private Player player;

    //DATABASE:
    private SQLiteDatabase db; public SQLiteDatabase getDatabase(){return this.db;}

    //SINGLETON INSTANCE:
    public static GameData instance;

    //CONSTRUCTOR-----------------------------------------------------------------------------------

    /**
     * Constructs a GameData object with all the other major models
     * @param gs GameSettings to be loaded
     * @param md MapData to be loaded
     * @param sd StructureData to be loaded
     * @param p Player data to be loaded
     */
    private GameData(GameSettings gs, MapData md, StructureData sd, Player p)
    {
        this.settings = gs;
        this.mapData = md;
        this.structureData = sd;
        this.player = p;
        this.db = null;
    }

    /**
     * Singleton getter
     * @return instance
     */
    public static GameData get()
    {
        if(instance == null)
        {
            instance = new GameData(GameSettings.get(),
                                    MapData.get(),
                                    StructureData.get(),
                                    Player.get());
        }
        return instance;
    }

    /**
     * Method initializes the 3 singletons using data from the database
     * @param context App Context to retrieve database
     */
    public void load(Context context)
    {
        if(db == null) db = new GameDbHelper(context.getApplicationContext()).getWritableDatabase();

        settings.setDatabase(db);
        settings.load();

        mapData = MapData.newInstance();
        mapData.setDatabase(db);
        mapData.load();

        player.setDatabase(db);
        player.load();


    }
}
