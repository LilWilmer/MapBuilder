package edu.curtin.mapbuilder.fragments;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.curtin.mapbuilder.GameManager;
import edu.curtin.mapbuilder.R;
import edu.curtin.mapbuilder.model.MapData;
import edu.curtin.mapbuilder.model.MapElement;
import edu.curtin.mapbuilder.model.Player;
import edu.curtin.mapbuilder.model.Structure;

/**
 * MapFragment is the controller for the map grid on the map screen.
 * The grid is implemented using a recycler view that layouts the mapelements with a
 * GridLayout manager.
 *
 * There are two major problems with this controller,
 * One - The recycling of view elements is noticeably slow due to the amount of work done on the
 *       Gui thread. I have a suspicion that its the inflating of the view elements that is causing
 *       this problem and thus removing work from the GUI thread is impossible.
 * Two - Depending on the resolution of the screen or on the dimensions of the MapData grid,
 *       small gaps appear between the view holders that make up the grid which doesn't look
 *       great.
 * @see MapData
 * @see MapElement
 * @author Will
 */
public class MapFragment extends Fragment
{


    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup ui, Bundle b)
    {
        View view = inf.inflate(R.layout.fragment_map, ui, false);
        MapData data = MapData.get();
        RecyclerView rv = view.findViewById(R.id.mapRecycleView);
        GridLayoutManager layoutManager = new GridLayoutManager(
                getActivity(),
                Math.max(1,data.getHeight()),
                GridLayoutManager.HORIZONTAL,
                false);
        rv.setLayoutManager(layoutManager);


        MapAdapter mapAdapter = new MapAdapter(data);

        rv.setAdapter(mapAdapter);

        rv.setHasFixedSize(true);
        rv.setItemViewCacheSize(200);


        return view;
    }

    public class MapAdapter extends RecyclerView.Adapter<MapElementViewHolder> implements MapElement.Observer
    {
        private MapData data;

        public MapAdapter(MapData data)
        {
            this.data = data;
            Activity activity = getActivity();
            MapElement[][] grid = data.getGrid();
            data.addObserver(this);
        }

        @NonNull
        @Override
        public MapElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            LayoutInflater li = LayoutInflater.from(getActivity());

            return new MapElementViewHolder(li, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull MapElementViewHolder holder, int position)
        {
            //Finds which MapElement to display (based off the grid position)
            int row = position % data.getHeight();
            int col = position / data.getHeight();
            MapElement me = data.get(row, col);

            //Get the ViewHolder to bind the passed in MapElement
            holder.bind(me);
        }

        @Override
        public int getItemCount()
        {
            return data.getWidth() * data.getHeight();
        }

        /**
         * The adpater acts as an observer for every viewholder to avoid excessive
         * objects being created and garbage collected when scrolling the map.
         *
         * This method simply passes the update along the responsible view holder
         * @param element element that generated the update
         */
        @Override
        public void update(MapElement element)
        {
            System.out.println("ADAPTER UPDATED");
            if(getActivity()!=null)
                getActivity().runOnUiThread(()->
                {
                    int position = element.getCoord()[0] + element.getCoord()[1]*data.getHeight();
                    System.out.println("On gui thread - updating element at pos: "+position);
                    notifyItemChanged(position);
                });
        }
    }

    public class MapElementViewHolder extends RecyclerView.ViewHolder implements MapElement.Observer
    {
        private MapElement element;
        private ImageView[] map_cell;
        private ImageView structure;

        public MapElementViewHolder(LayoutInflater li, ViewGroup parent)
        {
            //EXTRACTING VIEW ELEMENTS
            super(li.inflate(R.layout.grid_cell2, parent, false));
            this.map_cell = new ImageView[4];
            this.map_cell[0] = itemView.findViewById(R.id.cell1);
            this.map_cell[1] = itemView.findViewById(R.id.cell2);
            this.map_cell[2] = itemView.findViewById(R.id.cell3);
            this.map_cell[3] = itemView.findViewById(R.id.cell4);
            this.structure = itemView.findViewById(R.id.structure);

            //Makes the view holders square but is very buggy.
            //TODO: find a proper solution

            ViewGroup.LayoutParams lp = itemView.getLayoutParams();
            lp.width = Math.round(parent.getMeasuredHeight() / MapData.get().getHeight());

            /*Setting up on click listener for the ViewHolder
            First obtains the player to determine what mode the game is in
            (Currently has three cases)

            When building mode is on, a tap indicates the player is wanting to build at the
            selected ViewHolder. Before this occurs checks are made to see if this is a valid
            action.

            When inspecting mode is on, a tap indicates the player wants to see the details of the
            selected map element.

            When delete mode is on, a tap indicates the player wants to deconstruct a structure
            at the selected element. Before this occurs checks are made to see if this is a valid
            action.
            */
            this.structure.setOnClickListener(view ->
            {
                Player p = Player.get();
                switch(p.getMode())
                {
                    case BUILDING:
                        if(p.getBuildingSelection()!=null &&
                                MapElementViewHolder.this.element.isBuildable()
                                && (MapData.get().adjacentRoad(element.getCoord())
                                || p.getBuildingSelection().getType() == Structure.Type.ROAD)
                                && GameManager.get().purchaseBuilding(p))
                        {
                            Structure building = p.getBuildingSelection();
                            element.setStructure(building);
                            element.saveElement(MapData.get().getDatabase());
                            updateView();
                        }
                        else
                        {
                            MediaPlayer m = MediaPlayer.create(getContext(),R.raw.error_effect);
                            m.setOnCompletionListener(MediaPlayer::release);
                            m.start();
                        }
                        break;

                    case INSPECTING:
                        DetailsFragment detailFrag = new DetailsFragment();
                        detailFrag.setArgs(element);
                        ((MapManagerFragment)getParentFragment()).setDetailsFragment(
                                detailFrag,"detail_frag");
                        break;

                    case DELETING:
                        if(element.getStructure() != null)
                        {
                            Player.get().removeBuilding(element.getStructure().getType());
                            structure.setImageDrawable(null);
                            element.clear();
                            element.saveElement(MapData.get().getDatabase());
                        }
                        break;
                }
            });
        }

        public void bind(MapElement me)
        {
            /*if(this.element!=null)
            {
                this.element.removeObserver(this);
            }
            me.addObserver(this);*/
            this.element = me;
            if(me == null)
            {
                return;
            }
            updateView();
        }

        public void updateView()
        {
            MapElement me = this.element;
            this.map_cell[0].setImageResource(me.getNorthWest());
            this.map_cell[1].setImageResource(me.getNorthEast());
            this.map_cell[2].setImageResource(me.getSouthWest());
            this.map_cell[3].setImageResource(me.getSouthEast());

            if(me.getThumbnail() != null)
            {
                this.structure.setImageBitmap(me.getThumbnail());
            }
            else if(me.getStructure() != null)
                this.structure.setImageResource(me.getStructure().getDrawableId());
            else
                this.structure.setImageDrawable(null);
        }

        @Override
        public void update(MapElement element)
        {
            if(getActivity()!=null)
                getActivity().runOnUiThread(()->{
                    updateView();
                });
        }
    }
}
