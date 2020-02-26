package edu.curtin.mapbuilder.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import edu.curtin.mapbuilder.R;
import edu.curtin.mapbuilder.model.MapData;
import edu.curtin.mapbuilder.model.MapElement;
/**
 * DetailFragment is the fragment controller class for the details window that appears after
 * selecting a tile on the map while the player is in the 'Inspect Mode'.
 *
 *
 * @author Will
 */
public class DetailsFragment extends Fragment
{
    public static final int REQUEST_CODE = 23;
    public static final String X_COORDINATE = "x_coord";
    public static final String Y_COORDINATE = "y_coord";

    private int x;
    private int y;
    private MapElement me;

    public void initWithArgs(Bundle arguments)
    {
        //check if arguments have been passed to the fragment (or saved from onSaveState)
        //Use the extras to initialize the map element position
        if(arguments != null)
        {
            x = arguments.getInt(DetailsFragment.X_COORDINATE);
            y = arguments.getInt(DetailsFragment.Y_COORDINATE);
            this.me = MapData.get().get(x,y);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Bundle arguments = getArguments();
        initWithArgs(arguments);
    }

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup ui, Bundle b)
    {
        View view = inf.inflate(R.layout.fragment_details, ui, false);

        initWithArgs(getArguments());
        //MAP FRAGMENT SETUP
        FragmentManager fm = getChildFragmentManager();
        Fragment detailFragment = fm.findFragmentById(R.id.map_cell_details);
        if(detailFragment == null)
        {
            detailFragment = new DetailBarFragment();
            detailFragment.setArguments(getArguments());
            fm.beginTransaction().add(R.id.map_cell_details, detailFragment).commit();
        }

        Fragment cellFragment = fm.findFragmentById(R.id.map_cell);
        if(cellFragment == null)
        {
            cellFragment = new MapCellFragment();
            cellFragment.setArguments(getArguments());
            fm.beginTransaction().add(R.id.map_cell, cellFragment).commit();
        }

        ConstraintLayout background = view.findViewById(R.id.constraint_background);
        background.setOnClickListener((vi)->
        {
            fm.beginTransaction().remove(this).commit();
        });

        //only allow tiles with a structure to place an image
        ImageView camera = view.findViewById(R.id.camera_button);
        if(me.getStructure()!= null)
        {
            camera = view.findViewById(R.id.camera_button);
            camera.setOnClickListener(vi ->
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE);
            });
        }
        else
        {
            camera.setVisibility(View.GONE);
        }

        return view;
    }

    public void setArgs(MapElement me)
    {
        int[] coords = me.getCoord();
        Bundle bundle = new Bundle();
        bundle.putInt(X_COORDINATE,coords[0]);
        bundle.putInt(Y_COORDINATE,coords[1]);
        setArguments(bundle);
    }

    @Override
    public void onActivityResult(int r, int rq, Intent data)
    {
        if(r == REQUEST_CODE && rq == Activity.RESULT_OK)
        {
            System.out.println("click");
            Bitmap thumbnail =(Bitmap) data.getExtras().get("data");
            me.setThumbnail(thumbnail);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //In the event the phone is rotated
        outState.putInt(X_COORDINATE, this.x);
        outState.putInt(Y_COORDINATE, this.y);
    }
}
