package com.main.oneplusonegame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.main.oneplusonegame.adapters.QuestionAdapter;
import com.main.oneplusonegame.models.Question;
import com.main.oneplusonegame.models.QuestionType;
import com.main.oneplusonegame.models.Questions;

public class ManageQuestions extends Activity
{

	private QuestionAdapter				qAdapter;
	private ListView						questions_list;
	private Button							create;

	private static AsyncHttpClient	client;
	private Global							global;
	private Gson							gson;
	private Context						context;

	private Questions						list_of_questions;
	private Question						created_question;
	private int								game_id;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_questions);

		Bundle extras = getIntent().getExtras();
		game_id = extras.getInt("game_id");

		global = (Global) getApplicationContext();
		client = new AsyncHttpClient();
		gson = new Gson();
		context = this;

		loadQuestions();

		questions_list = (ListView) this.findViewById(R.id.activity_manage_questions_listview);
		create = (Button) this.findViewById(R.id.activity_manage_questions_create);

		create.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				newQuestionDialog();
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

	public void loadQuestions()
	{
		RequestParams params = new RequestParams();
		params.put("api_key", global.getCurrentUser().getKey());
		client.get(getString(R.string.server_url) + "questions.json", params,
				new JsonHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray questions)
			{
				Log.d("Discovery", questions.toString());
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
		answer.setText(getString(R.string.dialog_qa_button_show_answers));
		TextView description = (TextView) dialog.findViewById(R.id.dialog_qa_main);
		TextView location = (TextView) dialog.findViewById(R.id.dialog_qa_extra);
		description.setText(q.getDescription());
		location.setText(q.getLocation());
		answer.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(ManageQuestions.this, ShowAnswers.class);
				i.putExtra("question", q);
				startActivity(i);
			}
		});
		dialog.show();
	}

	public void newQuestionDialog()
	{
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_new_question);
		dialog.setTitle(getString(R.string.dialog_new_question_title));
		Button create = (Button) dialog.findViewById(R.id.dialog_new_question_button);
		final EditText questionText = (EditText) dialog.findViewById(R.id.dialog_new_question_text);
		final EditText description = (EditText) dialog
				.findViewById(R.id.dialog_new_question_description);
		final EditText location = (EditText) dialog.findViewById(R.id.dialog_new_question_location);
		final CheckedTextView quantifier = (CheckedTextView) dialog
				.findViewById(R.id.dialog_new_question_quant);
		final Spinner type = (Spinner) dialog.findViewById(R.id.dialog_new_question_spinner);
		final ArrayAdapter<QuestionType> adapter = new ArrayAdapter<QuestionType>(this,
				android.R.layout.simple_spinner_dropdown_item);
		adapter.addAll(global.getQuestionTypes());
		type.setAdapter(adapter);
		quantifier.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				((CheckedTextView) v).toggle();
			}
		});
		create.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (questionText.getText().toString().equals("")
						|| description.getText().toString().equals(""))
				{
					Toast.makeText(context, getString(R.string.incomplete_fields), Toast.LENGTH_LONG)
					.show();
				} else
				{
					createQuestion(questionText.getText().toString(), description.getText().toString(),
							location.getText().toString(), quantifier.isChecked(),
							adapter.getItem(type.getSelectedItemPosition()).getType());
					dialog.dismiss();
				}
			}
		});

		dialog.show();
	}

	public void createQuestion(String text, String description, String location,
			boolean isQuantifier, String type)
	{
		RequestParams params = new RequestParams();
		Map<String, String> question = new HashMap<String, String>();
		question.put("question_text", text);
		question.put("description", description);
		question.put("location", location);
		question.put("is_quantifier", new Boolean(isQuantifier).toString());
		question.put("question_type", type);
		question.put("game_id", "1");
		params.put("question", question);
		params.put("api_key", global.getCurrentUser().getKey());
		client.post(getApplicationContext(), getString(R.string.server_url) + "questions.json",
				params, new JsonHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub
				created_question = gson.fromJson(response.toString(), Question.class);

				Log.d("ManageQuestions", "Successfuly created question: " + created_question.toString());
				Log.d("ManageQuestions", "Response: " + response.toString());
				Toast.makeText(getApplicationContext(), getString(R.string.created_question),
						Toast.LENGTH_SHORT).show();
				loadQuestions();

				if (created_question.getQuestion_type().equals("Prop"))
				{
					Log.d("ManageQuestions", created_question.toString()+"\n\n"+response.toString());
					saveQR();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable,
					JSONObject errorResponse)
			{

				Toast.makeText(getApplicationContext(),
						getString(R.string.error_adding_question), Toast.LENGTH_LONG).show();

			}
		});

	}

	public void deleteQuestion(final Question q)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(getString(R.string.alert_confirm));
		builder
		.setMessage(getString(R.string.question_delete_message))
		.setCancelable(false)
		.setPositiveButton(getString(R.string.alert_yes), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				RequestParams params = new RequestParams();
				params.put("api_key", global.getCurrentUser().getKey());
				client.delete(context, getString(R.string.server_url) + "questions/" + q.getId()
						+ ".json", null, params, new JsonHttpResponseHandler()
				{
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response)
					{
						Log.d("ManageQuestions", response.toString());
						qAdapter.remove(q);
						Toast.makeText(getApplicationContext(),
								getString(R.string.deleted_question), Toast.LENGTH_LONG).show();
						loadQuestions();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, Throwable throwable,
							JSONObject errorResponse)
					{
						Log.d("ManageQuestions", errorResponse.toString());
						Toast.makeText(getApplicationContext(),
								getString(R.string.error_deleting_question), Toast.LENGTH_LONG)
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

		questions_list.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
			{
				deleteQuestion(qAdapter.getItem(position));
				return true;
			}
		});
	}
	
	public void saveQR()
	{
		QRCodeWriter writer = new QRCodeWriter();
		try
		{
			BitMatrix matrix = writer.encode(""+created_question.getId(), BarcodeFormat.QR_CODE, 400,
					400);
			// Now what??
			int height = matrix.getHeight();
			int width = matrix.getWidth();
			Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
			for (int x = 0; x < width; x++){
				for (int y = 0; y < height; y++){
					bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
				}
			}
			String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
					"/1plus1";
			File dir = new File(file_path);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			File file = new File(dir, "Prop: " + created_question.getLocation() + ".png");
			FileOutputStream fOut = new FileOutputStream(file);

			bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
			Toast.makeText(getApplicationContext(), "QR Code Saved as "+"Prop: " + created_question.getLocation() + ".png", Toast.LENGTH_SHORT).show();
		} 
		catch (WriterException e)
		{
			e.printStackTrace();
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
