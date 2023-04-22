package com.example.frontend;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frontend.entity.Result;
import com.example.frontend.entity.User;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CustomListAdapter extends BaseAdapter {

    LayoutInflater inflater;

    ArrayList<Model> list;

    private RequestBody requestBody;
    private OkHttpClient okHttpClient;

    public CustomListAdapter(Context context, ArrayList<Model> data) {
        // TODO Auto-generated constructor stub
        inflater = LayoutInflater.from(context);
        this.list = data;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;
        ViewHolder holder = null;

        if (v == null) {
            v = inflater.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder(v);
            v.setTag(holder);
            holder.group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                public void onCheckedChanged(RadioGroup group,
                                             int checkedId) {
                    Integer pos = (Integer) group.getTag();
                    Model element = list.get(pos);
                    String aid = String.valueOf(((LinkedTreeMap) UserActivity.appointmentData.get(pos)).get("appointment_id"));
                    aid = aid.substring(0, aid.length() - 2);
//                    UserActivity.appointmentData.remove(pos);
                    switch (checkedId) { // set the Model to hold the
                        // answer the user picked
                        case R.id.rbtn_delete:
                            element.current = Model.ANSWER_ONE_SELECTED;
                            deleteAppointment(aid);
                            break;
                        case R.id.rbtn_checkin:
                            element.current = Model.ANSWER_TWO_SELECTED;
                            Double checkin = (Double)((LinkedTreeMap) UserActivity.appointmentData.get(pos)).get("checkin");
                            updateCheckin(aid, checkin);
                            break;
                        default:
                            element.current = Model.NONE; // Something was
                            // wrong set to
                            // the default
                    }


                }
            });
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.group.setTag(new Integer(position)); // I passed the current
        // position as a tag

        holder.t.setText(list.get(position).question); // Set the question body

        if (list.get(position).current != Model.NONE) {
            RadioButton r = (RadioButton) holder.group.getChildAt(list.get(position).current);
            r.setChecked(true);
        } else {
            holder.group.clearCheck(); // This is required because although the
            // Model could have the current
            // position to NONE you could be dealing
            // with a previous row where
            // the user already picked an answer.

        }
        return v;
    }

    void updateCheckin(String id, Double checkin) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("id", id);
        if(checkin.doubleValue()==0) formBody.add("checkin", "1");
        else formBody.add("checkin", "0");

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
                        .url("http://43.138.218.156:8080/update_checkin")
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
                            UserActivity.mHandler.post(() -> {
                                Toast.makeText(UserActivity.context, "update successfully", Toast.LENGTH_SHORT).show();
                                String uid = Objects.requireNonNull(String.valueOf(MainActivity.user.get("id")));
                                uid = uid.substring(0, uid.length() - 2);
                                UserActivity.getAppointments(uid);
                            });
                        } else {
                            UserActivity.mHandler.post(() -> {
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

    void deleteAppointment(String id) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("id", id);


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
                        .url("http://43.138.218.156:8080/delete_appointment")
                        .delete(requestBody).build();
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
                            UserActivity.mHandler.post(() -> {
                                Toast.makeText(UserActivity.context, "delete successfully", Toast.LENGTH_SHORT).show();
                                String uid = Objects.requireNonNull(String.valueOf(MainActivity.user.get("id")));
                                uid = uid.substring(0, uid.length() - 2);
                                UserActivity.getAppointments(uid);
                            });
                        } else {
                            UserActivity.mHandler.post(() -> {
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

    class ViewHolder {
        TextView t = null;
        RadioGroup group;

        ViewHolder(View v) {
            t = (TextView) v.findViewById(R.id.tv_one);
            group = (RadioGroup) v.findViewById(R.id.rdg_user_checkin);
        }
    }

}

