package com.manech.app.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.manech.app.MainActivity;
import com.manech.app.R;
import com.manech.app.helper.MyColor;
import com.manech.app.model.Injection;
import com.manech.app.model.MNodeViewModel;

import static com.manech.app.App.CHANNEL_ID;

public class ManechService extends Service {
    private MNodeViewModel mNodeViewModel;

    @Override
    public void onCreate() {
        super.onCreate();
        mNodeViewModel = Injection.provideViewModelFactory(getApplicationContext()).create(MNodeViewModel.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final long userId = getApplicationContext()
                .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                .getLong(getString(R.string.saved_user_id), -1L);
        if (userId != -1) {
            final String action;
            if (intent != null && (action = intent.getAction()) != null) {
                if (action.equals("animate")) {

                    final int[] indexes = intent.getIntArrayExtra("indexes");
                    final int[] rgbColor = intent.getIntArrayExtra("color");
                    if (rgbColor == null || rgbColor.length < 3)
                        mNodeViewModel.locate(userId, indexes, MyColor.random(0, 200));
                    else
                        mNodeViewModel.locate(userId, indexes, new MyColor(rgbColor[0], rgbColor[1], rgbColor[2]));


                    //
                    Intent notificationIntent = new Intent(this, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

                    Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText("animated")
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentIntent(pendingIntent)
                            .build();
                    //
                    startForeground(1, notification);

                }
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
