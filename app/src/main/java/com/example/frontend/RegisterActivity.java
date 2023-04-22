package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend.entity.Result;
import com.example.frontend.entity.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mEtPhone;
    private Button mBtnRegister;
    private Button mBtnReturn;
    private RadioGroup mRG;
    private RadioButton rBtn;
    private String role;
    private Handler mHandler;
    private RequestBody requestBody;
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEtUsername = findViewById(R.id.et_name);
        mEtPassword = findViewById(R.id.et_password);
        mEtPhone = findViewById(R.id.et_phone);
        mBtnRegister = findViewById(R.id.btn_register);
        mRG = findViewById(R.id.rdg_1);
        mBtnReturn = findViewById(R.id.btn_return);
        mHandler = new Handler();

        mRG.setOnCheckedChangeListener((group, checkedId) -> {
            rBtn = findViewById(checkedId);
            role = rBtn.getText().toString();
        });

        mBtnReturn.setOnClickListener((view) -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });

        mBtnRegister.setOnClickListener((view) -> {
            String name = mEtUsername.getText().toString();
            String password = mEtPassword.getText().toString();
            String phone = mEtPhone.getText().toString();
            register(name, password, role, phone);
        });
    }

    private void register(String name, String password, String role, String phone) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("name", name);
        formBody.add("password", password);
        formBody.add("role", role);
        formBody.add("phone", phone);

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
                        .url("http://43.138.218.156:8080/register")
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
                                Toast.makeText(RegisterActivity.this, "register successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                            });
                        } else {
                            mHandler.post(() -> {
                                Toast.makeText(RegisterActivity.this, result.getErrorMsg(), Toast.LENGTH_SHORT).show();
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