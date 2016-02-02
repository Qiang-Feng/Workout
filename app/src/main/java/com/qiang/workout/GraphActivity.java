package com.qiang.workout;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.qiang.workout.Models.StopwatchTime;
import com.qiang.workout.Utilities.DBHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity
{
	private BarChart chart;
	private ArrayList<BarEntry> entries;
	private ArrayList<String> dateAxisLabels;

	private DBHandler dbHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Sets the activity_graph as this activity's display
		setContentView(R.layout.activity_graph);

		// Initialise variables
		chart = (BarChart) findViewById(R.id.chart);
		dbHandler = new DBHandler(this, null, null, 1);
		entries = new ArrayList<>();
		dateAxisLabels = new ArrayList<>();

		// Removes the default text from the BarChart
		chart.setDescription("");
		chart.setNoDataTextDescription("");
		chart.setNoDataText("");

		// Set the font size for text inside the BarChart
		chart.getAxisLeft().setTextSize(13F);
		chart.getXAxis().setTextSize(13F);

		chart.setPinchZoom(false);
		chart.setDoubleTapToZoomEnabled(false);

		// Removes right Y axis
		chart.getAxisRight().setEnabled(false);

		// Removes left Y axis line
		chart.getAxisLeft().setDrawAxisLine(false);

		// Removes grid lines
		chart.getXAxis().setDrawGridLines(false);
		chart.setDrawGridBackground(false);

		// Disables the legend
		chart.getLegend().setEnabled(false);

		// Make the X axis thicker
		chart.getXAxis().setAxisLineWidth(3F);

		// Place the X axis at the bottom of the screen
		chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

		// Disables highlighting of the bars
		chart.setHighlightPerDragEnabled(false);
		chart.setHighlightPerTapEnabled(false);

		// Prevents first and last bars from being "clipped" off the screen
		chart.getXAxis().setAvoidFirstLastClipping(true);

		// Sets the colour for the Y axis grid lines
		chart.getAxisLeft().setGridColor(Color.rgb(104, 241, 175));

		// Formats the times text on the Y axis
		chart.getAxisLeft().setValueFormatter(new YAxisValueFormatter()
		{
			@Override
			public String getFormattedValue(float time, YAxis yAxis)
			{
				return formatTimeFromSeconds(time);
			}
		});

		// Retrieves the times from the database
		List<StopwatchTime> times = dbHandler.allStopwatchTimes(dbHandler.getCategory(getIntent().getIntExtra("stopwatchCategoryID", 0)));

		// Adds all times to the "entries" variable
		for (int i = 0; i < times.size(); i++)
		{
			entries.add(new BarEntry(times.get(i).getTime(), i));

			// Formats the dates to be displayed on the X axis
			dateAxisLabels.add(new SimpleDateFormat("dd/MM/yy").format(times.get(i).getRecordDate() * 1000L));
		}

		BarDataSet dataSet = new BarDataSet(entries, "");

		// Sets the bar colour and the text size for the time displayed at top
		dataSet.setColor(Color.rgb(242, 247, 158));
		dataSet.setValueTextSize(13F);

		// Formats the time for each bar
		dataSet.setValueFormatter(new ValueFormatter()
		{
			@Override
			public String getFormattedValue(float time, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler)
			{
				return formatTimeFromSeconds(time);
			}
		});

		// Puts retried data into the BarChart
		chart.setData(new BarData(dateAxisLabels, dataSet));

		// Refreshes the chart
		chart.invalidate();
	}

	private String formatTimeFromSeconds(float time)
	{
		int minutes = (int) time / 60;
		int seconds = (int) time % 60;

		String timeString = "";

		// Prepends leading 0 to minutes if needed
		if (minutes < 10)
		{
			timeString += "0";
		}
		timeString += minutes;

		timeString += ":";

		// Prepends leading 0 to seconds if needed
		if (seconds < 10)
		{
			timeString += "0";
		}
		timeString += seconds;

		return timeString;
	}
}
