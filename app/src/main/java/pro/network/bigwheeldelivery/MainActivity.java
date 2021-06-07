package pro.network.bigwheeldelivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.network.bigwheeldelivery.app.AppConfig;
import pro.network.bigwheeldelivery.app.AppController;

import static pro.network.bigwheeldelivery.app.AppConfig.ORDER_GET_ALL;
import static pro.network.bigwheeldelivery.app.AppConfig.mypreference;

public class MainActivity extends AppCompatActivity implements StatusListener {

    RecyclerView orders_list;
    private ArrayList<Order> orderList;
    private OrderListAdapter mAdapter;
    private SearchView searchView;
    SharedPreferences sharedpreferences;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Orders");

        orders_list = findViewById(R.id.orders_list);

        orderList = new ArrayList<>();
        mAdapter = new OrderListAdapter(this, orderList, this);

        // white background notification bar
        whiteNotificationBar(orders_list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        orders_list.setLayoutManager(mLayoutManager);
        orders_list.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager addManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        orders_list.setLayoutManager(addManager1);
        orders_list.setAdapter(mAdapter);

        getallorder();

    }


    private void getallorder() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                ORDER_GET_ALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Order order = new Order();
                            order.setId(jsonObject.getString("id"));
                            order.setName(jsonObject.getString("name"));
                            order.setPhone(jsonObject.getString("phone"));
                            order.setAmount(jsonObject.getString("price"));
                            order.setQuantity(jsonObject.getString("quantity"));
                            order.setStatus(jsonObject.getString("status"));
                            order.setReson(jsonObject.getString("reason"));
                            order.setToPincode(jsonObject.getString("toPincode"));
                            order.setAddress(jsonObject.getString("address"));
                            order.setCoupon(jsonObject.getString("coupon"));
                            order.setCouponCost(jsonObject.getString("couponCost"));
                            order.setDelivery(jsonObject.getString("delivery"));
                            order.setPayment(jsonObject.getString("payment"));
                            order.setPaymentId(jsonObject.getString("paymentId"));
                            order.setDeliveryTime(jsonObject.getString("deliveryTime"));
                            order.setComments(jsonObject.getString("comments"));
                            order.setGrandCost(jsonObject.getString("grandCost"));
                            order.setCreatedOn(jsonObject.getString("createdon"));
                            order.setShipCost(jsonObject.getString("shipCost"));
                            order.setCreatedon(jsonObject.getString("createdon"));


                        }
                        mAdapter.notifyData(orderList);
                        getSupportActionBar().setSubtitle("Orders - " + orderList.size());

                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }
                hideDialog();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                // Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("shopid", sharedpreferences.getString(AppConfig.shopIdKey, ""));
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onDeliveredClick(String id) {

    }

    @Override
    public void onCallClick(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }
}