package com.example.frontend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend.entity.Result;
import com.example.frontend.entity.User;
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

public class MainActivity extends AppCompatActivity {

    public static LinkedTreeMap<String, String> user;


    private Button mBtn;
    private Button mBtnRegister;
    private EditText mEtUsername;
    private EditText mEtPassword;
    private RequestBody requestBody;
    private OkHttpClient okHttpClient;

    private Handler mHandler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtn = findViewById(R.id.btn_login);
        mBtnRegister = findViewById(R.id.btn_register_fw);
        mEtUsername = findViewById(R.id.et_name);
        mEtPassword = findViewById(R.id.et_password);
        mHandler = new Handler();

        mBtn.setOnClickListener((view) -> {
            String name = mEtUsername.getText().toString().trim();
            String password = mEtPassword.getText().toString().trim();
            login(name, password);
        });

        mBtnRegister.setOnClickListener((view)->{
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }

    private void login(final String name, final String password) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("name", name);
        formBody.add("password", password);

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
                        .url("http://43.138.218.156:8080/login")
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
                                Toast.makeText(MainActivity.this, "log in successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                                startActivity(intent);
                            });
                        } else {
                            mHandler.post(() -> {
                                Toast.makeText(MainActivity.this, result.getErrorMsg(), Toast.LENGTH_SHORT).show();
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