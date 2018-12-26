package com.yiyin.aobosh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.bean.LessonSearch;
import com.yiyin.aobosh.bean.TeacherBean;

import java.util.ArrayList;

/**
 * Created by ALIY on 2018/12/19 0019.
 */

public class TeacherListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<TeacherBean> mList;

    public TeacherListAdapter(Context context, ArrayList<TeacherBean> list) {
        mContext = context;
        mList = list;
    }

    public void addLast(ArrayList<TeacherBean> list) {
        mList.addAll(list);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.teacher_item, parent,false);

            holder = new ViewHolder();
            holder.photo = ((ImageView) convertView.findViewById(R.id.photo_img));
            holder.teacherdes = (TextView) convertView.findViewById(R.id.teacherdes_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext)
                .load(mList.get(position).getTeacherphoto())
                .into(holder.photo);

        holder.teacherdes.setText(mList.get(position).getTeacherdes());

        return convertView;
    }

    private class ViewHolder {

        public ImageView photo;  //图标
        public TextView teacherdes; //人数

    }

}
