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
import com.quaie.wms.utils.Logger;
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
public class OutboundActivity   extends Activity {

    protected String TAG;
    private EditText etBarcode;
    private EditText etOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outbound);
        new TitleBuilder(OutboundActivity.this).setTitleText("Order Pack");
        etBarcode = (EditText)findViewById(R.id.et_order_barcode);
        etOrder = (EditText)findViewById(R.id.et_order);
        etOrder.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                final HashMap<String, Object> commodities = new HashMap();
                final String orderNo = etOrder.getText().toString();
                final JSONArray result_array_barcode;
                String barcodes = etBarcode.getText().toString();
                String[] abarcodes = barcodes.split("\n");
                if(!TextUtils.isEmpty(orderNo)) {
                    if (orderNo.length() > 1) {
                        Logger.show(TAG,"111111111111111111111");
                        for (int i = 0; i < abarcodes.length; i++) {
                            int barcode_num = 1;
                            Iterator iter = commodities.entrySet().iterator();
                            while (iter.hasNext()) {
                                Map.Entry entry = (Map.Entry) iter.next();
                                String key = (String) entry.getKey();
                                Integer value = (Integer) entry.getValue();
                                if (abarcodes[i].equals(key)) {
                                    barcode_num = value + 1;
                                }
                            }
                            commodities.put(abarcodes[i], barcode_num);
                        }
                        result_array_barcode = new JSONArray();
                        Iterator iter = commodities.entrySet().iterator();
                        while (iter.hasNext()) {
                            Map.Entry entry = (Map.Entry) iter.next();
                            String key = (String) entry.getKey();
                            Integer value = (Integer) entry.getValue();

                            JSONObject jsonmap1 = new JSONObject();
                            try {
                                jsonmap1.put("commodityBarcode", key);
                                jsonmap1.put("quantity", value);
                                result_array_barcode.put(jsonmap1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        final ProgressDialog pd = ProgressDialog.show(OutboundActivity.this, "Connecting", "Connecting to server,please wait");
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String url = "http://www.24upost.com:5001/wms/android/unbound";
                        StringRequest request = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String result) {
                                        pd.dismiss();
                                        ToastUtils.showToast(OutboundActivity.this, "Successful", Toast.LENGTH_SHORT);

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.dismiss();
                                ToastUtils.showToast(OutboundActivity.this, "Error" + error.networkResponse.statusCode, Toast.LENGTH_SHORT);
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("token", Config.getCachedToken(OutboundActivity.this));
                                map.put("orderNo", orderNo);
                                map.put("outboundBarcode", result_array_barcode.toString());
                                return map;
                            }
                        };
                        request.setTag("wmsPost");
                        queue.add(request);
                        queue.start();
                    }
                    etBarcode.setText(null);
                    etOrder.setText(null);
                    etBarcode.setFocusable(true);
                    etOrder.setFocusable(false);
                }
                return false;
            }
        });
    }

}
