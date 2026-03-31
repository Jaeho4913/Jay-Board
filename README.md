# Spring-Boot-AJAX-Board

first spring boot web
웹 게시판

화면 깜빡임 없이 부드럽게 동작하는 100% AJAX 기반의 스프링 부트 비동기 게시판입니다.

* **사용언어**
    * Java
    * JavaScript
* **사용 라이브러리**
    * jQuery
    * MyBatis
* **사용 프레임워크**
    * Spring Boot
* **데이터베이스**
    * MySQL
* **개발환경**
    * Windows
    * Spring Tool Suite (STS) / Eclipse
    * Java
* **핵심 기능**
    * **회원 관리**
        * AJAX 기반 화면 회원가입 및 로그인
        * 가입 시 아이디 및 이메일 중복 검사
        * 계정 찾기 및 임시 비밀번호 이메일 발송 기능
        * BCrypt를 활용한 안전한 비밀번호 암호화 보관
    * **게시판 기능**
        * 비동기 통신(AJAX)을 이용한 게시글 등록, 삭제, 수정
        * 게시글 검색 기능 (제목, 내용, 작성자)
        * 게시글 리스트 페이징 처리 기능
        * 회원 및 비회원 권한 분리 검증 로직
