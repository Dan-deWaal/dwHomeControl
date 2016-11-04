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
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Control_Gate_Fragment extends Fragment implements View.OnClickListener{

    SharedPreferences devicePrefs;
    public static final String devicePrefsName = "DeviceSettings";

    private int mInterval = 500; // 0.5 seconds
    private Handler mHandler;

    private List<View> buttons = new ArrayList();

    ImageView gate_mode;

    public Control_Gate_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_gate, container, false);

        //Shared Preferences
        devicePrefs = getActivity().getSharedPreferences(devicePrefsName, Context.MODE_PRIVATE);

        //Timed tasks
        mHandler = new Handler();
        startRepeatingTask();

        //Button creation
        buttons.add(view.findViewById(R.id.btn_gate_car));
        buttons.add(view.findViewById(R.id.btn_gate_close_lock));
        buttons.add(view.findViewById(R.id.btn_gate_open_lock));
        buttons.add(view.findViewById(R.id.btn_gate_ped));
        buttons.add(view.findViewById(R.id.btn_gate_close));
        for (View b : buttons){
            b.setOnClickListener(this);
        }

        gate_mode = (ImageView)view.findViewById(R.id.img_gate_status);
        return view;
    }

    void timedTask(){
        Resources res = getResources();
        try{
            switch (devicePrefs.getInt(res.getString(R.string.key_Gate_Mode),0)){
                case 0:
                    gate_mode.setImageResource(R.drawable.gate_closed);
                    break;
                case 1:
                    gate_mode.setImageResource(R.drawable.gate_closed_locked);
                    break;
                case 2:
                    gate_mode.setImageResource(R.drawable.gate_ped);
                    break;
                case 3:
                    gate_mode.setImageResource(R.drawable.gate_open);
                    break;
                case 4:
                    gate_mode.setImageResource(R.drawable.gate_open_locked);
                    break;
            }
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
        Resources res = getResources();
        SharedPreferences.Editor deviceEditor = devicePrefs.edit();
        switch (v.getId()){
            case R.id.btn_gate_close:
                deviceEditor.putInt(res.getString(R.string.key_Gate_Mode), 0);
                break;
            case R.id.btn_gate_close_lock:
                deviceEditor.putInt(res.getString(R.string.key_Gate_Mode), 1);
                break;
            case R.id.btn_gate_ped:
                deviceEditor.putInt(res.getString(R.string.key_Gate_Mode), 2);
                break;
            case R.id.btn_gate_car:
                deviceEditor.putInt(res.getString(R.string.key_Gate_Mode), 3);
                break;
            case R.id.btn_gate_open_lock:
                deviceEditor.putInt(res.getString(R.string.key_Gate_Mode), 4);
                break;
        }
        deviceEditor.apply();
    }
}
