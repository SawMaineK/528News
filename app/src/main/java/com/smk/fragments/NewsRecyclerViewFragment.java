package com.smk.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.google.gson.Gson;
import com.smk.adapter.NewsRecyclerViewAdapter;
import com.smk.client.NetworkEngine;
import com.smk.five28news.R;
import com.smk.model.CityNews;
import com.smk.model.NewsCategory;
import com.smk.utils.EndlessRecyclerOnScrollListener;
import com.smk.utils.StoreUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class NewsRecyclerViewFragment extends Fragment {

    private static NewsCategory category;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<CityNews> mContentItems = new ArrayList<>();
    private Integer offset = 1;
    private Integer limit = 10;
    private int mPosition;
    private NewsCategory mCategory;
    private boolean isLoadMore = true;
    private boolean isLoading = false;
    private boolean isFirstLoaing = true;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    private FloatingActionButton mFloatingActionButton;

    public static NewsRecyclerViewFragment newInstance(int position, NewsCategory newsCategory) {
        NewsRecyclerViewFragment frag = new NewsRecyclerViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("_position", position);
        bundle.putString("_object", new Gson().toJson(newsCategory));
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPosition = getArguments() != null ? getArguments().getInt("_position") : 1;
        mCategory = getArguments() != null ? new Gson().fromJson(getArguments().getString("_object"), NewsCategory.class) : null;
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab_scoll_to_top);

        init();

        isFirstLoaing = true;


    }

    private void init(){

        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        if(mPosition == 0)
            mAdapter = new RecyclerViewMaterialAdapter(new NewsRecyclerViewAdapter(getActivity(), mContentItems, mCategory, true));
        else
            mAdapter = new RecyclerViewMaterialAdapter(new NewsRecyclerViewAdapter(getActivity(), mContentItems, mCategory, false));
        mRecyclerView.setAdapter(mAdapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
        List<CityNews> cityNews = StoreUtil.getInstance().selectFrom("news_"+mCategory.getName());
        if(cityNews != null){
            mContentItems.addAll(cityNews);
            mAdapter.notifyDataSetChanged();
            getNews();
        }else{
            getNews();
        }
        /*ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // startActivity(new Intent(getActivity().getApplicationContext(), PromotionItemDetailActivity.class));
            }
        });*/

        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if(!isLoading && isLoadMore)
                    getNews();
            }
        };

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, endlessRecyclerOnScrollListener);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            endlessRecyclerOnScrollListener.reset(0, true);
            isFirstLoaing = true;
            isLoadMore = true;
            offset = 1;
            getNews();
        }
    };

    private void getNews(){
        isLoading = true;
        NetworkEngine.getInstance().getCityNews(mCategory.getId(), offset, limit, new Callback<List<CityNews>>() {
            @Override
            public void success(final List<CityNews> cityNews, Response response) {

                if(isFirstLoaing){
                    mContentItems.clear();
                    isFirstLoaing = false;
                    if(mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    // Added fab item for header view;
                    mContentItems.add(new CityNews(mCategory.getAdvertiseName(), mCategory.getAdvertiseDesc(),mCategory.getAdvertiseImage()));
                }
                mContentItems.addAll(cityNews);

                StoreUtil.getInstance().saveTo("news_"+mCategory.getName(), mContentItems);

                mAdapter.notifyDataSetChanged();

                isLoading = false;
                if(cityNews.size() == limit){
                    offset++;
                    isLoadMore = true;
                }else{
                    isLoadMore = false;
                }
                Log.i("News Count: ", "Item:"+ mContentItems.size());

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


}