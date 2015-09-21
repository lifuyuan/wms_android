package com.quaie.wms.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.zxing.activity.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fuyuan on 2015/9/16.
 */
public class LoginActivity  extends Activity {
    protected String TAG;
    private EditText username = null;
    private EditText password = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        new TitleBuilder(LoginActivity.this).setTitleText("WMS");
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(username.getText())) {
                    ToastUtils.showToast(LoginActivity.this, "Username can't be blank!", Toast.LENGTH_SHORT);
                    return;
                }

                if (TextUtils.isEmpty(password.getText())) {
                    ToastUtils.showToast(LoginActivity.this, "Password can't be blank!", Toast.LENGTH_SHORT);
                    return;
                }

                final ProgressDialog pd = ProgressDialog.show(LoginActivity.this, "Connecting", "Connecting to server,please wait");
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = "http://www.24upost.com:5001/wms/android/accounts/sign_in";
                StringRequest request = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String result) {
                            pd.dismiss();
                            try {
                                JSONObject obj = new JSONObject(result);
                                Config.cacheToken(LoginActivity.this, obj.getString("token"));
                            } catch (JSONException e) {
                                ToastUtils.showToast(LoginActivity.this, "Sign In failed", Toast.LENGTH_SHORT);
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                            ToastUtils.showToast(LoginActivity.this, "Please check your username or password", Toast.LENGTH_SHORT);
                        }
                    }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("name", username.getText().toString());
                        map.put("password", password.getText().toString());
                        return map;
                    }
                };
                request.setTag("wmsPost");
                queue.add(request);
                queue.start();
            }
        });
    }
}
