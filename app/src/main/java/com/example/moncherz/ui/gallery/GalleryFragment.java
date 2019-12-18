package com.example.moncherz.ui.gallery;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.moncherz.R;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    public void grabData() throws IOException {

        HttpURLConnection urlConnection = null;
        HttpURLConnection conn = null;
        URL url = new URL("http://menu.dining.ucla.edu/Menus/DeNeve/2019-12-11/Breakfast");
        urlConnection = (HttpURLConnection) url.openConnection();

        InputStream in = new BufferedInputStream(urlConnection.getInputStream());

        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        final ArrayList<String> names = new ArrayList<String>();
        final ArrayList<Integer> sectIdx = new ArrayList<Integer>();
        final ArrayList<String> sectNames = new ArrayList<String>();

        String line = null;
        while ((line = br.readLine()) != null) {
            String foodPattern = ".*recipelink\" href=\"http.*\">(.*)<.*";

            Pattern r = Pattern.compile(foodPattern);
            Matcher m = r.matcher(line);
            if (m.matches()) {
                names.add(m.group(1));
                continue;
            }
            String sectionPattern = ".*<li class=\"sect-item\">.*";
            Pattern p = Pattern.compile(sectionPattern);
            Matcher mat = p.matcher(line);
            if (mat.matches()) {
                line = br.readLine();
                sectIdx.add(names.size());
                sectNames.add(line.trim());
            }
        }
        urlConnection.disconnect();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
            //code to do the HTTP request

            GalleryFragment.this.getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    LinearLayout linLay = GalleryFragment.this.getView().findViewById(R.id.linLay);
                    int nextSect = 0;
                    for(int i = 0; i < names.size(); i++) {

                        if(i == sectIdx.get(nextSect)) {
                            TextView sect = new TextView(GalleryFragment.this.getContext());
                            sect.setText(sectNames.get(nextSect));
                            sect.setTextSize(36);
                            sect.setTextColor(0xFF008577);
                            if(nextSect < sectNames.size() - 1)
                                nextSect++;
                            linLay.addView(sect);
                        }
                        TextView f = new TextView(GalleryFragment.this.getContext());
                        String s = "<p>" + names.get(i) + "</p>";
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);



        galleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);

            }
        });


        class PrimeThread extends Thread {

            public void run() {
                try {
                    Log.d("RAWR ", "RAWWWWR");
                    grabData();
                } catch (IOException e) {
                    Log.d("BIGBADERROR: ", "dhjasklhfjaskl");
                }
            }
        }


        PrimeThread pt = new PrimeThread();
        pt.start();


        return root;
    }
}