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
public class Control_Lights_Entrance_Fragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    SharedPreferences devicePrefs;
    public static final String devicePrefsName = "DeviceSettings";

    private int mInterval = 500; // 0.5 seconds
    private Handler mHandler;

    private List<View> buttons = new ArrayList();
    private SeekBar entrance_seekbar, outside_seekbar;

    public Control_Lights_Entrance_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_lights_entrance, container, false);
        Resources res = getResources();

        //Shared Preferences
        devicePrefs = getActivity().getSharedPreferences(devicePrefsName, Context.MODE_PRIVATE);

        //Timed tasks
        mHandler = new Handler();
        startRepeatingTask();

        //Button creation
        buttons.add(view.findViewById(R.id.btn_entrance_lights_off));
        buttons.add(view.findViewById(R.id.btn_entrance_lights_on));
        buttons.add(view.findViewById(R.id.btn_outside_lights_off));
        buttons.add(view.findViewById(R.id.btn_outside_lights_on));
        for (View b : buttons){
            b.setOnClickListener(this);
        }

        //Seekbar control
        entrance_seekbar = new SeekBar(getActivity());
        entrance_seekbar = (SeekBar)view.findViewById(R.id.sld_entrance_lights);
        entrance_seekbar.setOnSeekBarChangeListener(this);
        outside_seekbar = new SeekBar(getActivity());
        outside_seekbar = (SeekBar)view.findViewById(R.id.sld_outside_lights);
        outside_seekbar.setOnSeekBarChangeListener(this);

        return view;
    }

    void timedTask(){
        Resources res = getResources();
        try{
            entrance_seekbar.setProgress(devicePrefs.getInt(res.getString(R.string.key_Lights_Entrance_Entrance), 0));
            outside_seekbar.setProgress(devicePrefs.getInt(res.getString(R.string.key_Lights_Entrance_Outside), 0));
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
            case R.id.btn_entrance_lights_off:
                entrance_seekbar.setProgress(0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Entrance), 0);
                //TODO: send Network Data
                break;
            case R.id.btn_entrance_lights_on:
                entrance_seekbar.setProgress(100);
                deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Entrance), 100);
                //TODO: send Network Data
                break;
            case R.id.btn_outside_lights_off:
                outside_seekbar.setProgress(0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Outside), 0);
                //TODO: send Network Data
                break;
            case R.id.btn_outside_lights_on:
                outside_seekbar.setProgress(1);
                deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Outside), 1);
                //TODO: send Network Data
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
            case R.id.sld_entrance_lights:
                deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Entrance), seekBar.getProgress());
                //TODO: send Network Data
                break;
            case R.id.sld_outside_lights:
                deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Outside), seekBar.getProgress());
                //TODO: send Network Data
                break;
        }
        deviceEditor.apply();
    }
}
