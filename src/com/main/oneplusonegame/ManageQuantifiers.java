package com.main.oneplusonegame;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.main.oneplusonegame.adapters.QuantifierAdapter;
import com.main.oneplusonegame.models.Quantifier;
import com.main.oneplusonegame.models.Quantifiers;

public class ManageQuantifiers extends Activity
{

	private QuantifierAdapter			qAdapter;
	private ListView						quantifier_list;
	private Button							create;
	private EditText						quantifier_text;

	private static AsyncHttpClient	client;
	private Global							global;
	private Gson							gson;
	private Context						context;

	private Quantifiers					list_of_quantifiers;
	private Quantifier					created_quantifier;
	private int								game_id;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_quantifiers);

		Bundle extras = getIntent().getExtras();
		game_id = extras.getInt("game_id");

		global = (Global) getApplicationContext();
		client = new AsyncHttpClient();
		gson = new Gson();
		context = this;


		quantifier_list = (ListView) this.findViewById(R.id.activity_manage_quantifiers_listview);
		create = (Button) this.findViewById(R.id.activity_manage_quantifiers_create);
		quantifier_text = (EditText) this.findViewById(R.id.activity_manage_quantifiers_text);
		loadQuantifiers();

		create.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if(quantifier_text.getText().toString().equals(""))
				{
					Toast.makeText(getApplicationContext(), "Oops, you have to enter something!", Toast.LENGTH_SHORT).show();
				}
				else
				{
					createQuantifier(quantifier_text.getText().toString());
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_questions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
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
				Log.d("ManageQuantifiers", "QUANTIFIERS=>" + quantifiers.toString());
				list_of_quantifiers = gson.fromJson(quantifiers.toString(), Quantifiers.class);
				populateList();
			}
		});
	}


	public void createQuantifier(String text)
	{
		RequestParams params = new RequestParams();
		Map<String, String> quantifier = new HashMap<String, String>();
		quantifier.put("text", text);
		quantifier.put("game_id", "1");
		params.put("quantifier", quantifier);
		params.put("api_key", global.getCurrentUser().getKey());
		client.post(getApplicationContext(), getString(R.string.server_url) + "quantifiers.json",
				params, new JsonHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub
				created_quantifier = gson.fromJson(response.toString(), Quantifier.class);

				Log.d("ManageQuestions",
						"Successfuly created quantifier: " + created_quantifier.toString());
				Log.d("ManageQuestions", "Response: " + response.toString());
				Toast.makeText(getApplicationContext(), getString(R.string.created_quantifier),
						Toast.LENGTH_SHORT).show();
				loadQuantifiers();

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
					JSONObject errorResponse)
			{

				Toast.makeText(getApplicationContext(),
						getString(R.string.error_adding_quantifier), Toast.LENGTH_LONG).show();

			}
		});

	}

	public void deleteQuantifier(final Quantifier q)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(getString(R.string.alert_confirm));
		builder
		.setMessage(getString(R.string.quantifier_delete_message))
		.setCancelable(false)
		.setPositiveButton(getString(R.string.alert_yes), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				RequestParams params = new RequestParams();
				params.put("api_key", global.getCurrentUser().getKey());
				client.delete(context, getString(R.string.server_url) + "quantifiers/" + q.getId()
						+ ".json", null, params, new JsonHttpResponseHandler()
				{
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response)
					{
						Log.d("ManageQuantifiers", response.toString());
						qAdapter.remove(q);
						Toast.makeText(getApplicationContext(),
								getString(R.string.deleted_quantifier), Toast.LENGTH_LONG).show();
						loadQuantifiers();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, Throwable throwable,
							JSONObject errorResponse)
					{
						Log.d("ManageQuantifiers", errorResponse.toString());
						Toast.makeText(getApplicationContext(),
								getString(R.string.error_deleting_quantifier), Toast.LENGTH_LONG)
								.show();
					}
				});

			}
		})
		.setNegativeButton(getString(R.string.alert_no), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void populateList()
	{
		qAdapter = new QuantifierAdapter(this, R.id.qa_list_item_name, list_of_quantifiers);
		quantifier_list.setAdapter(qAdapter);
		YoYo.with(Techniques.BounceInDown).delay(100).duration(800).playOn(quantifier_list);

		quantifier_list.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
			{
				deleteQuantifier(qAdapter.getItem(position));
				return true;
			}
		});
	}
}
