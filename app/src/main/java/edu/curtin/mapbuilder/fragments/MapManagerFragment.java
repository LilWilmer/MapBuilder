package edu.curtin.mapbuilder.fragments;

import android.app.Activity;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import edu.curtin.mapbuilder.GameManager;
import edu.curtin.mapbuilder.R;
import edu.curtin.mapbuilder.model.MapData;
import edu.curtin.mapbuilder.model.Player;

/**
 * Where the magic happens!
 * This class manages 4 different fragments
 * MapFragment - Grid recycler view for displaying the map elements
 * SelectionFragment - The structure selection bar at the bottom of the screen
 * OptionFragment - The set options at the top of the screen for selecting a mode and restarting
 * DetailFragment - The overlay screen when inspecting a map element
 *
 *
 * @author Will
 */
public class MapManagerFragment extends Fragment
{
    private Player player;
    private boolean selectorOn;
    private boolean inSurp;
    private Player.Observer statsListener;
    private Player.Observer switchModeHandler;


    //SOUND EFFECTS:
    private SoundPool soundEffects;
    private int music;

    @Nullable
    @Override
    public View onCreateView(
                            @NonNull LayoutInflater inflater,
                            @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        GameManager manager = GameManager.get();
        this.player = Player.get();
        this.selectorOn = true;

        //MAP FRAGMENT SETUP
        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if(mapFragment == null)
        {
            mapFragment = new MapFragment();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        if(MapData.get().getHeight() == 0 && MapData.get().getWidth() == 0)
        {
            view.findViewById(R.id.brainlet).setVisibility(View.VISIBLE);
        }

        //STRUCTURE SELECTOR FRAGMENT SETUP
        SelectorFragment selectorFragment = (SelectorFragment)fm.findFragmentById(R.id.selector);
        if(selectorFragment == null)
        {
            selectorFragment = new SelectorFragment();
            fm.beginTransaction().add(R.id.selector, selectorFragment).commit();
        }

        //OPTIONS FRAGMENT SETUP
        OptionFragment optionFragment = (OptionFragment) fm.findFragmentById(R.id.option);
        if(optionFragment == null)
        {
            optionFragment = new OptionFragment();
            fm.beginTransaction().add(R.id.option, optionFragment).commit();
        }

        //DETAILS FRAGMENT SETUP


        //GAME TIMER VIEW SETUP
        manager.addObserver(()->{
            Activity activity = getActivity();
            if(activity != null)
            {
                activity.runOnUiThread(() ->
                {
                    TextView text = view.findViewById(R.id.time_counter);
                    if (text != null)
                    {
                        long seconds = manager.getTime()%60L;
                        long minutes = manager.getTime()/60L;
                        String time = String.format("%02d:%02d",minutes,seconds);
                        text.setText(time);
                    }
                });
            }
        });
        inSurp = player.getLastIncome() >= 0;
        ((ImageView)view.findViewById(R.id.last_income_symbol)).setImageDrawable( inSurp ?
                getResources().getDrawable(R.drawable.surplus_24dp) : getResources().getDrawable(R.drawable.deficit_24dp));

        //PLAYER STATS OVERLAY
        this.statsListener = ()->{
            Activity activity = getActivity();
            if(activity != null)
            {
                activity.runOnUiThread(() ->
                {
                    //If fragment is currently attached to a context abort
                    if(getContext()==null)return;

                    //Updating the money TextView
                    TextView money_field = view.findViewById(R.id.money_counter);
                    money_field.setText(String.valueOf(player.getMoney()));


                    TextView text = view.findViewById(R.id.last_income_counter);
                    ImageView img = view.findViewById(R.id.last_income_symbol);
                    //if (text != null && img !=null)
                    //{
                        int lastIncome = player.getLastIncome();
                        String out;
                        if (lastIncome >= 0)
                        {
                            if(!inSurp)
                            {
                                inSurp = true;
                                text.setTextColor(getResources().getColor(R.color.green));
                                img.setImageDrawable(getResources().getDrawable(R.drawable.def_to_surp));
                                ((Animatable2)img.getDrawable())
                                        .registerAnimationCallback(
                                            new Animatable2.AnimationCallback()
                                           {
                                               @Override
                                               public void onAnimationEnd(Drawable drawable)
                                               {
                                                   super.onAnimationEnd(drawable);
                                                   //Activity act = getActivity();
                                                   //if(act != null)
                       /*act.runOnUiThread( ()->*/img.setImageDrawable(getResources().getDrawable(R.drawable.surplus_24dp))/*))*/;
                                               }
                                           }

                                        );
                                ((Animatable2)img.getDrawable()).start();
                            }
                            out = String.format("+$%d",Math.abs(lastIncome));
                        }
                        else
                        {
                            if(inSurp)
                            {
                                inSurp = false;
                                img.setImageDrawable(getContext().getDrawable(R.drawable.surp_to_def));
                                ((Animatable2)img.getDrawable())
                                .registerAnimationCallback(new Animatable2.AnimationCallback()
                                {
                                    @Override
                                    public void onAnimationEnd(Drawable drawable)
                                    {
                                        super.onAnimationEnd(drawable);

                                        if(getContext()!=null)
                                            img.setImageDrawable(getResources().getDrawable(R.drawable.deficit_24dp));
                                    }
                                }

                                );
                                ((Animatable2)img.getDrawable()).start();
                            }
                            text.setTextColor(getResources().getColor(R.color.red));
                            out = String.format("-$%d",Math.abs(lastIncome));
                        }
                        text.setText(out);
                    //}

                    //POPULATION COUNT VIEW
                    text = view.findViewById(R.id.pop_counter);
                    if(text != null)
                    {
                        text.setText(String.valueOf(player.getPopulation()));
                    }

                    //EMPLOYEE PERCENT VIEW
                    text = view.findViewById(R.id.emp_percent_counter);
                    if(text != null)
                    {
                        text.setText(String.format("%2.2f%%",player.getEmploymentRate()*100f));
                    }
                });
            }
        };



        //When the mode button is pressed this handler will add or remove the selection bar
        // at the bottom of the Map fragment depending on which mode is active
        //TODO: Should use a state pattern
        this.switchModeHandler = ()->{
            if(player.getMode() == Player.Mode.BUILDING)
            {
                if( !selectorOn)
                {
                    selectorOn = true;
                    addSelector();
                }
            }
            else
            {
                if(selectorOn)
                {
                    selectorOn = false;
                    removeSelector();
                }
            }
        };
        //manager.start();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        GameManager.get().start();
        player.addObserver(statsListener);
        player.addObserver(switchModeHandler);

        //GAME MUSIC
        //I need to use a soundpool here because MediaPlayer doesnt loop seamlessly
        AudioAttributes attr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundEffects = new SoundPool.Builder()
                .setAudioAttributes(attr)
                .setMaxStreams(2)
                .build();

        soundEffects.setOnLoadCompleteListener((pool, id, eg)->
                pool.play(id,1f,1f,1,-1,1f));
        music = soundEffects.load(getContext(),R.raw.game_music_small,1);

        //soundEffects.play(music,1f,1f,1,-1,1f);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        soundEffects.pause(music);
        soundEffects.release();
        GameManager.get().stop();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        player.removeObserver(statsListener);
        player.removeObserver(switchModeHandler);
        soundEffects.release();
    }

    public void setDetailsFragment(Fragment frag, String tag)
    {
        //Retrieve and replace the fragment
        FragmentManager fm  = getChildFragmentManager();
        fm.beginTransaction()
        .replace(R.id.details,frag,tag)

        //allowing the user to press the back button to swap states
        .addToBackStack(null).commit();
    }

    public void addSelector()
    {
        FragmentManager fm = getChildFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.selector);
        if (frag == null)
        {
            frag = new SelectorFragment();
            fm.beginTransaction()
                .setCustomAnimations( R.anim.slide_in_left, R.anim.slide_out_right,
                                      R.anim.slide_in_right, R.anim.slide_out_left)
                .add(R.id.selector, frag).commit();
        }
    }

    public void removeSelector()
    {
        FragmentManager fm = getChildFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.selector);
        if (frag != null)
        {
            fm.beginTransaction().remove(frag).commit();
            //fm.popBackStackImmediate();
        }
    }
}
