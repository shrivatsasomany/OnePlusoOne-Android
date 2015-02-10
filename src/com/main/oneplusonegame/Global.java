package com.main.oneplusonegame;

import org.apache.http.Header;
import org.json.JSONArray;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.main.oneplusonegame.models.Quantifiers;
import com.main.oneplusonegame.models.QuestionTypes;
import com.main.oneplusonegame.models.User;

public class Global extends Application
{
	private User				currentUser;
	private Gson				gson		= new Gson();
	private AsyncHttpClient	client	= new AsyncHttpClient();
	private Quantifiers		list_of_quantifiers;
	private QuestionTypes	list_of_types;
	

	public void setCurrentUser(User user)
	{
		currentUser = user;
	}

	public User getCurrentUser()
	{
		return currentUser;
	}

	public void loadQuantifiers()
	{
		RequestParams params = new RequestParams();
		client.get(getString(R.string.server_url) + "quantifiers.json", params,
				new JsonHttpResponseHandler()
				{
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONArray quantifiers)
					{
						Log.d("Global", "QUANTIFIERS=>" + quantifiers.toString());
						list_of_quantifiers = gson.fromJson(quantifiers.toString(), Quantifiers.class);

					}
				});
	}

	public void loadTypes()
	{
		RequestParams params = new RequestParams();
		client.get(getString(R.string.server_url) + "question_types.json", params,
				new JsonHttpResponseHandler()
				{
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONArray types)
					{
						Log.d("Global", "TYPES=>" + types.toString());
						list_of_types = gson.fromJson(types.toString(), QuestionTypes.class);
						Log.d("Global", "TYPES ARRAY SIZE=>" + list_of_types.size());
					}
				});
	}

	public Quantifiers getQuantifiers()
	{
		return list_of_quantifiers;
	}

	public QuestionTypes getQuestionTypes()
	{
		return list_of_types;
	}

}
