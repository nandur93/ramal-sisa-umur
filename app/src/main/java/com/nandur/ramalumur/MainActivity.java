package com.nandur.ramalumur;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TOTAL_CLICK = "total_click";
    private static final String TAG_ADMOB = "Admob";
    private TextView tvResult;
    private TextInputEditText nama;
    public static TextInputEditText usia;
    public static String versName;
    public static int versCode;
    private DrawerLayout drawer;
    private Handler handler;
    private SharedPreferences sharedPrefs;
    private int totalClick;
    private SharedPreferences.Editor editor;
    private MaterialButton buttResult;
    private RewardedAd rewardedAd;
    private String predictStr;
    private String adViewStr;
    public static TextInputEditText textBirthDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        handler = new Handler();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPrefs.edit();
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        MobileAds.initialize(this, initializationStatus -> {
        });
        rewardedAd = new RewardedAd(this,
                getResources().getString(R.string.real_rewarded_video_ad_unit_id));
        buttResult = findViewById(R.id.buttonCheck);
        tvResult = findViewById(R.id.textViewResult);
        TextClock textClock = findViewById(R.id.textClock);
        String timeZone = TimeZone.getDefault().getID();
        textClock.setTimeZone(timeZone);
        totalClick = sharedPrefs.getInt(TOTAL_CLICK,0);
        nama = findViewById(R.id.textInputName);
        usia = findViewById(R.id.textInputUsia);
        textBirthDay = findViewById(R.id.textInputBirthday);
        predictStr = getResources().getString(R.string.button_check);
        adViewStr = getResources().getString(R.string.button_show_ad);
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(getResources().getString(R.string.ad_test_device))
                .build();
        mAdView.loadAd(adRequest);

        // Ad successfully loaded.
        // Ad failed to load.
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                Log.d(TAG_ADMOB, "Iklan berhasil diload siap ditampilkan");
                buttResult.setEnabled(true);
                if (sharedPrefs.getInt(TOTAL_CLICK, 1) >= 6) {
                    buttResult.setText(adViewStr);
                } else {
                    buttResult.setText(predictStr);
                }
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                Log.d(TAG_ADMOB, "Iklan gagal diload, iklan tidak bisa ditampilkan");
            }
        };

        //initial load ad
        Log.d(TAG_ADMOB,"-- Permintaan iklan dibuat --");
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        //initial count the current click from shared prefs
        if (sharedPrefs.getInt(TOTAL_CLICK,1)<6){
            //clik kurang dari 6
            buttResult.setText(predictStr);
        } else {
            //total click 6 atau lebih
            buttResult.setText(adViewStr);
        }

        textBirthDay.setOnClickListener(View -> showDatePicker());

        buttResult.setOnClickListener(View -> {
            //close keyboard
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(nama.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(usia.getWindowToken(),0);
            //jika button bertuliskan adview
            if (buttResult.getText().toString().equals(adViewStr)){
                Log.d(TAG_ADMOB,adViewStr);
                if (rewardedAd.isLoaded()) {
                    Activity activityContext = MainActivity.this;
                    RewardedAdCallback adCallback = new RewardedAdCallback() {

                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened. Iklan berjalan
                            Log.d(TAG_ADMOB,"Menjalankan iklan... Iklan sedang berjalan");
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.watch_ad_to_get_free), Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
                            Log.d(TAG_ADMOB,"Iklan ditutup oleh user...");
                            Toast.makeText(getApplicationContext(),"Ad Closed", Toast.LENGTH_LONG).show();
                            if (sharedPrefs.getInt(TOTAL_CLICK,1)<=3){
                                buttResult.setText(predictStr);
                                Log.d(TAG_ADMOB,"...setelah iklan ditonton secara penuh --");
                            } else {
                                buttResult.setText(adViewStr);
                                Log.d(TAG_ADMOB,"...ditengah jalan --");
                            }
                            rewardedAd = createAndLoadRewardedAd();
                        }
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            // User earned reward.
                            Log.d(TAG_ADMOB,"Video ditonton secara penuh, user mendapat reward");
                            editor.putInt(TOTAL_CLICK, 3).apply();
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.congrats_got_free), Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onRewardedAdFailedToShow(int errorCode) {
                            // Ad failed to display
                            Log.d(TAG_ADMOB,"onRewardedAdFailedToShow");
                            Toast.makeText(getApplicationContext(),"Ad failed to load", Toast.LENGTH_LONG).show();
                            buttResult.setText(adViewStr);
                        }
                    };
                    Log.d(TAG_ADMOB,"Menjalankan adCallback");
                    rewardedAd.show(activityContext, adCallback);
                } else {
                    buttResult.setEnabled(false);
                    buttResult.setText(getString(R.string.ad_unavailable));
                    Log.d(TAG_ADMOB, getString(R.string.ad_unavailable));
                }
            } else if (Objects.requireNonNull(nama.getText()).length()<2){
                Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_name_limit), Toast.LENGTH_SHORT).show();
            } else if (Objects.requireNonNull(nama.getText()).toString().equals("")){
                Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_empty_nameage), Toast.LENGTH_SHORT).show();
            } else if(Objects.requireNonNull(usia.getText()).toString().equals("")){
                Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_empty_nameage), Toast.LENGTH_SHORT).show();
            } else {
                //button bertuliskan predict
                predict();
                //then count click
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

        // get menu from navigationView
        Menu menu = navigationView.getMenu();
        // find MenuItem you want to change
        MenuItem nav_appversion = menu.findItem(R.id.nav_version_name);
        // set new title to the MenuItem
        nav_appversion.setTitle(versName);
    }

    @SuppressWarnings("WeakerAccess")
    public void showDatePicker(){
        DialogFragment newFragment = new MyDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "date picker");
    }

    private RewardedAd createAndLoadRewardedAd() {
        RewardedAd rewardedAd = new RewardedAd(this,
                getResources().getString(R.string.real_rewarded_video_ad_unit_id));
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                Log.d(TAG_ADMOB,"Iklan kedua berhasil diload siap ditampilkan");
                buttResult.setEnabled(true);
                if(sharedPrefs.getInt(TOTAL_CLICK,1)>=6){
                    buttResult.setText(adViewStr);
                } else {
                    buttResult.setText(predictStr);
                }
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                Log.d(TAG_ADMOB,"Iklan kedua gagal diload, iklan tidak bisa ditampilkan");
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
    }

    private void predict() {
        //setupMinMax
        int min = Integer.parseInt(Objects.requireNonNull(usia.getText()).toString());
        int max;
        if (min>=80) {
            String[] tua = getResources().getStringArray(R.array.tua);
            String rndTua = tua[new Random().nextInt(tua.length)];
            tvResult.setText(rndTua);
        } else if (min<=10) {
            String[] bocah = getResources().getStringArray(R.array.bocah);
            String rndBch = bocah[new Random().nextInt(bocah.length)];
            tvResult.setText(rndBch);
        } else {
            max = 80;
            int random = new Random().nextInt((max - min) + 1) + min;
            String[] death = getResources().getStringArray(R.array.death);
            String randomStr = death[new Random().nextInt(death.length)];
            Resources res = getResources();
            String yourName = Objects.requireNonNull(nama.getText()).toString();
            int usiaInt = Integer.parseInt(usia.getText().toString());
            String yourAge = res.getQuantityString(R.plurals.numberOfYear, usiaInt, usiaInt);
            String yearTotal = res.getQuantityString(R.plurals.numberOfYear, random-min,random-min);
            tvResult.setText(MessageFormat.format(getResources().getString(R.string.predict_pattern), yourName, yourAge, yearTotal, randomStr));
        }
        totalClick = sharedPrefs.getInt(TOTAL_CLICK, 1)+1;
        if (totalClick == 1) {//first time clicked to do this
            //Toast.makeText(getApplicationContext(), String.valueOf(totalClick), Toast.LENGTH_LONG).show();
            editor.putInt(TOTAL_CLICK, totalClick).apply();
        } else if (totalClick == 2) {//first time clicked to do this
            //Toast.makeText(getApplicationContext(), String.valueOf(totalClick), Toast.LENGTH_LONG).show();
            editor.putInt(TOTAL_CLICK, totalClick).apply();
        } else if (totalClick == 3) {//first time clicked to do this
            //Toast.makeText(getApplicationContext(), String.valueOf(totalClick), Toast.LENGTH_LONG).show();
            editor.putInt(TOTAL_CLICK, totalClick).apply();
        } else if (totalClick == 4) {//first time clicked to do this
            //Toast.makeText(getApplicationContext(), String.valueOf(totalClick), Toast.LENGTH_LONG).show();
            editor.putInt(TOTAL_CLICK, totalClick).apply();
        } else if (totalClick == 5) {//first time clicked to do this
            //Toast.makeText(getApplicationContext(), String.valueOf(totalClick), Toast.LENGTH_LONG).show();
            editor.putInt(TOTAL_CLICK, totalClick).apply();
        } else if (totalClick == 6) {//ganti tombol dengan rewarded ads
            buttResult.setText(adViewStr);
            Log.d(TAG_ADMOB, "Total klik sudah mecapai "+sharedPrefs.getInt(TOTAL_CLICK, 1));
            editor.putInt(TOTAL_CLICK, totalClick).apply();
        }
    }

    private void shareApp() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareSubject = getResources().getString(R.string.app_name);
        String bitly = getResources().getString(R.string.bitly_share)+getResources().getString(R.string.bitly_dynamic);
        String direct = getResources().getString(R.string.bitly_share)+getResources().getString(R.string.bitly_direct);
        String hashtag = getResources().getString(R.string.hashtag);
        String ofcWeb = getResources().getString(R.string.ofc_website);
        String download = getResources().getString(R.string.direct_download);
        String shareBody = tvResult.getText().toString()+"\n\n"+ofcWeb+bitly+"\n"+download+direct+"\n"+hashtag.trim();
        String shareVia = getResources().getString(R.string.menu_send);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject+" "+getResources().getString(R.string.version_title)+" "+versName+" "+getResources().getString(R.string.build_title)+" "+versCode);
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
            case R.id.nav_tools:
                closeDrawer();
                handler.postDelayed(this::goToSetting,250);
                break;
            case R.id.nav_share:
                closeDrawer();
                handler.postDelayed(this::shareApp,250);
                break;
            case R.id.nav_version_name:
                closeDrawer();
                handler.postDelayed(this::onInfoVersionName, 250);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onInfoVersionName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.version_title);
        builder.setMessage("Version Name: "+versName+"\n"+"Version Code: "+versCode);
        builder.setIcon(R.drawable.ic_info_outline_black_24dp);
        AlertDialog diag = builder.create();
        //Display the message!
        diag.show();
    }

    private void closeDrawer(){
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }  //super.onBackPressed();

    }
}
