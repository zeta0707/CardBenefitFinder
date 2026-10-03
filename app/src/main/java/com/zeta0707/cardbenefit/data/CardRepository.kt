package com.zeta0707.cardbenefit.data

import kotlinx.coroutines.flow.Flow

class CardRepository(private val db: AppDatabase) {

    fun getAllCards(): Flow<List<Card>> = db.cardDao().getAllCards()

    suspend fun getCardById(cardId: Long): Card? = db.cardDao().getCardById(cardId)

    fun getBenefitsForCard(cardId: Long): Flow<List<Benefit>> =
        db.benefitDao().getBenefitsForCard(cardId)

    suspend fun getBenefitById(benefitId: Long): Benefit? = db.benefitDao().getBenefitById(benefitId)

    fun getAllBenefitsWithCard(): Flow<List<BenefitWithCard>> =
        db.benefitDao().getAllBenefitsWithCard()

    fun searchBenefits(keyword: String): Flow<List<BenefitWithCard>> =
        db.benefitDao().searchBenefits(keyword)

    suspend fun addCard(card: Card): Long = db.cardDao().insertCard(card)

    suspend fun updateCard(card: Card) = db.cardDao().updateCard(card)

    suspend fun addBenefit(benefit: Benefit): Long = db.benefitDao().insertBenefit(benefit)

    suspend fun updateBenefit(benefit: Benefit) = db.benefitDao().updateBenefit(benefit)

    suspend fun deleteCard(card: Card) = db.cardDao().deleteCard(card)

    suspend fun deleteBenefit(benefit: Benefit) = db.benefitDao().deleteBenefit(benefit)
}
