package com.quaie.wms.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
public class OutboundActivity   extends Activity {

    protected String TAG;
    private TextView barcodeResultTextView;
    private TextView orderResultTextView;
    private Button btnScanOrder;
    private Button btnScanCommodity;
    private Button btnSubmit;
    private HashMap<String, Object> result;
    private int commodity_amount = 0;
    private JSONArray result_array_barcode;
    private String result_array_order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outbound);
        new TitleBuilder(OutboundActivity.this).setTitleText("Order Pack");
        orderResultTextView = (TextView)this.findViewById(R.id.tv_scan_order);
        barcodeResultTextView = (TextView)this.findViewById(R.id.tv_scan_commodity1);

        btnScanOrder = (Button)findViewById(R.id.btnScanOrder);
        btnScanOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OutboundActivity.this, CaptureActivity.class);
                intent.putExtra("isContinue", "no");
                intent.putExtra("scanType", "order_no");
                startActivityForResult(intent, 0);
            }
        });

        btnScanCommodity = (Button)findViewById(R.id.btnScanCommodity1);
        btnScanCommodity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OutboundActivity.this, CaptureActivity.class);
                intent.putExtra("isContinue", "yes");
                intent.putExtra("scanType", "outbound_commodity");
                startActivityForResult(intent, 0);
            }
        });

        btnSubmit = (Button)findViewById(R.id.btnSubmit3);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = ProgressDialog.show(OutboundActivity.this, "Connecting", "Connecting to server,please wait");
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = "http://www.24upost.com:5001/wms/android/unbound";
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String result) {
                                pd.dismiss();
                                ToastUtils.showToast(OutboundActivity.this, "Successful", Toast.LENGTH_SHORT);
                                OutboundActivity.this.finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        ToastUtils.showToast(OutboundActivity.this, "Error", Toast.LENGTH_SHORT);
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("token", Config.getCachedToken(OutboundActivity.this));
                        map.put("orderNo", result_array_order);
                        map.put("outboundBarcode", result_array_barcode.toString());
                        return map;
                    }
                };
                request.setTag("wmsPost");
                queue.add(request);
                queue.start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            SerializableMap resultmap = (SerializableMap)data.getExtras().getSerializable("result");
            String scanType = data.getStringExtra("scanType");
            result = (HashMap<String, Object>)resultmap.getMap();
            Iterator iter = result.entrySet().iterator();
            if (scanType.equals("outbound_commodity")) {
                result_array_barcode = new JSONArray();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String key = (String) entry.getKey();
                    Integer value = (Integer) entry.getValue();
                    commodity_amount = commodity_amount + value;

                    JSONObject jsonmap1 = new JSONObject();
                    try {
                        jsonmap1.put("commodityBarcode", key);
                        jsonmap1.put("quantity", value);
                        result_array_barcode.put(jsonmap1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                barcodeResultTextView.setText(result_array_barcode.toString());
            }
            else if (scanType.equals("order_no")) {
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    result_array_order = (String) entry.getKey();
                }
                orderResultTextView.setText(result_array_order);
            }
        }
    }
}
