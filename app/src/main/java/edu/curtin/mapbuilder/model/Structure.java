package edu.curtin.mapbuilder.model;

import java.util.HashSet;

/**
 * Represents a possible structure to be placed on the map. A structure simply contains a drawable
 * int reference, and a string label to be shown in the selector.
 */
public class Structure
{
    public static final int ROAD = 1;
    public static final int COMMERCIAL = 2;
    public static final int RESIDENTIAL = 3;
    public static final int COSMETIC = 4;

    public enum Type {ROAD, COMMERCIAL, RESIDENTIAL, COSMETIC, UNDEFINED}
    private Type type;
    private HashSet<Observer> observers;
    private boolean isActive = false;
    private final int drawableId;
    private String label;

    public Structure(int drawableId, String label, Type type)
    {
        if(drawableId == 0 || label == null) throw new IllegalArgumentException();
        this.observers = new HashSet<>();
        this.drawableId = drawableId;
        this.label = label;
        this.type = type;
    }

    public Structure(int drawableId, String label, int type)
    {
        if(drawableId == 0 || label == null) throw new IllegalArgumentException();
        this.observers = new HashSet<>();
        this.drawableId = drawableId;
        this.label = label;
        setType(type);
    }

    public static int getType(Type type)
    {
        switch(type)
        {
            case ROAD: return 1;
            case COMMERCIAL: return 2;
            case RESIDENTIAL: return 3;
            case COSMETIC: return 4;
        }
        return -1;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(int type)
    {
        Type t = Type.UNDEFINED;
        switch (type)
        {
            case ROAD:
                t = Type.ROAD;
                break;

            case COMMERCIAL:
                t = Type.COMMERCIAL;
                break;

            case RESIDENTIAL:
                t= Type.RESIDENTIAL;
                break;

            case COSMETIC:
                t= Type.COSMETIC;
                break;
        }
        this.type = t;
    }
    public void setType(Type type)
    {
        this.type = type;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public int getDrawableId()
    {
        return drawableId;
    }

    public String getLabel()
    {
        return label;
    }

    public void setActive(boolean isActive){this.isActive = isActive; notifyObservers();}
    public boolean isActive(){return isActive;}

    /*OBSERVE METHODS*/
    public void addObserver(Observer ob){this.observers.add(ob);}
    public void removeObserver(Observer ob){this.observers.remove(ob);}
    public void notifyObservers(){for(Observer ob : observers){ob.update();}}

    public static interface Observer
    {
        //lets observers know when the structure data has changed
        void update();
    }
}

