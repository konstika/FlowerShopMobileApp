package com.example.flowershop;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder>{
    private List<ProductCard> items;
    Context context;
    public CatalogAdapter(Context context, List<ProductCard> items){
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
        ProductCard item = items.get(position);
        holder.imageView.setImageResource(item.getImageId());
        holder.textViewTitle.setText(item.getTitle());
        holder.textViewPrice.setText(item.getPrice()+"₽");
        if(item.getInBasket()){
            holder.buttonToBasket.setText("Перейти в корзину");
            holder.buttonToBasket.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.addit_color)));
        }else{
            holder.buttonToBasket.setText("В корзину");
            holder.buttonToBasket.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.accent_color)));
        }
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
        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.IV_image_product);
            textViewTitle = view.findViewById(R.id.TV_title);
            textViewPrice = view.findViewById(R.id.TV_price);
            buttonToBasket = view.findViewById(R.id.BUT_to_basket);
        }
    }
}
