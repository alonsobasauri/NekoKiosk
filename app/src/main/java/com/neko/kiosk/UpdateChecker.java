package com.neko.kiosk;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import androidx.core.content.FileProvider;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private static final String TAG = "UpdateChecker";
    private static final String UPDATE_URL = "https://gist.githubusercontent.com/alonsobasauri/c12c8fa607f41c130da03b801b73c66a/raw/version.json";

    private Activity activity;
    private long downloadId = -1;

    public UpdateChecker(Activity activity) {
        this.activity = activity;
    }

    public void checkForUpdates() {
        new Thread(() -> {
            try {
                URL url = new URL(UPDATE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                int latestVersionCode = json.getInt("versionCode");
                String apkUrl = json.getString("apkUrl");

                int currentVersionCode = getCurrentVersionCode();

                Log.d(TAG, "Current version: " + currentVersionCode + ", Latest version: " + latestVersionCode);

                if (latestVersionCode > currentVersionCode) {
                    activity.runOnUiThread(() -> downloadAndInstallUpdate(apkUrl));
                } else {
                    Log.d(TAG, "App is up to date");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error checking for updates: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private int getCurrentVersionCode() {
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    private void downloadAndInstallUpdate(String apkUrl) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
            request.setTitle("Neko Kiosk Update");
            request.setDescription("Downloading update...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalFilesDir(activity, Environment.DIRECTORY_DOWNLOADS, "update.apk");

            DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadId = downloadManager.enqueue(request);

            // Register receiver for download completion
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (id == downloadId) {
                        installUpdate();
                        activity.unregisterReceiver(this);
                    }
                }
            };

            activity.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        } catch (Exception e) {
            Log.e(TAG, "Error downloading update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void installUpdate() {
        try {
            File downloadedFile = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "update.apk");

            if (!downloadedFile.exists()) {
                Log.e(TAG, "Downloaded APK file not found");
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(activity,
                    activity.getPackageName() + ".fileprovider", downloadedFile);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.setDataAndType(Uri.fromFile(downloadedFile), "application/vnd.android.package-archive");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);

        } catch (Exception e) {
            Log.e(TAG, "Error installing update: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
