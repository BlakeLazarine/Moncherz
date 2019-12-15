package com.example.moncherz.ui.gallery;

import android.os.Bundle;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.moncherz.R;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    public void grabData() throws IOException {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //code to do the HTTP request
                HttpURLConnection urlConnection = null;
                try {
                    HttpURLConnection conn = null;
                    URL url = new URL("http://menu.dining.ucla.edu/Menus/Covel/Yesterday/Lunch");
                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        String pattern = ".*recipelink\".*\">(.*)<.*";
                        Pattern r = Pattern.compile(pattern);
                        Matcher m = r.matcher(line);
                        if (m.matches())
                            Log.d("FOOD: ", m.group(1));
                    }
                } catch (IOException e) {
                    Log.d("test: ", e.toString());
                } finally {
                    urlConnection.disconnect();
                }

            }
        });

        thread.start();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        final LinearLayout linLay = root.findViewById(R.id.linLay);

        for(int i = 0; i < 100; i++) {
            TextView f = new TextView(this.getContext());
            f.setText("hi");
            linLay.addView(f);
        }

        galleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);

            }
        });



        try {
            Log.d("RAWR ", "RAWWWWR");
            grabData();
        } catch (IOException e) {
            Log.d("BIGBADERROR: ", "dhjasklhfjaskl");
        }



        return root;
    }
}