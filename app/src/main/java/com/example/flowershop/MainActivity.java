package com.example.flowershop;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

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
        FragmentManager fragmentManager = getSupportFragmentManager();
        CoordinatorLayout fragmentContainer = findViewById(R.id.main);
        fragmentManager.beginTransaction().add(R.id.main, new CatalogFragment()).commit();
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_nav);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.catalog){
                    fragmentManager.beginTransaction().replace(R.id.main, new CatalogFragment()).commit();
                }
                else if(item.getItemId() == R.id.basket){
                    fragmentManager.beginTransaction().replace(R.id.main, new BasketFragment()).commit();
                }
                else if(item.getItemId() == R.id.profile){
                    fragmentManager.beginTransaction().replace(R.id.main, new ProfileFragment()).commit();
                }
                return false;
            }
        });
    }
}