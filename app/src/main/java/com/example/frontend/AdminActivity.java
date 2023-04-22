package com.example.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend.entity.Result;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminActivity extends AppCompatActivity {

    public static LinkedTreeMap<String, String> user;


    private EditText mEtStoreSearch;
    private RequestBody requestBody;
    private OkHttpClient okHttpClient;
    private Handler mHandler;
    private Button mBtnAdminNew;
    private EditText mEtName;
    private EditText mEtLocaton;
    private EditText mEtLimit;
    private EditText mEtService;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mEtStoreSearch = findViewById(R.id.mEtStoreSearch);
        mBtnAdminNew = findViewById(R.id.btn_adminnew);
        mEtLocaton = findViewById(R.id.et_location);
        mEtName = findViewById(R.id.et_name);
        mEtLimit = findViewById(R.id.et_limit);
        mEtService = findViewById(R.id.et_service);
        mHandler = new Handler();
        mBtnAdminNew.setOnClickListener((view)->{
            String location = String.valueOf(mEtLocaton.getText());
            String name = String.valueOf(mEtName.getText());
            int limit = Integer.parseInt(mEtLimit.getText().toString());
            String service = String.valueOf(mEtService.getText());
            String phone = String.valueOf(MainActivity.user.get("phone"));
            createInstitution(phone, location, name, limit, service);
        });
//        mBtn.setOnClickListener((view) -> {
//            admin_search(Integer.parseInt(Objects.requireNonNull(MainActivity.user.get("uid"))));
//        });
    }

    private void createInstitution(String phone, String location, String name, int limit, String service) {
        FormBody.Builder formBody = new FormBody.Builder();
        String id = Objects.requireNonNull(String.valueOf(MainActivity.user.get("id")));
        id = id.substring(0,id.length()-2);
        formBody.add("uid", id);
        formBody.add("name", name);
        formBody.add("phone", phone);
        formBody.add("location", location);
        formBody.add("limitation", String.valueOf(limit));
        formBody.add("service", service);

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3000, TimeUnit.SECONDS)
                .callTimeout(3000, TimeUnit.SECONDS)
                .build();
        requestBody = formBody.build();
        //Okhttp3同步请求 开启线程
        Thread thread = new Thread() {
            @Override
            public void run() {
                //设置请求的地址
                Request request = new Request.Builder()
                        .url("http://43.138.218.156:8080/create_institution")
                        .post(requestBody).build();
                Response response = null;
                try {
                    //同步请求
                    response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String res = Objects.requireNonNull(response.body()).string();
                        Gson gson = new Gson();
                        Result result = gson.fromJson(res, Result.class);
                        System.out.println(result);
                        if (result.getSuccess()) {
                            mHandler.post(() -> {
                                Toast.makeText(AdminActivity.this, "create successfully!", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            mHandler.post(() -> {
                                Toast.makeText(AdminActivity.this, result.getErrorMsg(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        System.out.println("服务器连接失败");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void admin_search(int uid) {
        FormBody.Builder formBody = new FormBody.Builder();

        okHttpClient = new OkHttpClient.Builder().connectTimeout(3000, TimeUnit.SECONDS).callTimeout(3000, TimeUnit.SECONDS).build();
        requestBody = formBody.build();

        //Okhttp3同步请求 开启线程
        Thread thread = new Thread() {
            @Override
            public void run() {
                //设置请求的地址
                Request request = new Request.Builder().url("http://43.138.218.156:8080/get_institution?uid=" + Integer.toString(uid)).method("GET", requestBody).build();
                Response response = null;
                try {
                    //同步请求
                    response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String res = Objects.requireNonNull(response.body()).string();
                        Gson gson = new Gson();
                        Result result = gson.fromJson(res, Result.class);

                        System.out.println(result);

                        if (result.getSuccess()) {
                            mHandler.post(() -> {
                                /*
                                Toast.makeText(RegisterActivity.this, "register successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, AdminActivity.class);
                                startActivity(intent);
                                 */
                            });
                        } else {
                            mHandler.post(() -> {
                                //Toast.makeText(RegisterActivity.this, result.getErrorMsg(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        System.out.println("服务器连接失败");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }


}