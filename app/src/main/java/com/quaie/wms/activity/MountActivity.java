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
    private TextView barcodeResultTextView;
    private TextView shelfResultTextView;
    private String[] inbound_batch_nos;
    private String selected_ibn;
    private Button btnScanShelf;
    private Button btnScanBarcode;
    private Button btnSubmit;
    private HashMap<String, Object> result;
    private int commodity_amount = 0;
    private JSONArray result_array_barcode;
    private String result_array_shelf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mount);
        new TitleBuilder(MountActivity.this).setTitleText("Mount");
        shelfResultTextView = (TextView) this.findViewById(R.id.tv_scan_shelf);
        barcodeResultTextView = (TextView) this.findViewById(R.id.tv_scan_barcode);
        Intent i = getIntent();
        inbound_batch_nos = (String[])i.getCharSequenceArrayExtra("inbound_batch_nos");
        s1 = (Spinner)findViewById(R.id.spinner1);

        s1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_gallery_item, inbound_batch_nos));
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_ibn = inbound_batch_nos[position];
                ToastUtils.showToast(MountActivity.this, selected_ibn, Toast.LENGTH_SHORT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnScanShelf = (Button)findViewById(R.id.btnScanShelf);
        btnScanShelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MountActivity.this, CaptureActivity.class);
                intent.putExtra("isContinue", "no");
                intent.putExtra("scanType", "mount_shelf");
                startActivityForResult(intent, 0);
            }
        });

        btnScanBarcode = (Button)findViewById(R.id.btnScanBarcode);
        btnScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MountActivity.this, CaptureActivity.class);
                intent.putExtra("isContinue", "yes");
                intent.putExtra("scanType", "mount_commodity");
                startActivityForResult(intent, 0);
            }
        });


        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = ProgressDialog.show(MountActivity.this, "Connecting", "Connecting to server,please wait");
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = "http://www.24upost.com:5001/wms/android/mount";
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String result) {
                                pd.dismiss();
                                ToastUtils.showToast(MountActivity.this, "Successful", Toast.LENGTH_SHORT);
                                MountActivity.this.finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        ToastUtils.showToast(MountActivity.this, "Error", Toast.LENGTH_SHORT);
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("token", Config.getCachedToken(MountActivity.this));
                        map.put("inboundBatchNo", selected_ibn);
                        map.put("shelfNum", result_array_shelf);
                        map.put("mountedBarcode", result_array_barcode.toString());
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
            if (scanType.equals("mount_commodity")) {
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
            else if (scanType.equals("mount_shelf")) {
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    result_array_shelf = (String) entry.getKey();
                }
                shelfResultTextView.setText(result_array_shelf);
            }
        }
    }
}
