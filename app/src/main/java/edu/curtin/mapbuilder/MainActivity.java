package edu.curtin.mapbuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import edu.curtin.mapbuilder.fragments.LoadingFragment;
import edu.curtin.mapbuilder.fragments.TitleFragment;

/**
 * City Builders Entry point.
 * Before the application starts a few settings are adjusted.
 * First the app is made full screen by disabling the title and navigation bar and setting the
 * fullscreen window flags
 *
 * In addition to this strict mode has been enabled to ensure sqlite resources are being opened
 * and closed correctly.
 *
 * Once this setup is complete the model classes are initialized by either setting them to default
 * values or loading in data from the database. From here the title fragment is created and started.
 *
 * This class provides a set of functions for swapping the main fragment being displayed and control
 * the lifecycle of the fragments. This helps to prevents direct communication between fragments.
 * @author Will
 */
public class MainActivity extends AppCompatActivity implements FragmentSwapListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //making the app maximized
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        
        //USING THIS TO LOCATE LEAKS IN THE PROGRAM
        //https://blog.mindorks.com/use-strictmode-to-find-things-you-did-by-accident-in-android-development-4cf0e7c8d997
        if (BuildConfig.DEBUG) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        //INIT - calling constructor, then inflating view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        //INITIALIZING SINGLETONS FROM DB
        GameManager manager = GameManager.get();
        manager.setActivity(this);
        manager.load(this);

        //LOADING FRAGMENTS -
        FragmentManager fm = getSupportFragmentManager();
        Fragment titleFrag = fm.findFragmentById(R.id.manager_fragment);
        if(titleFrag == null)
        {
            titleFrag = new TitleFragment();
            fm.beginTransaction().add(R.id.manager_fragment, titleFrag, "TITLE").commit();
        }
    }

    @Override
    public void onBackPressed()
    {
        //Stops the loading fragment from returning on a back button press
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.manager_fragment);
        if(!(frag instanceof LoadingFragment) || ((LoadingFragment) frag).onBackPressed())
        {
            super.onBackPressed();
        }
    }

    public void setMainFragment(Fragment frag,String tag)
    {
        //Retrieve and replace the fragment
        FragmentManager fm = getSupportFragmentManager();
        if(fm.findFragmentByTag(tag) != null)
        {
            frag = fm.findFragmentByTag(tag);
            System.out.println("WE FOUND IT BOYS");
        }
        else
        {
            System.out.println("NO LUCK MATE");
        }
        fm.beginTransaction().replace(R.id.manager_fragment,frag,tag)

        //allowing the user to press the back button to swap states
        .addToBackStack(null).commit();

        runOnUiThread(fm::executePendingTransactions);

    }


    public void setMainFragmentToRight(Fragment frag,String tag)
    {
        //Retrieve and replace the fragment
        FragmentManager fm = getSupportFragmentManager();
        /*if(fm.findFragmentByTag(tag) != null)
        {
            frag = fm.findFragmentByTag(tag);
            System.out.println("WE FOUND IT BOYS");
        }
        else
        {
            System.out.println("NO LUCK MATE");
        }*/
        fm.beginTransaction()

                //allowing the user to press the back button to swap states
                .setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_left,
                        R.anim.slide_in_right,R.anim.slide_out_right)
                .replace(R.id.manager_fragment,frag,tag)
                .addToBackStack(null).commit();

        runOnUiThread(fm::executePendingTransactions);

    }

    public void setMainFragmentDown(Fragment frag,String tag)
    {
        //Retrieve and replace the fragment
        FragmentManager fm = getSupportFragmentManager();
        if(fm.findFragmentByTag(tag) != null)
        {
            frag = fm.findFragmentByTag(tag);
            System.out.println("WE FOUND IT BOYS");
        }
        else
        {
            System.out.println("NO LUCK MATE");
        }
        fm.beginTransaction()

                //allowing the user to press the back button to swap states
                .setCustomAnimations(R.anim.slide_in_top,R.anim.slide_out_bottom,
                        R.anim.slide_in_bottom,R.anim.slide_out_top)
                .replace(R.id.manager_fragment,frag,tag)
                .addToBackStack(null).commit();

        runOnUiThread(()->fm.executePendingTransactions());

    }

    public void popMainFragment()
    {
        runOnUiThread(()->{
            getSupportFragmentManager().popBackStack();
        });
    }
}
