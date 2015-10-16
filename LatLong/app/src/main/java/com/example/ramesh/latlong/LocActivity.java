package com.example.ramesh.latlong;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocActivity extends Activity{
    Button btnGPSShowLocation;
    Button btnShowAddress;
    private EditText applname,appladdress1,appltype;
    TextView tvAddress,latlng;
    private String locationAddress;
    private double latitude1,longitude1;

    AppLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc);
        latlng=(TextView)findViewById(R.id.textview1);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        appLocationService = new AppLocationService(
                LocActivity.this);

        btnGPSShowLocation = (Button) findViewById(R.id.btnGPSShowLocation);
        btnGPSShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Location gpsLocation = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);
                if (gpsLocation != null) {
                    double latitude = gpsLocation.getLatitude();
                    double longitude = gpsLocation.getLongitude();
                    String result = "Latitude: " + gpsLocation.getLatitude() +
                            " Longitude: " + gpsLocation.getLongitude();
                    latlng.setText(result);
                } else {
                    showSettingsAlert();
                }
            }
        });

       btnShowAddress = (Button) findViewById(R.id.btnShowAddress);
        btnShowAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Location location = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);

                //you can hard-code the lat & long if you have issues with getting it
                //remove the below if-condition and use the following couple of lines
                //double latitude = 37.422005;
                //double longitude = -122.084095

                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude,
                            getApplicationContext(), new GeocoderHandler());
                } else {
                    showSettingsAlert();
                }

            }
        });
        Button insert=(Button)findViewById(R.id.buttonsubmit);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);
              latitude1=location.getLatitude();
                longitude1=location.getLongitude();
                applname=(EditText)findViewById(R.id.name1);
                appltype=(EditText)findViewById(R.id.type1);
                insertToDatabase(latitude1,longitude1);
            }
        });

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                LocActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        LocActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }

            tvAddress.setText(locationAddress);
        }
    }
    private void insertToDatabase(double lati,double longi){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(LocActivity.this, "Please wait", "Loading...");
            }
            @Override
            protected String doInBackground(String... params) {
                String lat=String.valueOf(latitude1);
                String lon=String.valueOf(longitude1);
                String nam=applname.getText().toString();
                String add=tvAddress.getText().toString();

                String tp=appltype.getText().toString();
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("nam",nam));
                nameValuePairs.add(new BasicNameValuePair("add",add));
                nameValuePairs.add(new BasicNameValuePair("lat", lat));
                nameValuePairs.add(new BasicNameValuePair("lon", lon));
                nameValuePairs.add(new BasicNameValuePair("tp",tp));
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://pubgriev.16mb.com/insertdb.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();



                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }
                return "success";
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                loadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
}
