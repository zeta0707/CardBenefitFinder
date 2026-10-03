package com.zeta0707.cardbenefit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Card::class, Benefit::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao
    abstract fun benefitDao(): BenefitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "card_benefit.db"
            ).build()
        }

        /**
         * SeedData.kt에 있는 카드 중 DB에 아직 없는 카드(및 그 혜택)만 골라서 추가한다.
         * - 앱을 처음 설치했을 때: SeedData의 카드가 전부 채워진다.
         * - 이미 설치된 앱인데 SeedData.kt에 새 카드가 코드로 추가된 경우: 빠진 카드만 채워지고,
         *   이미 있는 카드/사용자가 직접 추가·수정한 데이터는 건드리지 않는다.
         * 앱을 지우고 재설치하지 않아도 되도록, 앱이 시작될 때마다 호출한다.
         */
        suspend fun seedMissingCards(db: AppDatabase) {
            val cardDao = db.cardDao()
            val benefitDao = db.benefitDao()

            val missingCards = SeedData.cards().filter { card ->
                cardDao.findCard(card.issuer, card.name) == null
            }
            if (missingCards.isEmpty()) return

            val newIds = cardDao.insertCards(missingCards)
            val cardIds = missingCards.zip(newIds).toMap()
            benefitDao.insertBenefits(SeedData.benefits(cardIds))
        }
    }
}
