<div align="center">

![Image](https://github.com/user-attachments/assets/deee05b6-047a-4141-9b80-27fcb867bfa6)
</div>

<h1> 🎮 Playcation: 게임 판매 플랫폼</h1>

## 💡 화면 구성
|                                                  메인 #1                                                   |                                                  로그인 #2                                                   |
|:--------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------:|
| <img src="https://github.com/user-attachments/assets/b2c0441c-ef3d-40ec-b41a-3ce16e92aa5e" width="400" > | <img src="https://github.com/user-attachments/assets/5bb2df5c-7604-44de-bb70-7730dfdd8a07" width="400" /> |

|                                                장바구니 #3                                                 |                                                 쿠폰 #4                                                 |
|:------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------:|
| <img src="https://github.com/user-attachments/assets/eb823a25-d758-4b08-82db-3911db8fa204" width="400"> | <img src="https://github.com/user-attachments/assets/9bec9e2c-efad-4442-8bf7-6d35d772c6a9" width="400"> |

<br/>

#### 🎮 주제 선정 배경 및 기획의도


```
저희 팀은 팀원 모두가 게임에 관심이 많았기에 게임과 관련된 프로젝트를 진행하기를 원하였고
게이머들과 게임을 연결해주는 게임 판매 플랫폼을 만들기로 결정하였습니다
```


<br/>

#### 🎮 한 줄 소개
```
“최고의 게임들을 한 번의 클릭으로 – 당신의 새로운 게이밍 여정을 여는 플랫폼.”
```
<details><summary> 더보기
</summary>

```
당신의 주말은 어떤 의미를 가지고 있나요?  
바쁜 일상 속 멈춰야 할 순간, 삶의 균형을 찾아야 할 때, 우리는 새로운 세계로의 초대장을 건넵니다.  
저희 플랫폼은 단순한 게임 판매를 넘어, 몰입과 여유의 가치를 제공합니다.  
매혹적인 서사와 치밀한 전략, 감각적인 미학이 조화를 이루는 게임들로 가득 찬 이 공간에서 당신은 일상에서 벗어나 자신만의 새로운 이야기를 써내려갈 수 있습니다.  
  
지루함과 반복에서 벗어나고 싶다면,  
고요함 속에 스며든 설렘을 찾고 싶다면,  
  
지금 이 순간, 클릭 한 번으로 무한한 가능성을 열어보세요.  
우리가 제공하는 것은 단지 상품이 아닙니다.  
당신의 주말을 한층 더 깊고, 넓고, 풍요롭게 채워줄 경험입니다.  
함께 떠나볼까요? 쉼과 게임, 그 경이로운 교차점으로.  
```
</details>


<br/>

## 📌 목차

1. [**팀원 소개**](#-팀원-소개)
2. [**개발 기간**](#-개발-일정)
3. [**기술 스택**](#-기술-스택)
4. [**프로젝트 구조**](#-프로젝트-구조)
5. [**프로젝트 아키텍처**](#-프로젝트-아키텍처)
6. [**아키텍처 설계도**](#-아키텍쳐-설계도)
7. [**핵심 기능**](#-핵심-기능)
8. [**Key Summary**](#key-summary)
9. [**기술적 의사 결정**](#기술적-의사-결정)
10. [**트러블 슈팅**](#트러블-슈팅)
11. [**소감**](#소감)

<br />



<br />

## 💁🏻‍♂ 팀원 소개
|                                            이서준                                            |                                            김민                                             |                                            강준혁                                            |                                            진하빈                                            |                                            하진이                                            |
|:-----------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------:|
| ![Image](https://github.com/user-attachments/assets/3450a7d4-42cc-4329-b5cf-c2ca8c82127f) | ![Image](https://github.com/user-attachments/assets/eed84f54-5a47-47cf-a105-b8867e37c527) | ![Image](https://github.com/user-attachments/assets/41d06a4e-10ce-474f-906e-1f64e3d252e7) | ![Image](https://github.com/user-attachments/assets/71dd3054-af8b-4451-8af2-67fa3d2d827c) | ![Image](https://github.com/user-attachments/assets/d17b5f68-50b5-4464-a4c2-47c4f62de187) |
|              회원 CRUD <br> 스프링 시큐리티 <br> (소셜로그인) <br> S3 첨부파일 구현 <br> 프론트 구현               |                          주문 CRUD <br> 배치 작업 <br> 프론트 구현 <br> 배포                           |                         게임,라이브러리, <br> 태그,카테고리 CRUD <br> 결제 기능 구현                         |                          장바구니 CRUD <br> 선착순 쿠폰 발급 구현 <br> 프론트 구현                          |             리뷰 CRUD <br> 리뷰 좋아요 구현 <br> 결제 확인 이메일 <br>  이메일 인증코드 <br> 프론트 구현              |
|                          [GitHub](https://github.com/K-da-fire)                           |                            [GitHub](https://github.com/KM4HS)                             |                         [GitHub](https://github.com/kangjunhyuk1)                         |                            [GitHub](https://github.com/JHabin)                            |                            [GitHub](https://github.com/gxnie)                             |


<br/>

## 📅  개발 일정
**개발 기간** : 2025.01.02 ~ 2025.02.07

<br/>

## 🛠 기술 스택

- **IDE** : IntelliJ
- **JDK** : openjdk version '17'
- **Framework** : springboot version '3.4.1', Spring Data JPA, Spring Security, Spring Batch
- **Library** : QueryDSL, OAuth2, Java Mail Sender, SSE, JWT, Redisson
- **Build Tool** : Gradle version '8.11.1', Docker
- **Database** : MySQL version '8.0.40', Redis
- **Infra** : AWS EC2, Amazon S3, AWS RDS (MySQL) AWS ElastiCache for Redis, Docker, GitHub Actions
- **Tool** : ERD Cloud, Github & git, Postman, Notion

<br/>

## 📁 프로젝트 구조

<details>
<summary><h3> 📂 백엔드 : 핵심 및 공통 기능 폴더 구조</h3></summary>

```
📦Playcation
 ┣ 📂src
 ┃ ┣ 📂main
 ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┗ 📂playcation
 ┃ ┃ ┃    ┣ 📂batch         // 배치
 ┃ ┃ ┃    ┣ 📂common        // BaseEntity, 페이징Dto, 토큰 세팅
 ┃ ┃ ┃    ┣ 📂config        // 애플리케이션 설정
 ┃ ┃ ┃    ┣ 📂coupon        // 선착순 쿠폰 발급 관련 기능
 ┃ ┃ ┃    ┣ 📂emailsender   // 결제 완료 이메일 및 이메일 인증 코드 전송
 ┃ ┃ ┃    ┣ 📂exception     // 커스텀 예외 클래스 및 예외 처리 로직
 ┃ ┃ ┃    ┣ 📂filter        // 로그인/로그아웃/JWT 필터
 ┃ ┃ ┃    ┣ 📂gametag       // 게임 태그
 ┃ ┃ ┃    ┣ 📂library       // 사용자의 게임 라이브러리
 ┃ ┃ ┃    ┣ 📂notification  // 실시간 알림 (SSE, Redis Pub/Sub)
 ┃ ┃ ┃    ┣ 📂oauth2        // 소셜 로그인 및 외부 인증 관련 기능
 ┃ ┃ ┃    ┣ 📂order         // 결제
 ┃ ┃ ┃    ┣ 📂review        // 게임 리뷰
 ┃ ┃ ┃    ┣ 📂scheduler     // 스케줄러
 ┃ ┃ ┃    ┣ 📂tag           // 콘텐츠 및 기타 태그 관리
 ┃ ┃ ┃    ┣ 📂token         // JWT 등 토큰 생성 및 관리 기능
 ┃ ┃ ┃    ┣ 📂toss          // Toss 결제
 ┃ ┃ ┃    ┣ 📂user          // 사용자 프로필 및 관리
 ┃ ┃ ┃    ┣ 📂util          // 기타 공통 유틸리티 클래스
 ┃ ┃ ┃    ┗ 📂...           // 기타 등등 도메인
 ┃ ┗ 📂resources             
 ┣ 📜Dockerfile   
 ┣ 📜build.gradle    
 ┗ 📜README.md     
```

</details>

<details>
<summary><h3> 📂 프론트엔드 : 핵심 및 공통 기능 폴더 구조</h3></summary>

``` 
추가예정입니다
```

</details>

<br/>


## 🗂️ 프로젝트 아키텍처

<details>
<summary><h3>✨ 와이어프레임 </h3></summary>

#### [ **와이어프레임 바로보기** ](https://www.figma.com/board/pl0yhzHetLU7Q51OL2m0aO/%EC%A3%BC%EB%A7%90%EC%97%90%EB%8A%94-%EC%89%AC%EC%A1%B0?node-id=0-1&t=viyzAdVSiAy3Pl1B-1)
</details>


<details>
<summary><h3>✨ ERD </h3></summary>

#### [  **ERD 바로보기** ](https://www.erdcloud.com/d/4uq8PnfaCAD9D3ggh)

</details>


<details>
<summary><h3> ✨ API 명세서 </h3></summary>

#### [  **API명세서 바로보기**  ](https://file.notion.so/f/f/83c75a39-3aba-4ba4-a792-7aefe4b07895/7907c98f-bacb-4e6e-a86a-976fcdd2a454/Playcation_API_%EB%AA%85%EC%84%B8%EC%84%9C.pdf?table=block&id=93320060-8678-4cbf-9ce1-ea6c1d6e1c30&spaceId=83c75a39-3aba-4ba4-a792-7aefe4b07895&expirationTimestamp=1739066400000&signature=lycOJ5LlXcxegtOB0-Gjh7KMaaR2PSVKT-OfhigvD24&downloadName=Playcation_API+%EB%AA%85%EC%84%B8%EC%84%9C.pdf)
</details>


<br/>


## 🛠️ 아키텍쳐 설계도
![Image](https://github.com/user-attachments/assets/dbfe1a00-5ed8-47a9-aae3-54f7558c71d8)



<br/>

## 🔔 핵심 기능

### 🌟 선착순 쿠폰 발급
- 쿠폰 재고 및 사용자 대기열 동시성 처리

### 🌟 VIP 혜택
- 매 월 1일 마다 구매 기록을 통해 VIP 등급 갱신
- 등급에 따른 포인트 지급

### 🌟 결제
- 장바구니에 있는 게임을 결제

### 🌟 실시간 알림 / 이메일
- 게임 구매시 결제 완료 이메일
- 이메일 인증 코드 발송
- 게임 리뷰 생성시 게임 등록자에게 실시간 알림

<br />

## 🔒 Key Summary

<details>
<summary><h3> 🔑 조회 인덱싱 </h3></summary>

- 게임 검색을 할 때, 단순 조회 쿼리로 진행을 하면 **1.9s가 발생하는 문제**가 있었습니다.
- title에 인덱싱을 통하여 문제를 해결하였습니다. **( 검색시간 835ms 약 56% 개선 )**
- 하지만 여전히 느린 속도 ( 835ms )이므로 추가적인 개선을 해야함.
</details>


<details>
<summary><h3> 🔑 분산락과 Atomic 성능차이 </h3></summary>

- **데이터 정합성 측면**
    - Atomic으로 진행할 때 소규모의 동시성 제어 테스트 결과 97% 성공률을 보여 일부 요청이 제대로 처리되지 않음을 확인하였습니다.
    - DistributedLock(분산락)을 적용하여 소규모 동시성 제어 시 99-100%의 성공률로 개선할 수 있었습니다.
- **속도 측면**
    - Atomic 처리 시간은 8006ms 이었으나 분산 락 처리 시 약 2배 이상의 시간이 소요되었습니다.

**쿠폰 감소**는 **빠르게 처리**해야 하므로 Atomic 연산을 사용하여 성능을 극대화하고, **사용자 대기열**은 **데이터 일관성**이 중요하므로 분산락을 활용하여 정합성을 유지하도록 함께 사용하였습니다.
</details>


<br />
<br />

## 📚 기술적 의사 결정

<details>
<summary><h3> 📕 실시간 알림 Redis </h3></summary>

### 도입 배경

---
구현이 간단하고, 설정과 유지 관리가 비교적 용이
Pub/Sub 모델을 기본적으로 지원하므로, 이를 통해 다수의 구독자에게 실시간 알림을 전파할 수 있음

### 선택지

---

|     | Redis                                                      | Kafka                                                                                    | RabbitMQ                                                    |
|:---:|:-----------------------------------------------------------|:-----------------------------------------------------------------------------------------|:------------------------------------------------------------|
| 설명  | 인메모리 데이터 저장소로, Pub/Sub 모델을 활용한 실시간 메시징 지원   | 분산 스트리밍 플랫폼으로, 대량 데이터를 고속 처리 및 로그 기록 지원                                                  | AMQP 기반 오픈소스 메시지 브로커로, 큐에 저장 후 비동기 소비 지원                    |
| 장점  | 빠른 성능 및 낮은 지연 시간 <br> 실시간 메시징/알림 시스템에 적합 <br> 설정 및 구현이 간단함 | 대규모 분산 시스템에서 고속 스트리밍 및 처리 <br> 내구성(디스크 저장), 확장성, 높은 가용성 <br> 메시지 순서 보장 및 다수의 프로듀서/컨슈머 지원 | 신뢰성 높은 메시징 및 메시지 내구성 보장 <br> 고급 라우팅 및 다양한 패턴 지원- 여러 프로토콜 지원 |
| 단점  | 서버 재시작 시 데이터 손실 가능 <br> 대규모 데이터 처리에 부적합    | 설정 및 관리 복잡- 높은 리소스 요구 <br> 소규모 시스템에서는 오버헤드 발생 가능                                         | 성능 제한적, 대규모 시스템에서 리소스 소모 큼 <br> 설정 및 관리 복잡                  |

### 최종 결정

---
실시간 알림 시스템에서 빠르고 간단한 구현을 하기 위해 Redis를 사용
</details>


<details>
<summary><h3> 📗 실시간 알림 SSE </h3></summary>

### 도입 배경

---
한 게임에 리뷰 생성 시, 게임 등록자에게 실시간 알림 발송
- 실시간 알림을 클라이언트에 빠르고 안정적으로 전달
- 새로운 알림이 발생할 때, 즉시 알림을 받을 수 있어야함

### 선택지

---
|     | Polling                                                       | WebSocket                                      | SSE                                                              |
|:---:|:--------------------------------------------------------------|:-----------------------------------------------|:-----------------------------------------------------------------|
| 설명  | 클라이언트가 서버에 주기적으로 요청을 보내서 <br> 새로운 데이터를 확인하는 방식                | 클라이언트와 서버가 양방향으로 지속적인 <br> 연결을 유지하며 실시간 데이터 전송 | 서버에서 클라이언트로 단방향 실시간 데이터 전송 <br> (HTTP 기반)                        |
| 장점  | 구현이 간단하고 기존 HTTP 프로토콜 사용 <br> 별도의 추가 설정 불필요 <br> 연결 유지가 필요 없음 | 양방향 통신 가능 <br> 빠른 데이터 전송 <br> 한 번의 연결로 지속적인 통신 | 실시간 데이터 수신 가능 <br> HTTP 기반으로 구현이 간단 <br> 단방향 통신으로 간단한 용도에 적합     |
| 단점  | 실시간성이 떨어짐 <br> 일정 주기로 요청해야 함 <br> 네트워크 및 서버 리소스 낭비 가능         | 양방향 통신이 필요하지 않을 때 불필요한 복잡성 초래 <br> 초기 연결 설정 복잡 <br> 지속 연결로 인한 리소스 부담                                              | 양방향 통신 불가능 <br> 서버에서 클라이언트로만 데이터 전송 <br> 특정 HTTP 환경에 제약이 있을 수 있음 |

### 최종 결정

---
실시간 알림 기능은 서버에서 클라이언트로 데이터 전송만 필요하기때문에, **단방향 통신이 적합** 하다고 판단  
**실시간성이 중요**해 Polling이나 Long Polling은 부적합  
단방향 통신을 요구하는 상황에서 양방향 통신이 필요한 WebSocket보다 **설정이 간단하고 효율적인 SSE를 선택**
</details>



<details>
<summary><h3> 📙 VIP 혜택 </h3></summary>

### 도입 배경

---
일정 주기마다 확인해야 하는 쿠폰 만료 기간, 회원 탈퇴한 유저의 유예 기간, 유저 VIP 등급을 처리하기 위해 스프링 배치를 도입
이 중 탈퇴한 유저는 데이터 건수가 적어 배치에서 제외, 최종적으로 쿠폰과 VIP 등급의 대용량 데이터 처리에 스케줄러를 병행하여 사용하기로 결정

### 선택지

---
|     | Quartz Scheduler                              | Apache Spark                                   | Spring Batch                                                                     |
|:---:|:----------------------------------------------|:-----------------------------------------------|:---------------------------------------------------------------------------------|
| 장점  | 단순한 데이터 처리 용이 <br> cron식을 지원하여 스케줄링에서 효과 좋음   | 실시간 스트리밍 가능 <br> 인메모리 연산으로 성능 뛰어남, 분산 병렬 처리 지원 | 스프링 부트와 연동이 쉽고, 스프링의 DI와 AOP를 적용할 수 있다. <br> 병렬 처리 가능, 트랜잭션과 롤백 지원하여 DB 중심 설계 용이 |
| 단점  | 대용량 처리에서 적합하지 않음 <br> 단계별 처리 기능 스프링 배치에 비해 부족 | 트랜잭션 지원 부족 <br> 인메모리 방식이기 때문에 메모리 사용량이 큼       | 실시간 처리 어려움 <br> 자체적인 스케줄링 기능 제공하지 않음                                             |

### 최종 결정

---
우리 프로젝트는 Spring batch를 사용하기로 했다.

1. Spring boot 기반의 프로젝트이므로 Spring batch를 사용했을 때 연동이 편하다.
2. 배치 처리하려는 작업들에 실시간 처리가 불필요하다.
3. 트랜잭션과 롤백을 지원하므로, 실패한 건이 없어야 하는 우리 프로젝트의 작업 특성에 맞다.
</details>


<details>
<summary><h3> 📓 토큰 로그인 방식 </h3></summary>

### 도입 배경

---
로그인 한 유저의 정보를 유지하기 위해 도입

### 선택지

---
|    | 세션 방식                                                          | 단일 토큰 <br> (Access Token)                                           | 이중 토큰 <br> (Access + Refresh Token)                                                        |
|:--:|:---------------------------------------------------------------|:--------------------------------------------------------------------|:-------------------------------------------------------------------------------------------|
| 장점 | 서버에서 세션 관리로 보안성 높음 <br> 로그인 유지 및 상태 관리 용이                      | 서버 확장성 우수 (세션 저장 불필요) <br> 클라이언트 단독 토큰 관리로 API 요청 간편 <br> 로드 밸런싱 용이 | 보안성 강화 (Access Token 탈취 시 Refresh Token으로 무효화 가능) <br> 짧은 Access Token 유효기간 <br> 자동 재인증 지원 |
| 단점 | 사용자 증가 시 서버 부하(메모리 사용 증가) <br> 확장성 낮음 (세션 공유 필요) <br> 쿠키 사용 필수 | 토큰 탈취 시 보안 위험 증가 <br> 토큰 길이로 인한 네트워크 부하 가능 <br> 토큰 만료 시 재로그인 필요     | Refresh Token 관리 필요 (보관 및 만료 정책 중요) <br> 구현 복잡도 증가 (토큰 갱신 로직 추가) <br> 유출 시 장기 악용 위험        |

### 최종 결정

---
JWT 단일 토큰 방식의 보안 문제를 보완하기 위해 **이중 토큰 방식**을 채택하였습니다.

- **Access Token의 유효기간을 짧게 설정**하여 탈취 시 피해를 최소화.
- **Refresh Token을 별도로 저장**하여 Access Token이 만료되더라도 자동으로 갱신 가능.
- **보안 강화:** Refresh Token을 안전한 저장소(예: HTTPOnly Cookie)에 보관하여 공격 가능성을 줄임.

</details>


<details>
<summary><h3> 📘 토스 결제 </h3></summary>

### 도입 배경

---
이커머스 사이트에서 필요한 결제 기능을 구현하기 위해 도입
- 결제 시스템 구현
- 테스트 api임으로 실제로 결제 X

### 선택지

---
| | 토스                                           | 포트원                                                                     |
|:-:|:---------------------------------------------|:------------------------------------------------------------------------|
|결제수단| 토스 페이먼트                                      | 카카오 페이                                                                  |
|테스트 가능 유무| 기본적으로 테스트 api 제공 <br> 토스에 연결된 결제수단은 전부 사용 가능 | PG(Payment Gateway)를 이용하여 <br> 가짜 사업자 번호를 만들어야함 <br> 카카오 페이만을 통해서 결제 가능 |



### 최종 결정

---
개발할 때의 가이드나 테스트 api등으로 개발에 더 쉽다는 점과  
결제 수단의 다양성(브랜드 페이), 결제 수단으로서의 대중성등을 고려하여 토스 페이먼트를 선택

---

</details>


<details>
<summary><h3> 📒 선착순 쿠폰 발급 </h3></summary>

### 도입 배경

---
한정된 수량의 쿠폰을 많은 사용자들이 동시에 요청하는 상황에서의 속도와 안정성을 보장하기 위해 도입하게 되었습니다.

- 쿠폰 재고 관리
    - 쿠폰 요청 시 재고 차감
- 사용자 대기열 관리
    - 요청 순서 관리

---

### 선택지

---
#### Redis VS RabbitMQ, Kafka

|  | 처리 속도 | 확장성 | 적합한 기능 |
| --- | --- | --- | --- |
| Redis | 매우 빠름 | 제한적 | 실시간 트랜잭션 |
| RabbitMQ | 중간 | 중간 | 메시지 브로커 등 |
| Kafka | 빠름 | 우수 | 대량 데이터 처리 |

선착순 쿠폰 발급 기능은 정확도와 재사용성보다 높은 성능과 빠른 처리 속도를 우선시해야 한다고 판단하여 Redis로 결정하였습니다.

#### Atomic VS 분산 락 (성능 비교 수치화 가능하다면 올리기)

|  | 속도 | 락 오버헤드 | 로직 처리 | 안전성 |
| --- | --- | --- | --- | --- |
| Atomic | 비교적 빠름 | X | 단순한 기능 구현 | 비교적 낮음 |
| 분산 락 | 비교적 느림 | O | 복잡한 로직 처리 가능 | 비교적 높음 |

초기에는 Atomic으로 구현했으나 쿠폰 발급 뿐만 아니라 알림 발송이나 검증 등의 작업 처리 수행과 데이터 정합성 측면에서 분산 락이 더 적절할 것이라는 생각이 들었습니다.


#### Redisson VS Lettuce

|  | 처리 방식 | 분산 락 | 라이브러리 크기 | 구현 방식 |
| --- | --- | --- | --- | --- |
| Redisson | 동기, 비동기 | RLock 등 제공 | 큼 | pub/sub |
| Lettuce | 비동기 | 직접 구현 | 작음 | spin lock |

Lettuce의 spin lock은 루프를 통해 지속적인 리소스를 가지고 있기에 레디스에 부하를 줄 수 있지만, Redisson은 이벤트시에만 작동하여 차지하는 리소스가 가벼워지는 등 오버헤드가 작아져 빠른 처리가 가능하기 때문에 선택했습니다.

---

### 최종 결정

---
선착순 쿠폰 발급 기능 특성상 빠른 처리가 중요하다고 판단하여 동시성 제어에 **Redis**를 적용하였습니다.

**Redisson 라이브러리** 를 통해 pub/sub 방식으로 구현하기로 결정하였습니다.

**동시성 제어를 위한 락 구현은 빠른 처리가 중요한 쿠폰 감소는** Atomic 연산을, **사용자  대기열 추가는 데이터 일관성이 중요**하므로 분산락을 활용하여 정합성을 유지하도록 함께 사용하였습니다.
</details>



<br />
<br />

## 💣 트러블 슈팅


<details>
<summary><h3> 🔎 SSE : 서버에 메세지 중복 처리로 인한 알림오류 </h3></summary>

## 문제 발생

리뷰 생성 후 알림은 Redis로 한 번 발송되어야 하지만, <br> 서버에서 동일한 메시지가 2번 수신되어 중복 처리가 발생하고, 이로 인해서 클라이언트로의 알림 전달에 문제가 발생함

## 시행 착오

1. 한 사용자(동일 사용자)가 리뷰 생성을 했을 때, 메시지 수신이 2번되어 있는 것을 확인
2. RedisPublisher과 RedisSubscriber의 메시지 처리 로직에는 디버깅을 통해 문제가 없음을 확인함
3. 아래 코드에서 RedisSubscriber의 onMessage 메서드를 확인했음

```java
@Override
  public void onMessage(Message message, byte[] pattern) {
    String payload = new String(message.getBody());
    System.out.println("Redis에서 받은 메시지: " + payload);

    Long userId = extractUserIdFromMessage(payload);
    String notificationMessage = extractNotificationMessage(payload);



    // 해당 사용자의 모든 SSE Emitter에 메시지 전송
    List<SseEmitter> emitters = sseEmitterRegistry.getEmitters(userId);
    for (SseEmitter emitter : emitters) {
      try {
        System.out.println(" SSE 전송 시작: " + notificationMessage);
        emitter.send(SseEmitter.event().name("newReview").data(notificationMessage));
        System.out.println(" SSE 전송 완료: " + notificationMessage);
      } catch (Exception e) {
        System.err.println(" SSE 전송 실패: " + e.getMessage());
        sseEmitterRegistry.removeEmitter(userId, emitter);
      }
    }
  }
```
- 디버깅 결과, SSE 연결 부분에서 동일 사용자에 대해 여러 개의 SseEmitter가 등록되어 있는 것이 중복 수신의 원인임을 파악

## 해결 방안

동일 사용자에 대해 중복된 SSE 연결이 존재하지 않도록, 새로운 연결을 생성하기 전에 기존에 등록된 SSE 연결들을 모두 제거하도록 코드를 추가

```java
    // 기존 SSE 연결이 있다면 먼저 제거
    List<SseEmitter> existingEmitters = sseEmitterRegistry.getEmitters(userId);
    for (SseEmitter existingEmitter : existingEmitters) {
      sseEmitterRegistry.removeEmitter(userId, existingEmitter); 
    }
```
- 새로운 SSE 연결을 생성하기 전에, SSE 연결을 관리하는 sseEmitterRegistry에서
  해당 사용자(userId)에 대해 이미 등록된 모든 SseEmitter 목록을 조회함.
- 조회된 모든 기존 연결을 제거하여 중복 등록을 방지함.
- Redis에서 한 번 발행된 메시지가 동일 사용자에게 중복 전송되는 문제를 해결함.


</details>


<details>
<summary><h3> 🔎 Batch : `deleteAllInBatch()`와`delete()` 두 가지 방법 비교 </h3></summary>

## 문제 발생

만료된 쿠폰을 삭제하는 배치 작업에서 writer의 메서드를 delete로 사용하고 있었습니다. 작동은 잘 되지만, **delete는 데이터를 하나씩 조회 후 삭제하므로 chunk 단위로 넘기는 이점이 줄어드는 게 아닌지** 고민이 생겼습니다. 따라서 `deleteAllInBatch()`를 사용하는 방법과 `delete()`를 사용하는 두 가지 방법을 비교해보기로 했습니다.

## 과정

1. deleteAllInBatch 적용을 위한 `CustomItemWriter` 작성
   만료된 쿠폰을 삭제하는 잡의 chunk는 <CouponUser, CouponUser>로 IO가 구성되어 있었습니다. 다만 deleteAllInBatch를 사용하기 위해서는 List<Long>이 필요하기 때문에 Id 값으로 처리하고자 커스텀 writer 클래스를 생성했습니다.

2. Reader가 읽는 값을 id(Long)로 변경
   위처럼 커스텀 클래스를 작성했을 때, deleteAllInBatch를 사용하는 잡이 현재 쿠폰 삭제 하나 뿐이므로 과한 대처라는 생각이 들었습니다. 커스텀 클래스를 생성하는 대신, Reader에서 id 목록을 건네주도록 로직을 수정했고, 따라 chunk의 IO를 <Long, Long>으로 변경했습니다.

<details><summary> delete 콘솔 화면
</summary>

![Image](https://github.com/user-attachments/assets/b9285277-2b55-4ba6-841e-35f2fab4a0f2)
</details>


기존에 `delete`를 사용한 잡은 실행시 find 후 delete를 한 데이터마다 반복하는 것을 콘솔 로그를 통해 확인했습니다. 하지만 `deleteAllInBatch` 사용시 삭제 작업에는 쿼리가 하나만 날아가게 됩니다.

## 결론

Reader가 반환하는 값을 id로 변경하고, delete 대신 deleteAllInBatch를 사용하여 반복되는 쿼리 전송 개수를 chunk 단위로 줄였습니다.
</details>


<details>
<summary><h3> 🔎 Security : Access Token과 Refresh Token이 프론트에서 전달 되지 않는 문제 </h3></summary>

## 문제 발생

Access Token과 Refresh Token이 프론트에서 잘 전달 되지 않는 문제가 발생하였습니다.

초기에는 Access Token을 Response Header를 Json형식으로 Response에 대입하여 응답하였습니다.

Resfresh Toekn을 Cookies에 포함하도록 설정하였습니다.

백엔드에서 설정한대로 Refresh Token은 Cookies에 포함이 되있지만, 프론트엔드에서 Access Token을 찾지 못하는 문제가 발생하였습니다.

## 시행착오

1. Access Token을 Cookies에 포함하여 응답
    - 브라우저로 전송이 되는 것을 확인하였습니다.
    - 하지만 쿠키는 자동으로 서버로 전송되기 때문에 CSRF( Cross-Site Request Forgery ) 공격에 취약할 수 있습니다.
2. Access Token을 응답 URL에 포함아여 응답
    - 브라우저로 전송이 되는 것을 확인하였습니다.
    - 하지만 URL에 포함하여 응답하는 것은 보안성이 너무 취약하다는 단점이 있습니다.
3. Access Token을 Response Body에 포함하여 응답
    - 브라우저로 전송이 되는 것을 확인하였습니다.
    - 하지만 매번 명시적으로 전달을 해주어야하는 문제가 있습니다.

## 해결방안

최종적으로 Access Token을 Response Body에 Json형식으로 전달하는 것을 선택하였습니다.

- 민감한 정보가 URL에 노출되지 않기 때문에 보안성이 높습니다.
- 응답에서 받은 Access Token을 클라이언트 측에서 로컬 스토리지 등에 안전하게 저장할 수 있으며, 사용자가 로그인 상태를 관리하는 방식에 유연성이 있습니다.
</details>


<details>
<summary><h3> 🔎 게임 : 게임 생성/수정: 파일 유효성, 리스트 이미지 처리 및 filePath 반환 문제 </h3></summary>

## 문제 발생

1. 게임 수정 기능에서 파일이 null이거나 공백일 경우 상황에 맞는 반환값이 다르게 나가야하는데 한 메서드에 모두 담기에는 메서드가 너무 길어짐
2. 게임 생성, 수정시 서브 이미지의 타입이 List임으로 uploadFile을 사용하지 못함
3. 게임 생성,수정후 반환값으로 해당 게임이 가지고 있는 filePath를 반환해야 하는데 게임 entity가 서브 이미지의 주소를 가지고 있지 않아 반환이 불가능하였음

## 해결 방안

1. 파일을 업로드하는 기능을 따로 빼서 메서드로 만들어줌으로서 게임 수정 메서드가 무거워지는 문제와 메서드로 만들면서 다른 메서드에서 사용이 가능해지면서 재사용성이 증가함
2. List타입을 받아서 업로드하는 uploadFiles를 만들어서 List타입의 파일도 업로드가 가능하게 변경
3. 서브 이미지 리스트를 반복문을 통해 filePath를 뽑아서 List형태로 반환하게 만들어 게임과 함께 DTO로 반환하여 해결
</details>


<details>
<summary><h3> 🔎 동시성 처리 : 동시성 제어 적용에도 Atomic, 분산 락 모두 재고량 음수값 발생 </h3></summary>

## 문제 발생

동시성 제어 코드를 적용했음에도 불구하고 재고량이 음수값이 되는 경우가 Atomic, 분산 락 두 상황 모두 발생하였습니다.

## 시행 착오

1. **Atomic에서의 재고량 중복 감소 해결 과정**  
   getRemainingCouponCount()와 decrementCouponCount() 사이의 시간차로 인해 여러 요청이 동시에 감소하는 상황이 일부 발생한 것이었습니다.
```java
  if (getRemainingCouponCount(couponName) > 0) {
      addQueue(userId, couponName);
      decrementCouponCount(couponName);
```

2. **DistributedLock(분산 락)에서의 재고량 중복 감소 해결 과정**  
   getRemainingCouponCount()와 updateCouponCount() 사이의 시간차로 인해 여러 요청이 동시에 감소하는 상황이 일부 발생한 것이었습니다. 또한, 재고 감소에 분산 락을 적용하는 것은 불필요한 성능 저하 요인이라고 판단하였습니다.
```java
  if (redisCouponRepository.getRemainingCouponCount(couponName) > 0) {
      addQueue(userId, couponName);
      updateCouponCount(couponName);
  }
```

```java
    RMap<String, Long> countMap = redissonClient.getMap(COUPON_COUNT_MAP, new LongCodec());
    countMap.addAndGet(getCouponKeyString(couponName), -1);
```

## 해결 방안

1. **Atomic에서의 재고량 중복 감소 해결 방안**  
   **[Lua Script 적용]**  
   재고 감소와 남은 재고 확인을 **동시에 수행**하여 재고량 **음수 방지**하고 Redis 내부에서 원자적으로 실행하도록 하였습니다. 또한, 재고 소진 상태를 반영하여 불필요한 Redis 요청을 줄이기 위해 재고가 0이 되면 sold_out 키의 value를 1로 바꾸고 즉시 차단하도록 하였습니다.
```java
  public Long decrementIfAvailable(String couponName) {
    String luaScript =
        "if tonumber(redis.call('get', KEYS[1])) > 0 then " +
            "    local new_stock = redis.call('decr', KEYS[1]) " +
            "    if new_stock == 0 then redis.call('set', KEYS[2], 'true') end " +
            "    return new_stock " +
            "else " +
            "    return -1 " +
            "end";

    return redisTemplate.execute(
        new DefaultRedisScript<>(luaScript, Long.class),
        Arrays.asList("coupon:count:" + couponName, "coupon_sold_out:" + couponName)
    )   
```

2. **DistributedLock(분산 락)에서의 재고량 중복 감소 해결 방안**
1) getRemaingCouponCount로 재고 소진 예외를 미리 처리하고 쿠폰 요청을 수행하도록 변경하였습니다.
```java
  if (redisCouponRepository.getRemainingCouponCount(couponName) <= 0) {
      throw new OutOfStockException(CouponErrorCode.COUPON_OUT_OF_STOCK);
  }
  redisCouponRepository.findUserFromQueue(userId, couponName);
  addQueue(userId, couponName);
  redisCouponRepository.decrementAndGetCouponCount(couponName);
```


2) RAtomicLong.decrementAndGet()을 사용하여 Redisson에서 원자적으로 감소하도록 구현하였습니다.
```
   RAtomicLong stock = redissonClient.getAtomicLong("coupon:count:" + couponName);
    // **원자적으로 감소 **
    stock.decrementAndGet();
```

</details>

<details>
<summary><h3> 🔎 SSE : SSE 미연결로 인한 알림 전송 실패 및 리뷰 롤백 </h3></summary>

## 문제 발생

게임 등록자가 SSE 연결을 열어두지 않아서 알림 전송이 실패하고, 그 실패가 리뷰 생성 트랜잭션 전체를 롤백시키는 현상

## 시행 착오

1. SSE + Redis는 연결되어 있는 상황에만, 알림이 날라옴
2. SSE를 알림페이지에 들어갔을 때만 연결되도록 설정
3. 게임 등록자가 알림페이지를 켜놓지 않으면, SSE 연결이 되지 않기 때문에 **알림 전송 오류 발생**
4. 리뷰 생성 로직은 **트랜잭션**으로 묶여, 중간에 **오류가뜨면 전체 트랜잭션이 롤백**되는 상황 발생
5. 리뷰가 생성되고 알림이 가기때문에, 이미 리뷰 생성 성공하고 DB에 리뷰가 저장되었음에도, 알림에서 오류가 떴기때문에 클라이언트에서 오류 반환
6. 즉, **알림 전송이 무조건 성공**해야함


## 해결 방안

알림 전송을 실패해도, 리뷰 생성은 정상적으로 되도록 예외처리

- 문제가 되는 리뷰 생성 로직에 try-catch문을 사용
- catch된 예외를 그냥 넘김 -> 나중에 문제 생기면 확인하기 위해, 예외 메세지 출력

```java
// 알림 전송 실패 시 예외처리해서, 리뷰 생성은 정상적으로 되도록 설정
  try {
      notificationService.notifyReview(review.getId());
    } catch (Exception e) {
    System.out.println("알림 전송 중 오류 발생 : " + e.getMessage());
    }
```
간단하게는 try-catch문을 사용할 수 있음.  
근본적인 원인은 알림 페이지에 들어갔을 때만 SSE 연결이 되는 것이기 때문에, 로그인하고 난 이후 모든 페이지를 SSE 연결해줌
```javascript
프론트 코드 추가 예정
```


</details>


<br />
<br />

## ❤️‍🔥 소감
|                                                                                                     |                                               소감                                                |
|:---------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------:|
| ![Image](https://github.com/user-attachments/assets/3450a7d4-42cc-4329-b5cf-c2ca8c82127f) <br> 이서준  |                    팀원들과 일정 공유가 중요하다는 것을 알게되었고, <br> 원하는 기능이 모두 마무리되어 뿌듯합니다.                     |
|  ![Image](https://github.com/user-attachments/assets/eed84f54-5a47-47cf-a105-b8867e37c527) <br> 김민  |          관심있던 분야의 플랫폼을 만드는 게 즐거웠고, <br> 유지보수를 위한 설계의 어려움을 직접 겪은 경험이 앞으로 도움이 될 것 같습니다.           |
| ![Image](https://github.com/user-attachments/assets/41d06a4e-10ce-474f-906e-1f64e3d252e7)  <br> 강준혁 |                       이번 프로젝트를 진행하면서 <br> 팀원과의 소통이 얼마나 중요한지 다시 한번 깨달았습니다.                       |
| ![Image](https://github.com/user-attachments/assets/71dd3054-af8b-4451-8af2-67fa3d2d827c)  <br> 진하빈 |     평소 이용하기만 했던 플랫폼을 직접 제작해보며 로직을 확인할 수 있어 좋았고, <br> 팀원들과 함께 고민하고 해결하는 과정에서 많이 배울 수 있었습니다.      |
| ![Image](https://github.com/user-attachments/assets/d17b5f68-50b5-4464-a4c2-47c4f62de187)  <br> 하진이 | 평소 관심있던 분야의 플랫폼을 만들 수 있어서 좋았고, <br> 목표로하던 기능까지 마무리 할 수 있어서 좋았습니다.  <br> 좋은 팀원들을 만나서 즐겁게 공부했습니다! |



