package pro.network.bigwheeldelivery;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import pro.network.bigwheeldelivery.app.AppConfig;
import pro.network.bigwheeldelivery.app.GlideApp;


public class OrderListSubAdapter extends RecyclerView.Adapter<OrderListSubAdapter.MyViewHolder> {

    private Context mainActivityUser;
    private ArrayList<StockList> myorderBeans;
    SharedPreferences preferences;
    int selectedPosition = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView product_image;
        private TextView qty;


        public MyViewHolder(View view) {
            super((view));
            product_image = (ImageView) view.findViewById(R.id.product_image);
            qty = (TextView) view.findViewById(R.id.qty);

        }
    }

    public OrderListSubAdapter(Context mainActivityUser, ArrayList<StockList> myorderBeans) {
        this.mainActivityUser = mainActivityUser;
        this.myorderBeans = myorderBeans;
    }

    public void notifyData(ArrayList<StockList> myorderBeans) {
        this.myorderBeans = myorderBeans;
        notifyDataSetChanged();
    }

    public void notifyData(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orders_list_sub, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final StockList myorderBean = myorderBeans.get(position);

        holder.qty.setText(myorderBean.getQty() + " "+myorderBean.getProductName());
        GlideApp.with(mainActivityUser)
                .load(AppConfig.getResizedImage(myorderBean.getImage(), true))
                .placeholder(R.drawable.bigwheel)
                .error(R.drawable.bigwheel)
                .into(holder.product_image);

    }

    public int getItemCount() {
        return myorderBeans.size();
    }

}

