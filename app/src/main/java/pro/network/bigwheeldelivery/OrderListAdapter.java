package pro.network.bigwheeldelivery;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pro.network.bigwheeldelivery.app.AppConfig;


public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder> {

    private Context mainActivityUser;
    private ArrayList<Order> myorderBeans;
    SharedPreferences preferences;
    StatusListener statusListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView name, quantity, amount, status, createdon,phone;
        Button delivery_product;
        ImageView call;
        RecyclerView cart_sub_list;

        public MyViewHolder(View view) {
            super((view));
            name = (TextView) view.findViewById(R.id.name);
            quantity = (TextView) view.findViewById(R.id.quantity);
            amount = (TextView) view.findViewById(R.id.amount);
            status = (TextView) view.findViewById(R.id.status);
            createdon = (TextView) view.findViewById(R.id.createdon);
            cart_sub_list = view.findViewById(R.id.cart_sub_list);
            phone = view.findViewById(R.id.phone);
            delivery_product = view.findViewById(R.id.delivery_product);
            call=view.findViewById(R.id.call);


        }
    }

    public OrderListAdapter(Context mainActivityUser, ArrayList<Order> myorderBeans,StatusListener statusListener) {
        this.mainActivityUser = mainActivityUser;
        this.myorderBeans = myorderBeans;
        this.statusListener=statusListener;
    }


    public void notifyData(ArrayList<Order> myorderBeans) {
        this.myorderBeans = myorderBeans;
        notifyDataSetChanged();
    }

    public OrderListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orders_list, parent, false);

        return new OrderListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Order myorderBean = myorderBeans.get(position);


        holder.name.setText(myorderBean.getAmount());
        holder.quantity.setText(myorderBean.getQuantity());
        holder.status.setText(myorderBean.getStatus());
        holder.createdon.setText(AppConfig.convertTimeToLocal(myorderBean.getCreatedon()));

        holder.delivery_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onDeliveredClick(myorderBean.id);
            }
        });
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onCallClick(myorderBean.phone);
            }
        });
        if (myorderBean.getStatus().equalsIgnoreCase("Delivered")
                && !AppConfig.checkReturnExpired(myorderBean.createdon)) {
            holder.delivery_product.setVisibility(View.VISIBLE);
        } else {
            holder.delivery_product.setVisibility(View.GONE);
        }

        OrderListSubAdapter myOrderListAdapter = new OrderListSubAdapter(mainActivityUser, myorderBean.getProductBeans());
        final LinearLayoutManager addManager1 = new LinearLayoutManager(mainActivityUser, LinearLayoutManager.HORIZONTAL, false);
        holder.cart_sub_list.setLayoutManager(addManager1);
        holder.cart_sub_list.setAdapter(myOrderListAdapter);


    }

    public int getItemCount() {
        return myorderBeans.size();
    }

}


