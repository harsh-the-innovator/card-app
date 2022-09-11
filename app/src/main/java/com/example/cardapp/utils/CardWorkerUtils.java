package com.example.cardapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.cardapp.R;
import com.example.cardapp.constants.Constants;

public final class CardWorkerUtils {
    private CardWorkerUtils() {
    }

    public static void makeStatusNotification(String msg, Context applicationContext) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = Constants.VERBOSE_NOTIFICATION_CHANNEL_NAME;
            String description = Constants.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =  new NotificationChannel(Constants.CHANNEL_ID,name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if(notificationManager!=null){
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(applicationContext,Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(Constants.NOTIFICATION_TITLE)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0]);

        NotificationManagerCompat.from(applicationContext).notify(Constants.NOTIFICATION_ID,builder.build());
    }

    public static void sleep(){
        try {
            Thread.sleep(Constants.DELAY_TIME_MILLIS,0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap overlayTextOnBitmap(@NonNull Bitmap bitmap,@NonNull Context applicationContext,@NonNull String quote){
        return null;
    }
}
