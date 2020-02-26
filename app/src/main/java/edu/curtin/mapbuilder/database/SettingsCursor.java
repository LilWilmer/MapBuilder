/*****************************************************************************
* AUTH: William Payne
* FILE: PlayerCursor.java
* LAST MOD: 5/10/19
* PURPOSE: Allows you to extract the Settings data from a Settings database 
           query.
           Extending CursorWrapper provides methods which abstract the use of
           the Cursor object.
*****************************************************************************/
package edu.curtin.mapbuilder.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Iterator;

import edu.curtin.mapbuilder.database.GameSettingsSchema.Table.Cols;
import edu.curtin.mapbuilder.model.GameSettings;
import edu.curtin.mapbuilder.model.MapElement;

public class SettingsCursor extends CursorWrapper
{
    //CONSTRUCTOR-------------------------------------------------------------
    public SettingsCursor(Cursor cursor)
    {
        super(cursor);
        cursor.moveToFirst();
    }

    /**
     * Initializes a GameSettings object using the data extracted from the 
     * setting queries cursor.
     * @param settings
     */
    public void loadSettings(GameSettings settings)
    {
        settings.setPID(getInt(getColumnIndex(Cols.PLAYER)));
        settings.setMapWidth(getInt(getColumnIndex(Cols.WIDTH)));
        settings.setMapHeight(getInt(getColumnIndex(Cols.HEIGHT)));
        settings.setInitialMoney(getInt(getColumnIndex(Cols.INITIAL_MONEY)));
        settings.setFamilySize(getInt(getColumnIndex(Cols.FAMILY_SIZE)));
        settings.setShopSize(getInt(getColumnIndex(Cols.SHOP_SIZE)));
        settings.setSalary(getInt(getColumnIndex(Cols.SALARY)));
        settings.setServiceCost(getInt(getColumnIndex(Cols.SERVICE_COST)));
        settings.setHouseBuildingCost(getInt(getColumnIndex(Cols.HOUSE_BUILDING_COST)));
        settings.setCommBuildingCost(getInt(getColumnIndex(Cols.COMM_BUILDING_COST)));
        settings.setRoadBuildingCost(getInt(getColumnIndex(Cols.ROAD_BUILDING_COST)));
        settings.setTaxRate(getFloat(getColumnIndex(Cols.TAX_RATE)));
    }
}
