package com.smk.client;


import com.smk.model.CityNews;
import com.smk.model.CurrecyRate;
import com.smk.model.NewsCategory;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface INetworkEngine {

	@GET("/api-v1/citynewscategory")
	void getNewsCategories(
			Callback<List<NewsCategory>> callback);
	
	@GET("/api-v1/citynews")
	void getCityNews(
			@Query("category_id") Integer category_id,
			@Query("offset") Integer offset,
			@Query("limit") Integer limit,
			Callback<List<CityNews>> callback);

	@GET("/api/latest")
	void getCurrencies(Callback<CurrecyRate> callback);

	@GET("/api/history/{date}")
	void getCurrenciesHistory(@Path("date") String date, Callback<CurrecyRate> callback);
	

}
