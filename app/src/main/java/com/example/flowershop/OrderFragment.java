package com.example.flowershop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderFragment extends Fragment {
    private ImageView selectedImageView;
    private String address;
    private String date;
    private String time;

    public OrderFragment() {}

    public static OrderFragment newInstance() {
        OrderFragment fragment = new OrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null){
            address = args.getString("address","");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView select_address = view.findViewById(R.id.menu_select_address);
        TextView select_date = view.findViewById(R.id.menu_select_date);
        TextView select_time = view.findViewById(R.id.menu_select_time);
        if(address!=null && !address.isEmpty()){
            select_address.setText("Адрес: "+address);
        }
        select_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, new MapFragment()).commit();
            }
        });

        ImageView choice1Image = view.findViewById(R.id.image1);
        ImageView choice2Image = view.findViewById(R.id.image2);
        choice1Image.setSelected(true);
        selectedImageView = choice1Image;
        choice1Image.setOnClickListener(v -> {selectImage(choice1Image);});
        choice2Image.setOnClickListener(v -> {selectImage(choice2Image);});
    }
    private void selectImage(ImageView imageView) {
        selectedImageView.setSelected(false);
        imageView.setSelected(true);
        selectedImageView = imageView;
    }
}