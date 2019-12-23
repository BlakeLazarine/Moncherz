package com.example.moncherz.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.moncherz.MenuFragment;
import com.example.moncherz.R;
import com.example.moncherz.Utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                final String files[] = HomeFragment.this.getContext().fileList();

                HomeFragment.this.getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        LinearLayout favLinLay = HomeFragment.this.getView().findViewById(R.id.FavLinLay);
                        TextView t = new TextView(HomeFragment.this.getContext());

                        t.setText(files[0] + " " + files[1] + " " + files.length);
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