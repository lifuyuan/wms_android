package com.quaie.wms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.quaie.wms.Config;
import com.quaie.wms.R;
import com.quaie.wms.adapter.UserItemAdapter;
import com.quaie.wms.application.MyApplication;
import com.quaie.wms.entity.UserItem;
import com.quaie.wms.utils.Logger;
import com.quaie.wms.utils.TitleBuilder;
import com.quaie.wms.utils.ToastUtils;
import com.quaie.wms.widget.WrapHeightListView;
import com.zxing.activity.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends Activity implements UserItemAdapter.Callback{
    protected String TAG;
    private String[] mer_name;
    private String[] mer_id;
    private String[] inbound_nos;
    private String[] inbound_batch_nos;

    //private ImageView iv_avatar;
    private TextView tv_subhead;
    private TextView tv_caption;

    //private TextView tv_status_count;
    //private TextView tv_follow_count;
    //private TextView tv_fans_count;

    private WrapHeightListView lv_user_items;
    private View view;

    private UserItemAdapter adapter;
    private List<UserItem> userItems;

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

        initView();
        setItem();
    }

    private void initView() {
        // 标题栏
        new TitleBuilder(MainActivity.this).setTitleText("WMS");
        // 用户信息
        //ll_userinfo = (LinearLayout) findViewById(R.id.ll_userinfo);

        //iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        tv_subhead = (TextView) findViewById(R.id.tv_subhead);
        tv_caption = (TextView) findViewById(R.id.tv_caption);
        tv_subhead.setText("fuyuan1");
        tv_caption.setText("admin");
        // 信息栏
        //tv_status_count = (TextView) findViewById(R.id.tv_status_count);
        //tv_follow_count = (TextView) findViewById(R.id.tv_follow_count);
        //tv_fans_count = (TextView) findViewById(R.id.tv_fans_count);
        // 列表
        lv_user_items = (WrapHeightListView) findViewById(R.id.lv_user_items);
        userItems = new ArrayList<UserItem>();
        adapter = new UserItemAdapter(MainActivity.this, userItems, this);
        lv_user_items.setAdapter(adapter);
    }

    // 设置栏列表
    private void setItem() {
        userItems.add(new UserItem(false, R.mipmap.push_icon_app_small_3, "Inbound", ""));
        userItems.add(new UserItem(false, R.mipmap.push_icon_app_small_3, "Mount", ""));
        userItems.add(new UserItem(false, R.mipmap.push_icon_app_small_3, "Pickup", "(2)"));
        userItems.add(new UserItem(false, R.mipmap.push_icon_app_small_3, "Pack", ""));

        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //MyApplication.getInstance().exit();
        new AlertDialog.Builder(this).setTitle("确认退出吗？")
                //.setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        MainActivity.super.onBackPressed();
                        MyApplication.getInstance().exit();
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();
    }

    @Override
    public void click(View v) {
        switch ((Integer) v.getTag()) {
            case 0:
                Intent intent = new Intent(MainActivity.this, InboundActivity.class);
                intent.putExtra("mer_name", mer_name);
                intent.putExtra("mer_id", mer_id);
                intent.putExtra("inbound_nos", inbound_nos);
                startActivity(intent);
                break;
            case 1:
                Intent intent1 = new Intent(MainActivity.this, MountActivity.class);
                intent1.putExtra("inbound_batch_nos", inbound_batch_nos);
                startActivity(intent1);
                break;
            case 2:
                Intent intent2 = new Intent(MainActivity.this, PickupActivity.class);
                startActivity(intent2);
                break;
            case 3:
                Intent intent3 = new Intent(MainActivity.this, OutboundActivity.class);
                startActivity(intent3);
                break;
            default:
                break;

        }
    }


}
