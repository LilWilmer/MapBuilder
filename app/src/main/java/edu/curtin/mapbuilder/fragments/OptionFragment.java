package edu.curtin.mapbuilder.fragments;

import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import edu.curtin.mapbuilder.MainActivity;
import edu.curtin.mapbuilder.R;
import edu.curtin.mapbuilder.model.Player;

/**
 * OptionFragment is the controller for the option bar used in the map mode.
 * It initializes and inflates a set of ImageViews as buttons inside of a simple linear layout
 * @author Will
 */
public class OptionFragment extends Fragment
{
    public static final int MODE_COUNT = 3;
    public static final Player.Mode[] PLAYER_MODE = {
            Player.Mode.BUILDING,
            Player.Mode.INSPECTING,
            Player.Mode.DELETING
    };

    public static final int[] ANIMATION = {
            R.drawable.build_to_inspect,
            R.drawable.inspect_to_delete,
            R.drawable.delete_to_build
    };

    private int mode;

    //We need to make sure the mode starts at 0 when the fragment is first created
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.mode = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup ui, Bundle b)
    {
        View view = inf.inflate(R.layout.fragment_option, ui, false);
        LinearLayout options = view.findViewById(R.id.options);
        int button_size = getResources().getDimensionPixelSize(R.dimen.option_button_size);

        //RESTART BUTTON SETUP:
        //Create new ImageView and set its size using the R.dimen resource file
        //Provide it a listener that s
        ImageView restart = new ImageView(getContext());
        restart.setImageResource(R.drawable.refresh_24dp);
        restart.setLayoutParams(new LinearLayout.LayoutParams(button_size,button_size));
        restart.setBackground(getResources().getDrawable(R.drawable.circle));
        restart.setOnClickListener(v->{
            MainActivity act = (MainActivity)getActivity();
            if(act != null)
            {
                //act.popMainFragment();
                act.setMainFragmentDown(new LoadingFragment(),"LOADING");
            }
        });

        /*Setup for the MODE BUTTON
        * View element is a ImageView
        * The image is assigned a listener responsible for cycling through each player mode
        * and player the correct animated vector drawable.
        * The layout parameters are set to the default button_size param
        */
        ImageView playerMode = new ImageView(getContext());
        playerMode.setImageResource(ANIMATION[mode]);
        playerMode.setLayoutParams(new LinearLayout.LayoutParams(button_size,button_size));
        playerMode.setBackground(getResources().getDrawable(R.drawable.circle));
        playerMode.setOnClickListener(vi->
        {
            if(!((Animatable2) playerMode.getDrawable()).isRunning())
            {
                mode = (mode+1)%MODE_COUNT;
                ((Animatable2) playerMode.getDrawable()).registerAnimationCallback(new Animatable2.AnimationCallback()
                {
                    @Override
                    public void onAnimationEnd(Drawable drawable)
                    {
                        super.onAnimationEnd(drawable);
                        playerMode.setImageResource(ANIMATION[mode]);
                    }
                });
                ((Animatable2) playerMode.getDrawable()).start();
                Player.get().setMode(PLAYER_MODE[mode]);
            }
        });
        options.addView(restart);
        options.addView(playerMode);

        return view;
    }

    //When the fragment is resumed we need the mode to be set to the last selected mode
    @Override
    public void onResume()
    {
        super.onResume();
        Player.get().setMode(PLAYER_MODE[mode]);
    }

}
