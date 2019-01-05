package com.yiyin.aobosh.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yiyin.aobosh.R;
import com.yiyin.aobosh.bean.VipOrderBean;
import com.yiyin.aobosh.bean.VipShow;

import java.util.List;

/**
 * Created by Administrator on 2018/12/25.
 */

public class VipOrderAdapter extends RecyclerView.Adapter<VipOrderAdapter.ViewHolder> {

    // ① 创建Adapter
    private List<VipOrderBean> mDatas;
    public VipOrderAdapter(List<VipOrderBean> data) {
        this.mDatas = data;
    }

    //② 创建ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView ordersn;
        public final TextView addtime;
        public final TextView level_name;
        public final TextView paytype;
        public final TextView paytime;
        public final TextView vipmoney;

        public ViewHolder(View v) {
            super(v);
            ordersn = v.findViewById(R.id.ordersn_tv);
            addtime = v.findViewById(R.id.addtime_tv);
            level_name = v.findViewById(R.id.level_name_tv);
            paytype = v.findViewById(R.id.paytype_tv);
            paytime = v.findViewById(R.id.paytime_tv);
            vipmoney = v.findViewById(R.id.vipmoney_tv);
        }
    }

    //③ 在Adapter中实现3个方法
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vip_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.ordersn.setText("订单ID："+mDatas.get(position).getId());
        holder.addtime.setText("下单时间："+mDatas.get(position).getAddtime());
        holder.level_name.setText("购买详情：购买[" + mDatas.get(position).getLevel_name()+"]-"+mDatas.get(position).getViptime()+"天");
        holder.paytype.setText("支付方式："+mDatas.get(position).getPaytype());
        holder.paytime.setText("付款时间："+mDatas.get(position).getPaytime());
        holder.vipmoney.setText("应付金额："+mDatas.get(position).getVipmoney()+"元");
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

}
