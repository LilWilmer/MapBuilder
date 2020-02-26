/*****************************************************************************
* AUTH: William Payne
* FILE: GameSettingsSchema.java
* LAST MOD: 5/10/19
* PURPOSE: Defines the Player databases table and column names
*****************************************************************************/
package edu.curtin.mapbuilder.database;

public class PlayerSchema
{
    public static class Table
    {
        public static final String NAME = "player";
        public static class Cols
        {
            public static final String PID = "pid";
            public static final String TIME = "time";
            public static final String MONEY = "money";
            public static final String IS_BUILDING = "is_building";
            public static final String POPULATION = "population";
            public static final String LAST_INCOME = "last_income";
            public static final String EMPLOYMENT_RATE = "employment";
            public static final String N_RESIDENTIAL = "nResidential";
            public static final String N_COMMERCIAL = "nCommercial";
            public static final String STRUCTURE_ID = "structure_id";
            public static final String STRUCTURE_LABEL = "structure_label";
            public static final String STRUCTURE_TYPE = "structure_type";
        }
    }
}
