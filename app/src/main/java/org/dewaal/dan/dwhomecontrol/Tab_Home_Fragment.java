package org.dewaal.dan.dwhomecontrol;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;


/**
 * A {@link Fragment} subclass.
 * Home uses the following Control Fragments:
 * DoorLock
 * Gate
 * Alarm
 * Lights_Deck
 * Lights_Entrance
 * Lights_Garage
 * Power
 * Security
 * System
 * Weather
 */

public class Tab_Home_Fragment extends Fragment implements View.OnClickListener{

    List<ToggleButton> buttons = new ArrayList();
    private List<View> infoButtons = new ArrayList();
    List<Fragment> fragments = new ArrayList();

    int id;
    boolean unToggle = false;

    SharedPreferences devicePrefs;
    SharedPreferences roomPrefs;
    public static final String devicePrefsName = "DeviceSettings";
    public static final String roomPrefsName = "RoomSettings";

    private int mInterval = 500; // 0.5 seconds
    private Handler mHandler;

    public Tab_Home_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_home, container, false);
        id = R.id.control_fragment_home;
        Resources res = getResources();

        //Shared Preferences
        devicePrefs = getActivity().getSharedPreferences(devicePrefsName, Context.MODE_PRIVATE);
        roomPrefs = getActivity().getSharedPreferences(roomPrefsName, Context.MODE_PRIVATE);

        //Timed tasks
        mHandler = new Handler();
        startRepeatingTask();

        //Button creation
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_door));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_gate));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_alarm_on));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_alarm_off));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_security));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_lights_deck));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_lights_entrance));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_lights_garage));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_lights_all_off));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_power));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_system));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_weather));
        for (ToggleButton b : buttons){
            b.setOnClickListener(this);
            b.setOnCheckedChangeListener(toggleListener);
        }

        //Info Button creation (order is important here!)
        infoButtons.add(view.findViewById(R.id.img_door));
        infoButtons.add(view.findViewById(R.id.img_gate));
        infoButtons.add(view.findViewById(R.id.img_alarm));
        infoButtons.add(view.findViewById(R.id.img_light_tvroom));
        infoButtons.add(view.findViewById(R.id.img_light_entrance));
        infoButtons.add(view.findViewById(R.id.img_light_garage));
        infoButtons.add(view.findViewById(R.id.img_light_deck));
        infoButtons.add(view.findViewById(R.id.img_power));
        infoButtons.add(view.findViewById(R.id.img_system));
        for (View ib : infoButtons){
            ib.setOnClickListener(this);
        }

        //Fragment Creation
//        Log.d("Home","Testing for savedInstanceState.");
//        if (savedInstanceState == null) {
//            Log.d("Home","savedInstanceState = null so creating frags.");
//            Resources res = getResources();
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            addFrag(ft, new Control_Blank_Fragment(), res.getString(R.string.fragTag_blank));
            addFrag(ft, new Control_DoorLock_Fragment(), res.getString(R.string.fragTag_doorlock));
            addFrag(ft, new Control_Gate_Fragment(), res.getString(R.string.fragTag_gate));
            addFrag(ft, new Control_Lights_Deck_Fragment(), res.getString(R.string.fragTag_lights_deck));
            addFrag(ft, new Control_Lights_Entrance_Fragment(), res.getString(R.string.fragTag_lights_entrance));
            addFrag(ft, new Control_Lights_Garage_Fragment(), res.getString(R.string.fragTag_lights_garage));
            addFrag(ft, new Control_Lights_TVRoom_Fragment(), res.getString(R.string.fragTag_lights_tvroom));
            addFrag(ft, new Control_Power_Fragment(), res.getString(R.string.fragTag_power));
            addFrag(ft, new Control_Security_Fragment(), res.getString(R.string.fragTag_security));
            addFrag(ft, new Control_System_Fragment(), res.getString(R.string.fragTag_system));
            addFrag(ft, new Control_Weather_Fragment(), res.getString(R.string.fragTag_weather));
            addFrag(ft, new Control_Alarm_Fragment(), res.getString(R.string.fragTag_alarm));
            ft.commit();
//        }

        return view;
    }

    void timedTask(){
        Resources res = getResources();
        try{
            ((ImageButton)infoButtons.get(0)).setImageResource(devicePrefs.getBoolean(res.getString(R.string.key_FrontDoor_Lock),false) ? R.drawable.homeinfo_doorlock_lock : R.drawable.homeinfo_doorlock_unlock);
            ((ImageButton)infoButtons.get(2)).setImageResource(devicePrefs.getBoolean(res.getString(R.string.key_Alarm_Status),false) ? R.drawable.homeinfo_alarm_on : R.drawable.homeinfo_alarm_off);
            ((ImageButton)infoButtons.get(3)).setImageResource(roomPrefs.getBoolean(res.getString(R.string.key_TVRoom_Lights),false) ? R.drawable.homeinfo_light_tvroom_on : R.drawable.homeinfo_light_tvroom_off);
            ((ImageButton)infoButtons.get(4)).setImageResource(roomPrefs.getBoolean(res.getString(R.string.key_Entrance_Lights),false) ? R.drawable.homeinfo_light_entrance_on : R.drawable.homeinfo_light_entrance_off);
            ((ImageButton)infoButtons.get(5)).setImageResource(roomPrefs.getBoolean(res.getString(R.string.key_Garage_Lights),false) ? R.drawable.homeinfo_light_garage_on : R.drawable.homeinfo_light_garage_off);
            ((ImageButton)infoButtons.get(6)).setImageResource(roomPrefs.getBoolean(res.getString(R.string.key_Deck_Lights),false) ? R.drawable.homeinfo_light_deck_on : R.drawable.homeinfo_light_deck_off);
            TypedArray house_power = res.obtainTypedArray(R.array.house_power);
            int power_level = roomPrefs.getInt(res.getString(R.string.key_House_Power),0);
            ((ImageButton)infoButtons.get(7)).setImageResource(house_power.getResourceId(power_level, 0));
            switch(devicePrefs.getInt(res.getString(R.string.key_Gate_Mode),0)){
                case 0:
                    ((ImageButton)infoButtons.get(1)).setImageResource(R.drawable.homeinfo_gate_closed);
                    break;
                case 1:
                    ((ImageButton)infoButtons.get(1)).setImageResource(R.drawable.homeinfo_gate_closed_locked);
                    break;
                case 2:
                    ((ImageButton)infoButtons.get(1)).setImageResource(R.drawable.homeinfo_gate_ped);
                    break;
                case 3:
                    ((ImageButton)infoButtons.get(1)).setImageResource(R.drawable.homeinfo_gate_open);
                    break;
                case 4:
                    ((ImageButton)infoButtons.get(1)).setImageResource(R.drawable.homeinfo_gate_open_locked);
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

    void unToggle(){
        unToggle = true;
        for (ToggleButton b : buttons){
            b.setChecked(false);
        }
        unToggle = false;
    }

    private void addFrag(FragmentTransaction ft, Fragment fragment, String tag){
        fragments.add(fragment);
        ft.add(id, fragments.get(fragments.size()-1), tag);
        ft.detach(fragments.get(fragments.size()-1));
    }

    private CompoundButton.OnCheckedChangeListener toggleListener = new CompoundButton.OnCheckedChangeListener(){
        boolean avoidRecursions = false;

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
            if(avoidRecursions) return;
            avoidRecursions = true;

            if(!unToggle) {
                //don't allow the un-checking
                if (!isChecked) {
                    buttonView.setChecked(true);
                    avoidRecursions = false;
                    return;
                }

                //un-check the previous checked button
                for (ToggleButton b : buttons) {
                    if (b != buttonView && b.isChecked()) b.setChecked(false);
                }
            }

            avoidRecursions = false;
        }
    };

    private void showFragment(String fragTag){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        for (Fragment fragment : fragments){
            if (fragment.getTag().equalsIgnoreCase(fragTag)){
                ft.attach(fragment);
            } else {
                ft.detach(fragment);
            }
        }
        ft.commit();
    }

    void alarm_on(){
        NumbPad numbPad_arm = new NumbPad();
        numbPad_arm.setAdditionalText("Enter Alarm Code");
        numbPad_arm.show(getActivity(), "Arm Alarm", NumbPad.HIDE_INPUT + NumbPad.NO_DECIMAL,
                new NumbPad.numbPadInterface() {
                    @Override
                    public String numPadInputValue(String value) {
                        if (value.equals("1234")){
                            ((MainActivity)getActivity()).showToast("Success: Arming Alarm");
                            SharedPreferences.Editor deviceEditor = devicePrefs.edit();
                            deviceEditor.putBoolean(getResources().getString(R.string.key_Alarm_Status), true);
                            deviceEditor.apply();
                        } else {
                            ((MainActivity)getActivity()).showToast("Code incorrect");
                        }
                        return null;
                    }

                    @Override
                    public String numPadCanceled() {
                        ((MainActivity)getActivity()).showToast("Cancelled");
                        return null;
                    }
                });
    }

    void alarm_off(){
        NumbPad numbPad_dis = new NumbPad();
        numbPad_dis.setAdditionalText("Alarm code");
        numbPad_dis.show(getActivity(), "Disarm Alarm", NumbPad.HIDE_INPUT + NumbPad.NO_DECIMAL,
                new NumbPad.numbPadInterface() {
                    @Override
                    public String numPadInputValue(String value) {
                        if (value.equals("1234")){
                            ((MainActivity)getActivity()).showToast("Success: Disarming Alarm");
                            SharedPreferences.Editor deviceEditor = devicePrefs.edit();
                            deviceEditor.putBoolean(getResources().getString(R.string.key_Alarm_Status), false);
                            deviceEditor.apply();
                        } else {
                            ((MainActivity)getActivity()).showToast("Code incorrect");
                        }
                        return null;
                    }

                    @Override
                    public String numPadCanceled() {
                        ((MainActivity)getActivity()).showToast("Cancelled");
                        return null;
                    }
                });
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor deviceEditor = devicePrefs.edit();
        Resources res = getResources();
        boolean alarm = devicePrefs.getBoolean(res.getString(R.string.key_Alarm_Status),false);
        switch (v.getId()){
            case R.id.tgl_alarm_on:
                if(!alarm){alarm_on();}
                break;
            case R.id.tgl_alarm_off:
                if(alarm){alarm_off();}
                break;
            case R.id.tgl_lights_all_off:
                deviceEditor.putInt(res.getString(R.string.key_Lights_Deck_Deck), 0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_Deck_Yard), 0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Entrance), 0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Outside), 0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_Garage_Garage), 0);
                break;
        }
        if(v.getId() == R.id.img_door ||
                v.getId() == R.id.img_gate ||
                v.getId() == R.id.img_alarm ||
                v.getId() == R.id.img_light_tvroom ||
                v.getId() == R.id.img_light_entrance ||
                v.getId() == R.id.img_light_garage ||
                v.getId() == R.id.img_light_deck ||
                v.getId() == R.id.img_power ||
                v.getId() == R.id.img_system){
            unToggle();
        }
        deviceEditor.apply();
        try {
            showFragment((v.findViewById(v.getId())).getTag().toString());
        } catch (NullPointerException e){
//            Log.d("Buttons", "Button: \""+((ToggleButton)v.findViewById(v.getId())).getTextOn()+"\" missing tag.");
            Log.d("Home Button", e.toString());
            ((MainActivity) getActivity()).showToast("Not Yet Implemented");
        }
    }
}
