package com.example.moncherz;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
        while (!Utilities.done) {
            try {
                Thread.sleep(10);

            } catch (Exception e) {

            }
        }

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

                        for (int i = 0; i < foods.size(); i++) {

                            if (i == sectIdx.get(nextSect)) {
                                TextView sect = new TextView(MenuFragment.this.getContext());
                                sect.setText(sectNames.get(nextSect));
                                sect.setTextSize(36);
                                sect.setTextColor(0xFF008577);
                                if (nextSect < sectNames.size() - 1)
                                    nextSect++;
                                linLay.addView(sect);
                            }

                            LinearLayout line = new LinearLayout(MenuFragment.this.getContext());
                            line.setOrientation(LinearLayout.HORIZONTAL);
                            final ImageButton b = new ImageButton(MenuFragment.this.getContext());
                            b.setImageResource(R.drawable.star);
                            b.setBackgroundColor(0);

                            final Boolean isFav = Utilities.favs.contains(foods.get(i));
                            if(isFav)
                                b.setColorFilter(0xffebe534);
                            else
                                b.setColorFilter(0xffa8a8a8);

                            final TextView f = new TextView(MenuFragment.this.getContext());
                            f.setTextSize(24);

                            View.OnClickListener click = new View.OnClickListener() {
                                Boolean pressed = isFav;

                                public void onClick(View v) {
                                    if (!pressed) {
                                        b.setColorFilter(0xffebe534);
                                        if(!Utilities.favs.contains(f.getText().toString()))
                                            Utilities.favs.add(f.getText().toString());
                                        pressed = true;
                                    } else {
                                        b.setColorFilter(0xffa8a8a8);
                                        if(Utilities.favs.contains(f.getText().toString()))
                                            Utilities.favs.remove(f.getText().toString());
                                        pressed = false;
                                    }


                                }
                            };
                            b.setOnClickListener(click);


                            f.setText(foods.get(i));
                            line.addView(b);
                            line.addView(f);
//                            line.setGravity(Gravity.CENTER_VERTICAL);
                            linLay.addView(line);

                            b.setPadding(0,0,0,0);
                            b.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            f.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            int diff = b.getMeasuredHeight() - f.getMeasuredHeight();
                            if(diff > 0){
                                f.setPadding(0,diff/2, 0, 0);

//                                b.setPadding(b.getMeasuredWidth() / 10, 0, b.getMeasuredWidth() / 10, 0);
//                                f.setText(diff + "");
                            }
                            else
                                b.setPadding(0, Math.abs(diff/2), 0, 0);

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
