package com.example.antilag;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Bundle;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, AntiLagService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        finish(); 
    }
}

class AntiLagService extends Service {
    static { System.loadLibrary("kk"); } // Gọi file libkk.so của bạn
    
    // ĐỔI CHỮ "kichHoatChongLag" NÀY THÀNH ĐÚNG TÊN HÀM GỐC TRONG FILE .SO CỦA BẠN
    public native void kichHoatChongLag(); 

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("antilag", "AntiLag", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this, "antilag")
                    .setContentTitle("Chế độ chơi game chống lag đang bật").setSmallIcon(android.R.drawable.ic_dialog_info).build();
            startForeground(1, notification);
        }

        // Vòng lặp chạy ngầm vĩnh viễn gọi file .so của bạn liên tục khi chơi game
        new Thread(() -> {
            while (true) {
                try {
                    kichHoatChongLag(); 
                    Thread.sleep(5000); // Tự động lặp lại tối ưu sau mỗi 5 giây
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return START_STICKY; 
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
