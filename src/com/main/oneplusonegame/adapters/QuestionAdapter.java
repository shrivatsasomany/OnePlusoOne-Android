package com.main.oneplusonegame.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.main.oneplusonegame.R;
import com.main.oneplusonegame.customclasses.ScrollingTextView;
import com.main.oneplusonegame.models.Question;
import com.main.oneplusonegame.models.Questions;

public class QuestionAdapter extends ArrayAdapter<Question>
{

	int		question_text_id;
	Activity	context;
	Questions questions;

	public QuestionAdapter(Activity context, int question_text_id, Questions questions)
	{
		super(context, R.layout.qa_list_item, questions);
		this.question_text_id = question_text_id;
		this.context = context;
		this.questions = questions;
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

		ScrollingTextView question_text = (ScrollingTextView) row.findViewById(question_text_id);
		question_text.setText(questions.get(position).getQuestion_text());

		return row;
	}
}
