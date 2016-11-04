package org.dewaal.dan.dwhomecontrol;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.TextViewCompat;
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
 * Deck uses the following Control Fragments:
 * Lights_Deck
 * Radio
 * CD
 */

public class Tab_Deck_Fragment extends Fragment implements View.OnClickListener{

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

    public Tab_Deck_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_deck, container, false);
        id = R.id.control_fragment_deck;
        Resources res = getResources();

        //Shared Preferences
        devicePrefs = getActivity().getSharedPreferences(devicePrefsName, Context.MODE_PRIVATE);
        roomPrefs = getActivity().getSharedPreferences(roomPrefsName, Context.MODE_PRIVATE);

        //Timed tasks
        mHandler = new Handler();
        startRepeatingTask();

        //Button creation
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_radio));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_cd));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_media_off));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_lights_on));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_lights_off));
        buttons.add((ToggleButton)view.findViewById(R.id.tgl_room_off));
        for (ToggleButton b : buttons){
            b.setOnClickListener(this);
            b.setOnCheckedChangeListener(toggleListener);
        }

        //Info Button creation (order is important here!)
        infoButtons.add(view.findViewById(R.id.img_light));
        infoButtons.add(view.findViewById(R.id.img_power));
        infoButtons.add(view.findViewById(R.id.btn_avname));
        infoButtons.add(view.findViewById(R.id.img_sound));
        infoButtons.add(view.findViewById(R.id.img_vol_down));
        infoButtons.add(view.findViewById(R.id.img_vol_up));
        for (View ib : infoButtons){
            ib.setOnClickListener(this);
        }
        textVol = (TextView)view.findViewById(R.id.txt_vol);
        textVol.setText("Vol: "+roomPrefs.getInt(res.getString(R.string.key_Deck_Volume),0));

        //Fragment Creation
//        Log.d("Deck","Testing for savedInstanceState.");
//        if (savedInstanceState == null) {
//            Log.d("Deck","savedInstanceState = null so creating frags.");
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            addFrag(ft, new Control_Blank_Fragment(), res.getString(R.string.fragTag_blank));
            addFrag(ft, new Control_Lights_Deck_Fragment(), res.getString(R.string.fragTag_lights_deck));
            addFrag(ft, new Control_Radio_Fragment(), res.getString(R.string.fragTag_radio));
            addFrag(ft, new Control_CD_Fragment(), res.getString(R.string.fragTag_cd));
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
//        SharedPreferences.Editor roomEditor = roomPrefs.edit();
        try{
            ((ImageButton)infoButtons.get(0)).setImageResource(roomPrefs.getBoolean(res.getString(R.string.key_Deck_Lights),false) ? R.drawable.roominfo_light_on : R.drawable.roominfo_light_off);
            TypedArray room_power = res.obtainTypedArray(R.array.room_power);
            int power_level = roomPrefs.getInt(res.getString(R.string.key_Deck_Power),0);
            ((ImageButton)infoButtons.get(1)).setImageResource(room_power.getResourceId(power_level, 0));
            ((Button)infoButtons.get(2)).setText(roomPrefs.getString(res.getString(R.string.key_Deck_AVName),""));
//            boolean roomSenseOn = roomPrefs.getBoolean(res.getString(R.string.key_Deck_Lights),true) || roomPrefs.getBoolean(res.getString(R.string.key_Deck_Media),true);
//            if (roomSenseOff && !roomPrefs.getBoolean(res.getString(R.string.key_Deck_RoomOff),true)) {
//                roomOff();
//            }
//            if (roomSenseOn){
//                roomEditor.putBoolean(res.getString(R.string.key_Deck_RoomOff), false);
//            } else {
//                roomEditor.putBoolean(res.getString(R.string.key_Deck_RoomOff), true);
//            }

        } catch (Exception e){
            Log.d("Timed Task", e.toString());
        } finally {
//            roomEditor.apply();
//            Log.d("Deck Timed RoomOff", String.valueOf(roomPrefs.getBoolean(res.getString(R.string.key_Deck_RoomOff), false)));
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
                ft.detach(fragment);
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
        deviceEditor.putBoolean(res.getString(R.string.key_Radio_Deck), false);
        deviceEditor.putBoolean(res.getString(R.string.key_CD_Deck), false);
        roomEditor.putBoolean(res.getString(R.string.key_Deck_Media), false);
        roomEditor.putString(res.getString(R.string.key_Deck_AVName), "");
        //TODO: send Network Data
        deviceEditor.putInt(res.getString(R.string.key_Lights_Deck_Deck), 0);
        deviceEditor.putInt(res.getString(R.string.key_Lights_Deck_Yard), 0);
        //TODO: send Network Data
        roomEditor.putBoolean(res.getString(R.string.key_Deck_RoomOff), true);
        showFragment(res.getString(R.string.fragTag_blank));
        roomEditor.commit();
        deviceEditor.commit();
//        Log.d("Deck Function RoomOff", String.valueOf(roomPrefs.getBoolean(res.getString(R.string.key_Deck_RoomOff), false)));
//        ((MainActivity)getActivity()).showToast("Room has now turned off.");
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor deviceEditor = devicePrefs.edit();
        SharedPreferences.Editor roomEditor = roomPrefs.edit();
        Resources res = getResources();
        boolean fragChange = false;
        int vol;
        switch (v.getId()){
            case R.id.tgl_lights_on:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Lights_Deck_Deck), 100);
                deviceEditor.putInt(res.getString(R.string.key_Lights_Deck_Yard), 1);
                //TODO: send Network Data
                break;
            case R.id.tgl_lights_off:
                fragChange = true;
                deviceEditor.putInt(res.getString(R.string.key_Lights_Deck_Deck), 0);
                deviceEditor.putInt(res.getString(R.string.key_Lights_Deck_Yard), 0);
                //TODO: send Network Data
                break;
            case R.id.tgl_room_off:
//                Log.d("Deck Button RoomOff", String.valueOf(roomPrefs.getBoolean(res.getString(R.string.key_Deck_RoomOff), false)));
                if(roomPrefs.getBoolean(res.getString(R.string.key_Deck_RoomOff), false) == false){
                    alertDialog.show();
                }
                break;
            case R.id.tgl_radio:
                fragChange = true;
                deviceEditor.putBoolean(res.getString(R.string.key_Radio_Deck), true);
                roomEditor.putBoolean(res.getString(R.string.key_Deck_Media), true);
                roomEditor.putString(res.getString(R.string.key_Deck_AVName), "Radio");
                //TODO: send Network Data
                break;
            case R.id.tgl_cd:
                fragChange = true;
                deviceEditor.putBoolean(res.getString(R.string.key_CD_Deck), true);
                roomEditor.putBoolean(res.getString(R.string.key_Deck_Media), true);
                roomEditor.putString(res.getString(R.string.key_Deck_AVName), "CD");
                //TODO: send Network Data
                break;
            case R.id.tgl_media_off:
                fragChange = true;
                deviceEditor.putBoolean(res.getString(R.string.key_Radio_Deck), false);
                deviceEditor.putBoolean(res.getString(R.string.key_CD_Deck), false);
                roomEditor.putBoolean(res.getString(R.string.key_Deck_Media), false);
                roomEditor.putString(res.getString(R.string.key_Deck_AVName), "");
                //TODO: send Network Data
                break;
            case R.id.img_light:
                fragChange = true;
                unToggle();
                break;
            case R.id.img_power:
                fragChange = true;
                break;
            case R.id.btn_avname:
                unToggle();
                if (roomPrefs.getString(res.getString(R.string.key_Deck_AVName),"").equals("Radio")){
                    fragChange = true;
                    v.findViewById(R.id.btn_avname).setTag(res.getString(R.string.fragTag_radio));
                } else if (roomPrefs.getString(res.getString(R.string.key_Deck_AVName),"").equals("CD")){
                    fragChange = true;
                    v.findViewById(R.id.btn_avname).setTag(res.getString(R.string.fragTag_cd));
                } else {
                    v.findViewById(R.id.btn_avname).setTag(res.getString(R.string.fragTag_blank));
                }
                break;
            case R.id.img_sound:
                if (roomPrefs.getBoolean(res.getString(R.string.key_Deck_Mute), false)){
                    //true, set false
                    roomEditor.putBoolean(res.getString(R.string.key_Deck_Mute), false);
                    ((ImageButton)infoButtons.get(3)).setImageResource(R.drawable.ic_lock_ringer_on); //3 while play buttons not added, otherwise 9
                } else {
                    //false, set true
                    roomEditor.putBoolean(res.getString(R.string.key_Deck_Mute), true);
                    ((ImageButton)infoButtons.get(3)).setImageResource(R.drawable.ic_lock_ringer_off); //3 while play buttons not added, otherwise 9
                }
                break;
            case R.id.img_vol_down:
                vol = roomPrefs.getInt(res.getString(R.string.key_Deck_Volume),0);
                vol = Math.max(--vol, 0);
                roomEditor.putInt(res.getString(R.string.key_Deck_Volume), vol);
                textVol.setText("Vol: "+vol);
                break;
            case R.id.img_vol_up:
                vol = roomPrefs.getInt(res.getString(R.string.key_Deck_Volume),0);
                vol = Math.min(++vol, res.getInteger(R.integer.max_vol));
                roomEditor.putInt(res.getString(R.string.key_Deck_Volume), vol);
                textVol.setText("Vol: "+vol);
                break;
        }
        deviceEditor.apply();
        roomEditor.apply();
        if (fragChange) {
            try {
                showFragment((v.findViewById(v.getId())).getTag().toString());
            } catch (Exception e) {
                //Log.d("Buttons", "Button: \""+((ToggleButton)v.findViewById(v.getId())).getTextOn()+"\" missing tag.");
                Log.d("Deck Button", e.toString());
                ((MainActivity) getActivity()).showToast("Not Yet Implemented");
            }
        }
    }
}
