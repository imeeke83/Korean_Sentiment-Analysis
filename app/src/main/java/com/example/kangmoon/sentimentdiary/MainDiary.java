package com.example.kangmoon.sentimentdiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Calendar;

public class MainDiary extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean IsDiaryHave = false;
    boolean IsAnalysisHave = false;
    String yyyymmdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_diary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 달력 객체
        final DatePicker DayCalendar = (DatePicker) findViewById(R.id.dayCalendar);

        setInit(DayCalendar);

        // 달력의 특정 날짜 선택 시
        DayCalendar.init(DayCalendar.getYear(), DayCalendar.getMonth(), DayCalendar.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int Year, int Month, int Date) {
                String ToDay = Year+ "-" + (Month+1) + "-" + Date;
                TextView WhatItDay = (TextView) findViewById(R.id.WhatItDay);
                WhatItDay.setText(ToDay);

                IsAnalysisHave = checkIsAnalysisFileIs(Year, Month, Date);
                if (IsAnalysisHave) {
                    readAnalysisFile();
                } else {
                    showAnalysisData("X");
                }
                IsDiaryHave = checkDiaryFileIs(Year, Month, Date);
                if(IsDiaryHave) {
                    Snackbar.make(view, "Diary Exist", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Diary Not Exist", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

        // 일기 수정 및 작성 버튼 터치 시
        FloatingActionButton EditDiary = (FloatingActionButton) findViewById(R.id.EditDiary);
        EditDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WriteDiary.class);
                intent.putExtra("IsDiaryHave", IsDiaryHave);
                intent.putExtra("IsAnalysisHave", IsAnalysisHave);
                intent.putExtra("yyyymmdd", yyyymmdd);
                startActivity(intent);

                IsAnalysisHave = checkIsAnalysisFileIs(yyyymmdd);
                if (IsAnalysisHave) {
                    readAnalysisFile();
                } else {
                    showAnalysisData("X");
                }
                IsDiaryHave = checkDiaryFileIs(yyyymmdd);
                if(IsDiaryHave) {
                    Snackbar.make(view, "Diary Exist", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Diary Not Exist", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // 초기 화면 설정
    public void setInit(DatePicker DayCalendar) {
        IsDiaryHave = checkDiaryFileIs(DayCalendar.getYear(), DayCalendar.getMonth(), DayCalendar.getDayOfMonth());

        String ToDay = DayCalendar.getYear()+ "-" + (DayCalendar.getMonth()+1) + "-" + DayCalendar.getDayOfMonth();
        TextView WhatItDay = (TextView) findViewById(R.id.WhatItDay);
        WhatItDay.setText(ToDay);

        IsDiaryHave = checkDiaryFileIs(yyyymmdd);
        if(IsDiaryHave) {
            Snackbar.make(DayCalendar, "Diary Exist", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            Snackbar.make(DayCalendar, "Diary Not Exist", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        IsAnalysisHave = checkIsAnalysisFileIs(yyyymmdd);
        if (IsAnalysisHave) {
            readAnalysisFile();
        } else {
            showAnalysisData("X");
        }
    }

    // 일기 파일명 생성
    public void makeFileName(int Year, int Month, int Date) {
        yyyymmdd = Year+""+ (Month+1) +""+Date+".txt";
    }

    // 날짜에 해당하는 일기 파일 존재 여부 확인
    public boolean checkDiaryFileIs(int Year, int Month, int Date) {
        makeFileName(Year, Month, Date);
        String FileName = "Diary_" + yyyymmdd;

        File File = new File(getFilesDir(), FileName);
        return File.exists();
    }

    // 날짜에 해당하는 일기 파일 존재 여부 확인
    public boolean checkDiaryFileIs(String yyyymmdd) {
        String FileName = "Diary_" + yyyymmdd;

        File File = new File(getFilesDir(), FileName);
        return File.exists();
    }

    // 날짜에 해당하는 분석 파일 존재 여부 확인
    public boolean checkIsAnalysisFileIs(int Year, int Month, int Date) {
        makeFileName(Year, Month, Date);
        String FileName = "Analysis_" + yyyymmdd;

        File File = new File(getFilesDir(), FileName);
        return File.exists();
    }

    // 날짜에 해당하는 분석 파일 존재 여부 확인
    public boolean checkIsAnalysisFileIs(String yyyymmdd) {
        String FileName = "Analysis_" + yyyymmdd;

        File File = new File(getFilesDir(), FileName);
        return File.exists();
    }

    // 분석 데이터 출력
    public void showAnalysisData(String Sentiment) {
        TextView DaySentiment = (TextView) findViewById(R.id.DaySentiment);
        DaySentiment.setText(Sentiment);
    }

    // 저장된 분석 데이터 읽어오기
    public void readAnalysisFile() {
        String FileName = "Analysis_" + yyyymmdd;
        File FileP = new File(getFilesDir(), FileName);
        FileReader FileR = null;
        BufferedReader BufferR = null;
        String Sentiment = "";

        try {
            FileR = new FileReader(FileP);
            BufferR = new BufferedReader(FileR);

            Sentiment = BufferR.readLine();

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

        showAnalysisData(Sentiment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_diary, menu);
        return true;
    }

    // 메뉴 선택
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // 네비게이션 뷰 선택
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = new Intent(getApplicationContext(), GraphReport.class);

        if (id == R.id.WeekReport) {
            intent.putExtra("Report", 'W');
            startActivity(intent);
        } else if (id == R.id.MonthReport) {
            intent.putExtra("Report", 'M');
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
