package com.quaie.wms.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import org.w3c.dom.Text;

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
    private EditText etBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbound);
        new TitleBuilder(InboundActivity.this).setTitleText("Inbound");
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etBarcode = (EditText)findViewById(R.id.et_scan_result);
        etBarcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                final String result = etBarcode.getText().toString();
                if(!TextUtils.isEmpty(result)) {
                    if(result.length()>3){
                        Logger.show(TAG, result);
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
                                ToastUtils.showToast(InboundActivity.this, "Error" + error.networkResponse.statusCode, Toast.LENGTH_SHORT);
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("token", Config.getCachedToken(InboundActivity.this));
                                map.put("merchantId", select_mer);
                                map.put("inboundNo", inbound_no);
                                map.put("commodityBarcode", result);
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
