package org.dewaal.dan.dwhomecontrol;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;


/**
 * A {@link Fragment} subclass.
 * Entrance uses the following Control Fragments:
 * Blinds
 * Lights_Entrance
 * DoorLock
 * Gate
 * Heatpump
 */

public class Tab_Entrance_Fragment extends Fragment implements View.OnClickListener{

    private MainActivity mainActivity = (MainActivity)getActivity();
    private AlertDialog alertDialog;

    private List<ToggleButton> buttons = new ArrayList();
    private List<View> infoButtons = new ArrayList();
    List<Fragment> fragments = new ArrayList();

    int id;
    boolean unToggle = false;
    TextView textVol;

    SharedPreferences devicePrefs;
    SharedPreferences roomPrefs;
    public static final String devicePrefsName = "DeviceSettings";
    public static final String roomPrefsName = "RoomSettings";

    private int mInterval = 500; // 0.5 seconds
    private Handler mHandler;

    public Tab_Entrance_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_entrance, container, false);
        id = R.id.control_fragment_entrance;
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
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_light_on));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_lights_off));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_heatpump_auto));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_heatpump_heat));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_heatpump_cool));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_heatpump_off));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_room_off));
        for (ToggleButton b : buttons){
            b.setOnClickListener(this);
            b.setOnCheckedChangeListener(toggleListener);
        }

        //Info Button creation (order is important here!)
        infoButtons.add(view.findViewById(R.id.btn_temp));
        infoButtons.add(view.findViewById(R.id.img_door));
        infoButtons.add(view.findViewById(R.id.img_gate));
        infoButtons.add(view.findViewById(R.id.img_alarm));
        infoButtons.add(view.findViewById(R.id.img_light));
        infoButtons.add(view.findViewById(R.id.img_power));
        for (View ib : infoButtons){
            ib.setOnClickListener(this);
        }

        //Fragment Creation
//        Log.d("Entrance","Testing for savedInstanceState.");
//        if (savedInstanceState == null) {
//            Log.d("Entrance","savedInstanceState = null so creating frags.");
//            Resources res = getResources();
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            addFrag(ft, new Control_Blank_Fragment(), res.getString(R.string.fragTag_blank));
            addFrag(ft, new Control_Alarm_Fragment(), res.getString(R.string.fragTag_alarm));
            addFrag(ft, new Control_Blinds_Fragment(), res.getString(R.string.fragTag_blinds));
            addFrag(ft, new Control_Lights_Entrance_Fragment(), res.getString(R.string.fragTag_lights_entrance));
            addFrag(ft, new Control_DoorLock_Fragment(), res.getString(R.string.fragTag_doorlock));
            addFrag(ft, new Control_Gate_Fragment(), res.getString(R.string.fragTag_gate));
            addFrag(ft, new Control_Heatpump_Fragment(), res.getString(R.string.fragTag_heatpump));
            ft.commit();
//        }

        //Room Off dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Are you sure you want to shut the room off?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity)getActivity()).showToast("Room turning off..");
                roomOff();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                unToggle();
                dialog.cancel();
            }
        });
        alertDialog = alertDialogBuilder.create();

        return view;
    }

    void timedTask(){
        Resources res = getResources();
        SharedPreferences.Editor roomEditor = roomPrefs.edit();
        try{
            int cur_temp = devicePrefs.getInt(res.getString(R.string.key_Heatpump_Entrance_CurTemp),res.getInteger(R.integer.cur_temp));
            int set_temp = devicePrefs.getInt(res.getString(R.string.key_Heatpump_Entrance_SetTemp),res.getInteger(R.integer.cur_temp));
            int mode = devicePrefs.getInt(res.getString(R.string.key_Heatpump_Entrance_Mode),0);
            String temp_guage = "--/--";
            Drawable icon = null;
            switch (mode){
                case 0:
                    ((Button)infoButtons.get(0)).setText(cur_temp + "°");
                    ((Button)infoButtons.get(0)).setCompoundDrawables(null, null, null, null);
                    break;
                case 1:
                    ((Button)infoButtons.get(0)).setText(cur_temp + "°/" + set_temp + "°");
                    icon = res.getDrawable(R.drawable.icon_green);
                    icon.setBounds(0,0,6,6);
                    ((Button)infoButtons.get(0)).setCompoundDrawables(null, null, icon, null);
                    break;
                case 2:
                    ((Button)infoButtons.get(0)).setText(cur_temp + "°/" + set_temp + "°");
                    icon = res.getDrawable(R.drawable.icon_blue);
                    icon.setBounds(0,0,6,6);
                    ((Button)infoButtons.get(0)).setCompoundDrawables(null, null, icon, null);
                    break;
                case 3:
                    ((Button)infoButtons.get(0)).setText(cur_temp + "°/" + set_temp + "°");
                    icon = res.getDrawable(R.drawable.icon_red);
                    icon.setBounds(0,0,6,6);
                    ((Button)infoButtons.get(0)).setCompoundDrawables(null, null, icon, null);
                    break;
            }
            ((ImageButton)infoButtons.get(4)).setImageResource(roomPrefs.getBoolean(res.getString(R.string.key_Entrance_Lights),false) ? R.drawable.roominfo_light_on : R.drawable.roominfo_light_off);
            TypedArray room_power = res.obtainTypedArray(R.array.room_power);
            int power_level = roomPrefs.getInt(res.getString(R.string.key_Entrance_Power),0);
            ((ImageButton)infoButtons.get(5)).setImageResource(room_power.getResourceId(power_level, 0));
            ((ImageButton)infoButtons.get(1)).setImageResource(devicePrefs.getBoolean(res.getString(R.string.key_FrontDoor_Lock),false) ? R.drawable.homeinfo_doorlock_lock : R.drawable.homeinfo_doorlock_unlock);
            ((ImageButton)infoButtons.get(3)).setImageResource(devicePrefs.getBoolean(res.getString(R.string.key_Alarm_Status),false) ? R.drawable.homeinfo_alarm_on : R.drawable.homeinfo_alarm_off);
            switch(devicePrefs.getInt(res.getString(R.string.key_Gate_Mode),0)){
                case 0:
                    ((ImageButton)infoButtons.get(2)).setImageResource(R.drawable.homeinfo_gate_closed);
                    break;
                case 1:
                    ((ImageButton)infoButtons.get(2)).setImageResource(R.drawable.homeinfo_gate_closed_locked);
                    break;
                case 2:
                    ((ImageButton)infoButtons.get(2)).setImageResource(R.drawable.homeinfo_gate_ped);
                    break;
                case 3:
                    ((ImageButton)infoButtons.get(2)).setImageResource(R.drawable.homeinfo_gate_open);
                    break;
                case 4:
                    ((ImageButton)infoButtons.get(2)).setImageResource(R.drawable.homeinfo_gate_open_locked);
                    break;
            }
        } catch (Exception e){
            Log.d("Timed Task", e.toString());
        } finally {
            roomEditor.apply();
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

    void unToggle(){
        unToggle = true;
        for (ToggleButton b : buttons){
            b.setChecked(false);
        }
        unToggle = false;
    }

    public void roomOff(){
        Resources res = getResources();
        SharedPreferences.Editor roomEditor = roomPrefs.edit();
        SharedPreferences.Editor deviceEditor = devicePrefs.edit();
        deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Entrance), 0);
        deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Outside), 0);
        //TODO: send Network Data
        deviceEditor.putInt(res.getString(R.string.key_Heatpump_Entrance_Mode), 0);
        deviceEditor.putInt(res.getString(R.string.key_Heatpump_Entrance_Fan), 0);
        //TODO: send Network Data
        roomEditor.putBoolean(res.getString(R.string.key_Entrance_RoomOff), true);
        showFragment(res.getString(R.string.fragTag_blank));
        roomEditor.commit();
        deviceEditor.commit();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor deviceEditor = devicePrefs.edit();
        SharedPreferences.Editor roomEditor = roomPrefs.edit();
        Resources res = getResources();
        boolean fragChange = false;
        int temp;
        switch (v.getId()){
            case R.id.tgl_room_off:
                if(roomPrefs.getBoolean(res.getString(R.string.key_Entrance_RoomOff), false) == false){
                    alertDialog.show();
                }
                break;
            case R.id.tgl_door:
                fragChange = true;
                break;
            case R.id.tgl_gate:
                fragChange = true;
                break;
            case R.id.tgl_alarm_on:
                fragChange = true;
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
                break;
            case R.id.tgl_alarm_off:
                fragChange = true;
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
                break;
            case R.id.tgl_light_on:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Entrance), 100);
                deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Outside), 1);
                break;
            case R.id.tgl_lights_off:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Entrance), 0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Outside), 0);
                break;
            case R.id.tgl_heatpump_auto:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_Entrance_Mode), 1);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_Entrance_Fan), 0);
                break;
            case R.id.tgl_heatpump_heat:
                fragChange = true;
                temp = devicePrefs.getInt(res.getString(R.string.key_Heatpump_Entrance_CurTemp),res.getInteger(R.integer.cur_temp));
                temp = Math.min(temp+3, 36);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_Entrance_Mode), 3);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_Entrance_Fan), 0);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_Entrance_SetTemp), temp);
                break;
            case R.id.tgl_heatpump_cool:
                fragChange = true;
                temp = devicePrefs.getInt(res.getString(R.string.key_Heatpump_Entrance_CurTemp),res.getInteger(R.integer.cur_temp));
                temp = Math.max(temp-3, 16);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_Entrance_Mode), 2);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_Entrance_Fan), 0);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_Entrance_SetTemp), temp);
                break;
            case R.id.tgl_heatpump_off:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_Entrance_Mode), 0);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_Entrance_Fan), 0);
                break;
            case R.id.btn_temp:
                fragChange = true;
                unToggle();
                break;
            case R.id.img_door:
                fragChange = true;
                unToggle();
                break;
            case R.id.img_gate:
                fragChange = true;
                unToggle();
                break;
            case R.id.img_alarm:
                fragChange = true;
                unToggle();
                break;
            case R.id.img_light:
                fragChange = true;
                unToggle();
                break;
            case R.id.img_power:
                fragChange = true;
                break;
        }
        deviceEditor.apply();
        roomEditor.apply();
        if (fragChange) {
            try {
                showFragment((v.findViewById(v.getId())).getTag().toString());
            } catch (NullPointerException e) {
//                Log.d("Buttons", "Button: \"" + ((ToggleButton) v.findViewById(v.getId())).getTextOn() + "\" missing tag.");
                Log.d("TVRoom Button", e.toString());
                ((MainActivity) getActivity()).showToast("Not Yet Implemented");
            }
        }
    }
}
