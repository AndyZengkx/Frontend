package com.example.frontend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend.entity.Result;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminActivity extends AppCompatActivity {

    public static LinkedTreeMap<String, String> user;


    private Button mBtn;
    private EditText mEtStoreSearch;
    private RequestBody requestBody;
    private OkHttpClient okHttpClient;

    private Handler mHandler;

    @SuppressLint("MissingInflatedId")

    ListView testlist;
    String [] test_arr = {"肖申克的救赎","阿甘正传","明天会更好","速度与激情","建军大业","你好李焕英"};

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // 获取ListView的引用
        mListView = findViewById(R.id.listview);
        // 定义一个数组作为ListView的数据源
        String[] data = new String[]{{"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"}};
        // 创建一个ArrayAdapter作为ListView的适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                data
        );
        // 将适配器设置给ListView
        mListView.setAdapter(adapter);

    }

    private void admin_search(int uid) {
        System.out.println("1234");

        FormBody.Builder formBody = new FormBody.Builder();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");

        //Okhttp3同步请求 开启线程
        Thread thread = new Thread() {
            @Override
            public void run() {
                //设置请求的地址
                Request request = new Request.Builder()
                        .url("http://43.138.218.156:8080/get_institution?uid="+Integer.toString(2))
                        .method("GET", body)
                        .build();
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