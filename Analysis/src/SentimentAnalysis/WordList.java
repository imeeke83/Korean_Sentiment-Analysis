package SentimentAnalysis;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class WordList {
	
	public HashMap<String,Double> makeWordList() {
		HashMap<String, Double> WordList = new HashMap<String, Double>();
		
		try{
            //파일 객체 생성
			String path = WordList.class.getResource("").getPath();
			File file = new File(path+"SentimentWord.txt");
            
            // BufferedReader 변수에 file을 넣는다
            BufferedReader reader = new BufferedReader(new FileReader(file));
            
            // 파일을 한줄씩 읽어 넣기 위한 변수 line
            String line = null;
            
            // 하나의 line을 split 하여 넣을 배열 splitedStr
            String[] splitedStr = null;
 
            // 한 줄씩 읽어서 line에 넣은 후 null이 아니면 실행
            while( (line = reader.readLine()) != null ) { 
                // 탭을 기준으로 잘라서 splitedStr 에 넣는다
                splitedStr = line.split("\t");
                
                WordList.put(splitedStr[0], Double.parseDouble(splitedStr[1]));
            }
            reader.close();
 
        } catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
        } catch( IOException e) {
            e.printStackTrace();
        }
		
		return WordList;
	}
}