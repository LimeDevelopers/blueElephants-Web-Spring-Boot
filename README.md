# 🌲🌲 푸른 코끼리 🌲🌲 (Spring Boot)

# 🔥🔥🔥 ISSUE NOTICE 🔥🔥🔥
## 1. QueryDSL not find Error (21.08.05) 수정
### Project Clone 시 QueryDSL 모듈 설정해줘야 함.
### 해결 방안
먼저 아래와 같이 Intellij의 File-Project Structure 메뉴를 보면 다음과 같이 'Source Folders'에 java만 있는 것을 확인해볼 수 있습니다.
![images1](https://github.com/momentjin/study/raw/master/resource/image/querydsl%EC%9D%B8%EC%8B%9D%EB%AC%B8%EC%A0%9C1.png?raw=true)

### 결과

다음과 같이 'source-generated' 폴더를 Sources로 설정하면 IDE가 해당 폴더를 Source로 인식해서 더 이상 오류를 보여주지 않습니다.

![img2](https://github.com/momentjin/study/raw/master/resource/image/querydsl%EC%9D%B8%EC%8B%9D%EB%AC%B8%EC%A0%9C2.png?raw=true)

# ⭐️⭐️⭐️ NOTICE ⭐️⭐️⭐️
### 소스코드 대폭 수정 (21.08.05)
### 기존 인계받은 원본 소스에 문제가 많아 New Project에 릴리즈 업데이트 했습니다.
