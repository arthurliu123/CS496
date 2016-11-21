package benderi.proj4;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddSale extends AppCompatActivity implements LocationListener {

    Spinner dropdown;
    private EditText mQuantity;
    private EditText mPrice;
    private EditText mTotal;
    private TextView mError;
    List<String> itemNames = new ArrayList<String>();
    List<String> itemKeys = new ArrayList<String>();
    String selectedKey;

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    double lat;
    double lng;
    String provider;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sale);
        JSONObject json = null;

        String itemPost;
        mQuantity = (EditText) findViewById(R.id.txt_quantity);
        mPrice = (EditText) findViewById(R.id.txt_price);
        mTotal = (EditText) findViewById(R.id.txt_total);
        mError = (TextView) findViewById(R.id.txt_error);

        try {
            json = new MyAsyncTaskGet().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println(json.names().toString());

        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                JSONObject value = json.getJSONObject(key);
                itemNames.add(value.getString("name"));
                itemKeys.add(value.getString("key"));
            } catch (Exception e) {
            }
        }

        System.out.println(itemNames.toString());
        System.out.println(itemKeys.toString());

        dropdown = (Spinner) findViewById(R.id.spinner_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemNames);
        dropdown.setAdapter(adapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            statusCheck();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        final JSONObject finalJson = json;
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                System.out.println(dropdown.getSelectedItemPosition());
                try {
                    int itemPos = dropdown.getSelectedItemPosition();
                    double itemPrice = Double.parseDouble(finalJson.getJSONObject(itemKeys.get(itemPos)).getString("price"));
                    selectedKey = finalJson.getJSONObject(itemKeys.get(itemPos)).getString("key");
                    mPrice.setText(String.format("%.2f", itemPrice));
                    int quantity;
                    if (mQuantity.getText().length() == 0)
                        quantity = 0;
                    else
                        quantity = Integer.parseInt(mQuantity.getText().toString());
                    mTotal.setText(String.format("%.2f",
                            Double.parseDouble(mPrice.getText().toString()) * quantity));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        mQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int quantity;
                if (mQuantity.getText().length() == 0)
                    quantity = 0;
                else
                    quantity = Integer.parseInt(mQuantity.getText().toString());
                try {
                    mTotal.setText(String.format("%.2f",
                            Double.parseDouble(mPrice.getText().toString()) * quantity));
                } catch (Exception e) {
                    mTotal.setText("0.00");
                }
            }
        });

        mPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int quantity;
                if (mQuantity.getText().length() == 0)
                    quantity = 0;
                else
                    quantity = Integer.parseInt(mQuantity.getText().toString());
                try {
                    mTotal.setText(String.format("%.2f",
                            Double.parseDouble(mPrice.getText().toString()) * quantity));
                } catch (Exception e) {
                    mTotal.setText("0.00");
                }
            }
        });
    }
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        latitude = String.format("%f", lat);
        lng = location.getLongitude();
        longitude = String.format("%f", lng);
    }

    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    private class MyAsyncTaskGet extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                return getItems();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) {

        }

        protected void onProgressUpdate(Integer... progress) {

        }

        public JSONObject getItems() throws JSONException, IOException {
            JSONObject json = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet("https://jiawei-liu-cs496-assign4.appspot.com/memos");

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpget);
            json = new JSONObject(EntityUtils.toString(response.getEntity()));

            return json;
        }
    }

    public void postSale(View view) throws IOException, ExecutionException, InterruptedException, JSONException {
        Intent i = new Intent(AddSale.this,
                ViewSale.class);
        Bundle b = new Bundle();
        boolean hasQuantity = true;
        boolean hasPrice = true;

        if (mQuantity.length() == 0) {
            hasQuantity= false;
            mError.setText("Quantity required!");
        }
        if (mPrice.length() == 0) {
            hasPrice= false;
            mError.setText("Price required!");
        }
        if (!(hasQuantity && hasPrice))
            return;

        JSONObject json =
                new MyAsyncTaskPost().execute(selectedKey,
                                  mPrice.getText().toString(),
                                  mQuantity.getText().toString()).get();

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
        System.out.println(json.getString("items"));
        System.out.println(json.getString("item_cost"));
        System.out.println(json.getString("quantity"));
        System.out.println(json.getString("total_cost"));
        System.out.println(json.getString("latitude"));
        System.out.println(json.getString("longitude"));
        System.out.println(json.getString("datetime"));

        b.putString("items", json.getString("items"));
        b.putString("item_cost", json.getString("item_cost"));
        b.putString("quantity", json.getString("quantity"));
        b.putString("total_cost", json.getString("total_cost"));
        b.putString("latitude", json.getString("latitude"));
        b.putString("longitude", json.getString("longitude"));
        b.putString("datetime", json.getString("datetime"));
        i.putExtras(b);
        startActivity(i);
    }

    private class MyAsyncTaskPost extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                return postData(params);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) {

        }

        protected void onProgressUpdate(Integer... progress) {

        }

        public JSONObject postData(String[] params) throws JSONException {
            // Create a new HttpClient and Post Header
            JSONObject json = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://webapi-1291.appspot.com/sale");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("items[]", params[0]));
                nameValuePairs.add(new BasicNameValuePair("item_cost[]", params[1]));
                nameValuePairs.add(new BasicNameValuePair("quantity[]", params[2]));
                nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
                nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                json = new JSONObject(EntityUtils.toString(response.getEntity()));

            } catch (ClientProtocolException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println(e);
            }
            return json;
        }
    }

    public void statusCheck()
    {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
