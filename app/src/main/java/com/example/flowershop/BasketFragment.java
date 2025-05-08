package com.example.flowershop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BasketFragment extends Fragment {
    private TextView tvSum;

    public BasketFragment() {}

    public static BasketFragment newInstance() {
        BasketFragment fragment = new BasketFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_basket, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvSum = view.findViewById(R.id.TV_sum);
        RecyclerView basketList = view.findViewById(R.id.RV_basket_list);
        basketList.setLayoutManager(new LinearLayoutManager(getContext()));
        FirestoreHandler firestoreHandler = FirestoreHandler.getInstance();
        firestoreHandler.getBasketProducts().observe(getViewLifecycleOwner(), products -> {
            if(products == null){products = new ArrayList<Product>();}
            BasketAdapter basketAdapter = new BasketAdapter(getContext(), products, BasketFragment.this);
            basketList.setAdapter(basketAdapter);
            changeSum(products);
        });
    }
    public void changeSum(List<Product> products){
        int sum = 0;
        for (Product product: products) {
            sum+= product.getSum();
        }
        tvSum.setText(sum+"₽");
    }
}