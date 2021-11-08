package pro.network.nanjilmartdelivery.product;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.network.nanjilmartdelivery.LoginActivity;
import pro.network.nanjilmartdelivery.R;
import pro.network.nanjilmartdelivery.app.AppConfig;
import pro.network.nanjilmartdelivery.app.AppController;
import pro.network.nanjilmartdelivery.app.PdfConfig;

import static pro.network.nanjilmartdelivery.app.AppConfig.ORDER_CHANGE_STATUS;
import static pro.network.nanjilmartdelivery.app.AppConfig.ORDER_GET_ALL;
import static pro.network.nanjilmartdelivery.app.AppConfig.mypreference;
import static pro.network.nanjilmartdelivery.app.AppConfig.user_id;


public class MainActivityOrder extends AppCompatActivity implements OrderAdapter.ContactsAdapterListener, StatusListener {
    private static final String TAG = MainActivityOrder.class.getSimpleName();
    ProgressDialog progressDialog;

    int offset = 0;
    ArrayList<String> dboysName = new ArrayList<>();
    Map<String, String> idNameMap = new HashMap<>();
    private RecyclerView recyclerView;
    private List<Order> orderList;
    private OrderAdapter mAdapter;
    private SearchView searchView;
    private OrderAdapter deliverAdapter;
    private ArrayList<Order> deliveredList;
    private RecyclerView recycler_view_delivered;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainorder);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setTitle(R.string.order);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.recycler_view);
        orderList = new ArrayList<>();
        mAdapter = new OrderAdapter(this, orderList, this, this);

        recycler_view_delivered = findViewById(R.id.recycler_view_delivered);
        deliveredList = new ArrayList<>();
        deliverAdapter = new OrderAdapter(this, deliveredList, this, this);


        // white background notification bar
        whiteNotificationBar(recyclerView);


        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager addManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(addManager1);
        recyclerView.setAdapter(mAdapter);

        recycler_view_delivered.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager deliManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_view_delivered.setLayoutManager(deliManager);
        recycler_view_delivered.setAdapter(deliverAdapter);


        fetchContacts();
    }

    private void fetchContacts() {
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
                        if (offset == 0) {
                            orderList = new ArrayList<>();
                            deliveredList = new ArrayList<>();
                        }
                        offset = offset + 1;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Order order = new Order();
                            order.setId(jsonObject.getString("id"));
                            order.setPrice(jsonObject.getString("price"));
                            order.setQuantity(jsonObject.getString("quantity"));
                            order.setStatus(jsonObject.getString("status"));
                            order.setItems(jsonObject.getString("items"));
                            order.setName(jsonObject.getString("name"));
                            order.setPhone(jsonObject.getString("phone"));
                            order.setAddress(jsonObject.getString("address"));
                            order.setReson(jsonObject.getString("reason"));
                            order.setCreatedOn(jsonObject.getString("createdon"));
                            order.setTotal(jsonObject.getString("total"));
                            order.setDcharge(jsonObject.getString("dcharge"));
                            order.setPincode(jsonObject.getString("pincode"));
                            ObjectMapper mapper = new ObjectMapper();
                            Object listBeans = new Gson().fromJson(jsonObject.getString("items"),
                                    Object.class);
                            ArrayList<Product> accountList = mapper.convertValue(
                                    listBeans,
                                    new TypeReference<ArrayList<Product>>() {
                                    }
                            );
                            order.setProductBeans(accountList);
                            if (order.getStatus().equalsIgnoreCase("ordered")) {
                                orderList.add(order);
                            } else {
                                deliveredList.add(order);
                            }
                        }
                        mAdapter.notifyData(orderList);
                        deliverAdapter.notifyData(deliveredList);
                        getSupportActionBar().setSubtitle("Orders - " + deliveredList.size());

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
                localHashMap.put("offset", offset * 10 + "");
                localHashMap.put("dboy", sharedpreferences.getString(user_id, ""));
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        //   logout = (ImageView) menu.findItem(R.id.logout);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.logout) {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(AppConfig.isLogin, false);
        editor.remove(AppConfig.name);
        editor.remove(AppConfig.phone);
        editor.remove(AppConfig.password);
        editor.remove(AppConfig.profile);
        editor.remove(AppConfig.license);
        editor.remove(AppConfig.aadhar);
        editor.remove(AppConfig.auth_key);
        editor.remove(AppConfig.user_id);
        editor.commit();
        startActivity(new Intent(MainActivityOrder.this, LoginActivity.class));
        finishAffinity();
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

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onContactSelected(Order contact) {
       /* Intent intent = new Intent(MainActivityOrder.this, ProductUpdate.class);
        intent.putExtra("data", contact);
        startActivity(intent);*/
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onDeliveredClick(String id) {
        statusChange(id, "Delivered", "Delivered by admin");
    }


    @Override
    public void onWhatsAppClick(String phone) {
        try {
            Uri uri = Uri.parse("smsto:91" + phone);
            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
            //  i.putExtra("sms_body", "Hello");
            i.setPackage("com.whatsapp.w4b");
            startActivity(i);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=91" + phone
                    + "&text=" + "Hi"));
            intent.setPackage("com.whatsapp.w4b");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=91" + phone
                    + "&text=" + "Hi"));
            intent.setPackage("com.whatsapp");
            startActivity(intent);
        }
    }

    @Override
    public void onCallClick(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @Override
    public void onCancelClick(final String id) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivityOrder.this);
        LayoutInflater inflater = MainActivityOrder.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        TextView title = dialogView.findViewById(R.id.title);
        final TextInputEditText reason = dialogView.findViewById(R.id.address);


        title.setText("* Do you want to cancel this order? If yes Order will be canceled.");
        dialogBuilder.setTitle("Alert")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (reason.getText().toString().length() > 0) {
                            statusChange(id, "canceled", reason.getText().toString());
                            dialog.cancel();
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter valid reason", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();
    }

    @Override
    public void onTrackOrder(String id) {
        Intent intent = new Intent(MainActivityOrder.this, Order_TimelineActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onCourier(String id) {

    }

    @Override
    public void InProgress(Order order) {
        statusChange(order.getId(), "InProgress", "InProgress By Admin");
    }

    @Override
    public void InPicked(Order order) {
        statusChange(order.getId(), "Delivery Boy Picked", "Picked By DeliveryBoy");
    }

    @Override
    public void Bill(Order position) {
        printFunction(MainActivityOrder.this, position);
    }

    public void printFunction(Context context, Order mainbean) {

        try {

            String path = getExternalCacheDir().getPath() + "/PDF";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();
            Log.d("PDFCreator", "PDF Path: " + path);
            File file = new File(dir, mainbean.getName().replace(" ", "_") + "_" + mainbean.getId() + ".pdf");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fOut = new FileOutputStream(file);

            float left = 0;
            float right = 0;
            float top = 0;
            float bottom = 0;
            com.itextpdf.text.Document document = new Document(new Rectangle(288, 512));
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fOut);


            document.open();
            PdfConfig.addMetaData(document);

           /* HeaderFooterPageEvent event = new HeaderFooterPageEvent(Image.getInstance(byteArray), Image.getInstance(byteArray1), isDigital, getConfigBean());
            pdfWriter.setPageEvent(event);*/
            PdfConfig.addContent(document, mainbean, MainActivityOrder.this);

            document.close();

            Uri fileUri = Uri.fromFile(file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                Intent intent = Intent.createChooser(target, "Open File");
                startActivity(intent);
            }
            hideDialog();
            // new UploadInvoiceToServer().execute(imageutils.getPath(fileUri) + "@@" + mainbean.getBuyerphone() + "@@" + mainbean.getDbid());

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            hideDialog();
        }


    }


    private void statusChange(final String id, final String status, final String reason) {
        String tag_string_req = "req_register";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                ORDER_CHANGE_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        offset = 0;
                        fetchContacts();
                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("id", id);
                localHashMap.put("status", status);
                localHashMap.put("reason", reason);
                return localHashMap;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
