package pro.network.nanjilmartdelivery.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pro.network.nanjilmartdelivery.R;
import pro.network.nanjilmartdelivery.app.AppConfig;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> implements Filterable {
    private final Context context;
    private final ContactsAdapterListener listener;
    StatusListener statusListener;
    private List<Order> orderList;
    private List<Order> orderListFiltered;

    public OrderAdapter(Context context, List<Order> orderList, ContactsAdapterListener listener, StatusListener statusListener) {
        this.context = context;
        this.listener = listener;
        this.orderList = orderList;
        this.orderListFiltered = orderList;
        this.statusListener = statusListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Order order = orderListFiltered.get(position);
        holder.price.setText(order.getPrice());
        holder.order_id.setText("#"+ order.getId());
        holder.quantity.setText(order.getQuantity());
        holder.status.setText(order.getStatus());
        holder.gstAmt.setText(order.gstAmt);
        holder.phone.setText(order.getPhone());
        holder.name.setText(order.getName());
        holder.address.setText(order.getAddress());
        holder.reason.setText(order.getReson());
        holder.orderedOn.setText(AppConfig.convertTimeToLocal(order.createdOn));

        if (order.getStatus().equalsIgnoreCase("Delivery Boy Assigned")) {
            holder.pickup.setVisibility(View.VISIBLE);
            holder.deliveredBtn.setVisibility(View.GONE);
        } else if (order.getStatus().equalsIgnoreCase("Delivery Boy Picked")
                || order.getStatus().equalsIgnoreCase("InProgress")) {
            holder.pickup.setVisibility(View.GONE);
            holder.deliveredBtn.setVisibility(View.VISIBLE);
        } else {
            holder.pickup.setVisibility(View.GONE);
            holder.deliveredBtn.setVisibility(View.GONE);
        }
        if ("NA".equalsIgnoreCase(order.subProduct)) {
            holder.subProduct.setVisibility(View.GONE);
            holder.subProduct.setText("");
        } else {
            holder.subProduct.setVisibility(View.VISIBLE);
            holder.subProduct.setText("Sub Product Type : "+order.subProduct);
        }

        OrderListSubAdapter OrderListAdapter = new OrderListSubAdapter(context, order.productBeans);
        final LinearLayoutManager addManager1 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.cart_sub_list.setLayoutManager(addManager1);
        holder.cart_sub_list.setAdapter(OrderListAdapter);

        holder.deliveredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onDeliveredClick(order.id);
            }
        });
        holder.pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.InPicked(order);
            }
        });

        holder.whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onWhatsAppClick(order.phone);
            }
        });
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onCallClick(order.phone);
            }
        });
        holder.trackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusListener.onTrackOrder(order.id);
            }
        });
        holder.bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusListener.Bill(order);
            }
        });
        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusListener.onLocation(order);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    orderListFiltered = orderList;
                } else {
                    List<Order> filteredList = new ArrayList<>();
                    for (Order row : orderList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        String val = row.getName();
                        if (val.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        } else if (row.getPhone().contains(charString.toLowerCase())) {
                            filteredList.add(row);

                        }
                    }

                    orderListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = orderListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                orderListFiltered = (ArrayList<Order>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyData(List<Order> orderList) {
        this.orderListFiltered = orderList;
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Order order);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, price, status, quantity,
                phone, orderedOn,gstAmt, address, reason,order_id,location,subProduct;
        public ImageView thumbnail;
        public RecyclerView cart_sub_list;
        Button deliveredBtn, whatsapp, call,
                trackOrder, pickup,bill;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            orderedOn = view.findViewById(R.id.orderedOn);
            order_id = view.findViewById(R.id.order_id);
            price = view.findViewById(R.id.price);
            name = view.findViewById(R.id.name);
            gstAmt = view.findViewById(R.id.gstAmt);
            phone = view.findViewById(R.id.phone);
            status = view.findViewById(R.id.status);
            quantity = view.findViewById(R.id.quantity);
            thumbnail = view.findViewById(R.id.thumbnail);
            cart_sub_list = view.findViewById(R.id.cart_sub_list);
            deliveredBtn = view.findViewById(R.id.deliveredBtn);
            whatsapp = view.findViewById(R.id.whatsapp);
            subProduct = view.findViewById(R.id.subProduct);
            call = view.findViewById(R.id.call);
            address = view.findViewById(R.id.address);
            reason = view.findViewById(R.id.reason);
            pickup = view.findViewById(R.id.assignDboy);
            trackOrder = view.findViewById(R.id.trackOrder);
            bill = view.findViewById(R.id.bill);
            location = view.findViewById(R.id.location);


        }
    }
}
