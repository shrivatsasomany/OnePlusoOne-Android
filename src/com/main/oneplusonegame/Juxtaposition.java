package com.main.oneplusonegame;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.main.oneplusonegame.adapters.QuestionAdapter;
import com.main.oneplusonegame.models.Quantifier;
import com.main.oneplusonegame.models.Question;
import com.main.oneplusonegame.models.Questions;

public class Juxtaposition extends Activity
{

	private TextView			juxtaposition;
	private TextView			juxtaposition_full;
	private ListView			questions_list;

	private Global				global;
	private AsyncHttpClient	client;
	private Gson				gson;
	private Context			context;

	private Questions			list_of_questions;
	private QuestionAdapter	qAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_juxtaposition);

		global = (Global) getApplicationContext();
		client = new AsyncHttpClient();
		gson = new Gson();
		context = this;

		loadQuestions();

		juxtaposition = (TextView) this.findViewById(R.id.juxtaposition_page_juxtaposition);
		juxtaposition_full = (TextView) this.findViewById(R.id.juxtaposition_page_juxtaposition_full);
		questions_list = (ListView) this.findViewById(R.id.juxtaposition_page_listview);

		YoYo.with(Techniques.FadeInDown).delay(100).duration(800).playOn(juxtaposition_full);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.juxtaposition, menu);
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

	public static boolean isTablet(Context context)
	{
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public void loadQuestions()
	{
		RequestParams params = new RequestParams("qtype", "Juxtaposition");
		params.put("api_key", global.getCurrentUser().getKey());
		client.get(getString(R.string.server_url) + "questions.json", params,
				new JsonHttpResponseHandler()
				{
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONArray questions)
					{
						Log.d("Juxtaposition", questions.toString());
						list_of_questions = gson.fromJson(questions.toString(), Questions.class);
						populateList();
					}
				});

	}

	public void questionInfoDialog(final Question q)
	{
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_qa_info);
		dialog.setTitle(q.getQuestion_text());
		Button answer = (Button) dialog.findViewById(R.id.dialog_qa_action);
		TextView description = (TextView) dialog.findViewById(R.id.dialog_qa_main);
		TextView location = (TextView) dialog.findViewById(R.id.dialog_qa_extra);
		description.setText(q.getDescription());
		location.setText(q.getLocation());
		answer.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				answerDialog(q);
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	public void answerDialog(final Question q)
	{
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_answer);
		dialog.setTitle(q.getQuestion_text());
		Button answer = (Button) dialog.findViewById(R.id.dialog_answer_button);
		final EditText answerText = (EditText) dialog.findViewById(R.id.dialog_answer_textbox);
		LinearLayout quantLayout = (LinearLayout) dialog
				.findViewById(R.id.dialog_answer_quant_layout);
		final Spinner quantifier = (Spinner) dialog.findViewById(R.id.dialog_answer_quant_spinner);
		final ArrayAdapter<Quantifier> adapter = new ArrayAdapter<Quantifier>(this,
				android.R.layout.simple_spinner_item);
		adapter.addAll(global.getQuantifiers());
		quantifier.setAdapter(adapter);
		if (!q.isQuantifier())
		{
			quantLayout.setVisibility(View.INVISIBLE);
		}
		answer.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(answerText.getText().toString()!="")
				{
					String quant = adapter.getItem(quantifier.getSelectedItemPosition()).getText();
					if(!q.isQuantifier())
					{
						quant = "";
					}
					postAnswer(answerText.getText().toString(), quant, q);
					dialog.dismiss();
				}
				else
				{
					Toast.makeText(context, getString(R.string.blank_answer), Toast.LENGTH_SHORT).show();
				}
			}
		});

		dialog.show();
	}

	public void postAnswer(String answerText, String quantifier, Question q)
	{
		Map<String, Object> answer = new HashMap<String, Object>();
		RequestParams params = new RequestParams();
		answer.put("quantifier", quantifier);
		answer.put("answer_text", answerText);
		answer.put("question_id", new Integer(q.getId()).toString());
		answer.put("user_id", new Integer(global.getCurrentUser().getId()).toString());
		params.put("api_key", global.getCurrentUser().getKey());
		params.put("answer", answer);
		client.post(getString(R.string.server_url) + "answers.json", params,
				new JsonHttpResponseHandler()
				{
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response)
					{
						Log.d("Juxtaposition", response.toString());
						Toast.makeText(context, getString(R.string.confirm_answer_post),
								Toast.LENGTH_SHORT).show();
					}
				});

	}

	public void populateList()
	{
		qAdapter = new QuestionAdapter(this, R.id.qa_list_item_name, list_of_questions);
		questions_list.setAdapter(qAdapter);
		YoYo.with(Techniques.BounceInDown).delay(100).duration(800).playOn(questions_list);
		questions_list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				questionInfoDialog(list_of_questions.get(position));
			}
		});
	}
}
