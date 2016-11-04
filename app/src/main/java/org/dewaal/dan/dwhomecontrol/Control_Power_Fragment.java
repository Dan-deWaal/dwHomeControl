package org.dewaal.dan.dwhomecontrol;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Control_Power_Fragment extends Fragment {

    SharedPreferences devicePrefs;
    SharedPreferences roomPrefs;
    public static final String devicePrefsName = "DeviceSettings";
    public static final String roomPrefsName = "RoomSettings";

    private int mInterval = 500; // 0.5 seconds
    private Handler mHandler;

    ImageView tv, ent, gar, deck, ht;

    public Control_Power_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_power, container, false);

        //Shared Preferences
        devicePrefs = getActivity().getSharedPreferences(devicePrefsName, Context.MODE_PRIVATE);
        roomPrefs = getActivity().getSharedPreferences(roomPrefsName, Context.MODE_PRIVATE);

        //Timed tasks
        mHandler = new Handler();
        startRepeatingTask();

        tv = (ImageView)view.findViewById(R.id.img_pwr_tvroom);
        ent = (ImageView)view.findViewById(R.id.img_pwr_entrance);
        gar = (ImageView)view.findViewById(R.id.img_pwr_garage);
        deck = (ImageView)view.findViewById(R.id.img_pwr_deck);
        ht = (ImageView)view.findViewById(R.id.img_pwr_house);

        return view;
    }

    void timedTask(){
        Resources res = getResources();
        try{
            TypedArray room_power = res.obtainTypedArray(R.array.room_power);
            tv.setImageResource(room_power.getResourceId(roomPrefs.getInt(res.getString(R.string.key_TVRoom_Power),0), 0));
            ent.setImageResource(room_power.getResourceId(roomPrefs.getInt(res.getString(R.string.key_Entrance_Power),0), 0));
            gar.setImageResource(room_power.getResourceId(roomPrefs.getInt(res.getString(R.string.key_Garage_Power),0), 0));
            deck.setImageResource(room_power.getResourceId(roomPrefs.getInt(res.getString(R.string.key_Deck_Power),0), 0));
            TypedArray house_power = res.obtainTypedArray(R.array.house_power);
            ht.setImageResource(house_power.getResourceId(roomPrefs.getInt(res.getString(R.string.key_House_Power),0), 0));
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

}
