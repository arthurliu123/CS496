package benderi.proj4;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
//
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.Manifest;


public class MainActivity extends AppCompatActivity {

    private EditText value;
    private Button btn;
    private ProgressBar pb;
    private EditText mName;
    private EditText mDesc;
    private EditText mPrice;
    private EditText mSize;
    //-------------------
    private Button takePictureButton;
    private ImageView imageView;
    private Uri file;
//---------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) throws SecurityException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mName = (EditText) findViewById(R.id.txt_items);
        mDesc = (EditText) findViewById(R.id.txt_item_cost);
        mPrice = (EditText) findViewById(R.id.txt_quantity);
        mSize = (EditText) findViewById(R.id.txt_total_cost);
        handleNotification();

        //------------------------------
        takePictureButton = (Button) findViewById(R.id.button_image);
        imageView = (ImageView) findViewById(R.id.imageview);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

    }

//----------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }

    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        startActivityForResult(intent, 100);
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("CameraDemo", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(file);
            }
        }
    }
//----------------
    public void addItem(View view) throws IOException, ExecutionException, InterruptedException, JSONException {
        Intent i = new Intent(MainActivity.this,
                ViewItem.class);
        Bundle b = new Bundle();
        boolean hasName = true;
        boolean hasDesc = true;
        boolean hasPrice = true;

        if (mName.length() == 0) {
            hasName = false;
            mName.setText("Name required!");
            mName.setTextColor(Color.RED);
        }
        if (mDesc.length() == 0) {
            hasDesc = false;
            mDesc.setText("Description required!");
            mDesc.setTextColor(Color.RED);
        }
        if (!(hasName && hasDesc))
            return;

        String postData = "name=" + mName.getText().toString() + "&" +
                "description=" + mDesc.getText().toString() + "&" +
                "importance=" + mPrice.getText().toString() + "&" +
                "finish=" + mSize.getText().toString();
        JSONObject json =
                new MyAsyncTask().execute(mName.getText().toString(),
                                  mDesc.getText().toString(),
                                  mPrice.getText().toString(),
                                  mSize.getText().toString()).get();

        System.out.println(json.getString("name"));
        System.out.println(json.getString("description"));
        System.out.println(json.getString("importance"));
        System.out.println(json.getString("finish"));

        b.putString("name", json.getString("name"));
        b.putString("description", json.getString("description"));
        b.putString("importance", json.getString("importance"));
        b.putString("finish", json.getString("finish"));
        i.putExtras(b);

        startActivity(i);

        mName.setText("");
        mDesc.setText("");
        mPrice.setText("");
        mSize.setText("");
    }

    public void newSale(View view) throws IOException, ExecutionException {
        Intent i = new Intent(MainActivity.this,
                AddSale.class);
        startActivity(i);
    }



    private class MyAsyncTask extends AsyncTask<String, Integer, JSONObject>{

        @Override
        protected JSONObject doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                return postData(params);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json){

        }

        protected void onProgressUpdate(Integer... progress) {

        }

        public JSONObject postData(String[] params) throws JSONException {
            // Create a new HttpClient and Post Header
            JSONObject json = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://jiawei-liu-cs496-assign4.appspot.com/memos");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("name", params[0]));
                nameValuePairs.add(new BasicNameValuePair("description", params[1]));
                nameValuePairs.add(new BasicNameValuePair("importance", params[2]));
                nameValuePairs.add(new BasicNameValuePair("finish", params[3]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                json = new JSONObject(EntityUtils.toString(response.getEntity()));

            } catch (IOException e) {
                System.out.println(e);
            }
            return json;
        }

    }

    private void handleNotification() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 41);
        calendar.set(Calendar.SECOND, 00);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 2000, pendingIntent);
    }
























}