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
import android.support.v4.content.ContextCompat;
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
 * TVRoom uses the following Control Fragments:
 * Blinds
 * DVD
 * Game
 * TV
 * CD
 * Lights_TVRoom
 * Heatpump
 * Radio
 */

public class Tab_TVRoom_Fragment extends Fragment implements View.OnClickListener{

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

    public Tab_TVRoom_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_tvroom, container, false);
        id = R.id.control_fragment_tv;
        Resources res = getResources();

        //Shared Preferences
        devicePrefs = getActivity().getSharedPreferences(devicePrefsName, Context.MODE_PRIVATE);
        roomPrefs = getActivity().getSharedPreferences(roomPrefsName, Context.MODE_PRIVATE);

        //Timed tasks
        mHandler = new Handler();
        startRepeatingTask();

        //Button creation
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_tv));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_dvd));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_game));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_radio));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_cd));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_media_off));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_lights_all_on));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_lights_tv));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_lights_reading));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_lights_off));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_blinds_down));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_blinds_10));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_blinds_50));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_blinds_open));
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
        infoButtons.add(view.findViewById(R.id.img_light));
        infoButtons.add(view.findViewById(R.id.img_blinds));
        infoButtons.add(view.findViewById(R.id.img_power));
        infoButtons.add(view.findViewById(R.id.btn_avname));
        infoButtons.add(view.findViewById(R.id.img_sound));
        infoButtons.add(view.findViewById(R.id.img_vol_down));
        infoButtons.add(view.findViewById(R.id.img_vol_up));
        for (View ib : infoButtons){
            ib.setOnClickListener(this);
        }
        textVol = (TextView)view.findViewById(R.id.txt_vol);
        textVol.setText("Vol: "+roomPrefs.getInt(res.getString(R.string.key_TVRoom_Volume),0));

        //Fragment Creation
//        Log.d("TVRoom","Testing for savedInstanceState.");
//        if (savedInstanceState == null) {
//            Log.d("TVRoom","savedInstanceState = null so creating frags.");
//            Resources res = getResources();
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            addFrag(ft, new Control_Blank_Fragment(), res.getString(R.string.fragTag_blank));
            addFrag(ft, new Control_TV_Fragment(), res.getString(R.string.fragTag_tv));
            addFrag(ft, new Control_DVD_Fragment(), res.getString(R.string.fragTag_dvd));
            addFrag(ft, new Control_Blinds_Fragment(), res.getString(R.string.fragTag_blinds));
            addFrag(ft, new Control_Game_Fragment(), res.getString(R.string.fragTag_game));
            addFrag(ft, new Control_CD_Fragment(), res.getString(R.string.fragTag_cd));
            addFrag(ft, new Control_Lights_TVRoom_Fragment(), res.getString(R.string.fragTag_lights_tvroom));
            addFrag(ft, new Control_Heatpump_Fragment(), res.getString(R.string.fragTag_heatpump));
            addFrag(ft, new Control_Radio_Fragment(), res.getString(R.string.fragTag_radio));
            ft.commit();
//        }

        //Room Off dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Are you sure you want to shut the room off?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                ((MainActivity)getActivity()).showToast("Room turning off..");
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
            int cur_temp = devicePrefs.getInt(res.getString(R.string.key_Heatpump_TVRoom_CurTemp),res.getInteger(R.integer.cur_temp));
            int set_temp = devicePrefs.getInt(res.getString(R.string.key_Heatpump_TVRoom_SetTemp),res.getInteger(R.integer.cur_temp));
            int mode = devicePrefs.getInt(res.getString(R.string.key_Heatpump_TVRoom_Mode),0);
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
            ((ImageButton)infoButtons.get(1)).setImageResource(roomPrefs.getBoolean(res.getString(R.string.key_TVRoom_Lights),false) ? R.drawable.roominfo_light_on : R.drawable.roominfo_light_off);
            TypedArray room_power = res.obtainTypedArray(R.array.room_power);
            int power_level = roomPrefs.getInt(res.getString(R.string.key_TVRoom_Power),0);
            int blinds_front = devicePrefs.getInt(res.getString(R.string.key_Blinds_Front_TVRoom), 0);
            int blinds_rear = devicePrefs.getInt(res.getString(R.string.key_Blinds_Rear_TVRoom), 0);
            int blinds_icon = 0;
            if (blinds_front > 0 || blinds_rear > 0){blinds_icon = 1;}
            if (blinds_front == 10 || blinds_rear == 10){blinds_icon = 2;}
            switch (blinds_icon){
                case 0:
                    ((ImageButton)infoButtons.get(2)).setImageResource(R.drawable.roominfo_blinds_down);
                    break;
                case 1:
                    ((ImageButton)infoButtons.get(2)).setImageResource(R.drawable.roominfo_blinds_50);
                    break;
                case 2:
                    ((ImageButton)infoButtons.get(2)).setImageResource(R.drawable.roominfo_blinds_open);
                    break;
            }
            ((ImageButton)infoButtons.get(3)).setImageResource(room_power.getResourceId(power_level, 0));
            ((Button)infoButtons.get(4)).setText(roomPrefs.getString(res.getString(R.string.key_TVRoom_AVName),""));
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

    public void showFragment(String fragTag){
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
        deviceEditor.putBoolean(res.getString(R.string.key_DVD_TVRoom), false);
        deviceEditor.putBoolean(res.getString(R.string.key_TV_TVRoom), false);
        deviceEditor.putBoolean(res.getString(R.string.key_Radio_TVRoom), false);
        deviceEditor.putBoolean(res.getString(R.string.key_CD_TVRoom), false);
        deviceEditor.putBoolean(res.getString(R.string.key_TV_TVRoom), false);
        roomEditor.putBoolean(res.getString(R.string.key_TVRoom_Media), false);
        roomEditor.putString(res.getString(R.string.key_TVRoom_AVName), "");
        //TODO: send Network Data
        deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Front), 0);
        deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Rear), 0);
        deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Feature), 0);
        //TODO: send Network Data
        deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Mode), 0);
        deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Fan), 0);
        //TODO: send Network Data
        roomEditor.putBoolean(res.getString(R.string.key_TVRoom_RoomOff), true);
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
        int vol, temp;
        switch (v.getId()){
            case R.id.tgl_room_off:
                if(roomPrefs.getBoolean(res.getString(R.string.key_TVRoom_RoomOff), false) == false){
                    alertDialog.show();
                }
                break;
            case R.id.tgl_tv:
                fragChange = true;
                deviceEditor.putBoolean(res.getString(R.string.key_Sky_TVRoom), true);
                deviceEditor.putBoolean(res.getString(R.string.key_TV_TVRoom), true);
                roomEditor.putBoolean(res.getString(R.string.key_TVRoom_Media), true);
                roomEditor.putString(res.getString(R.string.key_TVRoom_AVName), "MySkyHD");
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Front), 0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Rear), 30);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Feature), 100);
                //TODO: send Network Data
                break;
            case R.id.tgl_dvd:
                fragChange = true;
                deviceEditor.putBoolean(res.getString(R.string.key_DVD_TVRoom), true);
                deviceEditor.putBoolean(res.getString(R.string.key_TV_TVRoom), true);
                roomEditor.putBoolean(res.getString(R.string.key_TVRoom_Media), true);
                roomEditor.putString(res.getString(R.string.key_TVRoom_AVName), "DVD");
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Front), 0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Rear), 30);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Feature), 100);
                //TODO: send Network Data
                break;
            case R.id.tgl_game:
                fragChange = true;
                deviceEditor.putBoolean(res.getString(R.string.key_TV_TVRoom), true);
                roomEditor.putBoolean(res.getString(R.string.key_TVRoom_Media), true);
                roomEditor.putString(res.getString(R.string.key_TVRoom_AVName), "XBox");
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Front), 0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Rear), 30);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Feature), 100);
                //TODO: send Network Data
                break;
            case R.id.tgl_radio:
                fragChange = true;
                deviceEditor.putBoolean(res.getString(R.string.key_Radio_TVRoom), true);
                deviceEditor.putBoolean(res.getString(R.string.key_TV_TVRoom), false);
                roomEditor.putBoolean(res.getString(R.string.key_TVRoom_Media), true);
                roomEditor.putString(res.getString(R.string.key_TVRoom_AVName), "Radio");
                //TODO: send Network Data
                break;
            case R.id.tgl_cd:
                fragChange = true;
                deviceEditor.putBoolean(res.getString(R.string.key_CD_TVRoom), true);
                deviceEditor.putBoolean(res.getString(R.string.key_TV_TVRoom), false);
                roomEditor.putBoolean(res.getString(R.string.key_TVRoom_Media), true);
                roomEditor.putString(res.getString(R.string.key_TVRoom_AVName), "CD");
                //TODO: send Network Data
                break;
            case R.id.tgl_media_off:
                fragChange = true;
                deviceEditor.putBoolean(res.getString(R.string.key_DVD_TVRoom), false);
                deviceEditor.putBoolean(res.getString(R.string.key_TV_TVRoom), false);
                deviceEditor.putBoolean(res.getString(R.string.key_Radio_TVRoom), false);
                deviceEditor.putBoolean(res.getString(R.string.key_CD_TVRoom), false);
                deviceEditor.putBoolean(res.getString(R.string.key_TV_TVRoom), false);
                roomEditor.putBoolean(res.getString(R.string.key_TVRoom_Media), false);
                roomEditor.putString(res.getString(R.string.key_TVRoom_AVName), "");
                //TODO: send Network Data
                break;
            case R.id.tgl_lights_all_on:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Front), 100);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Rear), 100);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Feature), 100);
                //TODO: send Network Data
                break;
            case R.id.tgl_lights_tv:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Front), 0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Rear), 30);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Feature), 100);
                //TODO: send Network Data
                break;
            case R.id.tgl_lights_reading:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Front), 0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Rear), 100);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Feature), 20);
                //TODO: send Network Data
                break;
            case R.id.tgl_lights_off:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Front), 0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Rear), 0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Feature), 0);
                //TODO: send Network Data
                break;
            case R.id.tgl_blinds_down:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Blinds_Front_TVRoom), 0);
                deviceEditor.putInt(res.getString(R.string.key_Blinds_Rear_TVRoom), 0);
                //TODO: send Network Data
                break;
            case R.id.tgl_blinds_10:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Blinds_Front_TVRoom), 1);
                deviceEditor.putInt(res.getString(R.string.key_Blinds_Rear_TVRoom), 1);
                //TODO: send Network Data
                break;
            case R.id.tgl_blinds_50:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Blinds_Front_TVRoom), 5);
                deviceEditor.putInt(res.getString(R.string.key_Blinds_Rear_TVRoom), 5);
                //TODO: send Network Data
                break;
            case R.id.tgl_blinds_open:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Blinds_Front_TVRoom), 10);
                deviceEditor.putInt(res.getString(R.string.key_Blinds_Rear_TVRoom), 10);
                //TODO: send Network Data
                break;
            case R.id.tgl_heatpump_auto:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Mode), 1);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Fan), 0);
                break;
            case R.id.tgl_heatpump_heat:
                fragChange = true;
                temp = devicePrefs.getInt(res.getString(R.string.key_Heatpump_TVRoom_CurTemp),res.getInteger(R.integer.cur_temp));
                temp = Math.min(temp+3, 36);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Mode), 3);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Fan), 0);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_SetTemp), temp);
                break;
            case R.id.tgl_heatpump_cool:
                fragChange = true;
                temp = devicePrefs.getInt(res.getString(R.string.key_Heatpump_TVRoom_CurTemp),res.getInteger(R.integer.cur_temp));
                temp = Math.max(temp-3, 16);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Mode), 2);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Fan), 0);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_SetTemp), temp);
                break;
            case R.id.tgl_heatpump_off:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Mode), 0);
                deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Fan), 0);
                break;
            case R.id.btn_temp:
                fragChange = true;
                break;
            case R.id.img_light:
                fragChange = true;
                unToggle();
                break;
            case R.id.img_blinds:
                fragChange = true;
                unToggle();
                break;
            case R.id.img_power:
                fragChange = true;
                break;
            case R.id.btn_avname:
                unToggle();
                if(roomPrefs.getString(res.getString(R.string.key_TVRoom_AVName),"").equals("MySkyHD")){
                    fragChange = true;
                    v.findViewById(R.id.btn_avname).setTag(res.getString(R.string.fragTag_tv));
                } else if(roomPrefs.getString(res.getString(R.string.key_TVRoom_AVName),"").equals("DVD")){
                    fragChange = true;
                    v.findViewById(R.id.btn_avname).setTag(res.getString(R.string.fragTag_dvd));
                } else if(roomPrefs.getString(res.getString(R.string.key_TVRoom_AVName),"").equals("XBox")){
                    fragChange = true;
                    v.findViewById(R.id.btn_avname).setTag(res.getString(R.string.fragTag_game));
                } else if(roomPrefs.getString(res.getString(R.string.key_TVRoom_AVName),"").equals("Radio")){
                    fragChange = true;
                    v.findViewById(R.id.btn_avname).setTag(res.getString(R.string.fragTag_radio));
                } else if(roomPrefs.getString(res.getString(R.string.key_TVRoom_AVName),"").equals("CD")){
                    fragChange = true;
                    v.findViewById(R.id.btn_avname).setTag(res.getString(R.string.fragTag_cd));
                } else {
                    v.findViewById(R.id.btn_avname).setTag(res.getString(R.string.fragTag_blank));
                }
                break;
            case R.id.img_sound:
                if (roomPrefs.getBoolean(res.getString(R.string.key_TVRoom_Mute), false)){
                    //true, set false
                    roomEditor.putBoolean(res.getString(R.string.key_TVRoom_Mute), false);
                    ((ImageButton)infoButtons.get(5)).setImageResource(R.drawable.ic_lock_ringer_on); //5 while play buttons not added, otherwise 11
                } else {
                    //false, set true
                    roomEditor.putBoolean(res.getString(R.string.key_TVRoom_Mute), true);
                    ((ImageButton)infoButtons.get(5)).setImageResource(R.drawable.ic_lock_ringer_off); //5 while play buttons not added, otherwise 11
                }
                break;
            case R.id.img_vol_down:
                vol = roomPrefs.getInt(res.getString(R.string.key_TVRoom_Volume),0);
                vol = Math.max(--vol, 0);
                roomEditor.putInt(res.getString(R.string.key_TVRoom_Volume), vol);
                textVol.setText("Vol: "+vol);
                break;
            case R.id.img_vol_up:
                vol = roomPrefs.getInt(res.getString(R.string.key_TVRoom_Volume),0);
                vol = Math.min(++vol, res.getInteger(R.integer.max_vol));
                roomEditor.putInt(res.getString(R.string.key_TVRoom_Volume), vol);
                textVol.setText("Vol: "+vol);
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
