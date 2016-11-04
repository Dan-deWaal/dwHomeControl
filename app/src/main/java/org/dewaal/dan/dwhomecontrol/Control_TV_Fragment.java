package org.dewaal.dan.dwhomecontrol;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Control_TV_Fragment extends Fragment implements View.OnClickListener{

    private List<View> buttons = new ArrayList();

    public Control_TV_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_tv, container, false);
        //Button creation
        buttons.add(view.findViewById(R.id.btn_sky));
        buttons.add(view.findViewById(R.id.btn_tv_guide));
        buttons.add(view.findViewById(R.id.btn_tv_help));
        buttons.add(view.findViewById(R.id.btn_tv_text));
        buttons.add(view.findViewById(R.id.img_return));
        buttons.add(view.findViewById(R.id.img_tv_up));
        buttons.add(view.findViewById(R.id.img_tv_down));
        buttons.add(view.findViewById(R.id.img_tv_left));
        buttons.add(view.findViewById(R.id.img_tv_right));
        buttons.add(view.findViewById(R.id.img_tv_centre));
        buttons.add(view.findViewById(R.id.img_tv_rec));
        buttons.add(view.findViewById(R.id.img_tv_red));
        buttons.add(view.findViewById(R.id.img_tv_green));
        buttons.add(view.findViewById(R.id.img_tv_yellow));
        buttons.add(view.findViewById(R.id.img_tv_blue));
        buttons.add(view.findViewById(R.id.img_tv_plus));
        buttons.add(view.findViewById(R.id.img_tv_minus));
        buttons.add(view.findViewById(R.id.tv_0));
        buttons.add(view.findViewById(R.id.tv_1));
        buttons.add(view.findViewById(R.id.tv_2));
        buttons.add(view.findViewById(R.id.tv_3));
        buttons.add(view.findViewById(R.id.tv_4));
        buttons.add(view.findViewById(R.id.tv_5));
        buttons.add(view.findViewById(R.id.tv_6));
        buttons.add(view.findViewById(R.id.tv_7));
        buttons.add(view.findViewById(R.id.tv_8));
        buttons.add(view.findViewById(R.id.tv_9));
        for (View b : buttons){
            b.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        ((MainActivity)getActivity()).showToast(v.findViewById(v.getId()).toString());
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        this.setUserVisibleHint(false);
//    }
}
