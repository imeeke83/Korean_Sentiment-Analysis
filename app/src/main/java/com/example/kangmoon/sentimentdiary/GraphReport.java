package com.example.kangmoon.sentimentdiary;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class GraphReport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_report);
        ArrayList<String[]> Result = new ArrayList<>();

        char Report = getIntent().getCharExtra("Report", 'X');

        String FromDay = "";
        String ToDay = "";

        switch (Report) {
            case 'W' :
                FromDay = getDaysAgo('W', -7);
                ToDay = getToday();
                break;
            case 'M':
                FromDay = getDaysAgo('M', -1);
                ToDay = getToday();
                break;
        }

        Result = makeSentimentList(FromDay, ToDay);
        makeGraph(Result);
    }

    // 오늘 날짜 구하기
    public String getToday() {
        TimeZone Zone = TimeZone.getTimeZone("Asia/Seoul");
        Calendar Date = Calendar.getInstance(Zone);

        java.util.Date Today = Date.getTime();
        SimpleDateFormat Formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return Formatter.format(Today);
    }

    // 특정 일자 전 날짜 구하기
    public String getDaysAgo(char WM, int AgoDays) {
        TimeZone Zone = TimeZone.getTimeZone("Asia/Seoul");
        Calendar Date = Calendar.getInstance(Zone);

        switch (WM) {
            case 'W' :
                Date.add(Calendar.DATE, AgoDays);
                break;
            case 'M':
                Date.add(Calendar.MONTH, AgoDays);
                break;
        }

        java.util.Date AgoDate = Date.getTime();
        SimpleDateFormat Formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return Formatter.format(AgoDate);
    }

    // String to Calendar
    public Calendar stringToCalendar(String Day) {
        // Calendar 객체를 String으로 변경
        Calendar Date = Calendar.getInstance();

        try {
            SimpleDateFormat Formatter = new SimpleDateFormat("yyyyMMdd");
            Date.setTime(Formatter.parse(Day));
        } catch(Exception e) {
            e.printStackTrace();
        }

        return Date;
    }

    // Calendar to String
    public String calendarToString(Calendar cal) {
        // 날짜 String을 Calendar 객체로 변경
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(cal.getTime());
    }

    // 특정 일자 사이의 감정 수치 값 합산
    public ArrayList<String[]> makeSentimentList(String FromDay, String ToDay) {
        ArrayList<String[]> Result = new ArrayList<>();

        Calendar FromDate = stringToCalendar(FromDay);
        Calendar ToDate = stringToCalendar(ToDay);

        while (FromDate.compareTo(ToDate) != 1) {
            String[] DayResult = new String[2];
            String Date = calendarToString(FromDate);
            Date = Date.replace("-", "");

            String FileName = "Analysis_" + Date + ".txt";
            File FileP = new File(getFilesDir(), FileName);

            if (FileP.exists()){
                FileReader FileR = null;
                BufferedReader BufferR = null;
                String Sentiment = "";

                try {
                    FileR = new FileReader(FileP);
                    BufferR = new BufferedReader(FileR);

                    Sentiment = BufferR.readLine();

                    if (Double.valueOf(Sentiment) > 1 && Double.valueOf(Sentiment) < 7) {
                        DayResult[0] = Sentiment;                        DayResult[1] = Date;
                        Result.add(DayResult);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (BufferR != null)
                        BufferR.close();

                    if (FileR != null)
                        FileR.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            FromDate.add(Calendar.DATE, 1);
        }

        return Result;
    }

    public void makeGraph(ArrayList<String[]> Result) {
        LineChart lineChart = (LineChart) findViewById(R.id.chart);

        ArrayList<Entry> Entries = new ArrayList<>();
        ArrayList<String> Labels = new ArrayList<>();

        for (int i = 0; i < Result.size(); i++) {
            Entries.add(new Entry(i, Float.valueOf(Result.get(i)[0])));
            Labels.add(Result.get(i)[1]);
        }

        LineDataSet Dataset = new LineDataSet(Entries, "Sentiment Score");
        Dataset.setLineWidth(2);
        Dataset.setCircleRadius(6);
        Dataset.setCircleColor(Color.parseColor("#FFA1B4DC"));
        Dataset.setCircleHoleColor(Color.BLUE);
        Dataset.setColor(Color.parseColor("#FFA1B4DC"));
        Dataset.setDrawCircleHole(true);
        Dataset.setDrawCircles(true);
        Dataset.setDrawHorizontalHighlightIndicator(false);
        Dataset.setDrawHighlightIndicators(false);
        Dataset.setDrawValues(true);

        LineData Data = new LineData(Dataset);
        lineChart.setData(Data);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);
        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

    }
}
