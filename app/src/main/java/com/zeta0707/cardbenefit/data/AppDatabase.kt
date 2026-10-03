package com.zeta0707.cardbenefit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // DB가 최초로 생성될 때 한 번만 시드 데이터를 채운다.
                        CoroutineScope(Dispatchers.IO).launch {
                            seedInitialData(getInstance(context))
                        }
                    }
                })
                .build()
        }

        private suspend fun seedInitialData(db: AppDatabase) {
            val cardDao = db.cardDao()
            val benefitDao = db.benefitDao()

            val cardIds = mutableMapOf<Card, Long>()
            for (card in SeedData.cards()) {
                val newId = cardDao.insertCard(card)
                cardIds[card] = newId
            }

            benefitDao.insertBenefits(SeedData.benefits(cardIds))
        }
    }
}
