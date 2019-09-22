package com.nandur.ramalumur;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

import java.text.MessageFormat;
import java.util.Objects;

import static com.nandur.ramalumur.MainActivity.versCode;
import static com.nandur.ramalumur.MainActivity.versName;

public class SettingsActivity extends AppCompatActivity {

    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            //Preference prefAbout = findPreference("about");
            Preference prefVersion = findPreference("current_version");
            Preference prefCheckUpdate = findPreference("check_update");
            Preference prefSendFeedback = findPreference("feedback");

            //getVersionName
            if (prefVersion != null) {
                prefVersion.setSummary("Vers "+versName+" Build "+versCode);
            }

            if (prefVersion != null) {
                prefVersion.setOnPreferenceClickListener(preference -> {
                    showInstalledAppDetails(getContext(), Objects.requireNonNull(getActivity()).getPackageName());
                    return true;
                });
            }

            if (prefCheckUpdate != null) {
                prefCheckUpdate.setOnPreferenceClickListener(preference -> {
                    new AppUpdater(Objects.requireNonNull(getContext()))
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
                    sendFeedback(Objects.requireNonNull(getContext()));
                    return true;
                });
            }
        }

        private void showInstalledAppDetails(Context context, String packageName) {
            Intent intent = new Intent();
            final int apiLevel = Build.VERSION.SDK_INT;
            if (apiLevel >= 9) { // above 2.3
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts(SCHEME, packageName, null);
                intent.setData(uri);
            } else { // below 2.3
                final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                        : APP_PKG_NAME_21);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                        APP_DETAILS_CLASS_NAME);
                intent.putExtra(appPkgName, packageName);
            }
            context.startActivity(intent);

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
        intent.putExtra(Intent.EXTRA_SUBJECT, MessageFormat.format("{0} {1} v{2} b{3}", feedBody, appName, versName, versCode));
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, emailClient));
    }
}