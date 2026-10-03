package com.zeta0707.cardbenefit.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 특정 카드가 제공하는 혜택 한 건과 그 충족 조건을 나타낸다.
 */
@Entity(
    tableName = "benefits",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cardId")]
)
data class Benefit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardId: Long,
    val category: String,        // 혜택 분류 (예: 적립, 할인, 공항라운지, 마일리지)
    val title: String,           // 혜택 이름 (예: 스타벅스 할인)
    val rateOrAmount: String,    // 적립률/할인율/적립액 표기 (예: "1000원당 1마일", "10% 할인")
    val minSpend: Int?,          // 전월실적 최소 조건 (원). 조건 없으면 null
    val conditionDetail: String, // 전월실적 구간별 한도 등 세부 조건 설명
    val monthlyLimit: String?,   // 월 한도 (금액/횟수)
    val note: String = ""        // 기타 참고사항
)
