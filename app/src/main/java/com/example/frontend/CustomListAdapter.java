package com.example.frontend;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {

    LayoutInflater inflater;

    ArrayList<Model> list;

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
                    switch (checkedId) { // set the Model to hold the
                        // answer the user picked
                        case R.id.rbtn_delete:
                            element.current = Model.ANSWER_ONE_SELECTED;
                            System.out.println(element.question);
                            System.out.println(element.current);
                            break;
                        case R.id.rbtn_checkin:
                            element.current = Model.ANSWER_TWO_SELECTED;
                            System.out.println(element.question);
                            System.out.println(element.current);
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

    class ViewHolder {
        TextView t = null;
        RadioGroup group;

        ViewHolder(View v) {
            t = (TextView) v.findViewById(R.id.tv_one);
            group = (RadioGroup) v.findViewById(R.id.rdg_user_checkin);
        }
    }

}

