package com.nano.nano_weather.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nano.nano_weather.R;
import com.nano.nano_weather.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends BaseFragment {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.image_progress)
    ImageView imageView;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.main_fragment,container,false);
            ButterKnife.bind(this,view);
        }
        isCreateView = true;
        return view;
    }

    @Override
    public void lazyLoad() {

    }
}
