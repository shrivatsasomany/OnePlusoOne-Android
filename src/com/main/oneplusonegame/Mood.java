package com.main.oneplusonegame;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.main.oneplusonegame.customclasses.RoundedImageView;

public class Mood extends Activity {

	public RoundedImageView happy;
	public RoundedImageView naughty;
	public RoundedImageView oops;
	public RoundedImageView pressure;
	public RoundedImageView satisfied;
	
	public TextView happyText;
	public TextView naughtyText;
	public TextView oopsText;
	public TextView pressureText;
	public TextView satisfiedText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mood);
		
		happy = (RoundedImageView)this.findViewById(R.id.mood_happy);
		naughty = (RoundedImageView)this.findViewById(R.id.mood_malicious);
		oops = (RoundedImageView)this.findViewById(R.id.mood_oops);
		pressure = (RoundedImageView)this.findViewById(R.id.mood_pressure);
		satisfied = (RoundedImageView)this.findViewById(R.id.mood_poop);
		happyText = (TextView)this.findViewById(R.id.mood_happy_text);
		naughtyText = (TextView)this.findViewById(R.id.mood_naughty_text);
		oopsText = (TextView)this.findViewById(R.id.mood_oops_text);
		pressureText = (TextView)this.findViewById(R.id.mood_pressure_text);
		satisfiedText = (TextView)this.findViewById(R.id.mood_poop_text);


		
		YoYo.with(Techniques.BounceIn).duration(800).playOn(happy);
		YoYo.with(Techniques.BounceIn).duration(800).playOn(naughty);
		YoYo.with(Techniques.BounceIn).duration(800).playOn(oops);
		YoYo.with(Techniques.BounceIn).duration(800).playOn(pressure);
		YoYo.with(Techniques.BounceIn).duration(800).playOn(satisfied);
		
		YoYo.with(Techniques.FadeIn).duration(800).playOn(happyText);
		YoYo.with(Techniques.FadeIn).duration(800).playOn(naughtyText);
		YoYo.with(Techniques.FadeIn).duration(800).playOn(oopsText);
		YoYo.with(Techniques.FadeIn).duration(800).playOn(pressureText);
		YoYo.with(Techniques.FadeIn).duration(800).playOn(satisfiedText);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mood, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
