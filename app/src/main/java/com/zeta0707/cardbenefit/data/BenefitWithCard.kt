package com.zeta0707.cardbenefit.data

import androidx.room.Embedded

/**
 * 검색 결과에서 혜택과 해당 카드 정보를 함께 보여주기 위한 조합 모델.
 */
data class BenefitWithCard(
    @Embedded val benefit: Benefit,
    val cardName: String,
    val cardIssuer: String
)
