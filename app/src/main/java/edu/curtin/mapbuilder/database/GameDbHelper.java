/*****************************************************************************
* AUTH: William Payne
* FILE: GameDbHelper.java
* LAST MOD: 5/10/19
* PURPOSE: Responsible of intializing database tables and columns into the 
           sqlite database system.
*****************************************************************************/

package edu.curtin.mapbuilder.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Map;

import edu.curtin.mapbuilder.model.Player;

public class GameDbHelper extends SQLiteOpenHelper
{
    //FIELDS------------------------------------------------------------------
    private static final int VERSION = 10;
    private static final String DB_NAME = "structure.db";

    //CONSTRUCTOR-------------------------------------------------------------
    public GameDbHelper(Context context)
    {
        super(context, DB_NAME, null, VERSION);
    }

    //SQLiteOpenHelper METHODS------------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //MAP TABLE
        db.execSQL("create TABLE " + MapSchema.Table.NAME + "("+
                //"_id integer primary key autoincrement,"+
                MapSchema.Table.Cols.X_COORD +" INTEGER," +
                MapSchema.Table.Cols.Y_COORD +" INTEGER," +
                MapSchema.Table.Cols.ID +" TEXT," +
                MapSchema.Table.Cols.BUILDABLE + " INTEGER," +
                MapSchema.Table.Cols.NW+ " INTEGER," +
                MapSchema.Table.Cols.NE + " INTEGER," +
                MapSchema.Table.Cols.SW + " INTEGER," +
                MapSchema.Table.Cols.SE + " INTEGER," +
                MapSchema.Table.Cols.STRUCTURE_ID + " INTEGER," +
                MapSchema.Table.Cols.STRUCTURE_LABEL + " TEXT," +
                MapSchema.Table.Cols.STRUCTURE_TYPE + " INTEGER," +
                "PRIMARY KEY ("+MapSchema.Table.Cols.X_COORD+","+MapSchema.Table.Cols.Y_COORD+"))");

        //SETTINGS TABLE
        db.execSQL("create TABLE " + PlayerSchema.Table.NAME + "("+
                PlayerSchema.Table.Cols.TIME +" INTEGER," +
                PlayerSchema.Table.Cols.PID +" INTEGER primary key," +
                PlayerSchema.Table.Cols.MONEY +" INTEGER," +
                PlayerSchema.Table.Cols.IS_BUILDING + " INTEGER," +
                PlayerSchema.Table.Cols.POPULATION +" INTEGER," +
                PlayerSchema.Table.Cols.LAST_INCOME +" INTEGER," +
                PlayerSchema.Table.Cols.EMPLOYMENT_RATE +" REAL," +
                PlayerSchema.Table.Cols.N_RESIDENTIAL+" INTEGER," +
                PlayerSchema.Table.Cols.N_COMMERCIAL+" INTEGER," +
                PlayerSchema.Table.Cols.STRUCTURE_ID + " INTEGER," +
                PlayerSchema.Table.Cols.STRUCTURE_LABEL + " TEXT," +
                PlayerSchema.Table.Cols.STRUCTURE_TYPE + " INTEGER)");

        //PLAYER DATA TABLE
        db.execSQL("create TABLE " + GameSettingsSchema.Table.NAME + "("+
                GameSettingsSchema.Table.Cols.PLAYER +" INTEGER primary key," +
                GameSettingsSchema.Table.Cols.WIDTH + " INTEGER," +
                GameSettingsSchema.Table.Cols.HEIGHT + " INTEGER," +
                GameSettingsSchema.Table.Cols.INITIAL_MONEY + " INTEGER," +
                GameSettingsSchema.Table.Cols.FAMILY_SIZE + " INTEGER," +
                GameSettingsSchema.Table.Cols.SHOP_SIZE + " INTEGER," +
                GameSettingsSchema.Table.Cols.SALARY + " INTEGER," +
                GameSettingsSchema.Table.Cols.SERVICE_COST + " INTEGER," +
                GameSettingsSchema.Table.Cols.HOUSE_BUILDING_COST + " INTEGER," +
                GameSettingsSchema.Table.Cols.COMM_BUILDING_COST + " INTEGER," +
                GameSettingsSchema.Table.Cols.ROAD_BUILDING_COST + " INTEGER," +
                GameSettingsSchema.Table.Cols.TAX_RATE + " REAL)");
        System.out.println("NEW DB MADE");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        //SORRY OLD USERS BUT ITS IN WITH THE NEW
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+MapSchema.Table.NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+PlayerSchema.Table.NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+GameSettingsSchema.Table.NAME);
        System.out.println("EXECUTING UPGRADE");
        onCreate(sqLiteDatabase);
    }
}
