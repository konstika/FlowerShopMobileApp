package com.example.flowershop.ui.profile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.flowershop.ui.auth.AuthListener;
import com.example.flowershop.FirestoreHandler;
import com.example.flowershop.R;
import com.example.flowershop.adapter.OrderAdapter;

public class ProfileFragment extends Fragment {
    private AuthListener authListener;

    public ProfileFragment() {}

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AuthListener) {
            authListener = (AuthListener) context;
        }
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
        TextView tvUsername = view.findViewById(R.id.TV_username);
        tvUsername.setText(FirestoreHandler.getInstance().getUsername());
        RecyclerView rvOrders = view.findViewById(R.id.RV_orders_list);
        TextView tvEmpty = view.findViewById(R.id.emptyTextView);
        rvOrders.setLayoutManager(new
                LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        FirestoreHandler.getInstance().getOrders().observe(getViewLifecycleOwner(), orders -> {
            if(orders.size()>0) {
                OrderAdapter orderAdapter = new OrderAdapter(getContext(), orders);
                rvOrders.setAdapter(orderAdapter);
                rvOrders.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            }else{
                rvOrders.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });

        TextView exit = view.findViewById(R.id.menu_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirestoreHandler.getInstance().exit();
                authListener.onExit();
            }
        });
    }
}