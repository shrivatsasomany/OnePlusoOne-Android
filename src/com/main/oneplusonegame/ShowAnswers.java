package com.main.oneplusonegame;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.main.oneplusonegame.adapters.AnswerAdapter;
import com.main.oneplusonegame.models.Answer;
import com.main.oneplusonegame.models.Answers;
import com.main.oneplusonegame.models.Quantifier;
import com.main.oneplusonegame.models.Quantifiers;
import com.main.oneplusonegame.models.Question;

public class ShowAnswers extends Activity
{

	private AnswerAdapter				ansAdapter;
	private ListView						answers_list;
	private Spinner						quantifier_spinner;
	private EditText						location_filter;
	private LinearLayout					filter_layout;
	private Button							filter_button;


	private static AsyncHttpClient	client;
	private Global							global;
	private Gson							gson;
	private Context						context;

	private Answers						list_of_answers;

	private Question						passedQuestion;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_answers);

		Bundle extras = getIntent().getExtras();
		if(extras!=null)
		{
			if(extras.containsKey("question"))
			{
				passedQuestion = (Question) extras.get("question");
			}
		}

		global = (Global) getApplicationContext();
		client = new AsyncHttpClient();
		gson = new Gson();
		context = this;

		answers_list = (ListView) this.findViewById(R.id.activity_answers_listview);
		quantifier_spinner = (Spinner) this.findViewById(R.id.activity_answer_quantifier_spinner);
		location_filter = (EditText) this.findViewById(R.id.activity_answer_location_input);
		filter_layout = (LinearLayout) this.findViewById(R.id.activity_answer_filter_layout);
		filter_button = (Button) this.findViewById(R.id.activity_answer_filter_button);
		
		

		final ArrayAdapter<Quantifier> adapter = new ArrayAdapter<Quantifier>(this,
				android.R.layout.simple_spinner_dropdown_item);
		
		adapter.addAll(global.getQuantifiers());
		adapter.add(new Quantifier("None"));
		quantifier_spinner.setAdapter(adapter);
		quantifier_spinner.setSelection(global.getQuantifiers().size());
		filter_button.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if(location_filter.getText().toString() == "")
				{
					loadAnswers(adapter.getItem(quantifier_spinner.getSelectedItemPosition()).getText(), null);
				}
				else
				{
					loadAnswers(adapter.getItem(quantifier_spinner.getSelectedItemPosition()).getText(), location_filter.getText().toString());
				}
			}
		});
		disableFilters();

		loadAnswers();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.answers, menu);
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

	public void disableFilters()
	{
		if(!global.getCurrentUser().isAdmin())
		{
			filter_layout.setVisibility(View.GONE);
		}

	}

	public void loadAnswers()
	{
		RequestParams params = new RequestParams();
		params.put("api_key", global.getCurrentUser().getKey());
		if(passedQuestion!=null)
		{
			params.put("question_id", passedQuestion.getId());
		}
		if(!global.getCurrentUser().isAdmin())
		{
			params.put("user_id", global.getCurrentUser().getId());
		}
		client.get(getString(R.string.server_url) + "answers.json", params,
				new JsonHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray answers)
			{
				Log.d("ShowAnswers", answers.toString());
				list_of_answers = gson.fromJson(answers.toString(), Answers.class);
				populateList();
			}
			public void onSuccess(int statusCode, Header[] headers, JSONObject answer)
			{
				Log.d("ShowAnswers", answer.toString());
				Answer a = gson.fromJson(answer.toString(), Answer.class);
				list_of_answers = new Answers();
				list_of_answers.add(a);
				populateList();
			}
		});

	}
	
	public void loadAnswers(String quantifier, String location)
	{
		RequestParams params = new RequestParams();
		params.put("api_key", global.getCurrentUser().getKey());
		if(location!=null)
		{
			params.put("location", location);
		}
		if(!quantifier.equals("None"))
		{
			params.put("quantifier", quantifier);
		}
		if(passedQuestion!=null)
		{
			params.put("question_id", passedQuestion.getId());
		}
		if(!global.getCurrentUser().isAdmin())
		{
			params.put("user_id", global.getCurrentUser().getId());
		}
		client.get(getString(R.string.server_url) + "answers.json", params,
				new JsonHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray answers)
			{
				Log.d("ShowAnswers", answers.toString());
				list_of_answers = gson.fromJson(answers.toString(), Answers.class);
				populateList();
			}
			public void onSuccess(int statusCode, Header[] headers, JSONObject answer)
			{
				Log.d("ShowAnswers", answer.toString());
				Answer a = gson.fromJson(answer.toString(), Answer.class);
				list_of_answers = new Answers();
				list_of_answers.add(a);
				populateList();
			}
		});

	}

	public void answerInfoDialog(final Answer a)
	{
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_qa_info);
		dialog.setTitle(a.getQuestion().getQuestion_text());
		Button answer = (Button) dialog.findViewById(R.id.dialog_qa_action);
		answer.setText("Back");
		TextView description = (TextView) dialog.findViewById(R.id.dialog_qa_main);
		TextView location = (TextView) dialog.findViewById(R.id.dialog_qa_extra);
		final ImageView image = (ImageView) dialog.findViewById(R.id.dialog_qa_image);
		image.setVisibility(View.INVISIBLE);

		final ProgressBar pbar = (ProgressBar) dialog.findViewById(R.id.dialog_qa_progress_bar);
		description.setText(a.getAnswer_text());
		location.setText(a.getQuestion().getLocation());
		answer.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});

		if(!a.getImageURL().equals(""))
		{
			client.get(a.getImageURL(), null, new AsyncHttpResponseHandler() {            

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] fileData, Throwable arg3)
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] fileData)
				{
					// TODO Auto-generated method stub
					Bitmap b = BitmapFactory.decodeByteArray(fileData, 0, fileData.length);
					image.setImageBitmap(b);
					pbar.setVisibility(View.GONE);
					image.setVisibility(View.VISIBLE);
				}
			});
		}
		else
		{
			pbar.setVisibility(View.GONE);
		}

		dialog.show();
	}

	public void populateList()
	{
		ansAdapter = new AnswerAdapter(this, R.id.qa_list_item_name,list_of_answers);
		answers_list.setAdapter(ansAdapter);
		YoYo.with(Techniques.BounceInDown).delay(100).duration(800).playOn(answers_list);
		answers_list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				answerInfoDialog(list_of_answers.get(position));
			}
		});
	}
}
