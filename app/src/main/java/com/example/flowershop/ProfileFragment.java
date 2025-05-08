package com.example.flowershop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {}

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvOrders = view.findViewById(R.id.RV_orders_list);
        rvOrders.setLayoutManager(new
                LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        FirestoreHandler.getInstance().getOrders().observe(getViewLifecycleOwner(), orders -> {
            Log.d("LOG", orders.get(0).getProducts().toString());
            OrderAdapter orderAdapter = new OrderAdapter(getContext(), orders);
            rvOrders.setAdapter(orderAdapter);
        });
    }
}