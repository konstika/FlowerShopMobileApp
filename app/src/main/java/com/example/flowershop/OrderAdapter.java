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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<Order> orders;
    private Context context;
    public OrderAdapter(Context context, List<Order> orders) {
        this.orders=orders;
        this.context=context;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.orders_list_item, parent, false);
        return new OrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.tvDateOrder.setText("Заказ от "+order.getDate_order());
        holder.tvDate.setText("Дата и время: "+order.getDate()+" "+order.getTime());
        holder.tvAddress.setText("Адрес: "+order.getAddress());
        holder.rvProducts.setLayoutManager(new
                LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        holder.rvProducts.setAdapter(new ProductsOrderAdapter(context, order.getProducts()));
        String status = order.getStatus();
        holder.tvStatus.setText(status);
        if(status.equals("Отменен")){
            holder.tvStatus.setBackgroundTintList(
                    ColorStateList.valueOf(context.getColor(R.color.warn_color)));
            holder.butCancel.setVisibility(View.GONE);
        } else if (status.equals("Доставлен")) {
            holder.tvStatus.setBackgroundTintList(
                    ColorStateList.valueOf(context.getColor(R.color.ok_color)));
            holder.butCancel.setVisibility(View.GONE);
        }else {
            holder.tvStatus.setBackgroundTintList(
                    ColorStateList.valueOf(context.getColor(R.color.addit_color)));
            holder.butCancel.setVisibility(View.VISIBLE);
            holder.butCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    order.setStatus("Отменен");
                    FirestoreHandler.getInstance().updateOrder(order.getId(), order.getStatus());
                    holder.tvStatus.setText(order.getStatus());
                    holder.tvStatus.setBackgroundTintList(
                            ColorStateList.valueOf(context.getColor(R.color.warn_color)));
                    holder.butCancel.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateOrder;
        TextView tvDate;
        TextView tvAddress;
        TextView tvStatus;
        RecyclerView rvProducts;
        Button butCancel;
        ViewHolder(View view) {
            super(view);
            tvDateOrder = view.findViewById(R.id.TV_date_order);
            tvDate = view.findViewById(R.id.TV_date);
            tvAddress = view.findViewById(R.id.TV_address);
            tvStatus = view.findViewById(R.id.TV_status);
            rvProducts = view.findViewById(R.id.RV_products_order_list);
            butCancel = view.findViewById(R.id.BUT_cancel);
        }
    }
}
