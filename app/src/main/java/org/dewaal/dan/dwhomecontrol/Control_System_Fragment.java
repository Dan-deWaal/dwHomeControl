package org.dewaal.dan.dwhomecontrol;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Control_System_Fragment extends Fragment {

    String[] sysArray = {"Control System","Network","Door Lock","Gate","Alarm System","Security System","Lights Controller","SkyTV","DVD Player","Amplifier Radio","CD Player","Blinds Controller","Heatpump Controller"};

    public Control_System_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_system, container, false);

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.system_listview, sysArray);

        ListView listView = (ListView) view.findViewById(R.id.system_list);
        listView.setAdapter(adapter);
        adapter.getView(5, null, listView).setBackground(ContextCompat.getDrawable(getActivity(), android.R.color.holo_red_dark));
        adapter.getView(5, null, listView).setBackgroundColor(Color.RED);
//        Log.d("system", adapter.getView(5, null, listView).toString());

        return view;
    }

}
