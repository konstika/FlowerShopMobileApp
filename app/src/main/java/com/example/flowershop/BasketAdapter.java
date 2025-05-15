package com.example.flowershop;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.ViewHolder>{
    private List<Product> items;
    Context context;
    BasketFragment basketFragment;
    public BasketAdapter(Context context, List<Product> items, BasketFragment basketFragment){
        this.context=context;
        this.items=items;
        this.basketFragment = basketFragment;
    }
    @NonNull
    @Override
    public BasketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.basket_list_item, parent, false);
        return  new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull BasketAdapter.ViewHolder holder, int position) {
        int i = holder.getAdapterPosition();
        Product item = items.get(i);
        Glide.with(context)
                .load(item.getImageURL())
                .placeholder(R.drawable.icon_catalog)
                .error(R.drawable.icon_catalog)
                .into(holder.imageView);
        holder.textViewTitle.setText(item.getName());
        holder.textViewPrice.setText(item.getPrice()+"₽");
        holder.textViewCount.setText(String.valueOf(item.getCount()));
        holder.buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.addCount(-1);
                FirestoreHandler.getInstance().updateBasket(item.getId(), item.getCount());
                if(!item.inBasket()){
                    items.remove(i);
                    notifyItemRemoved(i);
                    notifyItemRangeChanged(i, items.size());
                }else{
                    holder.textViewCount.setText(String.valueOf(item.getCount()));
                }
                basketFragment.changeSum(items);
            }
        });

        holder.buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.addCount(1);
                FirestoreHandler.getInstance().updateBasket(item.getId(), item.getCount());
                holder.textViewCount.setText(String.valueOf(item.getCount()));
                basketFragment.changeSum(items);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public List<Product> getItems(){
        return items;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle;
        TextView textViewPrice;
        TextView textViewCount;
        ImageButton buttonMinus;
        ImageButton buttonPlus;
        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.IV_image_product);
            textViewTitle = view.findViewById(R.id.TV_title);
            textViewPrice = view.findViewById(R.id.TV_price);
            textViewCount = view.findViewById(R.id.TV_count);
            buttonMinus = view.findViewById(R.id.BUT_minus);
            buttonPlus = view.findViewById(R.id.BUT_plus);
        }
    }
}
