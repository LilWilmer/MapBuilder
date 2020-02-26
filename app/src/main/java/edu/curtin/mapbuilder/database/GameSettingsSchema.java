/*****************************************************************************
* AUTH: William Payne
* FILE: GameSettingsSchema.java
* LAST MOD: 5/10/19
* PURPOSE: Defines the Setting names for the table and columns 
*****************************************************************************/
package edu.curtin.mapbuilder.database;

public class GameSettingsSchema
{
    public static class Table
    {
        public static final String NAME = "settings";
        public static class Cols
        {
            public static final String PLAYER = "player";
            public static final String WIDTH = "width";
            public static final String HEIGHT = "height";
            public static final String INITIAL_MONEY = "initial_money";
            public static final String FAMILY_SIZE = "family_size";
            public static final String SHOP_SIZE = "shop_size";
            public static final String SALARY = "salary";
            public static final String SERVICE_COST = "service_cost";
            public static final String HOUSE_BUILDING_COST = "house_building_cost";
            public static final String COMM_BUILDING_COST = "comm_building_cost";
            public static final String ROAD_BUILDING_COST = "road_building_cost";
            public static final String TAX_RATE = "tax_rate";
        }
    }
}
