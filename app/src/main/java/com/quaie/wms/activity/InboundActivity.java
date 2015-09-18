package com.quaie.wms.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.common.StringUtils;
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
 * Created by fuyuan on 2015/9/17.
 */
public class InboundActivity   extends Activity {

    protected String TAG;
    private Spinner s1;
    private Spinner s2;
    private String[] mer_name;
    private String[] mer_id;
    private String[] inbound_nos;
    private String select_mer;
    private String inbound_no;
    private Button btnScan;
    private HashMap<String, Object> result;
    private int commodity_amount = 0;
    private JSONArray result_array;
    private TextView resultTextView;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbound);
        new TitleBuilder(InboundActivity.this).setTitleText("Inbound");
        resultTextView = (TextView) this.findViewById(com.ericssonlabs.R.id.tv_scan_result);
        Intent i = getIntent();
        mer_name = (String[])i.getCharSequenceArrayExtra("mer_name");
        mer_id = (String[])i.getCharSequenceArrayExtra("mer_id");
        inbound_nos = (String[])i.getCharSequenceArrayExtra("inbound_nos");
        s1 = (Spinner)findViewById(R.id.spinner1);

        s1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_gallery_item, mer_name));
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_mer = mer_id[position];
                ToastUtils.showToast(InboundActivity.this, select_mer, Toast.LENGTH_SHORT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        s2 = (Spinner)findViewById(R.id.spinner2);

        s2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_gallery_item, inbound_nos));
        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                inbound_no = inbound_nos[position];
                ToastUtils.showToast(InboundActivity.this, inbound_no, Toast.LENGTH_SHORT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnScan = (Button)findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InboundActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 0);
            }
        });


        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = ProgressDialog.show(InboundActivity.this, "Connecting", "Connecting to server,please wait");
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = "http://www.24upost.com:5001/wms/android/inbound";
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String result) {
                                pd.dismiss();
                                ToastUtils.showToast(InboundActivity.this, "Successful", Toast.LENGTH_SHORT);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        ToastUtils.showToast(InboundActivity.this, "Error", Toast.LENGTH_SHORT);
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("token", Config.getCachedToken(InboundActivity.this));
                        map.put("merchantId", select_mer);
                        if (!TextUtils.isEmpty(inbound_no)) {
                            map.put("inboundNo", inbound_no);
                        }
                        map.put("inboundBarcode", result_array.toString());
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
            result = (HashMap<String, Object>)resultmap.getMap();
            Iterator iter = result.entrySet().iterator();
            result_array = new JSONArray();
            while(iter.hasNext()) {
                Map.Entry entry = (Map.Entry)iter.next();
                String key = (String)entry.getKey();
                Integer value = (Integer)entry.getValue();
                commodity_amount = commodity_amount + value;
                //Map map = new HashMap();
                //map.put(key, value);
                //result_array.put(map);

                JSONObject jsonmap1 = new JSONObject();
                try {
                    jsonmap1.put("commodityBarcode", key);
                    jsonmap1.put("quantity", value);
                    result_array.put(jsonmap1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            resultTextView.setText(result_array.toString());
        }
    }
}
