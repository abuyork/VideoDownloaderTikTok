package com.abuyork.tiktokvideodownloader.services;

import android.app.*;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.abuyork.tiktokvideodownloader.MainActivity;
import com.abuyork.tiktokvideodownloader.R;
import com.abuyork.tiktokvideodownloader.tasks.downloadVideo;

import static com.abuyork.tiktokvideodownloader.utils.Constants.STARTFOREGROUND_ACTION;
import static com.abuyork.tiktokvideodownloader.utils.Constants.STOPFOREGROUND_ACTION;

public class ClipboardMonitor extends Service {
    private ClipboardManager mCM;
    IBinder mBinder;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    int mStartMode;

     @Override
    public void onCreate() {
        super.onCreate();
         mCM = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
         mCM.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);

     }
     @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       try {
           String action = intent.getAction();

           switch (action) {
               case STARTFOREGROUND_ACTION:
                   startInForeground();
                   break;
               case STOPFOREGROUND_ACTION:

                   stopForegroundService();
                   break;
           }
       }catch(Exception e) {
           System.out.println("Null pointer exception");
       }




        return START_STICKY;
    }
    private  void stopForegroundService(){

        Log.d("Foreground", "Stop foreground service.");
        prefs = getSharedPreferences("tikVideoDownloader", MODE_PRIVATE);

        editor=prefs.edit();

        editor.putBoolean("csRunning",false);
        editor.commit();

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
        mCM.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }
    private void startInForeground() {


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,getPackageName()+"-"+getString(R.string.app_name))
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .setContentTitle("Auto Download Service")
                .setContentText("Copy the link of video to start download")
                .setTicker("TICKER")
                .addAction(R.drawable.navigation_empty_icon,  "Stop", makePendingIntent(STOPFOREGROUND_ACTION))
                .setContentIntent(pendingIntent);
        Notification notification=builder.build();
        if(Build.VERSION.SDK_INT>=26) {
            NotificationChannel channel = new NotificationChannel(getPackageName()+"--"+getString(R.string.app_name), getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Tiktok Auto Download");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        prefs = getSharedPreferences("tikVideoDownloader", MODE_PRIVATE);
        editor = prefs.edit();
        //stopSelf();
        startForeground(1002, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
       // this.stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("destroyed",  "123123");
        stopForeground(true);
        stopSelf();
        if (mCM != null) {
            mCM.removePrimaryClipChangedListener(
                    mOnPrimaryClipChangedListener);
        }
        prefs = getSharedPreferences("tikVideoDownloader", MODE_PRIVATE);

//        if(prefs.getBoolean("csRunning",false)) {
//                Intent broadcastIntent = new Intent();
//                broadcastIntent.setAction("restartservice");
//                broadcastIntent.setClass(this, Restarter.class);
//                this.sendBroadcast(broadcastIntent);
//            }
    }
    public PendingIntent makePendingIntent(String name) {
        Intent intent = new Intent(getApplicationContext(), ClipboardMonitor.class);
         intent.setAction(name);
        return PendingIntent.getService(getApplicationContext(), 0, intent, 0);
    }

    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                     String newClip = mCM.getPrimaryClip().getItemAt(0).getText().toString();
                 //   Toast.makeText(getApplicationContext(), newClip, Toast.LENGTH_LONG).show();
                    Log.i("LOGClipboard", newClip + "");

                    downloadVideo.Start(getApplicationContext(),newClip,true);
                }
            };

}

