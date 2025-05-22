package com.example.flowershop.ui.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.flowershop.R;

import java.util.Arrays;
import java.util.List;

public class FilterDialogFragment extends DialogFragment {
    private int minPrice = 0;
    private int defaultMinPrice = 0;
    private int maxPrice = 10000;
    private int defaultMaxPrice = 10000;
    private String sort = "NOT";
    private List<String> sorts = Arrays.asList(new String[]{"NOT", "DESCENDING", "ASCENDING"});
    private FilterDialogListener listener;

    public FilterDialogFragment(Fragment fragment, int minPrice, int maxPrice, String sort){
        this.minPrice=minPrice;
        this.maxPrice=maxPrice;
        this.sort=sort;
        if (fragment instanceof FilterDialogListener) {
            listener = (FilterDialogListener) fragment;
        }
    }

    public interface FilterDialogListener {
        void onFiltersApplied(int minPrice, int maxPrice, String sort);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_filters, container, false);
        TextView tvMinPrice = view.findViewById(R.id.TV_min_price);
        SeekBar seekBarMin = view.findViewById(R.id.seek_bar_min);
        seekBarMin.setMin(defaultMinPrice);
        seekBarMin.setMax(defaultMaxPrice);
        seekBarMin.setProgress(minPrice);
        tvMinPrice.setText("Максимальная цена: "+minPrice+"₽");
        seekBarMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                minPrice = i;
                tvMinPrice.setText("Минимальная цена: "+i+"₽");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        TextView tvMaxPrice = view.findViewById(R.id.TV_max_price);
        SeekBar seekBarMax = view.findViewById(R.id.seek_bar_max);
        seekBarMax.setMin(defaultMinPrice);
        seekBarMax.setMax(defaultMaxPrice);
        seekBarMax.setProgress(maxPrice);
        tvMaxPrice.setText("Максимальная цена: "+maxPrice+"₽");
        seekBarMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                maxPrice = i;
                tvMaxPrice.setText("Максимальная цена: "+i+"₽");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        Spinner spinner = view.findViewById(R.id.spinner_sort);
        String[] sortsStr = new String[]{"Нет", "По убыванию", "По возрастанию"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (getContext(), android.R.layout.simple_spinner_item, sortsStr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(sorts.indexOf(sort));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sort = sorts.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sort = "NOT";
            }
        });

        Button butClear = view.findViewById(R.id.BUT_clear);
        butClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null) {
                    listener.onFiltersApplied(defaultMinPrice, defaultMaxPrice, "NOT");
                }
                dismiss();
            }
        });
        Button butApply = view.findViewById(R.id.BUT_apply);
        butApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null) {
                    listener.onFiltersApplied(minPrice, maxPrice, sort);
                }
                dismiss();
            }
        });
        return view;
    }
}
