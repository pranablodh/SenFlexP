package com.orela.senflexp.plotting;

import android.graphics.Color;
import androidx.annotation.ColorInt;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class mpPlotClass
{
    public static void initializeChart(LineChart chart, @ColorInt int colorId)
    {
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(false);
        chart.setBackgroundColor(colorId);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLUE);
        chart.setData(data);

        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.RED);

        XAxis xl = chart.getXAxis();
        xl.setDrawGridLines(true);
        xl.setEnabled(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setEnabled(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        chart.getAxisLeft().setDrawGridLines(true);
        chart.getXAxis().setDrawGridLines(true);
        chart.setDrawBorders(true);
    }

    public static LineDataSet createSet(String legend, @ColorInt int colorId)
    {
        LineDataSet set = new LineDataSet(null, legend);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3.0f);
        set.setColor(colorId);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setCubicIntensity(1.0f);
        return set;
    }

    public static void addEntry(float value, LineChart chart, int range, String legend, @ColorInt int colorId, Boolean autoRange)
    {
        LineData data = chart.getData();

        if (data != null)
        {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null)
            {
                set = createSet(legend, colorId);
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), value), 0);
            data.notifyDataChanged();
            chart.notifyDataSetChanged();
            if(autoRange)
            {
                chart.setVisibleXRangeMaximum(range);
            }
            chart.moveViewToX(data.getEntryCount());
        }
    }
}
