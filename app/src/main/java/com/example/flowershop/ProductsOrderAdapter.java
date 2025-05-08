package com.example.flowershop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductsOrderAdapter extends RecyclerView.Adapter<ProductsOrderAdapter.ViewHolder> {
    private Context context;
    private List<Product> products;
    ProductsOrderAdapter(Context context, List<Product> products){
        this.context=context;
        this.products=products;
    }
    @NonNull
    @Override
    public ProductsOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.products_order_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsOrderAdapter.ViewHolder holder, int position) {
        Product item = products.get(position);
        Glide.with(context)
                .load(item.getImageURL())
                .placeholder(R.drawable.icon_catalog)
                .error(R.drawable.icon_catalog)
                .into(holder.imageView);
        holder.textViewTitle.setText(item.getName());
        holder.textViewPrice.setText(item.getPrice()+"₽");
        holder.textViewCount.setText(String.valueOf(item.getCount()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle;
        TextView textViewPrice;
        TextView textViewCount;
        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.IV_image_product);
            textViewTitle = view.findViewById(R.id.TV_title);
            textViewPrice = view.findViewById(R.id.TV_price);
            textViewCount = view.findViewById(R.id.TV_count);
        }
    }
}
