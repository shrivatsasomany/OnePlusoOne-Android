package com.main.oneplusonegame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.main.oneplusonegame.models.User;

public class Registration extends Activity
{
	public Button				register;
	public EditText			name;
	public EditText			email;
	public EditText			password;
	public EditText			confirmation;
	public Spinner				role;

	public Global				global;
	public AsyncHttpClient	client;
	public Gson					gson;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

		setContentView(R.layout.activity_register);

		global = (Global) getApplicationContext();
		client = new AsyncHttpClient();
		gson = new Gson();

		register = (Button) this.findViewById(R.id.activity_register_button);
		name = (EditText) this.findViewById(R.id.activity_register_name);
		email = (EditText) this.findViewById(R.id.activity_register_email);
		password = (EditText) this.findViewById(R.id.activity_register_password);
		confirmation = (EditText) this
				.findViewById(R.id.activity_register_confirmation);
		role = (Spinner) this.findViewById(R.id.activity_register_roles);

		if (global.getCurrentUser() != null && global.getCurrentUser().isAdmin())
		{
			Toast.makeText(getApplicationContext(), getString(R.string.admin_registration), Toast.LENGTH_LONG).show();
			List<String> spinnerArray = new ArrayList<String>();
			spinnerArray.add("Admin");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				    this, android.R.layout.simple_spinner_item, spinnerArray);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			role.setAdapter(adapter);
		}

		register.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				String myName = name.getText().toString();
				String myEmail = email.getText().toString();
				String myPassword = password.getText().toString();
				String myConfirmation = confirmation.getText().toString();
				String myRole = (String) role.getSelectedItem();

				createUser(myName, myEmail, myPassword, myConfirmation,
						myRole.toLowerCase());
			}
		});
	}

	public boolean createUser(String name, String email, String password,
			String confirmation, String role)
	{
		try
		{

			if (global.getCurrentUser() != null
					&& !global.getCurrentUser().isAdmin())
			{
				if (role.equals("admin"))
				{
					Toast.makeText(getApplicationContext(), "How...?",
							Toast.LENGTH_LONG).show();
				}
				return false;
			}
			RequestParams params = new RequestParams();
			Map<String, String> user = new HashMap<String, String>();
			user.put("name", name);
			user.put("email", email);
			user.put("password", password);
			user.put("password_confirmation", confirmation);
			user.put("roles", role);
			user.put("game_id", "1");
			if(global.getCurrentUser()!=null && global.getCurrentUser().isAdmin())
			{
				user.put("isAdmin", "true");
			} else
			{
				user.put("isAdmin", "false");
			}
			params.put("user", user);
			client.post(getApplicationContext(), getString(R.string.server_url)
					+ "users.json", params, new JsonHttpResponseHandler()
			{
				@Override
				public void onSuccess(int arg0, Header[] arg1, JSONObject arg2)
				{
					// TODO Auto-generated method stub
					User current_user = gson.fromJson(arg2.toString(), User.class);

					Log.d("Registration", "Successfuly created user: "
							+ current_user.toString());
					Log.d("Registration", "Response: " + arg2.toString());
					global.setCurrentUser(current_user);
					Toast.makeText(getApplicationContext(),
							getString(R.string.confirm_login), Toast.LENGTH_SHORT)
							.show();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONObject errorResponse)
				{
					if (errorResponse.has("password_confirmation"))
					{
						Toast.makeText(getApplicationContext(),
								getString(R.string.password_error), Toast.LENGTH_LONG)
								.show();
					} else if (errorResponse.has("email"))
					{
						Toast.makeText(getApplicationContext(),
								getString(R.string.duplicate_user_error),
								Toast.LENGTH_LONG).show();
					}
				}
			});
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
