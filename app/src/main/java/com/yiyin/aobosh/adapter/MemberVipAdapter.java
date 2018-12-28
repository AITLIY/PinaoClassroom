package com.yiyin.aobosh.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yiyin.aobosh.R;
import com.yiyin.aobosh.bean.VipShow;

import java.util.List;

/**
 * Created by Administrator on 2018/12/25.
 */

public class MemberVipAdapter extends RecyclerView.Adapter<MemberVipAdapter.ViewHolder> {

    // ① 创建Adapter

    private List<VipShow.MemberVipListBean> mDatas;
    public MemberVipAdapter(List<VipShow.MemberVipListBean> data) {
        this.mDatas = data;
    }

    //② 创建ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView level_name;
        public final TextView discount;
        public final TextView validity;

        public ViewHolder(View v) {
            super(v);
            level_name = v.findViewById(R.id.level_name);
            discount = v.findViewById(R.id.discount);
            validity = v.findViewById(R.id.validity);
        }
    }

    //③ 在Adapter中实现3个方法
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_vip_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.level_name.setText(mDatas.get(position).getLevel().getLevel_name());
        holder.discount.setText(mDatas.get(position).getDiscount()+"%");
        holder.validity.setText(mDatas.get(position).getValidity());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //item 点击事件
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

}
