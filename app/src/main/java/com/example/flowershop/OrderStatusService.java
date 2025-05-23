package com.example.flowershop;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.flowershop.model.Order;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.Manifest;

public class OrderStatusService extends Service {
    private static final String TAG = "OrderStatusService";
    private static final String CHANNEL_ID = "order_status_channel";
    private static final int NOTIFICATION_ID = 1;
    private ListenerRegistration listenerRegistration;
    private String userID;
    private Map<String, String> statuses;

    public OrderStatusService() {statuses = new HashMap<>();}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.userID = getSharedPreferences("user",MODE_PRIVATE).getString("userID", "");
        createNotificationChannel();
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(startId>1){return START_STICKY;}
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Отслеживание заказов")
                .setContentText("Служба работает в фоновом режиме")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();
        startForeground(NOTIFICATION_ID, notification);
        startListeningForOrderStatusChanges();
        return START_STICKY;
    }

    private void startListeningForOrderStatusChanges() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        listenerRegistration = db.collection("order").whereEqualTo("userID", userID)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }
                    if (value != null) {
                        Log.w(TAG, "Listen success.", error);
                        for (DocumentSnapshot doc : value) {
                            String orderID = doc.getId();
                            String newStatus = doc.getString("status");
                            if (!statuses.containsKey(orderID)){
                                statuses.put(orderID, newStatus);
                            }
                            else if (!newStatus.equals(statuses.get(orderID))) {
                                Order order = doc.toObject(Order.class);
                                sendNotification(order.getStrDate_order(), newStatus);
                                statuses.put(orderID, newStatus);
                            }
                        }
                    }
                });
    }

    private void sendNotification(String order_date, String newStatus) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_app_foreground)
                .setContentTitle("Ваш заказ от " + order_date + " " + newStatus.toLowerCase())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        Log.d(TAG, "Service destroyed");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Order Status Channel";
            String description = "Channel for order status updates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}