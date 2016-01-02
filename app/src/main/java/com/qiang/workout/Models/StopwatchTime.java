package com.qiang.workout.Models;

public class StopwatchTime
{
	private int id;
	private int recordDate;
	private int time;

	public int getID()
	{
		return id;
	}

	public void setID(int id)
	{
		this.id = id;
	}

	public int getRecordDate()
	{
		return recordDate;
	}

	public void setRecordDate(int recordDate)
	{
		this.recordDate = recordDate;
	}

	public int getTime()
	{
		return time;
	}

	public void setTime(int time)
	{
		this.time = time;
	}
}