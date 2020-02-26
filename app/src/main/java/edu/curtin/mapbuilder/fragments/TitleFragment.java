package edu.curtin.mapbuilder.fragments;

import android.content.Context;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import edu.curtin.mapbuilder.FragmentSwapListener;
import edu.curtin.mapbuilder.R;

/**
 * TitleFragment controller class.
 * Requires parent context to implement FragmentSwapListener
 * @author Will
 * @see FragmentSwapListener
 */
public class TitleFragment extends Fragment
{
    private FragmentSwapListener activity;

    @Override
    public void onAttach(Context context)
    {
        //Ensuring the context is an instance of FragmentSwapListener
        //else throw class cast exception
        super.onAttach(context);
        if(context instanceof FragmentSwapListener)
        {
            this.activity = (FragmentSwapListener)context;
        }
        else
        {
            throw new ClassCastException("Parent context must be instance of FragmentSwapListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup parent, Bundle b)
    {
        View view = li.inflate(R.layout.title_fragment, parent, false);

        //Setup for Map Button
        //When the button is clicked the listener will swap this fragment out with the MapFragment
        //This swap will occur in the parent context of this fragment
        ImageView startButton = view.findViewById(R.id.start_game_button);
        startButton.setImageResource(R.drawable.map_mode_24dp);
        startButton.setOnClickListener((vi)->{
        startButton.setImageResource(R.anim.mappop_anim);
        Animatable2 anim = null;
            anim = (Animatable2)startButton.getDrawable();
            if(!anim.isRunning())
            {
                anim.registerAnimationCallback(new Animatable2.AnimationCallback()
                {
                    @Override
                    public void onAnimationEnd(Drawable drawable)
                    {
                        super.onAnimationEnd(drawable);
                        activity.setMainFragmentDown(new MapManagerFragment(),"MAP");
                    }
                });
                anim.start();
            }


        });


        //Setup for Settings button
        //Setting up listener to swap fragment in the parent context to the SettingsFragment
        ImageView settingsButton = view.findViewById(R.id.settings_button);
        settingsButton.setImageResource(R.drawable.settings_24dp);
        settingsButton.setOnClickListener((vi)->
        {
            settingsButton.setImageResource(R.anim.settingpop_anim);
            Animatable2 anim = null;
            anim = (Animatable2)settingsButton.getDrawable();
            if(!anim.isRunning())
            {
                anim.registerAnimationCallback(new Animatable2.AnimationCallback()
                {
                    @Override
                    public void onAnimationEnd(Drawable drawable)
                    {
                        super.onAnimationEnd(drawable);
                        activity.setMainFragmentToRight(new SettingsFragment(),"SETTINGS");
                    }
                });
                anim.start();
            }
        });

        return view;
    }
}
