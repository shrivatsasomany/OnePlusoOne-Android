package com.main.oneplusonegame.models;

import java.io.Serializable;

import javax.jdo.annotations.Key;

public class QuestionType implements Serializable
{
	@Key
	private String type_text;
	
	public QuestionType()
	{
		
	}
	
	public String getType()
	{
		return type_text;
	}
	
	@Override
	public String toString()
	{
		return type_text;
	}
}
