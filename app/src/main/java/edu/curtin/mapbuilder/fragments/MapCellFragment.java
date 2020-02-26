package edu.curtin.mapbuilder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import edu.curtin.mapbuilder.R;
import edu.curtin.mapbuilder.model.MapData;
import edu.curtin.mapbuilder.model.MapElement;

/**
 * Simple controller class for the MapCell Fragment
 * Handles inflating a view that represents a single map cell
 * Currently this Fragment is used for the details fragment screen when inspecting a single
 * map element.
 * @see DetailsFragment
 * @see MapElement
 * @author Will
 */
public class MapCellFragment extends Fragment implements MapElement.Observer
{
    private View view;
    private MapElement me;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments != null)
        {
            int x = arguments.getInt(DetailsFragment.X_COORDINATE);
            int y = arguments.getInt(DetailsFragment.Y_COORDINATE);
            this.me = MapData.get().get(x,y);
        }
        me.addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle)
    {
        view = inflater.inflate(R.layout.grid_cell, container, false);

        bind();

        return view;
    }

    public void bind()
    {
        ((ImageView)view.findViewById(R.id.cell1)).setImageResource(me.getNorthWest());
        ((ImageView)view.findViewById(R.id.cell2)).setImageResource(me.getNorthEast());
        ((ImageView)view.findViewById(R.id.cell4)).setImageResource(me.getSouthEast());
        ((ImageView)view.findViewById(R.id.cell3)).setImageResource(me.getSouthWest());

        ImageView structure = view.findViewById(R.id.structure);
        System.out.println("DETAIL ELEMENT ID: "+me+" COORD:"+me.getID()+" BITMAP: "+me.getThumbnail());
        if (me.getThumbnail() != null)
            structure.setImageBitmap(me.getThumbnail());
        else if (me.getStructure() != null)
            structure.setImageResource(me.getStructure().getDrawableId());
        else
            structure.setImageDrawable(null);

    }

    @Override
    public void update(MapElement element)
    {
        if(getActivity()!= null) getActivity().runOnUiThread(this::bind);
    }
}
