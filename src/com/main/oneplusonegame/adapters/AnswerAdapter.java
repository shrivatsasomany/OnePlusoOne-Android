package com.main.oneplusonegame.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.main.oneplusonegame.R;
import com.main.oneplusonegame.customclasses.ScrollingTextView;
import com.main.oneplusonegame.models.Answer;
import com.main.oneplusonegame.models.Answers;

public class AnswerAdapter extends ArrayAdapter<Answer>
{

	int		question_text_id;
	Activity	context;
	Answers answers;

	public AnswerAdapter(Activity context, int question_text_id, Answers answers)
	{
		super(context, R.layout.qa_list_item, answers);
		this.question_text_id = question_text_id;
		this.context = context;
		this.answers = answers;
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
		question_text.setText(answers.get(position).getAnswer_text());

		return row;
	}
}
