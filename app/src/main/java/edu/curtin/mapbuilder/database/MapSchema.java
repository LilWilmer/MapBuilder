/*****************************************************************************
* AUTH: William Payne
* FILE: MapSchema.java
* LAST MOD: 5/10/19
* PURPOSE: Defines the MapData database column and table names.
*****************************************************************************/
package edu.curtin.mapbuilder.database;

public class MapSchema
{
    public static class Table
    {
        public static final String NAME = "map";
        public static class Cols
        {
            public static final String X_COORD = "x_coord";
            public static final String Y_COORD = "y_coord";
            public static final String ID = "id";
            public static final String BUILDABLE = "buildable";
            public static final String NW = "terrainNorthWest";
            public static final String SW = "terrainSouthWest";
            public static final String NE = "terrainNorthEast";
            public static final String SE = "terrainSouthEast";
            public static final String STRUCTURE_ID = "structure_id";
            public static final String STRUCTURE_LABEL = "structure_label";
            public static final String STRUCTURE_TYPE = "structure_type";
        }
    }
}
