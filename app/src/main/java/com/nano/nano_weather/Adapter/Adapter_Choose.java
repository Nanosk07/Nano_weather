package com.nano.nano_weather.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nano.nano_weather.R;

import java.util.List;

/**
 * Created by 26039 on 2018/4/17.
 */



public class Adapter_Choose extends RecyclerView.Adapter<Adapter_Choose.ViewHolder>{

    private List<String> dataList;

    public Adapter_Choose(List<String> dataList){
        this.dataList = dataList;
    }

    @Override
    public Adapter_Choose.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recycler,parent,false);
        return new ViewHolder(view);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View weatherView;
        CardView cardView;
        TextView itemCity;



        public ViewHolder(View view) {
            super(view);
            weatherView=view;
            cardView=(CardView) view;
            itemCity= view.findViewById(R.id.item_city);
        }


    }

    @Override
    public void onBindViewHolder(final Adapter_Choose.ViewHolder holder,final int position) {
        holder.itemCity.setText(dataList.get(position));
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null)
        {
            holder.itemView.setOnClickListener(v -> {
                int pos = holder.getLayoutPosition();
                mOnItemClickLitener.onItemClick(holder.itemView, pos);
            });
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

    }
    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
