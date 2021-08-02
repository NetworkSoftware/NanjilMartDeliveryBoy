package pro.network.nanjilmartdelivery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import pro.network.nanjilmartdelivery.app.AndroidMultiPartEntity;
import pro.network.nanjilmartdelivery.app.AppConfig;
import pro.network.nanjilmartdelivery.app.AppController;
import pro.network.nanjilmartdelivery.app.BaseActivity;
import pro.network.nanjilmartdelivery.app.GlideApp;
import pro.network.nanjilmartdelivery.app.Imageutils;

import static pro.network.nanjilmartdelivery.app.AppConfig.CREATE_DATA;


public class RegisterPage extends BaseActivity implements Imageutils.ImageAttachmentListener {
    private static final int FINE_LOCATION_CODE = 199;
    private final String TAG = getClass().getSimpleName();
    Imageutils imageutils;
    ImageView image_adharcard, image_placeholder_adharcard;
    ImageView image_license, image_placeholder_license;
    ImageView image_profile, image_placeholder_profile;
    TextInputEditText name;
    TextInputEditText phone;
    TextInputEditText password;
    TextInputLayout nameText;
    TextInputLayout phoneText;
    TextInputLayout passwordText;
    CardView itemsAddprofile;
    CardView itemsAddlicense;
    CardView itemsAddadharcard;
    RegisterBean regMainbean = null;
    private String imageUrlProfile = "";
    private String imageUrllicense = "";
    private String imageUrladharcard = "";

    @Override
    protected void startDemo() {
        setContentView(R.layout.activity_register);

        if (ContextCompat.checkSelfPermission(RegisterPage.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegisterPage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
        }
        imageutils = new Imageutils(this);
        LinearLayout ll1 = findViewById(R.id.ll1);
        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        image_adharcard = findViewById(R.id.image_adharcard);
        image_license = findViewById(R.id.image_license);
        image_profile = findViewById(R.id.image_profile);
        image_placeholder_adharcard = findViewById(R.id.image_placeholder_adharcard);
        image_placeholder_license = findViewById(R.id.image_placeholder_license);
        image_placeholder_profile = findViewById(R.id.image_placeholder_profile);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);

        itemsAddprofile = findViewById(R.id.itemsAddprofile);
        itemsAddlicense = findViewById(R.id.itemsAddlicense);
        itemsAddadharcard = findViewById(R.id.itemsAddadharcard);


        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageutils.imagepicker(1);
            }
        });

        image_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageutils.imagepicker(2);
            }
        });

        image_adharcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageutils.imagepicker(3);
            }
        });


        ExtendedFloatingActionButton submit = findViewById(R.id.submit);
        try {
            regMainbean = (RegisterBean) getIntent().getSerializableExtra("data");

            name.setText(regMainbean.name);
            phone.setText(regMainbean.phone);
            password.setText(regMainbean.password);

            image_placeholder_adharcard.setVisibility(View.GONE);
            image_placeholder_license.setVisibility(View.GONE);
            image_placeholder_profile.setVisibility(View.GONE);

            GlideApp.with(getApplicationContext())
                    .load(regMainbean.profileImage)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(image_placeholder_profile);
            imageUrlProfile = regMainbean.profileImage;
            GlideApp.with(getApplicationContext())
                    .load(regMainbean.license)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(image_placeholder_license);
            imageUrllicense = regMainbean.license;
            GlideApp.with(getApplicationContext())
                    .load(regMainbean.adharcard)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(image_placeholder_adharcard);
            imageUrladharcard = regMainbean.adharcard;

        } catch (Exception e) {
        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().length() > 0 &&
                        phone.getText().toString().length() > 0 &&
                        password.getText().toString().length() > 0
                        && imageUrladharcard != null
                        && imageUrllicense != null
                        && imageUrlProfile != null
                ) {
                    registerUser();
                }
            }

        });


    }


    private void registerUser() {
        String tag_string_req = "req_register";
        pDialog.setMessage("Processing ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                CREATE_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");
                    String msg = jsonObject.getString("message");
                    if (success==1) {
                        final String delivername = name.getText().toString();
                        sendNotification(phone.getText().toString()
                                , delivername.length() > 30 ? delivername.substring(0, 29) + "..." :
                                        delivername);
                        finish();
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("name", name.getText().toString());
                localHashMap.put("phone", phone.getText().toString());
                localHashMap.put("image", imageUrlProfile);
                localHashMap.put("license", imageUrllicense);
                localHashMap.put("adharcard", imageUrladharcard);
                localHashMap.put("password", password.getText().toString());
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            return;
        }
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        String path = Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        imageutils.createImage(file, filename, path, false);
        pDialog.setMessage("Uploading...");
        showDialog();
        new UploadFileToServer().execute(imageutils.getPath(uri) + "@" + from);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageutils.onActivityResult(requestCode, resultCode, data);

    }

    private class UploadFileToServer extends AsyncTask<String, Integer, String> {
        public long totalSize = 0;
        String filepath;
        String type;

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pDialog.setMessage("Uploading..." + (progress[0]));
        }

        @Override
        protected String doInBackground(String... params) {
            filepath = params[0].split("@")[0];
            type = params[0].split("@")[1];
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(AppConfig.URL_IMAGE_UPLOAD);

                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filepath);
                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);

                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;

                }

            } catch (Exception e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("Response from server: ", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (type.equalsIgnoreCase("1")) {
                    if (!jsonObject.getBoolean("error")) {
                        GlideApp.with(getApplicationContext())
                                .load(filepath)
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(false)
                                .into(image_profile);
                        imageUrlProfile = AppConfig.IMAGE_URL + imageutils.getfilename_from_path(filepath);
                    } else {
                        imageUrlProfile = null;
                    }
                } else if (type.equalsIgnoreCase("2")) {
                    if (!jsonObject.getBoolean("error")) {
                        GlideApp.with(getApplicationContext())
                                .load(filepath)
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(false)
                                .into(image_license);
                        imageUrllicense = AppConfig.IMAGE_URL + imageutils.getfilename_from_path(filepath);
                    } else {
                        imageUrllicense = null;
                    }
                } else if (type.equalsIgnoreCase("3")) {
                    if (!jsonObject.getBoolean("error")) {
                        GlideApp.with(getApplicationContext())
                                .load(filepath)
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(false)
                                .into(image_adharcard);
                        imageUrladharcard = AppConfig.IMAGE_URL + imageutils.getfilename_from_path(filepath);
                    } else {
                        imageUrladharcard = null;
                    }
                }
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

            } catch (Error | Exception e) {
                Toast.makeText(getApplicationContext(), "Image not uploaded", Toast.LENGTH_SHORT).show();
            }
            hideDialog();
            // showing the server response in an alert dialog
            //showAlert(result);


            super.onPostExecute(result);
        }

    }

    private void sendNotification(String title, String description) {
        String tag_string_req = "req_register";
        showDialog();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to", "/topics/allDevices");
            jsonObject.put("priority", "high");
            JSONObject dataObject = new JSONObject();
            dataObject.put("title", title);
            dataObject.put("message", description);
            jsonObject.put("data", dataObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Register Response: ", response.toString());
                hideDialog();
                finish();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                return localHashMap;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap hashMap = new HashMap();
                hashMap.put("Content-Type", "application/json");
                hashMap.put("Authorization", "key=AAAAISMaBQA:APA91bF9Nr5JAxbb22sTmGm1kC0TTSB347gd_6UZ6DQqeLz-7gYyD73zSVsPT4gS0AtRVEN5AttPFmRZDKjWrHgiC3rOiuwUrgk41GFeMjT0JENvZ6FOKGbjmD2DWm2xqr2zhjQ5dzBC");
                return hashMap;
            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


}


