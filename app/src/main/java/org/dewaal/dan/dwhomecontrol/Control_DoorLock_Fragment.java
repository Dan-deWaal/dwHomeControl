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
public class Control_DoorLock_Fragment extends Fragment implements View.OnClickListener{

    SharedPreferences devicePrefs;
    public static final String devicePrefsName = "DeviceSettings";

    private int mInterval = 500; // 0.5 seconds
    private Handler mHandler;

    private List<View> buttons = new ArrayList();

    ImageView lock_status;

    public Control_DoorLock_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_doorlock, container, false);

        //Shared Preferences
        devicePrefs = getActivity().getSharedPreferences(devicePrefsName, Context.MODE_PRIVATE);

        //Timed tasks
        mHandler = new Handler();
        startRepeatingTask();

        //Button creation
        buttons.add(view.findViewById(R.id.btn_lock));
        buttons.add(view.findViewById(R.id.btn_unlock));
        for (View b : buttons){
            b.setOnClickListener(this);
        }

        lock_status = (ImageView)view.findViewById(R.id.img_lock_status);
        return view;
    }

    void timedTask(){
        Resources res = getResources();
        try{
            lock_status.setImageResource(devicePrefs.getBoolean(res.getString(R.string.key_FrontDoor_Lock),false) ? R.drawable.stock_lock : R.drawable.stock_lock_open);
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
            case R.id.btn_lock:
                deviceEditor.putBoolean(res.getString(R.string.key_FrontDoor_Lock), true);
                lock_status.setImageResource(R.drawable.stock_lock);
                break;
            case R.id.btn_unlock:
                deviceEditor.putBoolean(res.getString(R.string.key_FrontDoor_Lock), false);
                lock_status.setImageResource(R.drawable.stock_lock_open);
        }
        deviceEditor.apply();
    }
}
