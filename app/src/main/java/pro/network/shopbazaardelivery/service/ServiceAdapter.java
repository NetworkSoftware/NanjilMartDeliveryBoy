package pro.network.shopbazaardelivery.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import pro.network.shopbazaardelivery.R;

/**
 * Created by ravi on 16/11/17.
 */

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder>
        implements Filterable {
    private final Context context;
    private final StatusListener statusListener;
    private List<Service> productList;
    private List<Service> productListFiltered;

    public ServiceAdapter(Context context, List<Service> productList, StatusListener statusListener) {
        this.context = context;
        this.statusListener = statusListener;
        this.productList = productList;
        this.productListFiltered = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Service product = productListFiltered.get(position);
        holder.shopName.setText(product.getShopname());
        holder.customerid.setText("#CSN" + product.getId());
        holder.status.setText(product.getStatus());
        holder.customerName.setText(product.customerName);
        holder.customerPhone.setText("****");
        holder.price.setText("₹" + product.getAmount());


        if (product.getStatus().equalsIgnoreCase("Delivery boy assigned")) {
            holder.deliveredBtn.setVisibility(View.VISIBLE);
        } else {
            holder.deliveredBtn.setVisibility(View.GONE);
        }

        holder.deliveredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onDeliveredClick(product.id);
            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onCallClick(product.customerPhone);
            }
        });

        holder.trackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusListener.onTrackOrder(product.id);
            }
        });
        holder.bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusListener.onItemClick(product);
            }
        });


    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    productListFiltered = productList;
                } else {
                    List<Service> filteredList = new ArrayList<>();
                    for (Service row : productList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        String val = row.getShopname();
                        if (val.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        } else if (row.getId().contains(charString.toLowerCase())) {
                            filteredList.add(row);

                        }
                    }

                    productListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productListFiltered = (ArrayList<Service>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyData(List<Service> productList) {
        this.productListFiltered = productList;
        this.productList = productList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView customerName, customerPhone, shopName, customerid, status,price;
        MaterialButton deliveredBtn, call, trackOrder,bill;
        LinearLayout itemShow;

        public MyViewHolder(View view) {
            super(view);
            customerName = view.findViewById(R.id.customerName);
            customerPhone = view.findViewById(R.id.customerPhone);
            status = view.findViewById(R.id.status);
            shopName = view.findViewById(R.id.shopName);
            customerid = view.findViewById(R.id.customerid);
            deliveredBtn = view.findViewById(R.id.delivered);
            call = view.findViewById(R.id.call);
            trackOrder = view.findViewById(R.id.trackOrder);
            itemShow=view.findViewById(R.id.itemShow);
            price=view.findViewById(R.id.price);
            bill=view.findViewById(R.id.bill);
        }
    }
}
