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
import com.yiyin.aobosh.bean.CouponBean;
import com.yiyin.aobosh.bean.LessonSearch;

import java.util.ArrayList;

/**
 * Created by ALIY on 2018/12/19 0019.
 */

public class CouponAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<CouponBean> mList;

    public CouponAdapter(Context context, ArrayList<CouponBean> list) {
        mContext = context;
        mList = list;
    }

    public void addLast(ArrayList<CouponBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lesson_item, parent,false);

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

//        Glide.with(mContext)
//                .load(mList.get(position).getImages())
//                .into(holder.lessonImg);
//        holder.lessonPerson.setText(mList.get(position).getBuynum() + mList.get(position).getVirtual_buynum() + mList.get(position).getVisit_number() + "人已学");
//        holder.lessonName.setText(mList.get(position).getBookname());
//        holder.lessonPrice.setText(mList.get(position).getPrice()+"");
//        holder.lessonCount.setText(mList.get(position).getCount()+"");

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
