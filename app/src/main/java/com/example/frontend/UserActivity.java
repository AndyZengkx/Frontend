package com.example.frontend;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserActivity extends AppCompatActivity {

    public static LinkedTreeMap<String, String> user;


    private Button mBtn;
    private EditText mEtDateTime;
    private EditText mEtInsName;
    private RequestBody requestBody;
    private OkHttpClient okHttpClient;

    private Handler mHandler;

    private ListView mListView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mBtn = findViewById(R.id.btn_usersearch);
        mEtDateTime = findViewById(R.id.mEtTime);
        mEtInsName = findViewById(R.id.mEtStoreSearch);
        mHandler = new Handler();

        mBtn.setOnClickListener((view) -> {
            String datetime = String.valueOf(mEtDateTime.getText());
            String name = String.valueOf(mEtInsName.getText());
            Integer[] integers = Arrays.stream(datetime.split("/")).map(Integer::parseInt).toArray(Integer[]::new);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                datetime = LocalDateTime.of(integers[0], integers[1], integers[2], integers[3], 0).toString();
            }
            String id = Objects.requireNonNull(String.valueOf(MainActivity.user.get("id")));
            id = id.substring(0, id.length() - 2);
            createAppointment(name, id, datetime);
        });


        mListView = findViewById(R.id.listview);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View headView = inflater.inflate(R.layout.view_header, null, false);
        mListView.addHeaderView(headView);

        String[] data = new String[]{"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                UserActivity.this,
                R.layout.list_item,
                R.id.tv_one,
                data
        );
        // 将适配器设置给ListView

        mListView.setAdapter(adapter);
    }

    private void createAppointment(String name, String id, String datetime) {
        FormBody.Builder formBody = new FormBody.Builder();

        okHttpClient = new OkHttpClient.Builder().connectTimeout(3000, TimeUnit.SECONDS).callTimeout(3000, TimeUnit.SECONDS).build();
        requestBody = formBody.build();
        //Okhttp3同步请求 开启线程
        Thread thread = new Thread() {
            @Override
            public void run() {
                //设置请求的地址
                Request request = new Request.Builder().url("http://43.138.218.156:8080/get_ins_by_name?name=" + name).get().build();
                Response response = null;
                String rid = "null";
                try {
                    //同步请求
                    response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String res = Objects.requireNonNull(response.body()).string();
                        Gson gson = new Gson();
                        Result result = gson.fromJson(res, Result.class);
                        System.out.println(result);
                        if (!result.getSuccess()) {
                            mHandler.post(() -> {
                                Toast.makeText(UserActivity.this, result.getErrorMsg(), Toast.LENGTH_SHORT).show();
                            });
                            return;
                        }
                        rid = String.valueOf(((LinkedTreeMap) result.getData()).get("id"));
                        rid = rid.substring(0, rid.length() - 2);
                    } else {
                        System.out.println("服务器连接失败");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                formBody.add("rid",rid);
                formBody.add("uid", id);
                formBody.add("timeStamp", datetime);
                requestBody = formBody.build();
                request = new Request.Builder().url("http://43.138.218.156:8080/insert_appointment").post(requestBody).build();
                try {
                    //同步请求
                    response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String res = Objects.requireNonNull(response.body()).string();
                        Gson gson = new Gson();
                        Result result = gson.fromJson(res, Result.class);
                        System.out.println(result);
                        if (!result.getSuccess()) {
                            mHandler.post(() -> {
                                Toast.makeText(UserActivity.this, result.getErrorMsg(), Toast.LENGTH_SHORT).show();
                            });
                        }
                        else{
                            mHandler.post(() -> {
                                Toast.makeText(UserActivity.this, "create successfully", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        System.out.println("服务器连接失败");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }; thread.start();
    }
}