package com.main.oneplusonegame;

import java.util.HashMap;
import java.util.Map;

import jim.h.common.android.lib.zxing.config.ZXingLibConfig;
import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.main.oneplusonegame.models.Quantifier;
import com.main.oneplusonegame.models.Question;
import com.main.oneplusonegame.models.User;

public class OpeningPage extends Activity
{

	private TextView			creation;
	private TextView			discovery;
	private TextView			juxtaposition;
	private TextView			oneplusone;
	private final Context	context		= this;

	private AsyncHttpClient	client;
	private Global				global;
	private Gson				gson;

	private boolean			isLoggedIn	= false;

	private ZXingLibConfig	zxingLibConfig;
	
	private Question 			scannedQuestion;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

		setContentView(R.layout.activity_opening_page_alt);

		client = new AsyncHttpClient();
		global = (Global) getApplicationContext();
		gson = new Gson();
		// getWindow().setSharedElementEnterTransition(new MoveImage());
		// getWindow().setSharedElementExitTransition(new MoveImage());

		global.loadQuantifiers();
		global.loadTypes();

		zxingLibConfig = new ZXingLibConfig();

		creation = (TextView) this.findViewById(R.id.opening_page_creation);
		discovery = (TextView) this.findViewById(R.id.opening_page_discovery);
		juxtaposition = (TextView) this.findViewById(R.id.opening_page_juxtaposition);
		oneplusone = (TextView) this.findViewById(R.id.opening_page_oneplusone);

		if (isTablet(this))
		{
			creation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
			discovery.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
			juxtaposition.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
			oneplusone.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
		}

		YoYo.with(Techniques.BounceIn).duration(800).playOn(creation);
		YoYo.with(Techniques.BounceIn).duration(800).playOn(discovery);
		YoYo.with(Techniques.BounceIn).duration(800).playOn(juxtaposition);
		YoYo.with(Techniques.BounceIn).duration(800).playOn(oneplusone);

		creation.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (isLoggedIn)
				{
					Intent n = new Intent(OpeningPage.this, Creation.class);
					ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
							OpeningPage.this, creation, "creation_button");
					startActivity(n, options.toBundle());
				} else
				{
					loginDialog();
					Toast.makeText(getApplicationContext(), getString(R.string.not_logged_in),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		discovery.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				if (isLoggedIn)
				{
					Intent n = new Intent(OpeningPage.this, Discovery.class);
					ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
							OpeningPage.this, discovery, "discovery_button");
					startActivity(n, options.toBundle());
				} else
				{
					loginDialog();
					Toast.makeText(getApplicationContext(), getString(R.string.not_logged_in),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		juxtaposition.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (isLoggedIn)
				{
					Intent n = new Intent(OpeningPage.this, Juxtaposition.class);
					ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
							OpeningPage.this, juxtaposition, "juxtaposition_button");
					startActivity(n, options.toBundle());
				} else
				{
					loginDialog();
					Toast.makeText(getApplicationContext(), getString(R.string.not_logged_in),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		oneplusone.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (isLoggedIn)
				{
					Intent n = new Intent(OpeningPage.this, Connection.class);
					ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
							OpeningPage.this, oneplusone, "oneplusone_button");
					startActivity(n, options.toBundle());
				} else
				{
					loginDialog();
					Toast.makeText(getApplicationContext(), getString(R.string.not_logged_in),
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.opening_page, menu);
		if (isAdmin())
		{
			menu.findItem(R.id.admin_registration_button).setVisible(true);
			menu.findItem(R.id.admin_options).setVisible(true);
		} else
		{
			menu.findItem(R.id.admin_registration_button).setVisible(false);
			menu.findItem(R.id.admin_options).setVisible(false);
		}
		if (isLoggedIn)
		{
			menu.findItem(R.id.login_button).setVisible(false);
			menu.findItem(R.id.profile_button).setVisible(true);
			menu.findItem(R.id.logout_button).setVisible(true);
		} else
		{
			menu.findItem(R.id.login_button).setVisible(true);
			menu.findItem(R.id.profile_button).setVisible(false);
			menu.findItem(R.id.logout_button).setVisible(false);
		}
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
		if (id == R.id.camera_button)
		{
			startScan();
			return true;
		}
		if (id == R.id.login_button)
		{
			loginDialog();
			return true;
		}
		if (id == R.id.logout_button)
		{
			logoutUser();
			return true;
		}
		if (id == R.id.admin_registration_button)
		{
			Intent i = new Intent(context, Registration.class);
			startActivity(i);
			return true;
		}
		if (id == R.id.admin_options)
		{
			manageDialog();
			return true;
		}
		if (id == R.id.profile_button)
		{
			Intent i = new Intent(context, ShowAnswers.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static boolean isTablet(Context context)
	{
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public void loginUser(String email, String password, final Dialog dialog)
	{
		RequestParams params = new RequestParams("email", email);
		params.put("password", password);
		client.post(getApplicationContext(), getString(R.string.server_url) + "log-in.json", params,
				new JsonHttpResponseHandler()
				{

					@Override
					public void onSuccess(int arg0, Header[] arg1, JSONObject arg2)
					{
						// TODO Auto-generated method stub
						User current_user = gson.fromJson(arg2.toString(), User.class);

						Log.d("OpeningPage", "Successful login: " + current_user.toString());
						Log.d("OpeningPage", "Response: " + arg2.toString());
						global.setCurrentUser(current_user);
						Toast.makeText(getApplicationContext(), getString(R.string.confirm_login),
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						isLoggedIn = true;
						invalidateOptionsMenu();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, Throwable throwable,
							JSONObject errorResponse)
					{
						try
						{
							Toast.makeText(getApplicationContext(), errorResponse.getString("error"),
									Toast.LENGTH_LONG).show();
						} catch (JSONException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				});
	}

	public void logoutUser()
	{
		RequestParams params = new RequestParams("api_key", global.getCurrentUser().getKey());
		client.get(getApplicationContext(), getString(R.string.server_url) + "log-out.json", params,
				new JsonHttpResponseHandler()
				{

					@Override
					public void onSuccess(int arg0, Header[] arg1, JSONObject arg2)
					{
						// TODO Auto-generated method stub

						Log.d("OpeningPage", "Successful logout");
						Log.d("OpeningPage", "Response: " + arg2.toString());
						global.setCurrentUser(null);
						Toast.makeText(getApplicationContext(), getString(R.string.confirm_logout),
								Toast.LENGTH_SHORT).show();
						isLoggedIn = false;
						invalidateOptionsMenu();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, Throwable throwable,
							JSONObject errorResponse)
					{
						try
						{
							Toast.makeText(getApplicationContext(), errorResponse.getString("error"),
									Toast.LENGTH_LONG).show();
						} catch (JSONException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				});
	}

	public void loginDialog()
	{
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_login);
		dialog.setTitle(getString(R.string.menu_login_full));
		final EditText email = (EditText) dialog.findViewById(R.id.login_dialog_email);
		final EditText password = (EditText) dialog.findViewById(R.id.login_dialog_password);
		Button signin = (Button) dialog.findViewById(R.id.login_dialog_signin);
		Button register = (Button) dialog.findViewById(R.id.login_dialog_register);

		signin.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				loginUser(email.getText().toString(), password.getText().toString(), dialog);
			}
		});

		register.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
				Intent i = new Intent(context, Registration.class);
				startActivity(i);
			}
		});
		dialog.show();
	}

	public void manageDialog()
	{
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_manage_game);
		dialog.setTitle(getString(R.string.menu_admin_management));

		Button questions = (Button) dialog.findViewById(R.id.dialog_manage_questions);
		Button quantifiers = (Button) dialog.findViewById(R.id.dialog_manage_quantifiers);
		Button answers = (Button) dialog.findViewById(R.id.dialog_manage_show_answers);

		questions.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(context, ManageQuestions.class);
				i.putExtra("game_id", 1);
				startActivity(i);
			}
		});

		quantifiers.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(context, ManageQuantifiers.class);
				i.putExtra("game_id", 1);
				startActivity(i);
			}
		});

		answers.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(context, ManageQuestions.class);
				i.putExtra("game_id", 1);
				startActivity(i);
			}
		});
		dialog.show();
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
		LinearLayout quantLayout = (LinearLayout) dialog.findViewById(R.id.dialog_answer_quant_layout);
		final Spinner quantifier = (Spinner) dialog.findViewById(R.id.dialog_answer_quant_spinner);
		final ArrayAdapter<Quantifier> adapter = new ArrayAdapter<Quantifier>(this, android.R.layout.simple_spinner_item);
		adapter.addAll(global.getQuantifiers());
		quantifier.setAdapter(adapter);
		if(!q.isQuantifier())
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
				}			}
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
						Log.d("OpeningPage", response.toString());
						Toast.makeText(context, getString(R.string.confirm_answer_post),
								Toast.LENGTH_SHORT).show();
					}
				});

	}
	
	public void loadQuestion(int id)
	{
		RequestParams params = new RequestParams();
		params.put("api_key", global.getCurrentUser().getKey());
		client.get(getString(R.string.server_url) + "questions/"+id+".json", params,
				new JsonHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject question)
			{
				Log.d("OpeningPage", question.toString());
				try
				{
					scannedQuestion = gson.fromJson(question.toString(), Question.class);
					questionInfoDialog(scannedQuestion);
				}
				catch(Exception e)
				{
					Toast.makeText(getApplicationContext(), "Error downloading information!", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	public boolean isAdmin()
	{
		if (global.getCurrentUser() != null)
		{
			return global.getCurrentUser().isAdmin();
		}
		return false;
	}

	public void startScan()
	{
		IntentIntegrator.initiateScan(OpeningPage.this, zxingLibConfig);
	}

	/**
	 * 
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
		case IntentIntegrator.REQUEST_CODE:
			IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode,
					data);
			if (scanResult == null)
			{
				return;
			}
			final String result = scanResult.getContents();
			if (result != null)
			{
				Log.d("OpeningPage", result);
				try
				{
					loadQuestion(Integer.parseInt(result));
				}
				catch(Exception e)
				{
					Toast.makeText(getApplicationContext(), "Error: Invalid QR Code", Toast.LENGTH_SHORT)
					.show();
				}
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Error: Invalid QR Code", Toast.LENGTH_SHORT)
						.show();
			}
			/*
			 * AsyncTask to open table with QR code ID Hide QR Code and Temp table
			 * layouts
			 */

		default:
			break;

		}
	}

}
