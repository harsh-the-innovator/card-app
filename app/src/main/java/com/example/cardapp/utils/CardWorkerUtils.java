package com.example.cardapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.cardapp.R;
import com.example.cardapp.constants.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

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

        // Create the output bitmap
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmap.getConfig());

        Canvas canvas = new Canvas(output);
        float scale = applicationContext.getResources().getDisplayMetrics().density;

        // Create Paint - for text to display
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.rgb(61,61,61));
        textPaint.setTextSize(28*scale);
        textPaint.setShadowLayer(1f,0f,1f,Color.WHITE);

        int textWidth = (int)(canvas.getWidth() - 16*scale);


        // Overlay rectangle
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        Point centerOfCanvas = new Point(canvasWidth>>2,canvasHeight>>2);

        int left = centerOfCanvas.x - bitmap.getWidth();
        int top = centerOfCanvas.y - bitmap.getHeight();
        int right = centerOfCanvas.x + bitmap.getWidth();
        int bottom = centerOfCanvas.y + bitmap.getHeight();

        RectF textBg = new RectF(left,top,right,bottom);
        Paint recPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        recPaint.setColor(Color.DKGRAY);
        recPaint.setAlpha(255);
        recPaint.setStyle(Paint.Style.FILL);
        recPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));

        StaticLayout staticLayout = StaticLayout.Builder
                .obtain(quote,0,quote.length(),textPaint,textWidth)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setIncludePad(true)
                .setLineSpacing(1.0f,1.0f)
                .setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE)
                .setMaxLines(Integer.MAX_VALUE)
                .build();

        int textHeight = staticLayout.getHeight();
        float x = (bitmap.getWidth() - textWidth) >> 2;
        float y = (bitmap.getHeight() - textHeight) >> 2;

        canvas.save();

        canvas.drawBitmap(bitmap,0,0,textPaint);
        canvas.drawRect(textBg,recPaint);
        canvas.translate(x,y);
        staticLayout.draw(canvas);

        return output;
    }

    public static Uri writeBitmapToFile(@NonNull Context applicationContext,@NonNull Bitmap bitmap) {
        String name = String.format("card-processed-output%s.png", UUID.randomUUID().toString());
        File outputDir = new File(applicationContext.getFilesDir(), Constants.OUTPUT_PATH);
        if(!outputDir.exists()){
            outputDir.mkdirs();
        }

        File outputFile = new File(outputDir,name);
        try(FileOutputStream outputStream = new FileOutputStream(outputFile)){
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Uri.fromFile(outputFile);
    }
}
