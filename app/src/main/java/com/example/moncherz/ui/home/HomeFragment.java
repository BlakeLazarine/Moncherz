package com.example.moncherz.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.moncherz.R;
import com.example.moncherz.Utilities;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;


    private void checkFavs() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Utilities.done) {
                    try {
                        Thread.sleep(10);

                    } catch (Exception e) {

                    }
                }

                final ArrayList<View> texts = new ArrayList<>();

                if (Utilities.badInternet) {
                    TextView t = new TextView(HomeFragment.this.getContext());
                    t.setText("No Internet Owo");
                    t.setTextSize(30);
                    texts.add(t);
                } else
                    for (int p = 0; p < Utilities.numPlaces; p++) {
                        TextView place = new TextView(HomeFragment.this.getContext());
                        place.setText(Utilities.placeHumanNames[p].toUpperCase());
                        place.setTextColor(0xFF008577);
                        place.setTextSize(36);
                        Boolean placeAdded = false;
                        for (int t = 0; t < Utilities.numTimes; t++) {
                            TextView time = new TextView(HomeFragment.this.getContext());
                            time.setText(Utilities.timeNames[t].toUpperCase());
                            time.setTextSize(30);
                            Boolean timeAdded = false;
                            ArrayList<String> aMenu = Utilities.foods[p][t];
                            for (String f : aMenu) {
                                if (Utilities.binarySearch(Utilities.favs, f)) {
                                    LinearLayout line = new LinearLayout(HomeFragment.this.getContext());
                                    line.setOrientation(LinearLayout.HORIZONTAL);
                                    final ImageButton b = new ImageButton(HomeFragment.this.getContext());
                                    b.setImageResource(R.drawable.star);
                                    b.setBackgroundColor(0);

                                    final Boolean isFav = true;
                                    if (isFav)
                                        b.setColorFilter(0xffebe534);
                                    else
                                        b.setColorFilter(0xffa8a8a8);

                                    final TextView text = new TextView(HomeFragment.this.getContext());
                                    text.setTextSize(24);

                                    View.OnClickListener click = new View.OnClickListener() {
                                        Boolean pressed = isFav;

                                        public void onClick(View v) {
                                            if (!pressed) {
                                                b.setColorFilter(0xffebe534);
                                                Utilities.addFav(text.getText().toString(), HomeFragment.this.getContext());
                                                pressed = true;
                                            } else {
                                                b.setColorFilter(0xffa8a8a8);
                                                Utilities.removeFav(text.getText().toString(), HomeFragment.this.getContext());
                                                pressed = false;
                                            }


                                        }
                                    };
                                    b.setOnClickListener(click);


                                    text.setText(f);
                                    line.addView(b);
                                    line.addView(text);
//                            line.setGravity(Gravity.CENTER_VERTICAL);


                                    b.setPadding(0, 0, 0, 0);
                                    b.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                                    text.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                                    int diff = b.getMeasuredHeight() - text.getMeasuredHeight();
                                    if (diff > 0) {
                                        text.setPadding(0, diff / 2, 0, 0);

//                                b.setPadding(b.getMeasuredWidth() / 10, 0, b.getMeasuredWidth() / 10, 0);
//                                f.setText(diff + "");
                                    } else
                                        b.setPadding(0, Math.abs(diff / 2), 0, 0);
                                    if (!placeAdded) {
                                        texts.add(place);
                                        placeAdded = true;
                                    }
                                    if (!timeAdded) {
                                        texts.add(time);
                                        timeAdded = true;
                                    }
                                    texts.add(line);
                                }
                            }
                        }
                    }

                HomeFragment.this.getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        LinearLayout favLinLay = HomeFragment.this.getView().findViewById(R.id.FavLinLay);
                        if (texts.size() == 0) {
                            TextView t = new TextView(HomeFragment.this.getContext());
                            t.setText("Nothing Special :(");
                            t.setTextSize(30);
                            favLinLay.addView(t);
                        } else
                            for (View t : texts)
                                favLinLay.addView(t);

                    }
                });
            }
        });
        thread.start();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);

            }
        });
        checkFavs();

        return root;
    }
}