<img src="https://img.shields.io/badge/Tech-Sentiment_Analysis-blue"></img>
<img src="https://img.shields.io/badge/Tech-Syntax_Analysis-blue"></img>
<img src="https://img.shields.io/badge/Tech-NLP-blue"></img>

# Korean_Sentiment-Analysis
한국어 구문 분석을 통한 사용자 감성 분석 어플리케이션  
(User sentiment analysis application through the Korean syntax analysis)

+ 사용자로부터 입력받은 일기 데이터를 구문 분석 과정과 감성 분석 과정을 통해 분석하여, 사용자에게 일기 데이터의 긍·부정 감성 수치를 보여주는 어플리케이션.
+ 구문 분석 과정에서는 문장을 구성하는 형태소들을 정립한 한국어 문형 구조에 맞게 구문 트리를 생성합니다. 감성 분석 과정에서는 구문 분석 과정을 통해 생성된 구문 트리와 감성 사전에 등록된 감성 단어의 감성 수치를 활용하여 감성 수치를 연산합니다.

## 실행 결과
<img width="250" src="https://user-images.githubusercontent.com/40970516/67744503-068c2700-fa65-11e9-97da-d28a0f7e326e.png"></img>
<img width="250" src="https://user-images.githubusercontent.com/40970516/67744494-ff651900-fa64-11e9-981b-602b276593f2.png"></img>

## 정보
### 제작
고강문 (imeeke83@gmail.com)

### 배포 및 저작권
+ 본 어플리케이션은 GPL 2.0을 따르는 꼬꼬마 형태소 분석기를 따라 GPL 2.0을 준수하며 LICENSE에서 자세한 정보를 확인할 수 있습니다.
+ GPL 2.0을 따르지 않고 상업적 이용 등을 하고자 할 때는 별도의 협의가 필요합니다.

### 참고 문헌
+ 꼬꼬마 형태소 분석기
  - 이동주, 연종흠, 황인범, 이상구, "꼬꼬마: 관계형 데이터베이스를 활용한 세종 말뭉치 활용 도구", 2010, 정보과학회논문지: 컴퓨팅의 실제 및 레터 (Journal of KIISE: Computing Practices and Letters), Volume 16, No.11, Page 1046-1050
+ 한국어 문형 구조
  - 강은국, “조선어 문형 연구”, 도서출판 박이정, 1993, 308쪽
+ 감성 사전
  - 박인조, 민경환, "한국어 감정단어의 목록 작성과 차원 탐색", 한국심리학회지: 사회 및 성격 19권1호, 2005. 02, 109-129쪽
