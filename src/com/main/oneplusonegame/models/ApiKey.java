package com.main.oneplusonegame.models;

import java.io.Serializable;

import javax.jdo.annotations.Key;

public class ApiKey implements Serializable
{
	@Key
	private String key;
	
	public ApiKey()
	{
		
	}

	@Override
	public String toString()
	{
		return key;
	}
}
