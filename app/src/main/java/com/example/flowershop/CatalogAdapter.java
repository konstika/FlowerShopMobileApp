package com.example.flowershop;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder>{
    private List<Product> items;
    Context context;
    public CatalogAdapter(Context context, List<Product> items){
        this.context=context;
        this.items=items;
    }
    @NonNull
    @Override
    public CatalogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.catalog_list_item, parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogAdapter.ViewHolder holder, int position) {
        Product item = items.get(position);
        Glide.with(context)
                .load(item.getImageURL())
                .placeholder(R.drawable.icon_catalog)
                .error(R.drawable.icon_catalog)
                .into(holder.imageView);
        holder.textViewTitle.setText(item.getName());
        holder.textViewPrice.setText(item.getPrice()+"₽");
        holder.textViewCount.setText(String.valueOf(item.getCount()));
        if(item.inBasket()){
            holder.buttonToBasket.setVisibility(View.GONE);
            holder.layoutChangeCounts.setVisibility(View.VISIBLE);
        }else{
            holder.buttonToBasket.setVisibility(View.VISIBLE);
            holder.layoutChangeCounts.setVisibility(View.GONE);
        }
        holder.buttonToBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.addCount(1);
                holder.textViewCount.setText(String.valueOf(item.getCount()));
                holder.buttonToBasket.setVisibility(View.GONE);
                holder.layoutChangeCounts.setVisibility(View.VISIBLE);
            }
        });
        holder.buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.addCount(-1);
                if(!item.inBasket()){
                    holder.buttonToBasket.setVisibility(View.VISIBLE);
                    holder.layoutChangeCounts.setVisibility(View.GONE);
                }else{
                    holder.textViewCount.setText(String.valueOf(item.getCount()));
                }
            }
        });

        holder.buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.addCount(1);
                holder.textViewCount.setText(String.valueOf(item.getCount()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle;
        TextView textViewPrice;
        Button buttonToBasket;
        LinearLayout layoutChangeCounts;
        ImageButton buttonPlus;
        ImageButton buttonMinus;
        TextView textViewCount;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.IV_image_product);
            textViewTitle = view.findViewById(R.id.TV_title);
            textViewPrice = view.findViewById(R.id.TV_price);
            buttonToBasket = view.findViewById(R.id.BUT_to_basket);
            layoutChangeCounts = view.findViewById(R.id.BUTS_change_counts);
            buttonMinus = view.findViewById(R.id.BUT_minus);
            buttonPlus = view.findViewById(R.id.BUT_plus);
            textViewCount = view.findViewById(R.id.TV_count);
        }
    }
}
