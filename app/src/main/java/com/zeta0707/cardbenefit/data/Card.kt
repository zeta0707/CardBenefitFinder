package com.zeta0707.cardbenefit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 사용자가 보유한 신용카드 한 장을 나타낸다.
 */
@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val issuer: String,          // 카드사 (예: 삼성카드, 신한카드)
    val name: String,            // 카드 상품명 (예: THE iD. PLATINUM)
    val annualFeeDomestic: Int?, // 국내전용 연회비 (원)
    val annualFeeOverseas: Int?, // 해외겸용 연회비 (원)
    val homepageUrl: String,     // 카드사 상세 페이지 URL
    val memo: String = ""        // 사용자 메모 (선택)
)
