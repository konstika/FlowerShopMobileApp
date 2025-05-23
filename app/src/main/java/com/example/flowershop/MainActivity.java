package com.example.flowershop;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.flowershop.ui.auth.AuthFragment;
import com.example.flowershop.ui.basket.BasketFragment;
import com.example.flowershop.ui.catalog.CatalogFragment;
import com.example.flowershop.ui.profile.ProfileFragment;
import com.example.flowershop.ui.auth.AuthListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yandex.mapkit.MapKitFactory;

public class MainActivity extends AppCompatActivity implements AuthListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
        String userId = sharedPreferences.getString("userID", "");
        if(userId.equals("")){
            createViewForUnAuthUsers();
        }else {
            FirestoreHandler.getInstance().setUser(userId).observeForever(result -> {
                if(result){
                    Intent serviceIntent = new Intent(this, OrderStatusService.class);
                    startForegroundService(serviceIntent);
                    createViewForAuthUsers();
                }else{
                    createViewForUnAuthUsers();
                }
            });
        }
    }

    public void createViewForAuthUsers(){
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_nav);
        FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigation.setVisibility(View.VISIBLE);
        bottomNavigation.setSelectedItemId(R.id.catalog);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new CatalogFragment()).commit();
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.catalog) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, new CatalogFragment()).commit();
                    return true;
                } else if (item.getItemId() == R.id.basket) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, new BasketFragment()).commit();
                    return true;
                } else if (item.getItemId() == R.id.profile) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                    return true;
                }
                return false;
            }
        });
    }

    public void createViewForUnAuthUsers(){
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_nav);
        FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigation.setVisibility(View.GONE);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new AuthFragment()).commit();
    }

    @Override
    public void onAuthSuccess() {
        SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
        sharedPreferences.edit().putString("userID", FirestoreHandler.getInstance().getUserId()).apply();
        createViewForAuthUsers();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
        Intent serviceIntent = new Intent(this, OrderStatusService.class);
        startForegroundService(serviceIntent);
    }

    @Override
    public void onExit(){
        Intent serviceIntent = new Intent(this, OrderStatusService.class);
        stopService(serviceIntent);
        SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        createViewForUnAuthUsers();
    }
}