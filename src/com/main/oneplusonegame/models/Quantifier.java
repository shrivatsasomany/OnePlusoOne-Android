package com.main.oneplusonegame.models;

import java.io.Serializable;

import javax.jdo.annotations.Key;

public class Quantifier implements Serializable
{
	
	@Key
	private int id;
	@Key
	private String text;
	
	public Quantifier()
	{
		
	}
	
	public Quantifier(String text)
	{
		this.text = text;
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getText()
	{
		return text;
	}
	
	@Override
	public String toString()
	{
		return text;
	}
}
