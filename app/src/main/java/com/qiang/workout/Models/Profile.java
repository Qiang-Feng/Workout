package com.qiang.workout.Models;

public class Profile
{
	private int id;

	private int minutes;
	private int seconds;

	private boolean repeat;
	private int repeatNumber;

	private String name;

	public Profile(String name)
	{
		this.name = name;
	}

	public int getID()
	{
		return id;
	}

	public void setID(int id)
	{
		this.id = id;
	}

	public int getMinutes()
	{
		return minutes;
	}

	public void setMinutes(int minutes)
	{
		this.minutes = minutes;
	}

	public int getSeconds()
	{
		return seconds;
	}

	public void setSeconds(int seconds)
	{
		this.seconds = seconds;
	}

	public boolean isRepeat()
	{
		return repeat;
	}

	public void setRepeat(boolean repeat)
	{
		this.repeat = repeat;
	}

	public int getRepeatNumber()
	{
		return repeatNumber;
	}

	public void setRepeatNumber(int repeatNumber)
	{
		this.repeatNumber = repeatNumber;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}