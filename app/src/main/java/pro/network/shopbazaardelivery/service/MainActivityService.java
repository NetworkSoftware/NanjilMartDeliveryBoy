package pro.network.shopbazaardelivery.service;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

import pro.network.shopbazaardelivery.R;
import pro.network.shopbazaardelivery.SettingsActivity;
import pro.network.shopbazaardelivery.app.AppConfig;
import pro.network.shopbazaardelivery.app.AppController;
import pro.network.shopbazaardelivery.app.PdfConfig;

import static pro.network.shopbazaardelivery.app.AppConfig.SERVICE_CHANGE_STATUS;
import static pro.network.shopbazaardelivery.app.AppConfig.SERVICE_GET_ALL;
import static pro.network.shopbazaardelivery.app.AppConfig.mypreference;
import static pro.network.shopbazaardelivery.app.AppConfig.user_id;


public class MainActivityService extends AppCompatActivity implements StatusListener {
    private static final String TAG = MainActivityService.class.getSimpleName();
    ProgressDialog progressDialog;
    Map<String, String> idNameMap = new HashMap<>();
    ArrayList<String> dboysName = new ArrayList<>();

    Map<String, String> idShopMap = new HashMap<>();
    Map<String, String> shopIdMap = new HashMap<>();
    ArrayList<String> shopName = new ArrayList<>();
    private SearchView searchView;

    private RecyclerView recyclerView;
    private List<Service> servicesList;
    private ServiceAdapter mAdapter;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_service);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        getSupportActionBar().setTitle("Orders");
        recyclerView = findViewById(R.id.recycler_view);
        servicesList = new ArrayList<>();
        mAdapter = new ServiceAdapter(this, servicesList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        fetchContacts();
    }

    /**
     * fetches json by making http calls
     */
    private void fetchContacts() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                SERVICE_GET_ALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        servicesList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Service service = new Service();
                            service.setId(jsonObject.getString("id"));
                            String shopname = jsonObject.getString("shopname");
                            if (shopIdMap.containsKey(shopname)) {
                                service.setShopname(shopIdMap.get(shopname));
                            } else {
                                service.setShopname("Not assigned");
                            }
                            service.setCustomerName(jsonObject.getString("customerName"));
                            service.setCustomerPhone(jsonObject.getString("customerPhone"));
                            service.setProblem(jsonObject.getString("problem"));
                            service.setModel(jsonObject.getString("model"));
                            service.setImage(jsonObject.getString("image"));
                            service.setStatus(jsonObject.getString("status"));
                            service.setAmount(jsonObject.getString("amount"));
                            servicesList.add(service);
                        }
                        mAdapter.notifyData(servicesList);
                        getSupportActionBar().setSubtitle(servicesList.size() + "  Nos");

                    } else {
                        Toast.makeText(MainActivityService.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityService.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                fetchContacts();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("did", sharedpreferences.getString(user_id, ""));
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_profile) {
            startActivity(new Intent(MainActivityService.this, SettingsActivity.class));
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

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }


    @Override
    public void onDeliveredClick(String id) {
        statusChange(id, "Delivered",
                "Service delivered by " + sharedpreferences.getString(AppConfig.name, ""));
    }


    @Override
    public void onCallClick(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }


    @Override
    public void onItemClick(Service service) {
        printFunction(getApplicationContext(), service);
    }

    @Override
    public void onTrackOrder(String id) {
        Intent intent = new Intent(MainActivityService.this, TimelineActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }


    private void statusChange(final String id, final String status, final String reason) {
        String tag_string_req = "req_register";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                SERVICE_CHANGE_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
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
                hideDialog();
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

    public void printFunction(Context context, Service mainbean) {

        try {

            String path = getExternalCacheDir().getPath() + "/PDF";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();
            Log.d("PDFCreator", "PDF Path: " + path);
            File file = new File(dir, mainbean.getCustomerName().replace(" ", "_") + "_" + mainbean.getId() + ".pdf");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fOut = new FileOutputStream(file);

            float left = 0;
            float right = 0;
            float top = 0;
            float bottom = 0;
            // Document document = new Document(PageSize.A2, left, right, top, bottom);
            Document document = new Document(new Rectangle(288, 512));

            //   Document document = new Document(PageSize.A4, 30, 28, 40, 119);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fOut);

            /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
            getConfigBean().getOwnersignBit().compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            getConfigBean().getPoweredByLogoBit().compress(Bitmap.CompressFormat.PNG, 100, stream1);
            byte[] byteArray1 = stream1.toByteArray();
*/

            document.open();
            PdfConfig.addMetaData(document);

           /* HeaderFooterPageEvent event = new HeaderFooterPageEvent(Image.getInstance(byteArray), Image.getInstance(byteArray1), isDigital, getConfigBean());
            pdfWriter.setPageEvent(event);*/
            PdfConfig.addContent(document, mainbean, MainActivityService.this);

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


}
