package com.example.moncherz;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.moncherz.ui.gallery.GalleryFragment;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {


    public MenuFragment() {
        // Required empty public constructor
    }

    private int place;
    private int time;

    public static MenuFragment init(int place, int time) {
        MenuFragment menuFrag = new MenuFragment();
        Bundle args = new Bundle();
        args.putInt("place", place);
        args.putInt("time", time);
        menuFrag.setArguments(args);
        return menuFrag;
    }

    private void populate() {
        while(!Utilities.done) {
            try{
                Thread.sleep(10);

            } catch(Exception e) {

            }
        }
        Log.d("HEYHEY", "HEYHEYHEYHEY");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
            //code to do the HTTP request

            MenuFragment.this.getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    LinearLayout linLay = MenuFragment.this.getView().findViewById(R.id.menuLinLay);
                    int nextSect = 0;
                    ArrayList<String> foods = Utilities.foods[place][time];
                    ArrayList<Integer> sectIdx = Utilities.sectIdx[place][time];
                    ArrayList<String> sectNames = Utilities.sectNames[place][time];

                    for(int i = 0; i < foods.size(); i++) {

                        if(i == sectIdx.get(nextSect)) {
                            TextView sect = new TextView(MenuFragment.this.getContext());
                            sect.setText(sectNames.get(nextSect));
                            sect.setTextSize(36);
                            sect.setTextColor(0xFF008577);
                            if(nextSect < sectNames.size() - 1)
                                nextSect++;
                            linLay.addView(sect);
                        }
                        TextView f = new TextView(MenuFragment.this.getContext());
                        String s = "<p>" + foods.get(i) + "</p>";
                        f.setText(Html.fromHtml(s));
//                            f.setText("" + sectIdx.get(0));
                        f.setTextSize(24);
                        f.setSingleLine();

//                            f.setHeight(linLay.getHeight()/20);
                        linLay.addView(f);
                    }
                }
            });

            }
        });

        thread.start();

    }


    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        this.place = getArguments().getInt("place");
        this.time = getArguments().getInt("time");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_menu, container, false);
        LinearLayout linLay = root.findViewById(R.id.menuLinLay);


        class populateThread extends Thread {

            public void run() {
                populate();
            }
        }
        populateThread pt = new populateThread();
        pt.start();

        return root;
    }

}
