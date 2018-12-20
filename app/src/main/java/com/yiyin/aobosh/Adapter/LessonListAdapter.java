package com.yiyin.aobosh.Adapter;

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

import java.util.ArrayList;

/**
 * Created by ALIY on 2018/12/19 0019.
 */

public class LessonListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<LessonSearch> mList;

    public LessonListAdapter(Context context, ArrayList<LessonSearch> list) {
        mContext = context;
        mList = list;
    }

    public void addLast(ArrayList<LessonSearch> list) {
        mList.addAll(list);
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        }
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lesson_category_item, parent,false);

            holder = new ViewHolder();
            holder.lessonImg = ((ImageView) convertView.findViewById(R.id.lesson_img));
            holder.lessonPerson = (TextView) convertView.findViewById(R.id.lesson_person);
            holder.lessonName = (TextView) convertView.findViewById(R.id.lesson_name);
            holder.lessonPrice = (TextView) convertView.findViewById(R.id.lesson_price);
            holder.lessonCount = (TextView) convertView.findViewById(R.id.lesson_count);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext)
                .load(mList.get(position).getImages())
                .into(holder.lessonImg);
        holder.lessonPerson.setText(mList.get(position).getCount() + "人在学");
        holder.lessonPerson.setText(mList.get(position).getBookname());
        holder.lessonPrice.setText(mList.get(position).getPrice()+"");
        holder.lessonCount.setText(mList.get(position).getCount()+"");

        return convertView;
    }

    private class ViewHolder {

        public ImageView lessonImg;  //图标
        public TextView lessonPerson; //人数
        public TextView lessonName;  //类名
        public TextView lessonPrice; //价格
        public TextView lessonCount; //课程数量

    }

}
