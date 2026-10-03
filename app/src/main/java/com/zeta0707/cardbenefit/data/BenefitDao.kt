package com.zeta0707.cardbenefit.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BenefitDao {

    @Query("SELECT * FROM benefits WHERE cardId = :cardId ORDER BY category, title")
    fun getBenefitsForCard(cardId: Long): Flow<List<Benefit>>

    @Query("SELECT * FROM benefits WHERE id = :benefitId")
    suspend fun getBenefitById(benefitId: Long): Benefit?

    /**
     * 혜택 이름, 분류, 설명, 카드 이름/카드사 전체를 대상으로 키워드 검색.
     * 예: "스타벅스", "공항라운지", "삼성카드" 등으로 검색 가능.
     */
    @Query(
        """
        SELECT benefits.*, cards.name AS cardName, cards.issuer AS cardIssuer
        FROM benefits
        INNER JOIN cards ON benefits.cardId = cards.id
        WHERE benefits.title LIKE '%' || :keyword || '%'
           OR benefits.category LIKE '%' || :keyword || '%'
           OR benefits.conditionDetail LIKE '%' || :keyword || '%'
           OR cards.name LIKE '%' || :keyword || '%'
           OR cards.issuer LIKE '%' || :keyword || '%'
        ORDER BY cards.issuer, cards.name, benefits.category
        """
    )
    fun searchBenefits(keyword: String): Flow<List<BenefitWithCard>>

    @Query(
        """
        SELECT benefits.*, cards.name AS cardName, cards.issuer AS cardIssuer
        FROM benefits
        INNER JOIN cards ON benefits.cardId = cards.id
        ORDER BY cards.issuer, cards.name, benefits.category
        """
    )
    fun getAllBenefitsWithCard(): Flow<List<BenefitWithCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBenefit(benefit: Benefit): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBenefits(benefits: List<Benefit>): List<Long>

    @Update
    suspend fun updateBenefit(benefit: Benefit)

    @Delete
    suspend fun deleteBenefit(benefit: Benefit)
}
