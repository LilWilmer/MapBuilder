package edu.curtin.mapbuilder.model;

import java.util.Arrays;
import java.util.List;

import edu.curtin.mapbuilder.R;

/**
 * Stores the list of possible structures. This has a static get() method for retrieving an
 * instance, rather than calling the constructor directly.
 *
 * The remaining methods -- get(int), size(), add(Structure) and remove(int) -- provide
 * minimalistic list functionality.
 *
 * There is a static int array called DRAWABLES, which stores all the drawable integer references,
 * some of which are not actually used (yet) in a Structure object.
 */
public class StructureData
{
    public static Structure heldStructure = null;
    public static final int[] DRAWABLES = {
        0, // No structure
        R.drawable.ic_building1, R.drawable.ic_building2, R.drawable.ic_building3,
        R.drawable.ic_building4, R.drawable.ic_building5, R.drawable.ic_building6,
        R.drawable.ic_building7, R.drawable.ic_building8,
        R.drawable.ic_road_ns, R.drawable.ic_road_ew, R.drawable.ic_road_nsew,
        R.drawable.ic_road_ne, R.drawable.ic_road_nw, R.drawable.ic_road_se, R.drawable.ic_road_sw,
        R.drawable.ic_road_n, R.drawable.ic_road_e, R.drawable.ic_road_s, R.drawable.ic_road_w,
        R.drawable.ic_road_nse, R.drawable.ic_road_nsw, R.drawable.ic_road_new, R.drawable.ic_road_sew,
        R.drawable.ic_tree1, R.drawable.ic_tree2, R.drawable.ic_tree3, R.drawable.ic_tree4};

    private List<Structure> structureList = Arrays.asList(
            new Structure(R.drawable.ic_building1, "House", Structure.Type.RESIDENTIAL),
            new Structure(R.drawable.ic_building2, "House", Structure.Type.RESIDENTIAL),
            new Structure(R.drawable.ic_building3, "House", Structure.Type.RESIDENTIAL),
            new Structure(R.drawable.ic_building4, "House", Structure.Type.RESIDENTIAL),
            new Structure(R.drawable.ic_building5, "Factory", Structure.Type.COMMERCIAL),
            new Structure(R.drawable.ic_building6, "Garage", Structure.Type.COMMERCIAL),
            new Structure(R.drawable.ic_building7, "Tower", Structure.Type.COMMERCIAL),
            new Structure(R.drawable.ic_building8, "Bunker", Structure.Type.COMMERCIAL),
            new Structure(R.drawable.ic_road_ns, "Road", Structure.Type.ROAD),
            new Structure(R.drawable.ic_road_ew, "Road", Structure.Type.ROAD),
            new Structure(R.drawable.ic_road_nsew, "Road", Structure.Type.ROAD),
            new Structure(R.drawable.ic_road_ne, "Road", Structure.Type.ROAD),
            new Structure(R.drawable.ic_road_nw, "Road", Structure.Type.ROAD),
            new Structure(R.drawable.ic_road_se, "Road", Structure.Type.ROAD),
            new Structure(R.drawable.ic_road_sw, "Road", Structure.Type.ROAD),
            new Structure(R.drawable.ic_road_n, "Road", Structure.Type.ROAD),
            new Structure(R.drawable.ic_road_e, "Road", Structure.Type.ROAD),
            new Structure(R.drawable.ic_road_s, "Road", Structure.Type.ROAD),
            new Structure(R.drawable.ic_road_w, "Road", Structure.Type.ROAD),
            new Structure(R.drawable.ic_road_nse, "Road", Structure.Type.ROAD),
            new Structure(R.drawable.ic_road_nsw, "Road", Structure.Type.ROAD),
            new Structure(R.drawable.ic_road_new, "Road", Structure.Type.ROAD),
            new Structure(R.drawable.ic_road_sew, "Road", Structure.Type.ROAD),
            new Structure(R.drawable.ic_tree1, "Tree", Structure.Type.COSMETIC),
            new Structure(R.drawable.ic_tree2, "Tree", Structure.Type.COSMETIC),
            new Structure(R.drawable.ic_tree3, "Tree", Structure.Type.COSMETIC),
            new Structure(R.drawable.ic_tree4, "Tree", Structure.Type.COSMETIC));

    private static StructureData instance = null;

    public static StructureData get()
    {
        if(instance == null)
        {
            instance = new StructureData();
        }
        return instance;
    }

    protected StructureData() {}

    public Structure get(int i)
    {
        return structureList.get(i);
    }

    public int size()
    {
        return structureList.size();
    }

    public void add(Structure s)
    {
        structureList.add(0, s);
    }

    public void remove(int i)
    {
        structureList.remove(i);
    }
}
