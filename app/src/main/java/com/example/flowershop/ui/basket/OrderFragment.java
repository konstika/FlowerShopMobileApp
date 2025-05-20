package com.example.flowershop.ui.basket;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.flowershop.FirestoreHandler;
import com.example.flowershop.R;
import com.example.flowershop.model.Order;
import com.example.flowershop.model.Product;
import com.google.firebase.Timestamp;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrderFragment extends Fragment {
    private ImageView selectedImageView;
    private String address;
    private Calendar calendar;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private boolean dateSeted;
    private boolean timeSeted;
    private List<Product> productsInBasket;

    public OrderFragment() {}

    public static OrderFragment newInstance() {
        OrderFragment fragment = new OrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productsInBasket = (List<Product>) getArguments().getSerializable("products_in_basket");
        } else {productsInBasket = new ArrayList<>();}
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        dateSeted = false;
        timeSeted = false;
        if(savedInstanceState!=null){
            int[] savedValues = savedInstanceState.getIntArray("save_values");
            dateSeted = savedInstanceState.getBoolean("date_set");
            timeSeted = savedInstanceState.getBoolean("time_set");
            if(dateSeted){
                year = savedValues[0];
                month = savedValues[1];
                day = savedValues[2];
            }
            if(timeSeted){
                hour = savedValues[3];
                minute = savedValues[4];
            }
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

        //пункт меню: выбор адреса
        TextView select_address = view.findViewById(R.id.menu_select_address);
        select_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInstance();
                requireActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, new MapFragment())
                        .addToBackStack(null).commit();
            }
        });
        if(address!=null && !address.equals("")){
            select_address.setText("Адрес: "+address);
        }

        //пункт меню: выбор даты
        TextView select_date = view.findViewById(R.id.menu_select_date);
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                year=i;
                month=i1;
                day=i2;
                select_date.setText(String.format("Дата: %02d.%02d.%04d", day, month+1, year));
                dateSeted = true;
            }
        };
        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                        dateSetListener, year, month, day);
                datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePicker.show();
            }
        });
        if(dateSeted){
            select_date.setText(String.format("Дата: %02d.%02d.%04d", day, month+1, year));
        }

        //пункт меню: выбор времени
        TextView select_time = view.findViewById(R.id.menu_select_time);
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                hour = i;
                minute = i1;
                select_time.setText(String.format("Время: %02d:%02d", hour, minute));
                timeSeted = true;
            }
        };
        select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePicker = new TimePickerDialog(getContext(),
                        timeSetListener, hour, minute, true);
                timePicker.show();
            }
        });
        if(timeSeted){
            select_time.setText(String.format("Время: %02d:%02d", hour, minute));
        }


        ImageView choice1Image = view.findViewById(R.id.image1);
        ImageView choice2Image = view.findViewById(R.id.image2);
        choice1Image.setSelected(true);
        selectedImageView = choice1Image;
        choice1Image.setOnClickListener(v -> {selectImage(choice1Image);});
        choice2Image.setOnClickListener(v -> {selectImage(choice2Image);});

        Button butCreateOrder = view.findViewById(R.id.BUT_place_order);
        butCreateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dateSeted && timeSeted && !address.equals("") && !address.equals("Адрес не найден")) {
                    Order order = new Order();
                    order.setProducts(productsInBasket);
                    order.setStatus("В сборке");
                    order.setDate_order(new Timestamp(calendar.getTime()));
                    order.setDate_delivery(year, month+1, day, hour, minute);
                    order.setAddress(address);
                    order.setUserID(FirestoreHandler.getInstance().getUserId());
                    FirestoreHandler.getInstance().addOrder(order);
                    FirestoreHandler.getInstance().clearBasket();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
                else{
                    Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void selectImage(ImageView imageView) {
        selectedImageView.setSelected(false);
        imageView.setSelected(true);
        selectedImageView = imageView;
    }
    private void saveInstance(){
        Bundle savedInstanceState = new Bundle();
        savedInstanceState.putIntArray("save_values", new int[]{year, month, day, hour, minute});
        savedInstanceState.putBoolean("date_set", dateSeted);
        savedInstanceState.putBoolean("time_set", timeSeted);
    }
    public void setAddress(String address){
        this.address=address;
    }
}