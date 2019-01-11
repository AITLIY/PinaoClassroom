package com.yiyin.aobosh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.Interface.OrderClickInterface;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.bean.LessonOrder;
import com.yiyin.aobosh.commons.CommonParameters;

import java.util.ArrayList;

/**
 * Created by ALIY on 2018/12/19 0019.
 */

public class LessonOrderAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<LessonOrder> mList;
    private OrderClickInterface mOrderClick;

    public LessonOrderAdapter(Context context, ArrayList<LessonOrder> list, OrderClickInterface orderClick) {
        mContext = context;
        mList = list;
        mOrderClick = orderClick;
    }

    public void addLast(ArrayList<LessonOrder> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lessono_order_item, parent,false);

            holder = new ViewHolder();
            holder.orderItem = convertView.findViewById(R.id.order_item);
            holder.lessonImg = convertView.findViewById(R.id.lesson_img);
            holder.bookname_tv = convertView.findViewById(R.id.bookname_tv);
            holder.price_tv = convertView.findViewById(R.id.price_tv);
            holder.ordersn_tv = convertView.findViewById(R.id.ordersn_tv);
            holder.statusname_tv = convertView.findViewById(R.id.statusname_tv);
            holder.addtime_tv = convertView.findViewById(R.id.addtime_tv);
            holder.spec_day_tv = convertView.findViewById(R.id.spec_day_tv);
            holder.validity_tv = convertView.findViewById(R.id.validity_tv);
            holder.validity_ll = convertView.findViewById(R.id.validity_ll);
            holder.cancel_pay = convertView.findViewById(R.id.cancel_pay);
            holder.start_pay = convertView.findViewById(R.id.start_pay);
            holder.evalute_tv = convertView.findViewById(R.id.evalute_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext)
                .load(mList.get(position).getImages())
                .into(holder.lessonImg);
        holder.bookname_tv.setText(mList.get(position).getBookname());
        holder.price_tv.setText("¥"+mList.get(position).getPrice());
        holder.ordersn_tv.setText("订单编号：2"+mList.get(position).getOrdersn());
        holder.statusname_tv.setText(mList.get(position).getStatusname());
        holder.addtime_tv.setText("下单时间："+mList.get(position).getAddtime());
        holder.spec_day_tv.setText("规格："+mList.get(position).getSpec_day()+"天");

        LogUtils.d("订单status：" + mList.get(position).getStatus());
        int status = mList.get(position).getStatus();

        switch (status) {

            case CommonParameters.CANCEL:                                //已取消

                holder.validity_ll.setVisibility(View.INVISIBLE);
                holder.cancel_pay.setVisibility(View.GONE);
                holder.start_pay.setVisibility(View.GONE);
                holder.evalute_tv.setVisibility(View.GONE);
                break;

            case CommonParameters.NOT_PAID:                               //待付款

                holder.validity_ll.setVisibility(View.INVISIBLE);
                holder.cancel_pay.setVisibility(View.VISIBLE);
                holder.start_pay.setVisibility(View.VISIBLE);
                holder.evalute_tv.setVisibility(View.GONE);
                break;

            case CommonParameters.PAID:                                //已付款

                holder.validity_ll.setVisibility(View.VISIBLE);
                holder.validity_tv.setText(mList.get(position).getValidity()+"");
                holder.cancel_pay.setVisibility(View.GONE);
                holder.start_pay.setVisibility(View.GONE);
                holder.evalute_tv.setVisibility(View.VISIBLE);
                break;

            case CommonParameters.EVALUATE:                                //已评价

                holder.validity_ll.setVisibility(View.VISIBLE);
                holder.validity_tv.setText(mList.get(position).getValidity()+"");
                holder.cancel_pay.setVisibility(View.GONE);
                holder.start_pay.setVisibility(View.GONE);
                holder.evalute_tv.setVisibility(View.GONE);
                break;

        }

        holder.orderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrderClick.onOrder(mList.get(position));
            }
        });

        holder.cancel_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrderClick.onCancel(mList.get(position));
            }
        });

        holder.start_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrderClick.onPay(mList.get(position));
            }
        });

        holder.evalute_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrderClick.onEvaluate(mList.get(position));
            }
        });

        return convertView;
    }

    private class ViewHolder {

        public LinearLayout orderItem; //订单
        public ImageView lessonImg;     //图标
        public TextView bookname_tv;    //课程名称
        public TextView price_tv;       //课程价格
        public TextView ordersn_tv;     //订单编号
        public TextView statusname_tv;  //statusname
        public TextView addtime_tv;     //下单时间
        public TextView spec_day_tv;    //课程规格
        public TextView validity_tv;    //有效期
        public LinearLayout validity_ll;    //有效期
        public TextView cancel_pay;         //取消订单
        public TextView start_pay;          //立即支付
        public TextView evalute_tv;         //评价课程

    }

}
