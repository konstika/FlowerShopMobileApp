package com.example.flowershop.ui.basket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.flowershop.FirestoreHandler;
import com.example.flowershop.R;
import com.example.flowershop.adapter.BasketAdapter;
import com.example.flowershop.model.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BasketFragment extends Fragment {
    private TextView tvSum;
    private TextView tvEmpty;
    private BasketAdapter basketAdapter;
    private RecyclerView basketList;

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
        tvEmpty = view.findViewById(R.id.emptyTextView);
        basketList = view.findViewById(R.id.RV_basket_list);
        basketList.setLayoutManager(new LinearLayoutManager(getContext()));
        FirestoreHandler firestoreHandler = FirestoreHandler.getInstance();
        firestoreHandler.getBasketProducts().observe(getViewLifecycleOwner(), products -> {
            if(products == null){products = new ArrayList<Product>();}
            basketAdapter = new BasketAdapter(getContext(), products, BasketFragment.this);
            basketList.setAdapter(basketAdapter);
            changeSum(products);
        });
        Button butOrder = view.findViewById(R.id.BUT_place_order);
        butOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(basketAdapter.getItemCount()>0) {
                    OrderFragment orderFragment = new OrderFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("products_in_basket",(Serializable) basketAdapter.getItems());
                    orderFragment.setArguments(bundle);
                    requireActivity().getSupportFragmentManager().beginTransaction().
                            replace(R.id.fragment_container, orderFragment, "ORDER")
                            .addToBackStack(null).commit();
                }
            }
        });
    }
    public void changeSum(List<Product> products){
        int sum = 0;
        for (Product product: products) {
            sum+= product.getSum();
        }
        tvSum.setText(sum+"₽");
        if(sum==0){
            basketList.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }else if(basketList.getVisibility()==View.GONE){
            basketList.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }
}