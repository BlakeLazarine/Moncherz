package com.example.moncherz;

import android.app.FragmentTransaction;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TextView t = new TextView(this);
        class grabThread extends Thread {

            public void run() {
                try {
                    Utilities.startUp(MainActivity.this);

                } catch (Exception e) {
                    Log.d("BIGBADERROR: ", "Could not Grab Data");
                }
            }
        }

        grabThread gt = new grabThread();
        gt.start();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (Utilities.done) {
                    Utilities.done = false;
                    class refreshThread extends Thread {

                        public void run() {
                            try {
                                Snackbar.make(view, "Refreshing", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                Utilities.startUp(MainActivity.this);
                                Snackbar.make(view, "Done! Retap the sidebar option now plz", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                            } catch (Exception e) {
                                Log.d("BIGBADERROR: ", "Could not Grab Data");
                            }
                        }
                    }
                    refreshThread rt = new refreshThread();
                    rt.start();

                } else
                    Snackbar.make(view, "Hold your horses, I'm working!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_bplate, R.id.nav_covel,
                R.id.nav_deneve, R.id.nav_feast, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
