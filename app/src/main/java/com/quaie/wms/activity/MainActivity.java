package com.quaie.wms.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.quaie.wms.Config;
import com.quaie.wms.R;
import com.quaie.wms.utils.Logger;
import com.quaie.wms.utils.ToastUtils;
import com.zxing.activity.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public class MainActivity extends Activity{
    protected String TAG;
    private String[] mer_name;
    private String[] mer_id;
    private String[] inbound_nos;
    private String[] inbound_batch_nos;
    private Button btnInbound;
    private Button btnMound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ProgressDialog pd = ProgressDialog.show(MainActivity.this, "Connecting", "Connecting to server,please wait");
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url1 = "http://www.24upost.com:5001/wms/android/merchants?token=" + Config.getCachedToken(this);
        StringRequest request1 = new StringRequest(Request.Method.GET, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Logger.show(TAG, result);
                        try {
                            JSONObject obj = new JSONObject(result);
                            Logger.show(TAG, obj.getJSONArray("merchant").toString());
                            JSONArray merchant_array = obj.getJSONArray("merchant");
                            mer_name = new String[merchant_array.length()];
                            mer_id = new String[merchant_array.length()];
                            for(int i = 0; i < merchant_array.length(); i++) {//遍历JSONArray
                                JSONObject merchant = (JSONObject)merchant_array.get(i);
                                for(Iterator iter = merchant.keys(); iter.hasNext();){
                                    String key = (String)iter.next();
                                    String value = merchant.getString(key);
                                    Logger.show(TAG, key + value);
                                    mer_name[i] = key;
                                    mer_id[i] = value;
                                }
                            }
                        }catch (JSONException e) {
                            ToastUtils.showToast(MainActivity.this, "Obtain Merchant Failed!", Toast.LENGTH_SHORT);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG);
            }
        });
        queue.add(request1);


        String url2 = "http://www.24upost.com:5001/wms/android/inbound_nos?token=" + Config.getCachedToken(this);
        StringRequest request2 = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Logger.show(TAG, result);
                        try {
                            JSONObject obj = new JSONObject(result);
                            JSONArray inboundNo_array = obj.getJSONArray("inbound_no");
                            inbound_nos = new String[inboundNo_array.length()];
                            for(int i = 0; i < inboundNo_array.length(); i++) {//遍历JSONArray
                                String inbound_no = (String)inboundNo_array.get(i);
                                inbound_nos[i] = inbound_no;
                                Logger.show(TAG, inbound_no);
                            }
                            pd.dismiss();
                        }catch (JSONException e) {
                            ToastUtils.showToast(MainActivity.this, "Obtain Inbound No. Failed!", Toast.LENGTH_SHORT);
                            e.printStackTrace();
                        }
                        ToastUtils.showToast(MainActivity.this, result, Toast.LENGTH_LONG);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG);
                pd.dismiss();
            }
        });
        queue.add(request2);

        String url3 = "http://www.24upost.com:5001/wms/android/inbound_batch_nos?token=" + Config.getCachedToken(this);
        StringRequest request3 = new StringRequest(Request.Method.GET, url3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Logger.show(TAG, result);
                        try {
                            JSONObject obj = new JSONObject(result);
                            JSONArray inboundNo_array = obj.getJSONArray("inbound_no");
                            inbound_batch_nos = new String[inboundNo_array.length()];
                            for(int i = 0; i < inboundNo_array.length(); i++) {//遍历JSONArray
                                String inbound_no = (String)inboundNo_array.get(i);
                                inbound_batch_nos[i] = inbound_no;
                                Logger.show(TAG, inbound_no);
                            }
                            pd.dismiss();
                        }catch (JSONException e) {
                            ToastUtils.showToast(MainActivity.this, "Obtain Inbound Batch No. Failed!", Toast.LENGTH_SHORT);
                            e.printStackTrace();
                        }
                        ToastUtils.showToast(MainActivity.this, result, Toast.LENGTH_LONG);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG);
                pd.dismiss();
            }
        });
        queue.add(request3);
        queue.start();
        btnInbound = (Button)findViewById(R.id.btnInbound);
        btnInbound.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InboundActivity.class);
                intent.putExtra("mer_name", mer_name);
                intent.putExtra("mer_id", mer_id);
                intent.putExtra("inbound_nos", inbound_nos);
                startActivity(intent);
            }
        });
        btnMound = (Button)findViewById(R.id.btnMount);
        btnMound.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MountActivity.class);
                intent.putExtra("inbound_batch_nos", inbound_batch_nos);
                startActivity(intent);
            }
        });
    }

}
