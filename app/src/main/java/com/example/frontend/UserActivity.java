package com.example.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.example.frontend.utils.MyActivityManager;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserActivity extends AppCompatActivity {

    public static LinkedTreeMap<String, String> user;
    public static Context context;

    public static View view;
    public static Activity activity;

    public void showToast(String message) {
        Toast.makeText(UserActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public static ArrayList appointmentData;

    private Button mBtn;
    private EditText mEtDateTime;
    private EditText mEtInsName;
    private RequestBody requestBody;
    private static OkHttpClient okHttpClient;

    public static Handler mHandler = new Handler();

    private ListView mListView;


    private static ListView listView;
    private static ArrayList<Model> dataList;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        context = getApplicationContext();
        view = this.getWindow().getDecorView();
        activity = MyActivityManager.getInstance().getCurrentActivity();
        mBtn = findViewById(R.id.btn_usersearch);
        mEtDateTime = findViewById(R.id.mEtTime);
        mEtInsName = findViewById(R.id.mEtStoreSearch);
        mListView = findViewById(R.id.listview);
        listView = (ListView) findViewById(R.id.listview);
//        mHandler = new Handler();

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

        final LayoutInflater inflater = LayoutInflater.from(this);
        View headView = inflater.inflate(R.layout.view_header, null, false);
        mListView.addHeaderView(headView);

        dataList = new ArrayList<Model>();

        listView = (ListView) findViewById(R.id.listview);
        CustomListAdapter cadapter = new CustomListAdapter(UserActivity.this, dataList);

        String uid = Objects.requireNonNull(String.valueOf(MainActivity.user.get("id")));
        uid = uid.substring(0, uid.length() - 2);
        getAppointments(uid);
    }

    public static void getAppointments(String id) {
        okHttpClient = new OkHttpClient.Builder().connectTimeout(3000, TimeUnit.SECONDS).callTimeout(3000, TimeUnit.SECONDS).build();
        Thread thread = new Thread() {
            @Override
            public void run() {
                //设置请求的地址
                Request request = new Request.Builder().url("http://43.138.218.156:8080/get_app_by_uid?uid=" + id).get().build();
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
                                dataList.clear();
                                System.out.println(result.getData());
                                String[] data = new String[result.getTotal().intValue()];
                                Object object = result.getData();
                                appointmentData = (ArrayList) object;
                                for (int i = 0; i < result.getTotal().intValue(); i++) {
                                    StringBuilder builder = new StringBuilder();
                                    builder.append(((LinkedTreeMap) ((ArrayList) result.getData()).get(i)).get("ins_name"));
                                    builder.append("\t");
                                    builder.append(((LinkedTreeMap) ((ArrayList) result.getData()).get(i)).get("timeStamp"));
                                    builder.append("\n");
                                    if ((Double) ((LinkedTreeMap) ((ArrayList) result.getData()).get(i)).get("checkin") > 0)
                                        builder.append("checked in");
                                    else builder.append("waiting checked");
                                    data[i] = builder.toString();

                                    Model m = new Model(data[i]);
                                    dataList.add(m);
                                }


                                // 创建一个ArrayAdapter作为ListView的适配器
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        UserActivity.context,
                                        R.layout.list_item,
                                        R.id.tv_one,
                                        data
                                );
                                // 将适配器设置给ListView
                                CustomListAdapter cadapter = new CustomListAdapter(UserActivity.context, dataList);
                                listView.setAdapter(cadapter);
                            });
                        } else {
                            mHandler.post(() -> {
                                Toast.makeText(UserActivity.context, result.getErrorMsg(), Toast.LENGTH_SHORT).show();
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
                formBody.add("rid", rid);
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
                        } else {
                            mHandler.post(() -> {
                                Toast.makeText(UserActivity.this, "create successfully", Toast.LENGTH_SHORT).show();
                                String uid = Objects.requireNonNull(String.valueOf(MainActivity.user.get("id")));
                                uid = uid.substring(0, uid.length() - 2);
                                getAppointments(uid);
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