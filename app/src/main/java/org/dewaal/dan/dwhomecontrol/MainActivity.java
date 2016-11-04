package org.dewaal.dan.dwhomecontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SharedPreferences devicePrefs;
    SharedPreferences roomPrefs;
    public static final String devicePrefsName = "DeviceSettings";
    public static final String roomPrefsName = "RoomSettings";

    private int mInterval = 1000; // 1 second
    private Handler mHandler;

    private List<View> tab_views = new ArrayList();
    public List<TextView> tab_titles = new ArrayList();
    public List<ImageView> tab_img_l1 = new ArrayList();
    public List<ImageView> tab_img_l2 = new ArrayList();
    public List<ImageView> tab_img_r1 = new ArrayList();
    public List<ImageView> tab_img_r2 = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toolbar
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        for (int i=0; i<5 ; i++) {
            tabLayout.getTabAt(i).setCustomView(R.layout.custom_tab);
            tab_views.add(tabLayout.getTabAt(i).getCustomView());
            tab_titles.add((TextView) tab_views.get(i).findViewById(R.id.tab));
            tab_img_l1.add((ImageView) tab_views.get(i).findViewById(R.id.tab_img_l1));
            tab_img_l2.add((ImageView) tab_views.get(i).findViewById(R.id.tab_img_l2));
            tab_img_r1.add((ImageView) tab_views.get(i).findViewById(R.id.tab_img_r1));
            tab_img_r2.add((ImageView) tab_views.get(i).findViewById(R.id.tab_img_r2));
            tab_titles.get(i).setText(getResources().getStringArray(R.array.tabNames)[i]);
//            tab_img_r1.get(i).setImageResource(R.drawable.speaker);
//            tab_img_l1.get(i).setImageResource(R.drawable.icon_green);
//            tab_img_l2.get(i).setImageResource(R.drawable.icon_yellow);
        }

        //Tabs Pager Adapter
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final MainPagerAdapter adapter = new MainPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {            }
        });

        //Shared Preferences
        devicePrefs = getSharedPreferences(devicePrefsName, Context.MODE_PRIVATE);
        roomPrefs = getSharedPreferences(roomPrefsName, Context.MODE_PRIVATE);
        Resources res = getResources();

        SharedPreferences.Editor deviceEditor = devicePrefs.edit();
        deviceEditor.putBoolean(res.getString(R.string.key_FrontDoor_Lock), false);
        deviceEditor.putInt(res.getString(R.string.key_Gate_Mode), 0);
        deviceEditor.putBoolean(res.getString(R.string.key_Alarm_Status), false);
        deviceEditor.putString(res.getString(R.string.key_Alarm_Code), "1234");
        deviceEditor.putInt(res.getString(R.string.key_Lights_Deck_Deck), 0);
        deviceEditor.putInt(res.getString(R.string.key_Lights_Deck_Yard), 0);
        deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Entrance), 0);
        deviceEditor.putInt(res.getString(R.string.key_Lights_Entrance_Outside), 0);
        deviceEditor.putInt(res.getString(R.string.key_Lights_Garage_Garage), 0);
        deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Feature), 0);
        deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Front), 0);
        deviceEditor.putInt(res.getString(R.string.key_Lights_TVRoom_Rear), 0);
        deviceEditor.putInt(res.getString(R.string.key_Blinds_Front_TVRoom), 0);
        deviceEditor.putInt(res.getString(R.string.key_Blinds_Rear_TVRoom), 0);
        deviceEditor.putBoolean(res.getString(R.string.key_Sky_TVRoom), false);
        deviceEditor.putBoolean(res.getString(R.string.key_DVD_TVRoom), false);
        deviceEditor.putBoolean(res.getString(R.string.key_TV_TVRoom), false);
        deviceEditor.putBoolean(res.getString(R.string.key_Radio_TVRoom), false);
        deviceEditor.putBoolean(res.getString(R.string.key_Radio_Garage), false);
        deviceEditor.putBoolean(res.getString(R.string.key_Radio_Deck), false);
        deviceEditor.putBoolean(res.getString(R.string.key_CD_TVRoom), false);
        deviceEditor.putBoolean(res.getString(R.string.key_CD_Garage), false);
        deviceEditor.putBoolean(res.getString(R.string.key_CD_Deck), false);
        deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_SetTemp), 0);
        deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_CurTemp), res.getInteger(R.integer.cur_temp));
        deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Mode), 0);
        deviceEditor.putInt(res.getString(R.string.key_Heatpump_TVRoom_Fan), 0);
        deviceEditor.putBoolean(res.getString(R.string.key_Heatpump_TVRoom_PowerSave), false);
        deviceEditor.commit();
        SharedPreferences.Editor roomEditor = roomPrefs.edit();
        roomEditor.putInt(res.getString(R.string.key_Tab_Active),0);
        roomEditor.putInt(res.getString(R.string.key_House_Power), 0);
        roomEditor.putInt(res.getString(R.string.key_TVRoom_Power), 0);
        roomEditor.putBoolean(res.getString(R.string.key_TVRoom_Lights), false);
        roomEditor.putInt(res.getString(R.string.key_TVRoom_Heatpump), 0);
        roomEditor.putBoolean(res.getString(R.string.key_TVRoom_Media), false);
        roomEditor.putInt(res.getString(R.string.key_TVRoom_Volume), res.getInteger(R.integer.def_vol));
        roomEditor.putBoolean(res.getString(R.string.key_TVRoom_Mute), false);
        roomEditor.putString(res.getString(R.string.key_TVRoom_AVName), "");
        roomEditor.putBoolean(res.getString(R.string.key_TVRoom_RoomOff), true);
        roomEditor.putInt(res.getString(R.string.key_Entrance_Power), 0);
        roomEditor.putBoolean(res.getString(R.string.key_Entrance_Lights), false);
        roomEditor.putInt(res.getString(R.string.key_Entrance_Heatpump), 0);
        roomEditor.putBoolean(res.getString(R.string.key_Entrance_RoomOff), true);
        roomEditor.putInt(res.getString(R.string.key_Garage_Power), 0);
        roomEditor.putBoolean(res.getString(R.string.key_Garage_Lights), false);
        roomEditor.putInt(res.getString(R.string.key_Garage_Heatpump), 0);
        roomEditor.putBoolean(res.getString(R.string.key_Garage_Media), false);
        roomEditor.putInt(res.getString(R.string.key_Garage_Volume), res.getInteger(R.integer.def_vol));
        roomEditor.putBoolean(res.getString(R.string.key_Garage_Mute), false);
        roomEditor.putString(res.getString(R.string.key_Garage_AVName), "");
        roomEditor.putBoolean(res.getString(R.string.key_Garage_RoomOff), true);
        roomEditor.putInt(res.getString(R.string.key_Deck_Power), 0);
        roomEditor.putBoolean(res.getString(R.string.key_Deck_Lights), false);
        roomEditor.putBoolean(res.getString(R.string.key_Deck_Media), false);
        roomEditor.putInt(res.getString(R.string.key_Deck_Volume), res.getInteger(R.integer.def_vol));
        roomEditor.putBoolean(res.getString(R.string.key_Deck_Mute), false);
        roomEditor.putString(res.getString(R.string.key_Deck_AVName), "");
        roomEditor.putBoolean(res.getString(R.string.key_Deck_RoomOff), true);
        roomEditor.commit();

        //Timed tasks
        mHandler = new Handler();
        startRepeatingTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    void timedTask(){
        SharedPreferences.Editor deviceEditor = devicePrefs.edit();
        SharedPreferences.Editor roomEditor = roomPrefs.edit();
        Resources res = getResources();
        boolean light, media, roomOff;
        int heatpump, room_power, house_power;
        try{
            //Deck
            room_power = devicePrefs.getInt(res.getString(R.string.key_Lights_Deck_Deck), 0) / 40;
            room_power += devicePrefs.getInt(res.getString(R.string.key_Lights_Deck_Yard), 0);
            light = (devicePrefs.getInt(res.getString(R.string.key_Lights_Deck_Deck), 0) > 0) || (devicePrefs.getInt(res.getString(R.string.key_Lights_Deck_Yard), 0) > 0);
            roomEditor.putBoolean(res.getString(R.string.key_Deck_Lights), light);
            media = roomPrefs.getBoolean(res.getString(R.string.key_Deck_Media), false);
            if(media){room_power++;}
            room_power = Math.min(room_power, 5);
            roomEditor.putInt(res.getString(R.string.key_Deck_Power),room_power);
            roomOff = !(media || light);
            roomEditor.putBoolean(res.getString(R.string.key_Deck_RoomOff), roomOff);
            tab_img_l2.get(4).setImageResource(light ? R.drawable.icon_light : 0);
            tab_img_r1.get(4).setImageResource(media ? R.drawable.speaker : 0);

            //Garage
            room_power = devicePrefs.getInt(res.getString(R.string.key_Lights_Garage_Garage), 0);
            light = (devicePrefs.getInt(res.getString(R.string.key_Lights_Garage_Garage), 0) > 0);
            roomEditor.putBoolean(res.getString(R.string.key_Garage_Lights), light);
            heatpump = devicePrefs.getInt(res.getString(R.string.key_Heatpump_Garage_Mode),0);
            if(heatpump > 0){room_power+=2;}
            media = roomPrefs.getBoolean(res.getString(R.string.key_Garage_Media), false);
            if(media){room_power++;}
            room_power = Math.min(room_power, 5);
            roomEditor.putInt(res.getString(R.string.key_Garage_Power),room_power);
            if(light || media || heatpump > 0){
                roomOff = false;
            }
            roomEditor.putBoolean(res.getString(R.string.key_Garage_RoomOff), roomOff);
            tab_img_l2.get(3).setImageResource(light ? R.drawable.icon_light : 0);
            tab_img_r1.get(3).setImageResource(media ? R.drawable.speaker : 0);
            switch (heatpump){
                case 0:
                    tab_img_l1.get(3).setImageResource(0);
                    break;
                case 1:
                    tab_img_l1.get(3).setImageResource(R.drawable.icon_auto);
                    break;
                case 2:
                    tab_img_l1.get(3).setImageResource(R.drawable.icon_cool);
                    break;
                case 3:
                    tab_img_l1.get(3).setImageResource(R.drawable.icon_heat);
                    break;
            }

            //Entrance
            room_power = devicePrefs.getInt(res.getString(R.string.key_Lights_Entrance_Entrance), 0) / 40;
            room_power += devicePrefs.getInt(res.getString(R.string.key_Lights_Entrance_Outside), 0);
            light = (devicePrefs.getInt(res.getString(R.string.key_Lights_Entrance_Entrance), 0) > 0) || (devicePrefs.getInt(res.getString(R.string.key_Lights_Entrance_Outside), 0) > 0);
            roomEditor.putBoolean(res.getString(R.string.key_Entrance_Lights), light);
            heatpump = devicePrefs.getInt(res.getString(R.string.key_Heatpump_Entrance_Mode),0);
            if(heatpump > 0){room_power+=2;}
            room_power = Math.min(room_power, 5);
            roomEditor.putInt(res.getString(R.string.key_Entrance_Power),room_power);
            roomOff = true;
            if(light || heatpump > 0){
                roomOff = false;
            }
            roomEditor.putBoolean(res.getString(R.string.key_Entrance_RoomOff), roomOff);
            tab_img_l2.get(2).setImageResource(light ? R.drawable.icon_light : 0);
            switch (heatpump){
                case 0:
                    tab_img_l1.get(2).setImageResource(0);
                    break;
                case 1:
                    tab_img_l1.get(2).setImageResource(R.drawable.icon_auto);
                    break;
                case 2:
                    tab_img_l1.get(2).setImageResource(R.drawable.icon_cool);
                    break;
                case 3:
                    tab_img_l1.get(2).setImageResource(R.drawable.icon_heat);
                    break;
            }

            //TVRoom
            room_power = devicePrefs.getInt(res.getString(R.string.key_Lights_TVRoom_Front), 0) / 60;
            room_power += devicePrefs.getInt(res.getString(R.string.key_Lights_TVRoom_Rear), 0) / 60;
            light = (devicePrefs.getInt(res.getString(R.string.key_Lights_TVRoom_Front), 0) > 0)
                    || (devicePrefs.getInt(res.getString(R.string.key_Lights_TVRoom_Rear), 0) > 0)
                    || (devicePrefs.getInt(res.getString(R.string.key_Lights_TVRoom_Feature), 0) > 0);
            roomEditor.putBoolean(res.getString(R.string.key_TVRoom_Lights), light);
            if(devicePrefs.getBoolean(res.getString(R.string.key_TV_TVRoom),false)){room_power++;}
            media = roomPrefs.getBoolean(res.getString(R.string.key_TVRoom_Media),false);
            if(media){room_power++;}
            heatpump = devicePrefs.getInt(res.getString(R.string.key_Heatpump_TVRoom_Mode),0);
            if(heatpump > 0){room_power+=2;}
            room_power = Math.min(room_power, 5);
            roomEditor.putInt(res.getString(R.string.key_TVRoom_Power),room_power);
            roomOff = true;
            if(light || media || heatpump > 0){
                roomOff = false;
            }
            roomEditor.putBoolean(res.getString(R.string.key_TVRoom_RoomOff), roomOff);
            tab_img_l2.get(1).setImageResource(light ? R.drawable.icon_light : 0);
            tab_img_r1.get(1).setImageResource(media ? R.drawable.speaker : 0);
            switch (heatpump){
                case 0:
                    tab_img_l1.get(1).setImageResource(0);
                    break;
                case 1:
                    tab_img_l1.get(1).setImageResource(R.drawable.icon_auto);
                    break;
                case 2:
                    tab_img_l1.get(1).setImageResource(R.drawable.icon_cool);
                    break;
                case 3:
                    tab_img_l1.get(1).setImageResource(R.drawable.icon_heat);
                    break;
            }

            //House
            int hp = Math.max(roomPrefs.getInt(res.getString(R.string.key_Deck_Power),0) - 1, 0);
            hp += Math.max(roomPrefs.getInt(res.getString(R.string.key_TVRoom_Power),0) - 1, 0);
            roomEditor.putInt(res.getString(R.string.key_House_Power),hp);

        } catch (Exception e){
            Log.d("Timed Task", e.toString());
        } finally {
            roomEditor.apply();
            deviceEditor.apply();
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
}
