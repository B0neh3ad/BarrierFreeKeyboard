package com.example.barrierfreekeyboard.ui

object KeyboardConstants {

    const val KB_DEFAULT_USE_NUMPAD = true
    const val KB_DEFAULT_HEIGHT = 350
    const val KB_DEFAULT_SOUND = -1
    const val KB_DEFAULT_VIBRATE = -1
    const val KB_DEFAULT_INITIAL_INTERVAL = 350
    const val KB_DEFAULT_NORMAL_INTERVAL = 100

    const val KB_ENG = 0
    const val KB_KOR = 1
    const val KB_SYM = 2
    const val KB_EMO = 3
    const val KB_NUM = 4

    const val KB_AAC = 10

    const val VIB_INT = 70

    const val SPACEBAR = 32

    const val KEYCODE_LF = 10
    const val KEYCODE_DONE = -4
    const val KEYCODE_DELETE = -5

    /** about AAC keyboard **/
    const val SYMBOL_PER_LINE = 4
    const val PRELOAD_ITEM_COUNT = 8

    val SYMBOL_TEXT = arrayListOf(
        // 대화
        arrayListOf("안녕하세요", "안녕히 계세요", "감사합니다", "죄송합니다", "도와주세요", "괜찮아요",
            "제가 할게요", "화이팅", "최고", "건강하세요", "행복한 하루 되세요", "사랑해요", "식사하셨어요?",
            "잘 지내셨어요?", "생일 축하합니다", "새해 복 많이 받으세요", "즐거운 명절 보내세요", "메리 크리스마스",
            "저는 AAC를 사용해서 대화합니다", "길 잃었어요", "경찰 불러주세요", "구급차 불러주세요"),
        // 나
        arrayListOf("의사소통 책", "스케줄", "공책", "신분증", "가방", "안경", "컵", "수건", "칫솔", "치약",
            "지갑", "돈", "신발", "티셔츠", "바지", "치마", "잠바", "양말", "속옷", "우산", "모자", "목도리 장갑",
            "목걸이", "팔찌"),
        // 코로나
        arrayListOf("마스크", "마스크걸이", "손세정제", "체온계", "열재요", "몇 도예요?", "QR", "코로나 자가키트",
            "코로나 블루", "손 씻어요", "마스크 써요", "거리두기 해요", "선별진료소", "예방접종센터", "코 면봉검사",
            "주사 맞아요", "예방접종 증명서", "COOV", "음성이에요", "양성이에요", "확진되었어요", "밀접 접촉자에요"),
        // 놀이
        arrayListOf("핸드폰", "책", "컴퓨터", "TV", "게임", "음악", "보드게임", "장난감", "공", "비눗방울",
            "퍼즐", "그림", "앨범", "블록", "악기", "댄스", "소파", "트램폴린", "종이접기", "요리", "운동",
            "동물", "곤충", "유튜브"),
        // 감정
        arrayListOf("아파요", "슬퍼요", "재미있어요", "졸려요", "힘들어요", "화나요", "신나요", "더워요", "추워요",
            "배고파요", "무서워요", "심심해요"),
        // 건강
        arrayListOf("약", "밴드", "연고", "파스", "소독약"),
        // 장소
        arrayListOf("화장실", "집", "학교", "편의점", "마트", "노래방", "식당", "공원", "미용실", "치과", "수영장",
            "헬스장", "볼링장", "극장", "산", "바다", "놀이공원", "카페", "패스트푸드점", "아이스크림", "가게",
            "빵집", "도서관", "병원", "약국"),
        // 사람들
        arrayListOf("엄마·아빠", "선생님", "활동보조인", "친구(동료)", "형제", "자매", "할머니·할아버지", "친척",
            "이웃", "목사님", "스님", "수녀님"),
        // 문구
        arrayListOf("종이", "풀", "스티커", "색종이", "연필", "색연필", "크레파스", "지우개", "테이프", "가위",
            "싸인펜", "필통", "플레이도우", "비즈", "슬라임", "찰흙", "모래", "플레이콘", "천사점토", "자", "연필깎이",
            "도장", "매니큐어", "수정", "테이프"),
        // 색깔
        arrayListOf("흰색", "노랑", "연주황", "귤색", "주황", "다홍", "빨강", "분홍", "자주", "보라", "황토색",
            "갈색", "고동색", "연두", "초록", "청록색", "하늘색", "파랑", "군청색", "남색", "검정", "회색", "은색",
            "금색"),
        // 숫자
        arrayListOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "100", "십원", "오십원",
            "백원", "오백원", "천원", "오천원", "만원", "오만원", "시계", "엘리베이터", "전화번호", "나이"),
        // 시간,날짜
        arrayListOf("계절", "봄", "여름", "가을", "겨울", "아침", "점심", "저녁", "밤", "날씨", "미세먼지",
            "황사", "달력", "어제", "오늘", "내일", "생일", "휴일", "학교", "가는", "날", "온라인", "수업",
            "병원", "가는", "날", "센터", "가는", "날", "설날", "추석"),
        // 음식
        arrayListOf("밥", "국", "찌개", "반찬", "치킨", "피자", "김밥", "컵라면", "돈까스", "고기", "소세지",
            "계란", "햄버거", "자장면", "떡볶이", "튀김", "만두", "어묵", "샌드위치", "초밥", "볶음밥", "국수",
            "케찹"),
        // 간식
        arrayListOf("과자", "빵", "아이스크림", "초콜릿", "젤리", "사탕", "케이크", "소떡소떡", "핫도그", "치즈",
            "떡"),
        // 과일
        arrayListOf("바나나", "사과", "포도", "딸기", "수박", "블루베리", "복숭아", "배", "귤", "파인애플",
            "자몽", "키위"),
        // 음료
        arrayListOf("물", "커피", "코코아", "티", "아이스티", "우유", "두유", "요구르트", "요거트", "병쥬스",
            "탄산음료", "얼음"),
        // 채소
        arrayListOf("고구마", "감자", "옥수수", "호박", "토마토", "버섯", "마늘", "양파", "무", "오이", "당근",
            "배추"),
        // 마지막장
        arrayListOf("나", "너", "우리", "그거", "이거", "저거", "무엇?", "누구", "어디?", "언제?", "왜?",
            "어떻게?", "더", "다시", "그만", "같이", "많이", "조금", "안/못", "빨라요", "느려요", "커요",
            "작아요", "있어요", "없어요", "위", "아래", "안", "밖", "앞", "뒤", "옆", "사요", "열어요",
            "닫아요", "찾아요", "꺼요", "켜요", "가요", "와요", "넣어요", "꺼내요", "말해요", "가져요", "빼요",
            "잘라요", "읽어요", "써요", "먹어요", "보내요", "펴요", "나가요", "돌려요", "타요", "주세요", "해요",
            "돼요", "봐요", "놔요", "버려요")
    )
}