package com.hzag.smartgarden;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {


    private CustomGauge tempGauge;
    private CustomGauge lightGauge;
    private CustomGauge humGauge;
    private TextView    tempView;
    private TextView    lightView;
    private TextView    humView;
    private double temp;
    private double light;
    private double hum;
    private  Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int status = api.isGooglePlayServicesAvailable(this);
        Log.e("api", String.valueOf(status));
        Log.e("FirebaseToken", "token "  + FirebaseInstanceId.getInstance().getToken());

        tempGauge = findViewById(R.id.gauge1);
        lightGauge = findViewById(R.id.gauge2);
        humGauge = findViewById(R.id.gauge3);

        tempView  = findViewById(R.id.textView1);
        lightView  = findViewById(R.id.textView2);
        humView = findViewById(R.id.textView3);

        tempView.setText(Integer.toString(tempGauge.getValue()) + "°");
        lightView.setText(Integer.toString(lightGauge.getValue()) + "lx");
        humView.setText(Integer.toString(humGauge.getValue()) + "%");
        handler = new Handler();


        handler.post(new Runnable() {
            @Override
            public void run() {

                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url = "http://192.168.1.220/web_service.php";
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    temp = response.getDouble("temp") ;
                                    light = response.getDouble("light") ;
                                    hum = response.getDouble("hum") ;

                                    tempGauge.setValue( (int) temp);
                                    lightGauge.setValue( (int )light);
                                    humGauge.setValue( (int )hum);

                                    tempView.setText( temp + "°");
                                    lightView.setText(light + "lx");
                                    humView.setText(hum + "%");



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.getMessage());
                    }
                });
                queue.add(jsObjRequest);

                handler.postDelayed(this,1000*60);
            }
        });
    }

}
