package com.main.oneplusonegame.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.main.oneplusonegame.R;
import com.main.oneplusonegame.customclasses.ScrollingTextView;
import com.main.oneplusonegame.models.Quantifier;
import com.main.oneplusonegame.models.Quantifiers;
import com.main.oneplusonegame.models.Questions;

public class QuantifierAdapter extends ArrayAdapter<Quantifier>
{

	int		quantifier_text_id;
	Activity	context;
	Quantifiers questions;

	public QuantifierAdapter(Activity context, int question_text_id, Quantifiers quantifiers)
	{
		super(context, R.layout.qa_list_item, quantifiers);
		this.quantifier_text_id = question_text_id;
		this.context = context;
		this.questions = quantifiers;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		View row = convertView;

		if (row == null)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.qa_list_item, null);
		}

		ScrollingTextView quantifier_text = (ScrollingTextView) row.findViewById(quantifier_text_id);
		quantifier_text.setText(questions.get(position).getText());

		return row;
	}
}
