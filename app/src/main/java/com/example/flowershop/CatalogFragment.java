package com.example.flowershop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CatalogFragment extends Fragment {

    public CatalogFragment() {}

    public static CatalogFragment newInstance() {
        CatalogFragment fragment = new CatalogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView catalogList = view.findViewById(R.id.RV_catalog_list);
        catalogList.setLayoutManager(new GridLayoutManager(getContext(),2));

        FirestoreHandler firestoreHandler = FirestoreHandler.getInstance();
        firestoreHandler.getAllProducts().observe(getViewLifecycleOwner(), products -> {
            Log.d("CATALOG","DJPDHFOTYBT");
            if (products != null) {
                CatalogAdapter catalogAdapter = new CatalogAdapter(getContext(), products);
                catalogList.setAdapter(catalogAdapter);
            } else {
                Toast.makeText(getContext(), "Ошибка загрузки продуктов", Toast.LENGTH_SHORT).show();
            }
        });
    }
}