package org.dewaal.dan.dwhomecontrol;


import android.app.TabActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Control_Heatpump_Fragment extends Fragment implements RadioGroup.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener, Switch.OnCheckedChangeListener {

    SharedPreferences devicePrefs;
    public static final String devicePrefsName = "DeviceSettings";

    private int mInterval = 500; // 0.5 seconds
    private Handler mHandler;

    private RadioGroup mode_group, fan_group;
    private SeekBar cur_seekbar, set_seekbar;
//    private ProgressBar cur_progBar;
    private Switch power_save;
    private TextView temp;

    private View view;
    private boolean showSetTemp = false;

    public Control_Heatpump_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_control_heatpump, container, false);
        Resources res = getResources();

        //Shared Preferences
        devicePrefs = getActivity().getSharedPreferences(devicePrefsName, Context.MODE_PRIVATE);

        //Timed tasks
        mHandler = new Handler();
        startRepeatingTask();

        mode_group = (RadioGroup)view.findViewById(R.id.mode_group);
        mode_group.setOnCheckedChangeListener(this);
        fan_group = (RadioGroup)view.findViewById(R.id.fan_group);
        fan_group.setOnCheckedChangeListener(this);

        //Seekbar control
        cur_seekbar = new SeekBar(getActivity());
        cur_seekbar = (SeekBar)view.findViewById(R.id.sld_cur_temp);
        cur_seekbar.setOnSeekBarChangeListener(this);
        cur_seekbar.setEnabled(false);
//        cur_progBar = new ProgressBar(getActivity());
//        cur_progBar = (ProgressBar)view.findViewById(R.id.prg_cur_temp);
        set_seekbar = new SeekBar(getActivity());
        set_seekbar = (SeekBar)view.findViewById(R.id.sld_set_temp);
        set_seekbar.setOnSeekBarChangeListener(this);

        power_save = new Switch(getActivity());
        power_save.setOnCheckedChangeListener(this);

        temp = (TextView)view.findViewById(R.id.txt_display_set_temp);

        return view;
    }

    void timedTask(){
        Resources res = getResources();
        try{
            cur_seekbar.setProgress(devicePrefs.getInt(res.getString(R.string.key_Heatpump_TVRoom_CurTemp), 0)-16);
            set_seekbar.setProgress(devicePrefs.getInt(res.getString(R.string.key_Heatpump_TVRoom_SetTemp), 0)-16);
            switch (devicePrefs.getInt(res.getString(R.string.key_Heatpump_TVRoom_Mode),0)){
                case 0:
                    mode_group.check(R.id.rd_off);
                    break;
                case 1:
                    mode_group.check(R.id.rd_auto);
                    break;
                case 2:
                    mode_group.check(R.id.rd_cool);
                    break;
                case 3:
                    mode_group.check(R.id.rd_heat);
                    break;
            }
            switch (devicePrefs.getInt(res.getString(R.string.key_Heatpump_TVRoom_Fan),0)){
                case 0:
                    fan_group.check(R.id.rd_fan_auto);
                    break;
                case 1:
                    fan_group.check(R.id.rd_fan_low);
                    break;
                case 2:
                    fan_group.check(R.id.rd_fan_med);
                    break;
                case 3:
                    fan_group.check(R.id.rd_fan_high);
                    break;
            }
            power_save.setChecked(devicePrefs.getBoolean(res.getString(R.string.key_Heatpump_TVRoom_PowerSave),false));
            if(showSetTemp == false){
                temp.setText(String.valueOf(devicePrefs.getInt(res.getString(R.string.key_Heatpump_TVRoom_CurTemp), 0)));
            }
            //foo
//            TabHost tabHost = (TabHost)getActivity().findViewById(android.R.id.tabhost);
//            Log.d("Tab", this.getParentFragment().toString());
            //bar
        } catch (Exception e){
            Log.d("Timed Task", e.toString());
        }
    }

    Runnable mStatusChecker = new Runnable(){
        @Override
        public void run(){
            timedTask();
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        temp.setText(String.valueOf(progress+16));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        showSetTemp = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        showSetTemp = false;
        SharedPreferences.Editor deviceEditor = devicePrefs.edit();
        Resources res = getResources();
        deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_SetTemp), seekBar.getProgress()+16);
        deviceEditor.apply();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        SharedPreferences.Editor deviceEditor = devicePrefs.edit();
        Resources res = getResources();
        switch (checkedId){
            case R.id.rd_off:
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Mode), 0);
                break;
            case R.id.rd_auto:
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Mode), 1);
                break;
            case R.id.rd_cool:
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Mode), 2);
                break;
            case R.id.rd_heat:
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Mode), 3);
                break;
            case R.id.rd_fan_auto:
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Fan), 0);
                break;
            case R.id.rd_fan_low:
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Fan), 1);
                break;
            case R.id.rd_fan_med:
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Fan), 2);
                break;
            case R.id.rd_fan_high:
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Fan), 3);
                break;
        }
        deviceEditor.apply();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences.Editor deviceEditor = devicePrefs.edit();
        Resources res = getResources();
        deviceEditor.putBoolean(res.getString(R.string.key_Heatpump_TVRoom_PowerSave), isChecked);
        deviceEditor.apply();
    }
}
