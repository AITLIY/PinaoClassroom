package com.yiyin.aobosh.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yiyin.aobosh.Interface.JoinVipInterface;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.bean.VipShow;

import java.util.List;

/**
 * Created by Administrator on 2018/12/25.
 */

public class LevelVipAdapter extends RecyclerView.Adapter<LevelVipAdapter.ViewHolder> {

    // ① 创建Adapter
    private List<VipShow.LevelListBean> mDatas;
    private JoinVipInterface mJoinVip;
    public LevelVipAdapter(List<VipShow.LevelListBean> data, JoinVipInterface joinVip) {
        mDatas = data;
        mJoinVip = joinVip;
    }

    //② 创建ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView level_name;
        public final TextView level_price;
        public final TextView join_tv;

        public ViewHolder(View v) {
            super(v);
            level_name = v.findViewById(R.id.level_name);
            level_price = v.findViewById(R.id.level_price);
            join_tv = v.findViewById(R.id.join_tv);
        }
    }

    //③ 在Adapter中实现3个方法
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_vip_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.level_name.setText(mDatas.get(position).getLevel_name()+"["+mDatas.get(position).getLevel_validity()+"天]");
        holder.level_price.setText(mDatas.get(position).getLevel_price()+"元");
        holder.join_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mJoinVip.onPayVip();
            }
        });
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
