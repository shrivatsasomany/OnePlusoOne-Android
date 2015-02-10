package com.main.oneplusonegame.models;

import java.io.Serializable;

import javax.jdo.annotations.Key;

public class Answer implements Serializable
{

	@Key
	private int id;
	@Key
	private String answer_text;
	@Key
	private String quantifier;
	@Key
	private Question question;
	@Key
	private String image;
	
	public Answer()
	{
		
	}

	public String getAnswer_text()
	{
		return answer_text;
	}

	public void setAnswer_text(String answer_text)
	{
		this.answer_text = answer_text;
	}

	public String getQuantifier()
	{
		return quantifier;
	}

	public void setQuantifier(String quantifier)
	{
		this.quantifier = quantifier;
	}

	public Question getQuestion()
	{
		return question;
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getImageURL()
	{
		return image;
	}

	@Override
	public String toString()
	{
		return "Answer [answer_text=" + answer_text + ", quantifier=" + quantifier + ", question="
				+ question + "]";
	}
	
}
