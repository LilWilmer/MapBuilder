package edu.curtin.mapbuilder.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import edu.curtin.mapbuilder.R;
import edu.curtin.mapbuilder.model.MapData;
import edu.curtin.mapbuilder.model.MapElement;

/**
 * DetailBarFragment sets up a list of build
 *
 *
 * @author Will
 */
public class DetailBarFragment extends Fragment
{
    private MapElement me;
    private int x;
    private int y;

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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if(arguments == null)
        {
            arguments = savedInstanceState;
        }

        //InitWithArgs will load arguments if they were passed to the fragment
        initWithArgs(getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle)
    {
        View view = inflater.inflate(R.layout.fragment_detail_bar, container, false);


        //if the fragment is recreated & savedState exists, use it to initialize the map element pos
        initWithArgs(bundle);

        //Setup for the Coordinate TextView
        //Using the passed arguments, set the Coordinate TextView
        TextView coord = view.findViewById(R.id.coordinate_text);
        coord.setText(String.format("x:%d y:%d", x, y));

        //Setup for the structure type TextField.
        //If a structure exists on the element, its type field is used to set the TextField.
        //If a type cannot be extracted, the textField will be set to Wilderness.
        TextView type = view.findViewById(R.id.structure_type_text);
        String type_string = me.getType();
        if (type_string == null) type_string = "Wilderness";
        type.setText(type_string);

        //Setup for the editable name field
        //If a structure exists on the map element, init the field to the structures name
        //By default the name field is set to "-"
        //The ImeOption is set to done so the keyboard hides after entering a replacement name
        EditText name = view.findViewById(R.id.structure_name_text);
        if(me.getStructure()!= null)
        {
            String name_string = me.getLabel();
            if (name_string == null) name_string = "-";
            name.setText(name_string);
            name.setImeOptions(EditorInfo.IME_ACTION_DONE);
            //Setup for the TextWatcher callback
            //When a new name is entered it replaces the current elements label
            name.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                    me.setLabel(charSequence.toString());
                    me.saveElement(MapData.get().getDatabase());
                }

                @Override
                public void afterTextChanged(Editable editable)
                {

                }
            });
        }
        else
        {
            view.findViewById(R.id.name_field_parent).setVisibility(View.GONE);
        }

        return view;
    }



    public void updateView()
    {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //Save the fragments state in the event the phone is rotated.
        outState.putInt(DetailsFragment.X_COORDINATE,this.x);
        outState.putInt(DetailsFragment.Y_COORDINATE,this.y);
    }
}
