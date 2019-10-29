package com.example.kangmoon.sentimentdiary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.snu.ids.ha.ma.MorphemeAnalyzer;
import org.snu.ids.ha.ma.MExpression;
import org.snu.ids.ha.ma.Sentence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import SyntaxAnalysis.Preprocessing;
import SyntaxAnalysis.TreeNode;
import SyntaxAnalysis.SentenceStructure;
import SentimentAnalysis.SentimentTree;

public class WriteDiary extends AppCompatActivity {

    boolean IsDiaryHave;
    boolean IsAnalysisHave;
    String yyyymmdd;

    EditText DiaryContent;
    Button SaveContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_diary);

        DiaryContent = (EditText) findViewById(R.id.DiaryContent);
        SaveContent = (Button) findViewById(R.id.SaveContent);

        setVariable();

        if(IsDiaryHave) {
            readDiaryFile();
        }

        SaveContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeDiaryFile();
                finish();
            }
        });
    }

    // Main Activity에서 변수 불러오기
    public void setVariable() {
        IsDiaryHave = getIntent().getBooleanExtra("IsDiaryHave", false);
        IsAnalysisHave = getIntent().getBooleanExtra("IsAnalysisHave", false);
        yyyymmdd = getIntent().getStringExtra("yyyymmdd");
    }

    // 저장된 일기 데이터 출력
    public void readDiaryFile() {
        String FileName = "Diary_" + yyyymmdd;
        File FileP = new File(getFilesDir(), FileName);
        FileReader FileR = null;
        BufferedReader BufferR = null;
        String Line;
        String Content = "";

        try {
            FileR = new FileReader(FileP);
            BufferR = new BufferedReader(FileR);

            while (!(Line = BufferR.readLine()).isEmpty())
                Content = Content + Line + "\r\n";

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

        DiaryContent.setText(Content);
    }

    // 작성된 일기 데이터 저장
    public void writeDiaryFile() {
        String FileName = "Diary_" + yyyymmdd;
        File FileP = new File(getFilesDir(), FileName);
        String Content = DiaryContent.getText().toString();

        if (!Content.isEmpty()) {
            FileWriter FileW = null;
            BufferedWriter BufferW = null;

            try {
                FileW = new FileWriter(FileP);
                BufferW = new BufferedWriter(FileW);

                BufferW.write(Content);
                BufferW.newLine();

                BufferW.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (BufferW != null)
                    BufferW.close();

                if (FileW != null)
                    FileW.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String[] Line = Content.split("\r\n");
            Content = "";

            for (int i = 0; i < Line.length; i++) {
                if (Line[i].charAt(Line[i].length()-1) != '.')
                    Line[i] = Line[i] + '.';
                Content = Content + Line[i] + "\r\n";
            }

            writeAnalysisFile(Content);
        } else {
            FileP.delete();
            FileName = "Analysis_" + yyyymmdd;
            File FileA = new File(getFilesDir(), FileName);
            FileA.delete();
        }
    }

    // 분석 결과 파일로 저장
    public void writeAnalysisFile(String string) {
        double Result;
        Result = doSentimentAnalysis(string);

        String FileName = "Analysis_" + yyyymmdd;
        File FileP = new File(getFilesDir(), FileName);

        if (Result > 1 && Result < 7) {
            FileWriter FileW = null;
            BufferedWriter BufferW = null;
            String Content = String.valueOf(Result);

            try {
                FileW = new FileWriter(FileP);
                BufferW = new BufferedWriter(FileW);

                BufferW.write(Content);
                BufferW.newLine();

                BufferW.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (BufferW != null)
                    BufferW.close();

                if (FileW != null)
                    FileW.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            FileP.delete();
        }
    }

    // 일기 내용 감성분석
    public double doSentimentAnalysis(String string) {
        try {
            MorphemeAnalyzer ma = new MorphemeAnalyzer();
            Preprocessing Preprocess = new Preprocessing();
            SentenceStructure ParseTree = new SentenceStructure();
            SentimentTree SentimentTree = new SentimentTree();

            double Total = 0;

            List<MExpression> ret = ma.analyze(string);
            ret = ma.postProcess(ret);
            ret = ma.leaveJustBest(ret);

            List<List<String[]>> AllText = new ArrayList<List<String[]>>();
            List<TreeNode> TreeList = new ArrayList<TreeNode>();

            List<Sentence> stl = ma.divideToSentences(ret);
            for( int i = 0; i < stl.size(); i++ ) {
                Sentence st = stl.get(i);
                AllText.add(Preprocess.doPreprocess(st));
            }

            for(int i = 0; i < AllText.size(); i++) {
                for(int j = 0; j < AllText.get(i).size(); j++) {
                    System.out.print(Arrays.toString(AllText.get(i).get(j)));
                }
                TreeList.add(ParseTree.createParseTree(AllText.get(i)));
            }

            System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
            Total = SentimentTree.createSentmentTree(TreeList);
            System.out.println("전체 문장의 감정 수치 : " + Total);

            if (Total > 1 && Total < 7)
                return Total;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
