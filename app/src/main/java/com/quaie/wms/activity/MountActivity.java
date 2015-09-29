package com.quaie.wms.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
 * Created by fuyuan on 2015/9/20.
 */
public class MountActivity extends Activity {
    protected String TAG;
    private Spinner s1;
    private String[] inbound_batch_nos;
    private String selected_ibn;
    private HashMap<String, Object> result;
    private EditText etShelf;
    private EditText etBarcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mount);
        new TitleBuilder(MountActivity.this).setTitleText("Mount");
        Intent i = getIntent();
        inbound_batch_nos = (String[])i.getCharSequenceArrayExtra("inbound_batch_nos");
        s1 = (Spinner)findViewById(R.id.spinner1);

        s1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_gallery_item, inbound_batch_nos));
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_ibn = inbound_batch_nos[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etShelf = (EditText)findViewById(R.id.et_shelf);
        etBarcode = (EditText)findViewById(R.id.et_mount_barcode);
        etBarcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                final String barcode = etBarcode.getText().toString();
                final String shelf = etShelf.getText().toString();
                if(!TextUtils.isEmpty(barcode)) {
                    if(barcode.length()>3){
                        final ProgressDialog pd = ProgressDialog.show(MountActivity.this, "Connecting", "Connecting to server,please wait");
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String url = "http://www.24upost.com:5001/wms/android/mount";
                        StringRequest request = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String result) {
                                        pd.dismiss();
                                        ToastUtils.showToast(MountActivity.this, "Successful", Toast.LENGTH_SHORT);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.dismiss();
                                ToastUtils.showToast(MountActivity.this, "Error"+error.networkResponse.statusCode, Toast.LENGTH_SHORT);
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("token", Config.getCachedToken(MountActivity.this));
                                map.put("inboundBatchNo", selected_ibn);
                                map.put("shelfNum", shelf);
                                map.put("commodityBarcode", barcode);
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
