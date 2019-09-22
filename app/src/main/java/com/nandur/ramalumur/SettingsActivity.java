package com.nandur.ramalumur;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

import java.text.MessageFormat;

import static com.nandur.ramalumur.MainActivity.*;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            //Preference prefAbout = findPreference("about");
            Preference prefCheckUpdate = findPreference("check_update");
            Preference prefSendFeedback = findPreference("feedback");

            if (prefCheckUpdate != null) {
                prefCheckUpdate.setOnPreferenceClickListener(preference -> {
                        new AppUpdater(SettingsActivity.this)
                                //.setUpdateFrom(UpdateFrom.GITHUB)
                                //.setGitHubUserAndRepo("javiersantos", "AppUpdater")
                                .setUpdateFrom(UpdateFrom.XML)
                                .setUpdateXML("https://raw.githubusercontent.com/nandur93/KNers/master/update-changelog.xml")
                                .setDisplay(Display.DIALOG)
                                .setButtonDoNotShowAgain(null)
                                .showAppUpdated(true)
                                .start();
                        return true;
                    });
            }

            if (prefSendFeedback != null) {
                prefSendFeedback.setOnPreferenceClickListener(preference -> {
                    sendFeedback(SettingsActivity.this);
                    return true;
                });
            }
        }
    }

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        String feedBody = context.getString(R.string.feedback_body);
        String appName = context.getString(R.string.app_name);
        String emailClient = context.getString(R.string.choose_email_client);
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nTolong jangan hapus bagian ini\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"nandang.dhe@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, MessageFormat.format("{0} {1} {2}", feedBody, appName, versName));
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, emailClient));
    }
}