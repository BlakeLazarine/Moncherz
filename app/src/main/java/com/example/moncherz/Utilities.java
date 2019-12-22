package com.example.moncherz;

import android.content.Context;
import android.text.Html;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
    public static final int numPlaces = 1;
    public static final int BPlate = 0;
    public static final int Covel = 1;
    public static final int DeNeve = 2;
    public static final int Feast = 3;
    public static final String[] placeNames = {"BruinPlate", "Covel", "DeNeve", "Feast"};

    public static final int numTimes = 3;
    public static final int Breakfast = 0;
    public static final int Lunch = 1;
    public static final int Dinner = 2;
    public static final String[] timeNames = {"Breakfast", "Lunch", "Dinner"};

    public static ArrayList<String>[][] foods = new ArrayList[numPlaces][numTimes];
    public static ArrayList<Integer>[][] sectIdx = new ArrayList[numPlaces][numTimes];
    public static ArrayList<String>[][] sectNames = new ArrayList[numPlaces][numTimes];

    public static ArrayList<String> favs = new ArrayList<>();

    public static class Status {
        public String updateDate;
        //This is where notification settings and stuff like that will go
    }
    public static Status stats;

    public static Boolean done = false;

    public static void grabData() throws IOException {
        for (int p = 0; p < numPlaces; p++) {
            for (int t = 0; t < numTimes; t++) {
                foods[p][t] = new ArrayList<String>();
                sectIdx[p][t] = new ArrayList<Integer>();
                sectNames[p][t] = new ArrayList<String>();
                HttpURLConnection urlConnection = null;
                HttpURLConnection conn = null;
                URL url = new URL("http://menu.dining.ucla.edu/Menus/" + placeNames[p] + "/2019-12-11/" + timeNames[t]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                final BufferedReader br = new BufferedReader(new InputStreamReader(in));
                Log.d("YOYOYOOYOYYOYOYOYOYOYO", "IT GOT HERE DAWWWG");

                String line = null;
                while ((line = br.readLine()) != null) {
                    String foodPattern = ".*recipelink\" href=\"http.*\">(.*)<.*";

                    Pattern r = Pattern.compile(foodPattern);
                    Matcher m = r.matcher(line);
                    if (m.matches()) {
                        String s = "<p>" + m.group(1) + "</p>";
                        foods[p][t].add(Html.fromHtml(s).toString().trim());
                        continue;
                    }
                    String sectionPattern = ".*<li class=\"sect-item\">.*";
                    Pattern pat = Pattern.compile(sectionPattern);
                    Matcher mat = pat.matcher(line);
                    if (mat.matches()) {
                        line = br.readLine();
                        sectIdx[p][t].add(foods[p][t].size());
                        sectNames[p][t].add(line.trim());
                    }
                }
                urlConnection.disconnect();
            }
        }

        done = true;
    }

    private static class Menu {
        public ArrayList<String>[][] f;
        public ArrayList<Integer>[][] i;
        public ArrayList<String>[][] n;
    }

    public static void saveMenu(Context context) {

        Menu m = new Menu();
        m.f = foods;
        m.i = sectIdx;
        m.n = sectNames;
        try {
            FileOutputStream fos = context.openFileOutput("TodayMenu", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(m);
            os.close();
            fos.close();
        } catch(Exception e) {
        }
    }

    public static void loadMenu(Context context) {
        try {
            FileInputStream fis = context.openFileInput("TodayMenu");
            ObjectInputStream is = new ObjectInputStream(fis);
            Menu m = (Menu) is.readObject();
            is.close();
            fis.close();
            foods = m.f;
            sectIdx = m.i;
            sectNames = m.n;
        } catch(Exception e) {

        }
    }
    public static void removeFav(String s, Context context) {
        if(favs.contains(s)) {
            favs.remove(s);
            try {
                FileOutputStream fos = context.openFileOutput("Favorites", Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(favs);
                os.close();
                fos.close();
            } catch(Exception e) {
            }
        }
    }

    public static void addFav(String s, Context context) {
        if(!favs.contains(s)) {
            favs.add(s);
            try {
                FileOutputStream fos = context.openFileOutput("Favorites", Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(favs);
                os.close();
                fos.close();
            } catch(Exception e) {
            }
        }
    }

    public static void loadFavs(Context context) {
        try {
            FileInputStream fis = context.openFileInput("TodayMenu");
            ObjectInputStream is = new ObjectInputStream(fis);
            ArrayList<String> f = (ArrayList<String>) is.readObject();
            is.close();
            fis.close();
            favs = f;
        } catch(Exception e) {

        }
    }

    public static void saveStatus(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput("TodayMenu", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(stats);
            os.close();
            fos.close();
        } catch(Exception e) {
        }
    }

    public static void loadStatus(Context context) {
        try {
            FileInputStream fis = context.openFileInput("TodayMenu");
            ObjectInputStream is = new ObjectInputStream(fis);
            stats = (Status) is.readObject();
            is.close();
            fis.close();
        } catch(Exception e) {

        }
    }

}
