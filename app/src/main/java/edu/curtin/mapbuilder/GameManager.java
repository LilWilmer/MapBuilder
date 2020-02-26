package edu.curtin.mapbuilder;

import android.content.Context;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import edu.curtin.mapbuilder.fragments.LoadingFragment;
import edu.curtin.mapbuilder.model.GameData;
import edu.curtin.mapbuilder.model.GameSettings;
import edu.curtin.mapbuilder.model.MapData;
import edu.curtin.mapbuilder.model.Player;
import edu.curtin.mapbuilder.model.Structure;
import edu.curtin.mapbuilder.model.StructureData;

/**
 * GameManager is the main controller class for the actual game logic and flow of events.
 * It is responsible for starting up the game time system and the game rules.
 * @author Will
 */
public class GameManager
{
    //SINGLETON
    private static GameManager instance;
    private MainActivity activity; public void setActivity(MainActivity a){this.activity=a;}


    //TIME SHIT
    private long time;
    private Timer timer;
    private HashSet<Observer> observers;

    //GAME DATA
    private GameData model;
    private GameSettings settings;
    private MapData mapData;
    private StructureData structureData;
    private Player player;

    public static GameManager get()
    {
        if(instance == null)
        {
            instance = new GameManager(
                    GameData.get(),
                    GameSettings.get(),
                    MapData.get(),
                    StructureData.get(),
                    Player.get());
        }
        return instance;
    }
    private GameManager(GameData model, GameSettings gs, MapData md, StructureData sd, Player player)
    {
        this.model = model;
        this.time = 0;
        this.timer = null;
        this.settings = gs;
        this.mapData = md;
        this.structureData = sd;
        this.player = player;
        this.observers = new HashSet<>();
        this.observers.add(this::onUpdate);
    }

    public void load(Context context)
    {
        this.model.load(context);
    }


    /**
     * Creates a new timertask that represents each 'tick' as time passes.
     * the tick will invoke all the update observers
     */
    public void start()
    {
        this.time = player.getTime();
        if(timer == null)
        {
            this.timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run()
                {
                    for(Observer ob : observers) ob.onUpdate();
                }
            },1000,1000);
        }
        /*ELSE THROW EXCEPTION*/
    }


    /**
     * Stops the timertask and sets it to null
     */
    public void stop()
    {
        if(timer != null)
        {
            this.timer.cancel();
            this.timer = null;
        }
    }

    public void gameOver()
    {
        System.out.println("GAME OVER MAN! GAME OVER!");
        activity.runOnUiThread(()-> {

            if(activity != null)
            {
                //activity.popMainFragment();
                //TODO: make a custom toast
                CharSequence text = "Game Over!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(activity, text, duration);
                toast.show();

                activity.setMainFragment(new LoadingFragment(), "LOADING");
            }
        });
        //reset();
    }

    public void reset()
    {
        stop();
        mapData.setWidth(settings.getMapWidth());
        mapData.setHeight(settings.getMapHeight());
        mapData.regenerate();
        time = 0;
        player.setTime(time);
        player.setBuilding(false);
        player.setBuildingSelection(null);
        player.setEmploymentRate(0f);
        player.setPopulation(0);
        player.setLastIncome(0);
        player.setMoney(settings.getInitialMoney());
        player.setnCommercial(0);
        player.setnResidential(0);
        start();
    }


    /**
     * Useful as reset() is generally run off the main thread due to the cpu heavy map regeneration
     * @param after the task to complete after the game is reset
     */
    public void resetWithListener(Runnable after)
    {
        reset();
        after.run();
    }

    /**
     * Method is invoked on each 'tick' while the game is active. From here the time related game
     * stats are calculated and updated
     */
    public void onUpdate()
    {
        if(player.getMoney() < 0)
        {
            gameOver();
        }
        else
        {
            this.time++;
            player.incTime();
            calculateEarnings();
            calculatePopulation();
            calculateEmploymentRate();
        }
    }

    public void calculateEarnings()
    {
        int income = (int) (player.getPopulation()
                            * (player.getEmploymentRate()
                            * settings.getSalary()
                            * settings.getTaxRate())
                            - settings.getServiceCost());
        player.setLastIncome(income);
        player.setMoney(player.getMoney()+income);

    }

    public void calculatePopulation()
    {
        player.setPopulation(settings.getFamilySize()*player.getnResidential());
    }

    public void calculateEmploymentRate()
    {
        if(player.getPopulation() > 0)
        {
            player.setEmploymentRate(
                    Math.min((float)1,
                            ((float)player.getnCommercial()
                          * (float)settings.getShopSize() / (float)player.getPopulation()))
            );
        }
        else
        {
            player.setEmploymentRate(0);
        }
    }


    /**
     * PurchaseBuilding controls the logic over purchasing a building and updating the models
     * after a successful purchase
     * @param p the player
     * @return purchased A bool indicating whether or not the transaction was successful
     */
    public boolean purchaseBuilding(Player p)
    {
        boolean purchased = true;
        int price = 0;
        int playerMoney = player.getMoney();
        Structure structure = player.getBuildingSelection();
        switch(structure.getType())
        {
            case ROAD:
                price = settings.getRoadBuildingCost();
                if(purchase(playerMoney, price))
                {
                    //no stats need to be updated here
                }
                else
                    purchased = false;
                break;

            case COMMERCIAL:
                price = settings.getCommBuildingCost();
                if(purchase(playerMoney, price))
                    player.setnCommercial(player.getnCommercial()+1);
                else
                    purchased = false;
                break;

            case RESIDENTIAL:
                price = settings.getHouseBuildingCost();
                if(purchase(playerMoney, price))
                    player.setnResidential(player.getnResidential()+1);
                else
                    purchased = false;
                break;

            default:
        }
        return purchased;
    }


    /**
     * Simple function that attempts to purchase a structure if the player has enough funds
     * and reports the success of the transaction
     * @param playerMoney the players money
     * @param price the price of the structure being purchased
     * @return
     */
    public boolean purchase(int playerMoney, int price)
    {
        if(playerMoney >= price)
        {
            player.setMoney(playerMoney-price);
            return true;
        }
        return false;
    }

    public int getCost(Structure.Type type)
    {
        switch(type)
        {
            case ROAD: return settings.getRoadBuildingCost();
            case COMMERCIAL: return settings.getCommBuildingCost();
            case RESIDENTIAL: return settings.getHouseBuildingCost();
            default: return 0;
        }
    }

    public long getTime()
    {
        return time;
    }

    //OBSERVER METHODS------------------------------------------------------------------------------
    public void addObserver(Observer ob){this.observers.add(ob);}

    public interface Observer
    {
        void onUpdate();
    }
}
