package com.main.oneplusonegame.models;

import java.io.Serializable;
import java.util.ArrayList;

import javax.jdo.annotations.Key;


public class User implements Serializable
{
	@Key
	private String		name;
	@Key
	private String		email;
	@Key
	private Integer	id;
	@Key
	private Integer	game_id;
	@Key
	private String		password;
	@Key
	private String		password_confirmation;
	@Key
	private boolean	isAdmin;
	@Key
	private ArrayList<String> roles = new ArrayList<String>();
	@Key
	private String key;

	public User(String name, String email, Integer game_id, String password,
			String password_confirmation, boolean isAdmin)
	{
		super();
		this.name = name;
		this.email = email;
		this.game_id = game_id;
		this.password = password;
		this.password_confirmation = password_confirmation;
		this.isAdmin = isAdmin;
	}

	public User()
	{

	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public Integer getGame_id()
	{
		return game_id;
	}

	public void setGame_id(Integer game_id)
	{
		this.game_id = game_id;
	}

	public Integer getId()
	{
		return id;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getPassword_confirmation()
	{
		return password_confirmation;
	}

	public void setPassword_confirmation(String password_confirmation)
	{
		this.password_confirmation = password_confirmation;
	}
	
	public boolean isAdmin()
	{
		return isAdmin;
	}
	
	public void addRole(String role)
	{
		roles.add(role);
	}
	
	public void removeRole(String role)
	{
		roles.remove(role);
	}
	
	public ArrayList<String> getRoles()
	{
		return roles;
	}
	
	public String getKey()
	{
		return key;
	}

	@Override
	public String toString()
	{
		return "User [name=" + name + ", email=" + email + ", id=" + id
				+ ", game_id=" + game_id + ", password=" + password
				+ ", password_confirmation=" + password_confirmation + ", isAdmin="
				+ isAdmin + ", roles=" + roles + ", key=" + key + "]";
	}

}
