package com.zeta0707.cardbenefit.data

/**
 * 혜택 분류 프리셋. 혜택 추가/수정 화면의 분류 선택과, 검색/카드상세 화면의
 * 카테고리 필터에서 함께 사용한다. 목록에 없는 분류를 직접 입력해도 저장은 가능하다.
 */
object BenefitCategories {
    const val ALL_LABEL = "전체"

    val PRESET: List<String> = listOf(
        "적립",
        "할인",
        "마일리지",
        "공항라운지",
        "기프트",
        "주유",
        "영화",
        "기타"
    )
}
