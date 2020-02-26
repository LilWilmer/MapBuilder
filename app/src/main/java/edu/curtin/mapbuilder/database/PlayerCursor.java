/*****************************************************************************
* AUTH: William Payne
* FILE: PlayerCursor.java
* LAST MOD: 5/10/19
* PURPOSE: Allows you to extract the Player data from a Player database query.
           Extending CursorWrapper provides methods which abstract the use of
           the Cursor object.
*****************************************************************************/
package edu.curtin.mapbuilder.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import edu.curtin.mapbuilder.database.PlayerSchema.Table.Cols;
import edu.curtin.mapbuilder.model.Player;
import edu.curtin.mapbuilder.model.Structure;

public class PlayerCursor extends CursorWrapper
{
    //CONSTRUCTOR-------------------------------------------------------------
    public PlayerCursor(Cursor cursor)
    {
        super(cursor);
        cursor.moveToFirst();
    }

    /*************************************************************************
    * Initializes the player object passed in via the import field using data
    * extracted from the cursor.
    * @param player
    */
    public void loadPlayer(Player player)
    {
        player.setPID(getInt(getColumnIndex(Cols.PID)));
        player.setTime(getLong(getColumnIndex(Cols.TIME)));
        player.setMoney(getInt(getColumnIndex(Cols.MONEY)));
        player.setBuilding(getInt(getColumnIndex(Cols.IS_BUILDING)) > 0);
        player.setPopulation(getInt(getColumnIndex(Cols.POPULATION)));
        player.setLastIncome(getInt(getColumnIndex(Cols.LAST_INCOME)));
        player.setEmploymentRate(getFloat(getColumnIndex(Cols.EMPLOYMENT_RATE)));
        player.setnResidential(getInt(getColumnIndex(Cols.N_RESIDENTIAL)));
        player.setnCommercial(getInt(getColumnIndex(Cols.N_COMMERCIAL)));

        int structure_id = getInt(getColumnIndex(Cols.STRUCTURE_ID));
        String structure_label = getString(getColumnIndex(Cols.STRUCTURE_LABEL));
        int structure_type = getInt(getColumnIndex(Cols.STRUCTURE_TYPE));

        //ATTEMPT TO RETRIEVE THE STRUCTURE DATA
        Structure structure = null;
        try
        {
            structure = new Structure(structure_id, structure_label, structure_type);
        }
        catch (IllegalArgumentException e)
        {
            structure = null;
        }
        finally
        {
            player.setBuildingSelection(structure);
        }
    }
}
