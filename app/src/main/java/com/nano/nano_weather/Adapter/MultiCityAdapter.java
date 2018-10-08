package com.nano.nano_weather.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.nano.nano_weather.Other.BaseViewHolder;
import com.nano.nano_weather.R;
import com.nano.nano_weather.json.Weather;
import com.nano.nano_weather.utils.OtherUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MultiCityAdapter extends RecyclerView.Adapter<MultiCityAdapter.MultiCityViewHolder> {
    private Context mContext;
    private List<Weather> mWeatherList;
    private onMultiCityClick mMultiCityClick;

    public void setMultiCityClick(onMultiCityClick multiCityClick) {
        this.mMultiCityClick = multiCityClick;
    }

    public MultiCityAdapter(List<Weather> weatherList) {
        mWeatherList = weatherList;
    }

    @Override
    public MultiCityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new MultiCityViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multy_city, parent, false));
    }

    @Override
    public void onBindViewHolder(MultiCityViewHolder holder, int position) {

        holder.bind(mWeatherList.get(position));
        holder.itemView.setOnLongClickListener(v -> {
            mMultiCityClick.longClick(mWeatherList.get(holder.getAdapterPosition()).basic.location);
            return true;
        });
        holder.itemView.setOnClickListener(v -> mMultiCityClick.click(mWeatherList.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }

    public boolean isEmpty() {
        return 0 == mWeatherList.size();
    }


    class MultiCityViewHolder extends BaseViewHolder<Weather> {

        @BindView(R.id.dialog_city)
        TextView mDialogCity;
        @BindView(R.id.dialog_icon)
        ImageView mDialogIcon;
        @BindView(R.id.dialog_temp)
        TextView mDialogTemp;
        @BindView(R.id.cardView)
        CardView mCardView;

        public MultiCityViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bind(Weather weather) {

            try {
                mDialogCity.setText(weather.basic.location);
                mDialogTemp.setText(String.format("%s℃", weather.now.tmp));
            } catch (NullPointerException e) {
                Log.e("null",e.getMessage());
            }

            Glide.with(mContext)
                    .load(OtherUtil.getInstance().getInt(weather.now.cond_code+"", R.mipmap.unkonw))
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            mDialogIcon.setImageBitmap(resource);
                        }
                    });

            int code = Integer.valueOf(weather.now.cond_code);
            mCardView.setCardElevation(10);
            mCardView.setRadius(10);
            new CardCityAdapter().applyStatus(code, mCardView);
        }
    }

    public interface onMultiCityClick {
        void longClick(String city);

        void click(Weather weather);
    }

}
