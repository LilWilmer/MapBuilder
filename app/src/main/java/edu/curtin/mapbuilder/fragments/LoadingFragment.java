package edu.curtin.mapbuilder.fragments;

import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import edu.curtin.mapbuilder.GameManager;
import edu.curtin.mapbuilder.MainActivity;
import edu.curtin.mapbuilder.R;

/**
 * This fragment plays an animation continuously until the map has regenerated and calls
 * popMainFragment
 * @author Will
 */
public class LoadingFragment extends Fragment
{
    private boolean done;
    private Animatable2 anim;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        done = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup ui, Bundle b)
    {
        View view = inf.inflate(R.layout.fragment_loading, ui, false);

        //Setup for animated vector drawable and listener
        //After the animation has finished a cycle, it is immediately played again using the
        //animationCallback
        anim = (Animatable2)((ImageView)view.findViewById(R.id.loading_anim)).getDrawable();
        anim.registerAnimationCallback(new Animatable2.AnimationCallback()
            {
                @Override
                public void onAnimationStart(Drawable drawable)
                {
                    super.onAnimationStart(drawable);
                }

                @Override
                public void onAnimationEnd(Drawable drawable)
                {
                    super.onAnimationEnd(drawable);
                    anim.start();
                }
            }
        );
        anim.start();
        return view;
    }

    //Starts a new thread that will do the cpu heavy map regeneration and pops the loading fragment
    //once is completes
    @Override
    public void onResume()
    {
        super.onResume();

        MainActivity act = (MainActivity)getActivity();
        new Thread(()-> GameManager.get().resetWithListener(()->{
            LoadingFragment.this.done = true;
            act.runOnUiThread(()->{
                if(getContext()!= null)
                    act.popMainFragment();
            });
        })).start();
    }

    public boolean onBackPressed()
    {
        return done;
    }

    //Prevents users from pressing the back button while the map is loading
    @Override
    public void onStop()
    {
        super.onStop();
        anim.stop();
    }
}
