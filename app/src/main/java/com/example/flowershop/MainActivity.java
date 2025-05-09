package com.example.flowershop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yandex.mapkit.MapKitFactory;

public class MainActivity extends AppCompatActivity implements AuthListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("e1dd47ae-8b52-47dc-8a62-98002f8f601e");
        MapKitFactory.initialize(this);
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
        fragmentManager.beginTransaction().add(R.id.fragment_container, new AuthFragment()).commit();
    }

    @Override
    public void onAuthSuccess() {
        SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
        sharedPreferences.edit().putString("userID", FirestoreHandler.getInstance().getUserId()).apply();
        createViewForAuthUsers();
    }
}