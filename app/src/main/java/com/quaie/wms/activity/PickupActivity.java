package com.quaie.wms.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.quaie.wms.Config;
import com.quaie.wms.R;
import com.quaie.wms.utils.TitleBuilder;
import com.quaie.wms.utils.ToastUtils;
import com.utils.SerializableMap;
import com.zxing.activity.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by fuyuan on 2015/9/21.
 */
public class PickupActivity  extends Activity {
    protected String TAG;
    private EditText etWaveNo;
    private EditText etBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);
        new TitleBuilder(PickupActivity.this).setTitleText("Pickup");
        etWaveNo = (EditText)findViewById(R.id.et_wave);
        etBarcode = (EditText)findViewById(R.id.et_wave_barcode);

        etBarcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                final String waveNo = etWaveNo.getText().toString();
                final String barcodeNo = etBarcode.getText().toString();
                if(!TextUtils.isEmpty(barcodeNo)) {
                    if (barcodeNo.length() > 3) {
                        final ProgressDialog pd = ProgressDialog.show(PickupActivity.this, "Connecting", "Connecting to server,please wait");
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String url = "http://www.24upost.com:5001/wms/android/sorting";
                        StringRequest request = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String result) {
                                        pd.dismiss();
                                        ToastUtils.showToast(PickupActivity.this, "Successful", Toast.LENGTH_SHORT);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.dismiss();
                                ToastUtils.showToast(PickupActivity.this, "Error"+error.networkResponse.statusCode, Toast.LENGTH_SHORT);
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("token", Config.getCachedToken(PickupActivity.this));
                                map.put("waveNo", waveNo);
                                map.put("commodityBarcode", barcodeNo);
                                return map;
                            }
                        };
                        request.setTag("wmsPost");
                        queue.add(request);
                        queue.start();
                    }
                    etBarcode.setText(null);
                }
                return false;
            }
        });

    }

}
