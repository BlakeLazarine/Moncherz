package com.example.moncherz;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class Utilities {
    public static final int numPlaces = 4;
    public static final int BPlate = 0;
    public static final int Covel = 1;
    public static final int DeNeve = 2;
    public static final int Feast = 3;
    public static final String[] placeHumanNames = {"BPlate", "Covel", "DeNeve", "Feast"};
    public static final String[] placeURLNames = {"BruinPlate", "Covel", "DeNeve", "FeastAtRieber"};

    public static final int numTimes = 3;
    public static final int Breakfast = 0;
    public static final int Lunch = 1;
    public static final int Dinner = 2;
    public static final String[] timeNames = {"Breakfast", "Lunch", "Dinner"};

    public static ArrayList<String>[][] foods = new ArrayList[numPlaces][numTimes];
    public static ArrayList<Integer>[][] sectIdx = new ArrayList[numPlaces][numTimes];
    public static ArrayList<String>[][] sectNames = new ArrayList[numPlaces][numTimes];

    public static ArrayList<String> favs;

    public static class Status implements java.io.Serializable{
        public String updateDate;
        public boolean loaded;
        //This is where notification settings and stuff like that will go
    }
    public static Status stats;

    public static boolean done = false;
    public static boolean badInternet = false;

    //binary search method heavily heavily based on https://www.javatpoint.com/binary-search-in-java
    public static boolean binarySearch(ArrayList<String> arr, String key){
        int first = 0;
        int last = arr.size()-1;
        int mid = (first + last)/2;
        if(last == -1)
            return false;
        while( first <= last ){
            if ( arr.get(mid).compareTo(key) < 0){
                first = mid + 1;
            }else if ( arr.get(mid).equals(key) ){
                return true;
            }else{
                last = mid - 1;
            }
            mid = (first + last)/2;
        }
        if ( first > last ){
            return false;
        }
        return true;
    }


    //the 2 network checking methods are straight-up from this stackoverflow page
    //https://stackoverflow.com/questions/6493517/detect-if-android-device-has-internet-connection/25816086#25816086
    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static boolean hasInternetAccess(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0);
            } catch (IOException e) {
                Log.e(TAG, "Error checking internet connection", e);
            }
        } else {
            Log.d(TAG, "No network available!");
        }
        return false;
    }

    public static void grabData(String date) throws IOException {
        for (int p = 0; p < numPlaces; p++) {
            for (int t = 0; t < numTimes; t++) {
                foods[p][t] = new ArrayList<String>();
                sectIdx[p][t] = new ArrayList<Integer>();
                sectNames[p][t] = new ArrayList<String>();
                HttpURLConnection urlConnection = null;
                HttpURLConnection conn = null;
                URL url = new URL("http://menu.dining.ucla.edu/Menus/" + placeURLNames[p] + "/" + date + "/" + timeNames[t]);
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
    }

    private static class Menu implements java.io.Serializable{
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
        if(binarySearch(favs, s)) {
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
        if(!binarySearch(favs, s)) {
            favs.add(s);
            Collections.sort(favs);
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
            FileInputStream fis = context.openFileInput("Favorites");
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
            FileOutputStream fos = context.openFileOutput("Stats", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(stats);
            os.close();
            fos.close();
        } catch(Exception e) {
        }
    }

    public static void loadStatus(Context context) {
        try {
            FileInputStream fis = context.openFileInput("Stats");
            ObjectInputStream is = new ObjectInputStream(fis);
            stats = (Status) is.readObject();
            is.close();
            fis.close();
        } catch(Exception e) {
            Log.d("MESSAGE", e.toString());

        }
    }

    public static void startUp(Context context) {
        final String files[] = context.fileList();
        boolean hasStats = false;
        boolean hasFavs = false;
        for (int i = 0; i < files.length; i++) {
//            Log.d("MESSAGE", "HERE" + files[i]);
            if(files[i].equals("Stats"))
//                Log.d("MESSAGE", "HERE2");
                hasStats = true;
            if(files[i].equals("Favorites"))
//                Log.d("MESSAGE", "HERE3");
                hasFavs = true;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = "2019-12-11";//sdf.format(new Date());
        //HEY YOU, YOU SHOULD UNCOMMENT THAT LINE ABOVE THIS ONE. IMA MAKE THIS REAL OBNOXIOUS
        //**************************************************************************************
        //**************************************************************************************
        //**************************************************************************************
        //**************************************************************************************
        //**************************************************************************************
        //**************************************************************************************
        //**************************************************************************************
        //**************************************************************************************
        //**************************************************************************************
        //**************************************************************************************
        if(hasStats) {

            loadStatus(context);
            stats.loaded = true;
            if(stats.updateDate.equals(currentDate)) {
                loadMenu(context);
            } else {
                try {
                    if(hasInternetAccess(context)) {
                        grabData(currentDate);
                        saveMenu(context);
                        stats.updateDate = currentDate;
                    } else {
                        badInternet = true;
                    }

                } catch(Exception e) {

                }
            }
        } else {
            stats = new Status();
            stats.loaded = false;
            try {
                if(hasInternetAccess(context)) {
                    grabData(currentDate);
                    saveMenu(context);
                    stats.updateDate = currentDate;
                    saveStatus(context);
                } else {
                    badInternet = true;
                }

            } catch(Exception e) {

            }
        }

        if(hasFavs) {
            loadFavs(context);
        } else {
            favs = new ArrayList<>();
        }

        done = true;
    }
}
