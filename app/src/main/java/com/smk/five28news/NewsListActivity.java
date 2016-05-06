package com.smk.five28news;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.nullwire.ExceptionHandler;
import com.smk.client.NetworkEngine;
import com.smk.fragments.NewsRecyclerViewFragment;
import com.smk.model.NewsCategory;
import com.smk.utils.StoreUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewsListActivity extends AppCompatActivity {

    private MaterialViewPager mViewPager;
    private Toolbar toolbar;
    private List<NewsCategory> Categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        ExceptionHandler.register(this, "http://shopyface.com/api/v1/errorReports");

        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);
        toolbar = mViewPager.getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setTitle("");
            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }

            List<NewsCategory> newsCategories = StoreUtil.getInstance().selectFrom("categories");
            if(newsCategories != null && newsCategories.size() > 0){
                Categories.add(new NewsCategory(null, "All", newsCategories.get(0).getBackgroundColor(), newsCategories.get(0).getBackgroundImage(), newsCategories.get(0).getAdvertiseName(), newsCategories.get(0).getAdvertiseDesc(), newsCategories.get(0).getAdvertiseImage()));
                Categories.addAll(newsCategories);
                init(Categories);
                getNewsCategoryForBackgroud();
            }else{
                Categories.add(new NewsCategory(null, "All", null, null, null, null, null));
                getNewsCategory();
            }


        }

    }

    private void init(final List<NewsCategory> newsCategories){
        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return NewsRecyclerViewFragment.newInstance(position,newsCategories.get(position));
            }

            @Override
            public int getCount() {
                return newsCategories.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return newsCategories.get(position).getName();
            }
        });

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                try {
                    if(newsCategories.get(page).getBackgroundColor() != null && newsCategories.get(page).getBackgroundImage() != null){
                        return HeaderDesign.fromColorAndUrl(
                                Color.parseColor(newsCategories.get(page).getBackgroundColor()),
                                "http://api.truetaximyanmar.com/category_background_image/x400/"+newsCategories.get(page).getBackgroundImage());
                    }else{
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.colorPrimary,
                                "http://api.truetaximyanmar.com/advertisement/x400/photo_20151128150455_1805997707kandawgyi.jpg");
                    }

                }catch (StringIndexOutOfBoundsException e){

                }
                return null;

            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());
    }

    private void getNewsCategory(){
        NetworkEngine.getInstance().getNewsCategories(new Callback<List<NewsCategory>>() {
            @Override
            public void success(final List<NewsCategory> newsCategories, Response response) {
                Categories.addAll(newsCategories);
                StoreUtil.getInstance().saveTo("categories", newsCategories);
                init(Categories);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void getNewsCategoryForBackgroud(){
        NetworkEngine.getInstance().getNewsCategories(new Callback<List<NewsCategory>>() {
            @Override
            public void success(final List<NewsCategory> newsCategories, Response response) {
                StoreUtil.getInstance().saveTo("categories", newsCategories);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        finish();
        return super.getSupportParentActivityIntent();
    }
}
