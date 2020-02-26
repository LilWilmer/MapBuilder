package edu.curtin.mapbuilder.fragments;

import android.graphics.drawable.Animatable2;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import edu.curtin.mapbuilder.GameManager;
import edu.curtin.mapbuilder.R;
import edu.curtin.mapbuilder.model.Player;
import edu.curtin.mapbuilder.model.Structure;
import edu.curtin.mapbuilder.model.StructureData;

/**
 * SelectorFragment is the controller for the structure selection bar used while on the map screen
 * This class sets up a recycler view that displays all the structures in the StructureData
 * @author Will
 */
public class SelectorFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater li, ViewGroup ui, Bundle bundle)
    {
        // Inflate the fragment frame
        View view = li.inflate(R.layout.fragment_selector, ui, false);

        //Get the recycler view from the xml and set it to be linear horizontal.
        SnapHelper snapHelper = new LinearSnapHelper();
        RecyclerView rv = view.findViewById(R.id.selectorRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(
                getActivity(),
                LinearLayout.HORIZONTAL,
                false));

        //Get the StructureData single and pass it to the new SelectorAdapter
        StructureData structureData = StructureData.get();
        Player player = Player.get();

        SelectorAdapter adapter = new SelectorAdapter(structureData, player);
        rv.setAdapter(adapter);

        snapHelper.attachToRecyclerView(rv);

        return view;
    }

    /****
     * CLASS: SelectorAdapter
     * PURP: Adapter for recyclerview
     * RESP: Responsible for translating data into view holders to be displayed in
     *       in the recycler view
     *
     */
    public class SelectorAdapter extends RecyclerView.Adapter<StructureViewHolder>
    {
        private StructureData data;
        private Player player;

        SelectorAdapter(StructureData data, Player player)
        {
            this.data = data;
            this.player = player;
        }

        @NonNull
        @Override
        public StructureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            //Give the inflater to the view holder
            LayoutInflater li = LayoutInflater.from(getActivity());
            return new StructureViewHolder(li, parent, player);
        }

        @Override
        public void onBindViewHolder(@NonNull StructureViewHolder holder, int position)
        {
            holder.bind(data.get(position));
        }

        @Override
        public int getItemCount()
        {
            return data.size();
        }
    }

    /**
     * CLASS: StructureViewHolder
     * PURP: Controls the tiny view xml in the recycler view
     * RESP: One for each 'visible' list row, updates its pos with the data from adapter
     */
    public class StructureViewHolder extends RecyclerView.ViewHolder implements Structure.Observer
    {
        private Player p;
        private Structure structure;
        private ImageView structureIcon;
        private TextView label;
        private TextView cost;
        StructureViewHolder(LayoutInflater li, ViewGroup parent, Player p)
        {
            //PASSES the inflated view to the ViewHolder constructor
            super(li.inflate(R.layout.list_selection, parent, false));
            this.structureIcon = itemView.findViewById(R.id.selectionIcon);
            this.label = itemView.findViewById(R.id.selection_title);
            this.cost = itemView.findViewById(R.id.cost);
            this.p = p;

            //Setting up the structure selection feature
            //When a structure is tapped it calls this listener to handle
            //adding or removing a structure from the users selection
            this.structureIcon.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    //If a structure is currently selected it is unselected
                    //If the selected structure is selected again it is unselected
                    //Else the new building is assigned to the player and made active
                    //
                    // NOTE: There are two things happening here
                    // 1. The players held structure is being set
                    // 2. The structure is being set 'active' so that appears selected
                    //    in the selection bar
                    if(p.getBuildingSelection() != null)
                    {
                        p.getBuildingSelection().setActive(false);
                    }
                    if(p.getBuildingSelection() != structure || !p.isBuilding())
                    {
                        p.setBuilding(true);
                        p.setBuildingSelection(structure);
                        structure.setActive(true);
                    }
                    else
                    {
                        p.setBuilding(false);
                        p.setBuildingSelection(null);
                    }
                }
            });
        }

        /**
         * Called by the Adapter when a the viewholder needs to display a new Structure
         * @param struct Structure being represented by the ViewHolder
         */
        void bind(Structure struct)
        {
            if(this.structure!= null) this.structure.removeObserver(this);
            this.structure = struct;
            this.structure.addObserver(this);
            refreshView();
        }

        /**
         * Listens for Structure state changes, updating the view as necessary
         */
        public void update()
        {
            //updates could originate from any thread
            if(getActivity()!=null)
                getActivity().runOnUiThread(this::refreshView);
        }

        /**
         * Updates the view elements with the data stored in the structure object
         */
        private void refreshView()
        {
            this.structureIcon.setImageResource(structure.getDrawableId());
            this.label.setText(structure.getLabel());
            this.cost.setText("$"+ GameManager.get().getCost(structure.getType()));
            ImageView img = this.itemView.findViewById(R.id.selectionIcon);
            this.label.setTextColor(getContext().getResources().getColor(R.color.black));
            img.setBackgroundDrawable(null);
            if(structure.isActive())
            {
                img.setBackgroundResource(R.anim.pop_select_anim);
                ((Animatable2)img.getBackground()).start();
                this.label.setTextColor(getContext().getResources().getColor(R.color.white));
            }
        }
    }
}
