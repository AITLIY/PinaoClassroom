package com.yiyin.aobosh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yiyin.aobosh.R;
import com.yiyin.aobosh.bean.VipOrderBean;

import java.util.List;

/**
 * Created by ALIY on 2019/1/5 0005.
 */

public class VipOrderAdapter2 extends BaseAdapter {

    private Context mContext;
    private List<VipOrderBean> mDatas;

    public VipOrderAdapter2(Context context, List<VipOrderBean> list) {
        mContext = context;
        mDatas = list;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VipOrderAdapter2.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.vip_order_item, parent,false);

            holder = new VipOrderAdapter2.ViewHolder();
            holder.ordersn = convertView.findViewById(R.id.ordersn_tv);
            holder.addtime = convertView.findViewById(R.id.addtime_tv);
            holder.level_name = convertView.findViewById(R.id.level_name_tv);
            holder.paytype = convertView.findViewById(R.id.paytype_tv);
            holder.paytime = convertView.findViewById(R.id.paytime_tv);
            holder.vipmoney = convertView.findViewById(R.id.vipmoney_tv);

            convertView.setTag(holder);
        } else {
            holder = (VipOrderAdapter2.ViewHolder) convertView.getTag();
        }

        holder.ordersn.setText("订单ID："+mDatas.get(position).getId());
        holder.addtime.setText("下单时间："+mDatas.get(position).getAddtime());
        holder.level_name.setText("购买详情：购买[" + mDatas.get(position).getLevel_name()+"]-"+mDatas.get(position).getViptime()+"天");
        holder.paytype.setText("支付方式："+mDatas.get(position).getPaytype());
        holder.paytime.setText("付款时间："+mDatas.get(position).getPaytime());
        holder.vipmoney.setText("应付金额："+mDatas.get(position).getVipmoney()+"元");


        return convertView;
    }

    private class ViewHolder {

        public TextView ordersn;
        public TextView addtime;
        public TextView level_name;
        public TextView paytype;
        public TextView paytime;
        public TextView vipmoney;

    }
}
