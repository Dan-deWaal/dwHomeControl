package org.dewaal.dan.dwhomecontrol;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Control_DVD_Fragment extends Fragment implements View.OnClickListener{

    private List<View> buttons = new ArrayList();

    public Control_DVD_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_dvd, container, false);

        //Button creation
        buttons.add(view.findViewById(R.id.btn_dvd_menu));
        buttons.add(view.findViewById(R.id.btn_dvd_guide));
        buttons.add(view.findViewById(R.id.btn_dvd_help));
        buttons.add(view.findViewById(R.id.btn_dvd_text));
        buttons.add(view.findViewById(R.id.img_dvd_return));
        buttons.add(view.findViewById(R.id.img_dvd_up));
        buttons.add(view.findViewById(R.id.img_dvd_down));
        buttons.add(view.findViewById(R.id.img_dvd_left));
        buttons.add(view.findViewById(R.id.img_dvd_right));
        buttons.add(view.findViewById(R.id.img_dvd_centre));
        buttons.add(view.findViewById(R.id.img_dvd_rec));
        buttons.add(view.findViewById(R.id.img_dvd_red));
        buttons.add(view.findViewById(R.id.img_dvd_green));
        buttons.add(view.findViewById(R.id.img_dvd_yellow));
        buttons.add(view.findViewById(R.id.img_dvd_blue));
        buttons.add(view.findViewById(R.id.img_dvd_plus));
        buttons.add(view.findViewById(R.id.img_dvd_minus));
        buttons.add(view.findViewById(R.id.dvd_0));
        buttons.add(view.findViewById(R.id.dvd_1));
        buttons.add(view.findViewById(R.id.dvd_2));
        buttons.add(view.findViewById(R.id.dvd_3));
        buttons.add(view.findViewById(R.id.dvd_4));
        buttons.add(view.findViewById(R.id.dvd_5));
        buttons.add(view.findViewById(R.id.dvd_6));
        buttons.add(view.findViewById(R.id.dvd_7));
        buttons.add(view.findViewById(R.id.dvd_8));
        buttons.add(view.findViewById(R.id.dvd_9));
        for (View b : buttons){
            b.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        ((MainActivity)getActivity()).showToast(v.findViewById(v.getId()).toString());
    }
}
