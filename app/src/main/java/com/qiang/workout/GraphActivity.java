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

		// Customise the bar chart
		chart.setDescription("");
		chart.setNoDataTextDescription("");
		chart.setNoDataText("");
		chart.setPinchZoom(false);
		chart.setDoubleTapToZoomEnabled(false);
		chart.getAxisRight().setEnabled(false);
		chart.getXAxis().setDrawGridLines(false);
		chart.setDrawGridBackground(false);
		chart.getLegend().setEnabled(false);
		chart.getXAxis().setAxisLineWidth(3F);
		chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
		chart.getAxisLeft().setTextSize(13F);
		chart.getXAxis().setTextSize(13F);
		chart.setHighlightPerDragEnabled(false);
		chart.setHighlightPerTapEnabled(false);
		chart.getXAxis().setAvoidFirstLastClipping(true);
		chart.getAxisLeft().setDrawAxisLine(false);
		chart.getAxisLeft().setGridColor(Color.rgb(104, 241, 175));

		chart.getAxisLeft().setValueFormatter(new YAxisValueFormatter()
		{
			@Override
			public String getFormattedValue(float time, YAxis yAxis)
			{
				return formatTimeFromSeconds(time);
			}
		});

		List<StopwatchTime> times = dbHandler.allStopwatchTimes(dbHandler.getCategory(getIntent().getIntExtra("stopwatchCategoryID", 0)));

		for (int i = 0; i < times.size(); i++)
		{
			entries.add(new BarEntry(times.get(i).getTime(), i));
			dateAxisLabels.add(new SimpleDateFormat("dd/MM/yy").format(times.get(i).getRecordDate() * 1000L));
		}

		BarDataSet dataSet = new BarDataSet(entries, "");
		dataSet.setColor(Color.rgb(242, 247, 158));
		dataSet.setValueTextSize(13F);

		dataSet.setValueFormatter(new ValueFormatter()
		{
			@Override
			public String getFormattedValue(float time, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler)
			{
				return formatTimeFromSeconds(time);
			}
		});

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
