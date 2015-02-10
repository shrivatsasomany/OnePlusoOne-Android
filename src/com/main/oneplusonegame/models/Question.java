package com.main.oneplusonegame.models;

import java.io.Serializable;

import javax.jdo.annotations.Key;

public class Question implements Serializable
{
	@Key
	private int		id;
	@Key
	private String	question_text;
	@Key
	private String	description;
	@Key
	private String	location;
	@Key
	private String	picture;
	@Key
	private String question_type;
	@Key
	private boolean is_quantifier;

	public Question()
	{
		
	}
	
	public Question(String question_text, String description,
			String location)
	{
		this.question_text = question_text;
		this.description = description;
		this.location = location;
	}

	public int getQuestion_id()
	{
		return id;
	}

	public void setQuestion_id(int question_id)
	{
		this.id = question_id;
	}

	public String getQuestion_text()
	{
		return question_text;
	}

	public void setQuestion_text(String question_text)
	{
		this.question_text = question_text;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public String getPicture()
	{
		return picture;
	}

	public void setPicture(String picture)
	{
		this.picture = picture;
	}
	
	

	public String getQuestion_type()
	{
		return question_type;
	}

	public void setQuestion_type(String question_type)
	{
		this.question_type = question_type;
	}

	public int getId()
	{
		return id;
	}
	
	public boolean isQuantifier()
	{
		return is_quantifier;
	}

	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Question other = (Question) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "Question [question_id=" + id + ", question_text="
				+ question_text + ", description=" + description + ", location="
				+ location + ", question_type=" + question_type + "]";
	}
}
