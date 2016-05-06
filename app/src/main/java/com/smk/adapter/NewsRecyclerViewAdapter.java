package com.smk.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.smk.client.ForexNetworkEngine;
import com.smk.five28news.R;
import com.smk.model.CityNews;
import com.smk.model.CurrecyRate;
import com.smk.model.NewsCategory;
import com.smk.skconnectiondetector.SKConnectionDetector;
import com.smk.utils.CircleTransform;
import com.smk.utils.StoreUtil;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ItemViewHolder> {

    private final NewsCategory category;
    private final boolean firstPage;
    private Context ctx;
    List<CityNews> contents;

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;
    private String reformattedStr;

    public NewsRecyclerViewAdapter(Context ctx, List<CityNews> contents, NewsCategory newsCategory, boolean isFirstItem) {
        this.ctx = ctx;
        this.contents = contents;
        this.category = newsCategory;
        this.firstPage = isFirstItem;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_HEADER;
            default:
                return TYPE_CELL;
        }
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    @Override
    public NewsRecyclerViewAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_news_header, parent, false);
                return new NewsRecyclerViewAdapter.ItemViewHolder(view, TYPE_HEADER) {
                };
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news_item, parent, false);
                return new NewsRecyclerViewAdapter.ItemViewHolder(view, TYPE_CELL) {
                };
            }
        }
        return null;
    }


    @Override
    public void onBindViewHolder(final NewsRecyclerViewAdapter.ItemViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                if(firstPage){
                    if(SKConnectionDetector.getInstance((Activity) ctx).isConnectingToInternet()){
                        ForexNetworkEngine.getInstance().getCurrencies(new Callback<CurrecyRate>() {
                            @Override
                            public void success(CurrecyRate currecyRate, Response response) {
                                StoreUtil.getInstance().saveTo("currencies_"+getTodayDateString(), currecyRate);
                                holder.layout_currency.setVisibility(View.VISIBLE);
                                holder.usd_val.setText(currecyRate.getRates().getUSD());
                                holder.sgd_val.setText(currecyRate.getRates().getSGD());
                                holder.cny_val.setText(currecyRate.getRates().getCNY());
                                holder.thb_val.setText(currecyRate.getRates().getTHB());

                                CurrecyRate yesterdayCurrecyRate = StoreUtil.getInstance().selectFrom("currencies_"+getYesterdayDateString());
                                if(yesterdayCurrecyRate != null){
                                    setupCurrencyHistory(yesterdayCurrecyRate, currecyRate, holder);
                                }else{
                                    getYesterdayCurrencty(currecyRate, holder);
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });
                    }else{
                        CurrecyRate currecyRate = StoreUtil.getInstance().selectFrom("currencies_"+getTodayDateString());
                        if(currecyRate != null){
                            holder.layout_currency.setVisibility(View.VISIBLE);
                            holder.usd_val.setText(currecyRate.getRates().getUSD());
                            holder.sgd_val.setText(currecyRate.getRates().getSGD());
                            holder.cny_val.setText(currecyRate.getRates().getCNY());
                            holder.thb_val.setText(currecyRate.getRates().getTHB());

                            CurrecyRate yesterdayCurrecyRate = StoreUtil.getInstance().selectFrom("currencies_"+getYesterdayDateString());
                            if(yesterdayCurrecyRate != null){
                                setupCurrencyHistory(yesterdayCurrecyRate, currecyRate, holder);
                            }
                        }

                    }


                }else{
                    holder.layout_currency.setVisibility(View.GONE);
                    holder.advertise_title.setText(category.getAdvertiseName());
                    holder.advertise_desc.setText(category.getAdvertiseDesc());
                    if(contents.get(position).getImage() != null && contents.get(position).getImage().length() > 0)
                        Picasso.with(ctx).load("http://api.truetaximyanmar.com/category_background_image/x400/"+category.getAdvertiseImage()).into(holder.advertise_photo);
                }
                break;
            case TYPE_CELL:
                holder.title.setText(contents.get(position).getTitle());
                holder.content.setText(contents.get(position).getDescription());
                holder.date.setText(changeDateFormat(contents.get(position).getCreated_at()));
                holder.time.setText(changeTimeFormat(contents.get(position).getCreated_at()));
                if(contents.get(position).getImage() != null && contents.get(position).getImage().length() > 0)
                    Picasso.with(ctx).load("http://api.truetaximyanmar.com/news_photo/x400/"+contents.get(position).getImage()).placeholder(R.drawable.placeholder).into(holder.photo);
                if(contents.get(position).getCredit_image_url() != null && contents.get(position).getCredit_image_url().length() > 0)
                    Picasso.with(ctx).load(contents.get(position).getCredit_image_url()).placeholder(getIconicDrawable(GoogleMaterial.Icon.gmd_account_circle.toString(), R.color.colorAccent, 35)).transform(new CircleTransform()).into(holder.imgCredit);
                holder.credit.setText(contents.get(position).getCredit_name());
                break;
        }
    }

    public IconicsDrawable getIconicDrawable(String icon, int color, int size) {
        return new IconicsDrawable(ctx)
                .icon(icon)
                .color(ctx.getResources().getColor(color))
                .sizeDp(size);
    }

    private void getYesterdayCurrencty(final CurrecyRate currentRate, final ItemViewHolder holder){
        ForexNetworkEngine.getInstance().getCurrenciesHistory(getYesterdayDate(), new Callback<CurrecyRate>() {
            @Override
            public void success(CurrecyRate currecyRate, Response response) {
                StoreUtil.getInstance().saveTo("currencies_"+getYesterdayDateString(), currecyRate);
                setupCurrencyHistory(currecyRate, currentRate, holder);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void setupCurrencyHistory(CurrecyRate yesterdayCurrecyRate, CurrecyRate currecyRate, ItemViewHolder holder){
        if(Double.valueOf(currecyRate.getRates().getUSD().replace(",","")) > Double.valueOf(yesterdayCurrecyRate.getRates().getUSD().replace(",",""))){
            Double usdDiffRate = Double.valueOf(currecyRate.getRates().getUSD().replace(",","")) - Double.valueOf(yesterdayCurrecyRate.getRates().getUSD().replace(",",""));
            holder.usd_history.setText("{cmd_trending_up} "+String.format( "%.2f", usdDiffRate ));
        }else{
            Double usdDiffRate = Double.valueOf(yesterdayCurrecyRate.getRates().getUSD().replace(",","")) - Double.valueOf(currecyRate.getRates().getUSD().replace(",",""));
            holder.usd_history.setText("{cmd_trending_down} "+String.format( "%.2f", usdDiffRate));
        }

        if(Double.valueOf(currecyRate.getRates().getSGD().replace(",","")) > Double.valueOf(yesterdayCurrecyRate.getRates().getSGD().replace(",",""))){
            Double sgdDiffRate = Double.valueOf(currecyRate.getRates().getSGD().replace(",","")) - Double.valueOf(yesterdayCurrecyRate.getRates().getSGD().replace(",",""));
            holder.sgd_history.setText("{cmd_trending_up} "+String.format( "%.2f", sgdDiffRate));
        }else{
            Double sgdDiffRate = Double.valueOf(yesterdayCurrecyRate.getRates().getSGD().replace(",","")) - Double.valueOf(currecyRate.getRates().getSGD().replace(",",""));
            holder.sgd_history.setText("{cmd_trending_down} "+String.format( "%.2f", sgdDiffRate));
        }

        if(Double.valueOf(currecyRate.getRates().getCNY().replace(",","")) > Double.valueOf(yesterdayCurrecyRate.getRates().getCNY().replace(",",""))){
            Double cnyDiffRate = Double.valueOf(currecyRate.getRates().getCNY().replace(",","")) - Double.valueOf(yesterdayCurrecyRate.getRates().getCNY().replace(",",""));
            holder.cny_history.setText("{cmd_trending_up} "+String.format( "%.2f", cnyDiffRate));
        }else{
            Double cnyDiffRate = Double.valueOf(yesterdayCurrecyRate.getRates().getCNY().replace(",","")) - Double.valueOf(currecyRate.getRates().getCNY().replace(",",""));
            holder.cny_history.setText("{cmd_trending_down} "+String.format( "%.2f", cnyDiffRate));
        }

        if(Double.valueOf(currecyRate.getRates().getTHB().replace(",","")) > Double.valueOf(yesterdayCurrecyRate.getRates().getTHB().replace(",",""))){
            Double thbDiffRate = Double.valueOf(currecyRate.getRates().getTHB().replace(",","")) - Double.valueOf(yesterdayCurrecyRate.getRates().getTHB().replace(",",""));
            holder.thb_history.setText("{cmd_trending_up} "+String.format( "%.2f", thbDiffRate));
        }else{
            Double thbDiffRate = Double.valueOf(yesterdayCurrecyRate.getRates().getTHB().replace(",","")) - Double.valueOf(currecyRate.getRates().getTHB().replace(",",""));
            holder.thb_history.setText("{cmd_trending_down} "+String.format( "%.2f", thbDiffRate));
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView advertise_title;
        public TextView advertise_desc;
        public IconicsImageView imgCredit;
        public TextView title;
        public ExpandableTextView content;
        public TextView date;
        public TextView time;
        public TextView credit;
        public ImageView photo;
        public ImageView advertise_photo;
        public LinearLayout layout_currency;
        public TextView usd_val;
        public TextView usd_history;
        public TextView sgd_val;
        public TextView sgd_history;
        public TextView cny_val;
        public TextView cny_history;
        public TextView thb_val;
        public TextView thb_history;

        public ItemViewHolder(View itemView, int viewType) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.txt_title);
            content = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
            date = (TextView) itemView.findViewById(R.id.txt_date);
            time = (TextView) itemView.findViewById(R.id.txt_time);
            credit = (TextView) itemView.findViewById(R.id.txt_credit_name);
            imgCredit = (IconicsImageView) itemView.findViewById(R.id.img_credit);
            photo = (ImageView) itemView.findViewById(R.id.img_news);
            advertise_title = (TextView) itemView.findViewById(R.id.txt_advertise_title);
            advertise_desc = (TextView) itemView.findViewById(R.id.txt_advertise_description);
            advertise_photo = (ImageView) itemView.findViewById(R.id.img_advertise);
            usd_val = (TextView) itemView.findViewById(R.id.txt_usd_value);
            sgd_val = (TextView) itemView.findViewById(R.id.txt_sgd_value);
            cny_val = (TextView) itemView.findViewById(R.id.txt_cny_value);
            thb_val = (TextView) itemView.findViewById(R.id.txt_thb_value);
            usd_history = (TextView) itemView.findViewById(R.id.txt_usd_history);
            sgd_history = (TextView) itemView.findViewById(R.id.txt_sgd_history);
            cny_history = (TextView) itemView.findViewById(R.id.txt_cny_history);
            thb_history = (TextView) itemView.findViewById(R.id.txt_thb_history);
            layout_currency = (LinearLayout) itemView.findViewById(R.id.layout_currency);
        }

        @Override
        public void onClick(View v) {

        }
    }

    private String changeDateFormat(String date){
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat myFormat = new SimpleDateFormat("E, MMM dd yyyy");

        try {

            reformattedStr = myFormat.format(fromUser.parse(date));

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e1){

        }
        return reformattedStr;
    }

    public String getCurrentDateTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

    public String getTodayDateString(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

    private String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    private String getYesterdayDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    private String changeTimeFormat(String date){
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat myFormat = new SimpleDateFormat("hh:mm aaa");

        try {

            reformattedStr = myFormat.format(fromUser.parse(date));

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e2){

        }
        return reformattedStr;
    }
}