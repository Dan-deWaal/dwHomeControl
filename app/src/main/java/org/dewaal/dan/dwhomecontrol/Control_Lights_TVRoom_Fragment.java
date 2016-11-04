package org.dewaal.dan.dwhomecontrol;


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
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Control_Lights_TVRoom_Fragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    SharedPreferences devicePrefs;
    public static final String devicePrefsName = "DeviceSettings";

    private int mInterval = 500; // 0.5 seconds
    private Handler mHandler;

    private List<View> buttons = new ArrayList();
    private SeekBar front_seekbar, rear_seekbar, feature_seekbar;

    public Control_Lights_TVRoom_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_lights_tvroom, container, false);
        Resources res = getResources();

        //Shared Preferences
        devicePrefs = getActivity().getSharedPreferences(devicePrefsName, Context.MODE_PRIVATE);

        //Timed tasks
        mHandler = new Handler();
        startRepeatingTask();

        //Button creation
        buttons.add(view.findViewById(R.id.btn_front_lights_off));
        buttons.add(view.findViewById(R.id.btn_front_lights_on));
        buttons.add(view.findViewById(R.id.btn_rear_lights_off));
        buttons.add(view.findViewById(R.id.btn_rear_lights_on));
        buttons.add(view.findViewById(R.id.btn_feature_lights_off));
        buttons.add(view.findViewById(R.id.btn_feature_lights_on));
        buttons.add(view.findViewById(R.id.btn_tvroom_lights_save));
        for (View b : buttons){
            b.setOnClickListener(this);
        }

        //Seekbar control
        front_seekbar = new SeekBar(getActivity());
        front_seekbar = (SeekBar)view.findViewById(R.id.sld_front_lights);
        front_seekbar.setOnSeekBarChangeListener(this);
        rear_seekbar = new SeekBar(getActivity());
        rear_seekbar = (SeekBar)view.findViewById(R.id.sld_rear_lights);
        rear_seekbar.setOnSeekBarChangeListener(this);
        feature_seekbar = new SeekBar(getActivity());
        feature_seekbar = (SeekBar)view.findViewById(R.id.sld_feature_lights);
        feature_seekbar.setOnSeekBarChangeListener(this);

        return view;
    }

    void timedTask(){
        Resources res = getResources();
        try{
            front_seekbar.setProgress(devicePrefs.getInt(res.getString(R.string.key_Lights_TVRoom_Front), 0));
            rear_seekbar.setProgress(devicePrefs.getInt(res.getString(R.string.key_Lights_TVRoom_Rear), 0));
            feature_seekbar.setProgress(devicePrefs.getInt(res.getString(R.string.key_Lights_TVRoom_Feature), 0));
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
    public void onClick(View v) {
        SharedPreferences.Editor deviceEditor = devicePrefs.edit();
        Resources res = getResources();
        switch (v.getId()){
            case R.id.btn_front_lights_off:
                front_seekbar.setProgress(0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Front), 0);
                break;
            case R.id.btn_front_lights_on:
                front_seekbar.setProgress(100);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Front), 100);
                break;
            case R.id.btn_rear_lights_off:
                rear_seekbar.setProgress(0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Rear), 0);
                break;
            case R.id.btn_rear_lights_on:
                rear_seekbar.setProgress(100);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Rear), 100);
                break;
            case R.id.btn_feature_lights_off:
                feature_seekbar.setProgress(0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Feature), 0);
                break;
            case R.id.btn_feature_lights_on:
                feature_seekbar.setProgress(100);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Feature), 100);
                break;
            case R.id.btn_tvroom_lights_save:
                //Dan saves!
                break;
        }
        deviceEditor.apply();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        SharedPreferences.Editor deviceEditor = devicePrefs.edit();
        Resources res = getResources();
        switch(seekBar.getId()){
            case R.id.sld_front_lights:
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Front), seekBar.getProgress());
                //TODO: send Network Data
                break;
            case R.id.sld_rear_lights:
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Rear), seekBar.getProgress());
                //TODO: send Network Data
                break;
            case R.id.sld_feature_lights:
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Feature), seekBar.getProgress());
                //TODO: send Network Data
                break;
        }
        deviceEditor.apply();
    }
}
