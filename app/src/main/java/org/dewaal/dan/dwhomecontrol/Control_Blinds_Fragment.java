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
public class Control_Blinds_Fragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    SharedPreferences devicePrefs;
    public static final String devicePrefsName = "DeviceSettings";

    private int mInterval = 500; // 0.5 seconds
    private Handler mHandler;

    private List<View> buttons = new ArrayList();
    private SeekBar front_seekbar, rear_seekbar;

    public Control_Blinds_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_blinds, container, false);
        Resources res = getResources();

        //Shared Preferences
        devicePrefs = getActivity().getSharedPreferences(devicePrefsName, Context.MODE_PRIVATE);

        //Timed tasks
        mHandler = new Handler();
        startRepeatingTask();

        //Button creation
        buttons.add(view.findViewById(R.id.btn_blinds_front_close));
        buttons.add(view.findViewById(R.id.btn_blinds_front_open));
        buttons.add(view.findViewById(R.id.btn_blinds_rear_close));
        buttons.add(view.findViewById(R.id.btn_blinds_rear_open));
        buttons.add(view.findViewById(R.id.btn_blinds_save));
        for (View b : buttons){
            b.setOnClickListener(this);
        }

        //Seekbar control
        front_seekbar = new SeekBar(getActivity());
        front_seekbar = (SeekBar)view.findViewById(R.id.sld_front_blinds);
        front_seekbar.setOnSeekBarChangeListener(this);
        rear_seekbar = new SeekBar(getActivity());
        rear_seekbar = (SeekBar)view.findViewById(R.id.sld_rear_blinds);
        rear_seekbar.setOnSeekBarChangeListener(this);

        return view;
    }

    void timedTask(){
        Resources res = getResources();
        try{
            front_seekbar.setProgress(devicePrefs.getInt(res.getString(R.string.key_Blinds_Front_TVRoom), 0));
            rear_seekbar.setProgress(devicePrefs.getInt(res.getString(R.string.key_Blinds_Rear_TVRoom), 0));
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
            case R.id.btn_blinds_front_close:
                front_seekbar.setProgress(0);
                deviceEditor.putInt(res.getString(R.string.key_Blinds_Front_TVRoom), 0);
                break;
            case R.id.btn_blinds_front_open:
                front_seekbar.setProgress(10);
                deviceEditor.putInt(res.getString(R.string.key_Blinds_Front_TVRoom), 10);
                break;
            case R.id.btn_blinds_rear_close:
                rear_seekbar.setProgress(0);
                deviceEditor.putInt(res.getString(R.string.key_Blinds_Rear_TVRoom), 0);
                break;
            case R.id.btn_blinds_rear_open:
                rear_seekbar.setProgress(10);
                deviceEditor.putInt(res.getString(R.string.key_Blinds_Rear_TVRoom), 10);
                break;
            case R.id.btn_blinds_save:
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
            case R.id.sld_front_blinds:
                deviceEditor.putInt(res.getString(R.string.key_Blinds_Front_TVRoom), seekBar.getProgress());
                //TODO: send Network Data
                break;
            case R.id.sld_rear_blinds:
                deviceEditor.putInt(res.getString(R.string.key_Blinds_Rear_TVRoom), seekBar.getProgress());
                //TODO: send Network Data
                break;
        }
        deviceEditor.apply();
    }
}
