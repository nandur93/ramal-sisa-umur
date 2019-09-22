package com.nandur.ramalumur;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvResult;
    private TextInputEditText nama;
    private TextInputEditText usia;
    public static String versName;
    public static int versCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        Button buttResult = findViewById(R.id.buttonCheck);
        tvResult = findViewById(R.id.textViewResult);
        nama = findViewById(R.id.TextInputName);
        usia = findViewById(R.id.TextInputUsia);
        buttResult.setOnClickListener(View -> {
            if (Objects.requireNonNull(nama.getText()).toString().equals("")){
                Toast.makeText(MainActivity.this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else if(Objects.requireNonNull(usia.getText()).toString().equals("")){
                Toast.makeText(MainActivity.this, "Umur tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else {
                predict();
            }
        });

        //getVersionName
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versName = pInfo.versionName;
            versCode = pInfo.versionCode;
            Log.d("MyApp", "Version Name : " + versName + "\n Version Code : " + versCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.d("MyApp", "PackageManager Catch : " + e.toString());
        }
    }

    private void predict() {
        //setupMinMax
        int min = Integer.parseInt(Objects.requireNonNull(usia.getText()).toString());
        int max;
        if (min>=70) {
            String[] tua = getResources().getStringArray(R.array.tua);
            String rndTua = tua[new Random().nextInt(tua.length)];
            tvResult.setText(rndTua);
        } else if (min<=15) {
            String[] bocah = getResources().getStringArray(R.array.bocah);
            String rndBch = bocah[new Random().nextInt(bocah.length)];
            tvResult.setText(rndBch);
        } else {
            max = 70;
            int random = new Random().nextInt((max - min) + 1) + min;
            String[] death = getResources().getStringArray(R.array.death);
            String randomStr = death[new Random().nextInt(death.length)];
            tvResult.setText(MessageFormat.format("Hai {0}, sisa umur loe adalah {1} tahun. Loe bakalan mati secara {2}.", Objects.requireNonNull(nama.getText()).toString(), random-min, randomStr));
        }
    }

    private void shareApp() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareSubject = getResources().getString(R.string.app_name);
        String shareBody = tvResult.getText().toString(); //getResources().getString(R.string.share_body);
        String shareVia = getResources().getString(R.string.menu_send);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject+" Versi "+versName+" Build "+versCode);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, shareVia));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            goToSetting();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToSetting() {
            Intent settingsIntent = new
                    Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingsIntent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                // Handle the camera action
                break;
            case R.id.nav_gallery:

                break;
            case R.id.nav_slideshow:

                break;
            case R.id.nav_tools:

                break;
            case R.id.nav_share:
                shareApp();
                break;
            case R.id.nav_send:

                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
