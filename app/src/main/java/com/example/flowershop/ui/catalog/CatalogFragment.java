package com.example.flowershop.ui.catalog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flowershop.FirestoreHandler;
import com.example.flowershop.R;
import com.example.flowershop.adapter.CatalogAdapter;
import com.example.flowershop.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatalogFragment extends Fragment implements FilterDialogFragment.FilterDialogListener {
    private RecyclerView catalogList;
    private String searchText="";
    private int minPrice=0;
    private int maxPrice=10000;
    private String sort="NOT";
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
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        catalogList = view.findViewById(R.id.RV_catalog_list);
        catalogList.setLayoutManager(new GridLayoutManager(getContext(),2));
        updateCatalog();

        EditText etSerchBar = view.findViewById(R.id.ET_search_bar);
        etSerchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchText = etSerchBar.getText().toString();
                    updateCatalog();
                    handled = true;
                }
                return handled;
            }
        });

        ImageButton butFilter = view.findViewById(R.id.BUT_filter);
        butFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialogFragment filterDialog = new FilterDialogFragment(CatalogFragment.this);
                filterDialog.show(getChildFragmentManager(), null);
            }
        });
    }
    public void updateCatalog(){
        FirestoreHandler firestoreHandler = FirestoreHandler.getInstance();
        firestoreHandler.getAllProducts(searchText, minPrice, maxPrice, sort).observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                CatalogAdapter catalogAdapter = new CatalogAdapter(getContext(), products);
                catalogList.setAdapter(catalogAdapter);
            } else {
                Toast.makeText(getContext(), "Ошибка загрузки продуктов", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFiltersApplied(int minPrice, int maxPrice, String sort) {
        this.minPrice=minPrice;
        this.maxPrice=maxPrice;
        this.sort=sort;
        updateCatalog();
    }
}