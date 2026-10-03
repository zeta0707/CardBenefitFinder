package com.zeta0707.cardbenefit.data

/**
 * 앱 최초 실행 시 DB에 채워 넣는 초기 카드/혜택 데이터.
 *
 * 아래 데이터는 각 카드사 공식 홈페이지 및 공개된 카드 비교 사이트(뱅크샐러드, 카드고릴라, 나무위키 등)를
 * 참고하여 정리한 것으로, 실제 적립률/한도/전월실적 조건은 카드사 정책 변경에 따라 달라질 수 있습니다.
 * "정확한 조건은 카드사 홈페이지 확인 필요"라고 표시된 항목은 자동화된 조회로는 100% 확인하지 못한
 * 값이니 카드사 공식 사이트에서 최신 내용으로 직접 검증 후 사용하세요.
 */
object SeedData {

    // ---- 카드 정의 ----
    val idPlatinum = Card(
        issuer = "삼성카드",
        name = "THE iD. PLATINUM(포인트)",
        annualFeeDomestic = 215_000,
        annualFeeOverseas = 220_000,
        homepageUrl = "https://www.samsungcard.com/home/card/cardinfo/PGHPPCCCardCardinfoDetails001?code=AAP1779",
        memo = "연회비가 높은 프리미엄 카드. 전월실적 50만원 이상부터 주요 혜택이 활성화됨."
    )

    val asianaGnMe = Card(
        issuer = "삼성카드",
        name = "아시아나 삼성 지앤미 플래티늄",
        annualFeeDomestic = null,
        annualFeeOverseas = 20_000,
        homepageUrl = "https://www.samsungcard.com/personal/card/cardfinder/UHPPCA0102M0.jsp?code=AAP0320",
        memo = "삼성카드에서 발급하는 아시아나항공 마일리지 적립 카드(해외겸용). " +
            "삼성카드 공식 홈페이지는 자동조회가 막혀 있어 뱅크샐러드/카드고릴라 정보로 보완함. " +
            "갱신 연회비는 15,000원으로 첫해보다 낮게 안내된 자료도 있어 확인이 필요함."
    )

    val mrLife = Card(
        issuer = "신한카드",
        name = "Mr.Life",
        annualFeeDomestic = 15_000,
        annualFeeOverseas = 18_000,
        homepageUrl = "https://www.shinhancard.com/pconts/html/card/apply/credit/1187937_2207.html",
        memo = "생활밀착형 할인 카드. 전월실적 구간(30/50/100만원)에 따라 할인 한도가 커짐."
    )

    val travelZero = Card(
        issuer = "코나아이(도깨비트래블)",
        name = "도깨비 트래블제로카드",
        annualFeeDomestic = 0,
        annualFeeOverseas = 0,
        homepageUrl = "https://event.konacard.co.kr/travelzero/card/intro.html",
        memo = "일반 신용카드가 아니라 코나아이가 발급하는 선불 충전식 해외여행 전용카드(베트남 특화). " +
            "전월실적 개념 자체가 없고, 평생 연회비 0원."
    )

    val hanaTravelog = Card(
        issuer = "하나카드",
        name = "트래블로그 체크카드(유니온페이)",
        annualFeeDomestic = 0,
        annualFeeOverseas = 0,
        homepageUrl = "https://www.hanacard.co.kr/OPI41000000D.web?CD_PD_SEQ=15414",
        memo = "신용카드가 아니라 하나머니 잔액으로 결제하는 체크카드(선불 성격). 국제브랜드는 " +
            "마스터카드 또는 유니온페이 중 선택 가능하며 이 카드는 유니온페이 버전. " +
            "전월실적 조건 없이 매월 동일하게 혜택 제공, 연회비 면제."
    )

    fun cards(): List<Card> = listOf(idPlatinum, asianaGnMe, mrLife, travelZero, hanaTravelog)

    /**
     * cardIds: 위 Card들 중 DB에 새로 insert된 것들만의 (원본 Card -> 생성된 id) 매핑.
     * 이미 DB에 있어서 새로 추가하지 않은 카드는 이 map에 없을 수 있으며, 그 경우
     * 해당 카드의 혜택은 만들지 않는다 (이미 있는 혜택을 건드리지 않기 위함).
     */
    fun benefits(cardIds: Map<Card, Long>): List<Benefit> = buildList {
        cardIds[idPlatinum]?.let { addAll(idPlatinumBenefits(it)) }
        cardIds[asianaGnMe]?.let { addAll(asianaGnMeBenefits(it)) }
        cardIds[mrLife]?.let { addAll(mrLifeBenefits(it)) }
        cardIds[travelZero]?.let { addAll(travelZeroBenefits(it)) }
        cardIds[hanaTravelog]?.let { addAll(hanaTravelogBenefits(it)) }
    }

    // ---------- THE iD. PLATINUM(포인트) ----------
    private fun idPlatinumBenefits(idPlatinumId: Long): List<Benefit> =
        listOf(
            Benefit(
                cardId = idPlatinumId,
                category = "적립",
                title = "국내 가맹점 빅포인트 적립",
                rateOrAmount = "1.0% 적립",
                minSpend = null,
                conditionDetail = "전월실적 조건 없음, 적립 한도 없음",
                monthlyLimit = null
            ),
            Benefit(
                cardId = idPlatinumId,
                category = "적립",
                title = "생활편의 적립 (온라인쇼핑몰/할인점/백화점/배달앱)",
                rateOrAmount = "1.2% 적립",
                minSpend = null,
                conditionDetail = "전월실적 조건 없음, 적립 한도 없음",
                monthlyLimit = null
            ),
            Benefit(
                cardId = idPlatinumId,
                category = "적립",
                title = "특별 가맹점 적립 (해외/면세점/항공/여행/공연)",
                rateOrAmount = "1.5% 적립",
                minSpend = null,
                conditionDetail = "전월실적 조건 없음, 적립 한도 없음",
                monthlyLimit = null
            ),
            Benefit(
                cardId = idPlatinumId,
                category = "할인",
                title = "디지털콘텐츠 할인",
                rateOrAmount = "50% 할인",
                minSpend = 500_000,
                conditionDetail = "전월실적 50만원 이상 시 적용",
                monthlyLimit = "월 최대 1만원"
            ),
            Benefit(
                cardId = idPlatinumId,
                category = "할인",
                title = "스타벅스 할인",
                rateOrAmount = "3,000원 할인",
                minSpend = 500_000,
                conditionDetail = "전월실적 50만원 이상, 월 1회 한정",
                monthlyLimit = "월 1회"
            ),
            Benefit(
                cardId = idPlatinumId,
                category = "공항라운지",
                title = "국내외 공항 라운지 무료 이용",
                rateOrAmount = "본인 무료 이용",
                minSpend = 500_000,
                conditionDetail = "전월실적 50만원 이상 시 이용 가능",
                monthlyLimit = "1일 2회, 연 6회"
            ),
            Benefit(
                cardId = idPlatinumId,
                category = "기프트",
                title = "연간 기프트 서비스",
                rateOrAmount = "5종 중 택1 (호텔/골프/패션/신라면세점/신세계상품권, 15~16만원 상당)",
                minSpend = 6_000_000,
                conditionDetail = "첫해는 50만원 이상 이용 시 제공, 2차년도부터는 전년도 이용금액 600만원 이상 시 제공",
                monthlyLimit = "연 1회"
            )
        )

    // ---------- 아시아나 삼성 지앤미 플래티늄 ----------
    private fun asianaGnMeBenefits(asianaId: Long): List<Benefit> =
        listOf(
            Benefit(
                cardId = asianaId,
                category = "마일리지",
                title = "전 가맹점 마일리지 적립",
                rateOrAmount = "1,000원당 아시아나항공 1마일리지 적립",
                minSpend = null,
                conditionDetail = "정확한 전월실적 조건은 카드사 홈페이지 확인 필요",
                monthlyLimit = null
            ),
            Benefit(
                cardId = asianaId,
                category = "마일리지",
                title = "카페 우대 적립 (스타벅스/커피빈/파스쿠찌 등)",
                rateOrAmount = "1,000원당 5원 상당 우대 적립",
                minSpend = null,
                conditionDetail = "정확한 전월실적 조건은 카드사 홈페이지 확인 필요",
                monthlyLimit = null
            ),
            Benefit(
                cardId = asianaId,
                category = "할인",
                title = "아웃백스테이크하우스 할인",
                rateOrAmount = "10% 청구할인",
                minSpend = null,
                conditionDetail = "정확한 전월실적 조건은 카드사 홈페이지 확인 필요",
                monthlyLimit = null
            ),
            Benefit(
                cardId = asianaId,
                category = "할인",
                title = "스타벅스 청구할인",
                rateOrAmount = "1,000원 할인",
                minSpend = null,
                conditionDetail = "정확한 전월실적 조건은 카드사 홈페이지 확인 필요",
                monthlyLimit = null
            ),
            Benefit(
                cardId = asianaId,
                category = "주유",
                title = "S-OIL 보너스포인트 적립",
                rateOrAmount = "리터당 40포인트 적립",
                minSpend = null,
                conditionDetail = "정확한 전월실적 조건은 카드사 홈페이지 확인 필요",
                monthlyLimit = null
            ),
            Benefit(
                cardId = asianaId,
                category = "영화",
                title = "영화 할인 (메가박스/CGV/인터파크)",
                rateOrAmount = "3,000원 할인",
                minSpend = null,
                conditionDetail = "정확한 전월실적 조건은 카드사 홈페이지 확인 필요",
                monthlyLimit = null
            )
        )

    // ---------- 신한카드 Mr.Life ----------
    private fun mrLifeBenefits(mrLifeId: Long): List<Benefit> =
        listOf(
            Benefit(
                cardId = mrLifeId,
                category = "할인",
                title = "공과금 할인 (전기/도시가스/통신요금)",
                rateOrAmount = "10% 할인",
                minSpend = 300_000,
                conditionDetail = "전월실적 30~50만원: 월 한도 3천원 / 50~100만원: 7천원 / 100만원 이상: 1만원",
                monthlyLimit = "1회 최대 5천원, 월 1회 승인건 기준"
            ),
            Benefit(
                cardId = mrLifeId,
                category = "할인",
                title = "TIME 할인 - All Day (편의점/병원·약국/세탁소)",
                rateOrAmount = "10% 할인",
                minSpend = 300_000,
                conditionDetail = "전월실적 30~50만원: 월 1만원 / 50~100만원: 2만원 / 100만원 이상: 3만원",
                monthlyLimit = "1회 최대 1천원 (1회 1만원 이용 시)"
            ),
            Benefit(
                cardId = mrLifeId,
                category = "할인",
                title = "TIME 할인 - Night 21시~09시 (온라인쇼핑/택시/식음료)",
                rateOrAmount = "10% 할인",
                minSpend = 300_000,
                conditionDetail = "All Day 할인과 월 한도 통합 (전월실적 구간별 월 1만~3만원)",
                monthlyLimit = "1회 최대 1천원"
            ),
            Benefit(
                cardId = mrLifeId,
                category = "할인",
                title = "주말 마트 할인",
                rateOrAmount = "10% 할인",
                minSpend = 300_000,
                conditionDetail = "토·일요일에만 적용",
                monthlyLimit = "1회 최대 5천원"
            ),
            Benefit(
                cardId = mrLifeId,
                category = "할인",
                title = "주말 주유 할인",
                rateOrAmount = "리터당 60원 할인",
                minSpend = 300_000,
                conditionDetail = "토·일요일에만 적용",
                monthlyLimit = "월 30만원 이용분까지"
            ),
            Benefit(
                cardId = mrLifeId,
                category = "할인",
                title = "인터파크몰 특별할인",
                rateOrAmount = "20% 할인",
                minSpend = null,
                conditionDetail = "정확한 전월실적 조건은 카드사 홈페이지 확인 필요",
                monthlyLimit = "월 4회까지"
            )
        )

    // ---------- 도깨비 트래블제로카드 ----------
    private fun travelZeroBenefits(travelZeroId: Long): List<Benefit> =
        listOf(
            Benefit(
                cardId = travelZeroId,
                category = "해외",
                title = "해외 가맹점 결제 수수료 면제",
                rateOrAmount = "수수료 0원",
                minSpend = null,
                conditionDetail = "전월실적 조건 없음 (선불 충전식 카드)",
                monthlyLimit = null
            ),
            Benefit(
                cardId = travelZeroId,
                category = "해외",
                title = "환전 수수료 면제",
                rateOrAmount = "환율우대 100%, 환전 수수료 0원",
                minSpend = null,
                conditionDetail = "원화 충전 후 결제 시점에 USD 기준으로 실시간 자동 환전",
                monthlyLimit = null
            ),
            Benefit(
                cardId = travelZeroId,
                category = "해외",
                title = "해외 ATM 출금 수수료 면제",
                rateOrAmount = "건당 $3 수수료 면제",
                minSpend = null,
                conditionDetail = "전월실적 조건 없음",
                monthlyLimit = null
            ),
            Benefit(
                cardId = travelZeroId,
                category = "적립",
                title = "국내 가맹점 캐시백",
                rateOrAmount = "0.2~0.3% 캐시백",
                minSpend = null,
                conditionDetail = "전월실적 조건 없음, 적립 한도 없음",
                monthlyLimit = null
            ),
            Benefit(
                cardId = travelZeroId,
                category = "할인",
                title = "제휴 매장 할인 (커피빈 외)",
                rateOrAmount = "커피빈 15%, 부어치킨·코지하우스·또봉이통닭 7% 할인",
                minSpend = null,
                conditionDetail = "정확한 적용 조건은 카드사 홈페이지 확인 필요",
                monthlyLimit = null
            )
        )

    // ---------- 하나카드 트래블로그 체크카드(유니온페이) ----------
    private fun hanaTravelogBenefits(hanaTravelogId: Long): List<Benefit> =
        listOf(
            Benefit(
                cardId = hanaTravelogId,
                category = "해외",
                title = "해외 가맹점 결제 수수료 면제",
                rateOrAmount = "해외서비스수수료 면제(건당 $0.5), 국제브랜드수수료 면제(1%)",
                minSpend = null,
                conditionDetail = "전월실적 조건 없음 (지난달 실적과 무관하게 매월 동일 제공)",
                monthlyLimit = null
            ),
            Benefit(
                cardId = hanaTravelogId,
                category = "해외",
                title = "해외 ATM 출금 수수료 면제",
                rateOrAmount = "해외인출수수료 면제(건당 $3), 국제브랜드수수료 면제(1%)",
                minSpend = null,
                conditionDetail = "전월실적 조건 없음",
                monthlyLimit = null
            ),
            Benefit(
                cardId = hanaTravelogId,
                category = "적립",
                title = "국내 가맹점 하나머니 적립",
                rateOrAmount = "0.3% 적립",
                minSpend = null,
                conditionDetail = "전월실적 조건 없음, 적립 한도 없음",
                monthlyLimit = null
            )
        )
}
