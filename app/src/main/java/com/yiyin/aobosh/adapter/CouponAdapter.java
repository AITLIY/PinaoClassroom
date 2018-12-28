package com.yiyin.aobosh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiyin.aobosh.R;
import com.yiyin.aobosh.bean.CouponBean;

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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.coupon_item, parent,false);

            holder = new ViewHolder();

            holder.amount_tv = convertView.findViewById(R.id.amount_tv);
            holder.category_tv = convertView.findViewById(R.id.category_tv);
            holder.conditions_tv = convertView.findViewById(R.id.conditions_tv);
            holder.date_tv = convertView.findViewById(R.id.date_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.amount_tv.setText(mList.get(position).getAmount());
        holder.category_tv.setText(mList.get(position).getAmount() + "全场通用券");
        holder.conditions_tv.setText("满"+mList.get(position).getConditions()+"使用");
        holder.date_tv.setText("有效期："+mList.get(position).getStartDate()+" 至 " + mList.get(position).getEndDate());

        return convertView;
    }

    private class ViewHolder {

        public TextView amount_tv; //人数
        public TextView category_tv;  //类名
        public TextView conditions_tv; //价格
        public TextView date_tv; //课程数量

    }

}
