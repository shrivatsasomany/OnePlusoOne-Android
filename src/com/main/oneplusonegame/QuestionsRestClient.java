package com.main.oneplusonegame;

import org.apache.http.HttpEntity;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class QuestionsRestClient
{
	private static String base_url;
	
	public QuestionsRestClient(String base_url)
	{
		this.base_url = base_url;
	}
	
	private static AsyncHttpClient client = new AsyncHttpClient();
	
	 public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      client.get(getAbsoluteUrl(url), params, responseHandler);
	  }

	  public void post(Context context, String url, HttpEntity entity, String contentType, AsyncHttpResponseHandler responseHandler) {
	      client.post(context, getAbsoluteUrl(url), entity, contentType, responseHandler);
	  }
	  
	  public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
	  {
		  client.put(getAbsoluteUrl(url), params, responseHandler);
	  }

	  private static String getAbsoluteUrl(String relativeUrl) {
	      return base_url + relativeUrl;
	  }
}
