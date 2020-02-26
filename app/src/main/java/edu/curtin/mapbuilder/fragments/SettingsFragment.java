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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.curtin.mapbuilder.R;
import edu.curtin.mapbuilder.model.GameSettings;

/**
 * SettingsFragment sets up a recycler view to display all the GameSetting fields
 * Each ViewHolder is provided an edit text to enable modification of the GameSettings
 * @author Will
 */

public class SettingsFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        GameSettings settings = GameSettings.get();

        RecyclerView rv = view.findViewById(R.id.recyclerView_settings);
        rv.setLayoutManager(
                new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        SettingsAdapter sa = new SettingsAdapter(settings);

        rv.setAdapter(sa);


        return view;
    }

    /**
     * This adapter is responsible for created multiple kinds of ViewHolders depending on the
     * data type of setting which adds a bit extra complexity to its responsibilities.
     *
     * Currently there are two types of ViewHolder, one for discrete value settings
     * and another for real value settings. To determine which to create the recycler view
     * type field has been utilized.
     *
     * In addition to this producer and consumer interfaces have been created to simulate
     * function pointer style variable that allow the view holders to communicate information
     * back to the settings dynamically.
     */
    public class SettingsAdapter extends RecyclerView.Adapter
    {
        public static final int INT_TYPE = 0;
        public static final int FLOAT_TYPE = 1;

        private GameSettings settings;
        private ArrayList<SettingHolder<IntConsumer, IntProducer>> intSettings;
        private ArrayList<SettingHolder<FloatConsumer, FloatProducer>> floatSettings;

        //Constructor extracts the settings from GameSettings and stores them into a list
        public SettingsAdapter(GameSettings settings)
        {
            this.settings = settings;
            this.intSettings = new ArrayList<>(10);
            this.intSettings.add(new SettingHolder<>("Map Width",
                    settings::setMapWidth,settings::getMapWidth));
            this.intSettings.add(new SettingHolder<>("Map Height",
                    settings::setMapHeight, settings::getMapHeight));
            this.intSettings.add(new SettingHolder<>("Initial Money",
                    settings::setInitialMoney,settings::getInitialMoney));
            this.intSettings.add(new SettingHolder<>("Family Size",
                    settings::setFamilySize,settings::getFamilySize));
            this.intSettings.add(new SettingHolder<>("Shop Size",
                    settings::setShopSize,settings::getShopSize));
            this.intSettings.add(new SettingHolder<>("Salary",
                    settings::setSalary,settings::getSalary));
            this.intSettings.add(new SettingHolder<>("Service Cost",
                    settings::setServiceCost,settings::getServiceCost));
            this.intSettings.add(new SettingHolder<>("Residential Building Cost",
                    settings::setHouseBuildingCost,settings::getHouseBuildingCost));
            this.intSettings.add(new SettingHolder<>("Commercial Building Cost",
                    settings::setCommBuildingCost,settings::getCommBuildingCost));
            this.intSettings.add(new SettingHolder<>("Road Cost",
                    settings::setRoadBuildingCost,settings::getRoadBuildingCost));

            this.floatSettings = new ArrayList<>(2);
            this.floatSettings.add(new SettingHolder<>("Tax Rate",
                    settings::setTaxRate,settings::getTaxRate));
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            LayoutInflater li = LayoutInflater.from(getActivity());
            if(viewType == INT_TYPE)
                return new IntegerSettingsViewHolder(li,parent);
            else
                return new FloatSettingsViewHolder(li,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
        {
            if(position < intSettings.size())
            {
                ((IntegerSettingsViewHolder)holder)
                    .bind(intSettings.get(position).getName(),
                            intSettings.get(position).getConsumer(),
                            intSettings.get(position).getProducer().getInteger());
            }
            else
            {
                position -= intSettings.size();
                ((FloatSettingsViewHolder)holder)
                        .bind(floatSettings.get(position).getName(),
                                floatSettings.get(position).getConsumer(),
                                floatSettings.get(position).getProducer().getFloat());
            }
        }

        @Override
        public int getItemCount()
        {
            return intSettings.size()+floatSettings.size();
        }

        @Override
        public int getItemViewType(int position)
        {
            //Simply checks if the setting is from the first or second list,
            //IF in the first list -> viewholder is of type INT
            //ELSE in the second list -> viewholder is of type FLOAT
            if(position < intSettings.size())
            {
                return INT_TYPE;
            }
            else
            {
                return FLOAT_TYPE;
            }
        }
    }

    //VIEW HOLDERS----------------------------------------------------------------------------------
    public class IntegerSettingsViewHolder extends RecyclerView.ViewHolder
    {
        private TextView settingsName;
        private EditText settingsInput;
        private IntConsumer del;

        public IntegerSettingsViewHolder(LayoutInflater li, ViewGroup parent)
        {
            super(li.inflate(R.layout.viewholder_setting_int, parent, false));

            this.settingsName = itemView.findViewById(R.id.setting_name);
            this.settingsInput = itemView.findViewById(R.id.setting_input);
            this.settingsInput.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                    //NO PURP:
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                    if(del != null)
                    {
                        try
                        {
                            IntegerSettingsViewHolder.this
                                    .del.setInteger(Integer.parseInt(charSequence.toString()));
                        }
                        catch (NumberFormatException e)
                        {
                            // no op
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable)
                {
                    //NO PURP:
                }
            });
            settingsInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }

        /**
         * @param settingName Used for displaying which setting is being changed
         * @param del 'Function pointer' used to change the setting field
         * @param currValue the value of the current setting
         */
        public void bind(String settingName, IntConsumer del, int currValue)
        {
            this.del = del;
            this.settingsInput.setText(String.valueOf(currValue));
            this.settingsName.setText(settingName);
        }

    }

    public class FloatSettingsViewHolder extends RecyclerView.ViewHolder
    {
        private TextView settingsName;
        private EditText settingsInput;
        private FloatConsumer del;

        public FloatSettingsViewHolder(LayoutInflater li, ViewGroup parent)
        {
            super(li.inflate(R.layout.viewholder_settings_float, parent, false));

            this.settingsName = itemView.findViewById(R.id.setting_name);
            this.settingsInput = itemView.findViewById(R.id.setting_input);
            this.settingsInput.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                    //NO PURP:
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                    if(del != null)
                    {
                        try
                        {
                            FloatSettingsViewHolder.this
                                    .del.setFloat(Float.parseFloat(charSequence.toString()));
                        }
                        catch (NumberFormatException e)
                        {
                            //SUP
                            //Should occur as the EditText enforces numeric input
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable)
                {
                    //NO PURP:
                }
            });
            settingsInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }


        /**
         * @param settingName Used for displaying which setting is being changed
         * @param del 'Function pointer' used to change the setting field
         * @param f the value of the current setting
         */
        public void bind(String settingName, FloatConsumer del, float f)
        {
            this.del = del;
            this.settingsInput.setText(String.valueOf(f));
            this.settingsName.setText(settingName);
        }

    }

    //SETTINGS HOLDER-------------------------------------------------------------------------------

    /**
     * @param <T> the consumer method
     * @param <P> the producer method
     */
    //Class allows you to store simple consumer and producer method references
    public class SettingHolder<T,P>
    {
        //FIELDS:
        private String name;
        private T consumer;
        private P producer;

        public SettingHolder(String name, T consumer, P producer)
        {
            this.name = name;
            this.consumer = consumer;
            this.producer = producer;
        }

        public String getName()
        {
            return name;
        }

        public T getConsumer() {return consumer;}
        public P getProducer()
        {
            return producer;
        }
    }

    //NESTED INTERFACES-----------------------------------------------------------------------------

    //The following interfaces are required for referencing getter and setter methods as variables
    // for the Settings holder class
    private interface IntProducer{ int getInteger();}
    private interface IntConsumer{ void setInteger(int f);}

    private interface FloatProducer{ float getFloat();}
    private interface FloatConsumer{ void setFloat(float f);}

}
