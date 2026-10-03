package com.zeta0707.cardbenefit

import android.app.Application
import com.zeta0707.cardbenefit.data.AppDatabase
import com.zeta0707.cardbenefit.data.CardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CardBenefitApp : Application() {

    lateinit var repository: CardRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(this)
        repository = CardRepository(db)

        // 앱이 시작될 때마다 SeedData.kt에 있는데 DB에는 아직 없는 카드를 채워 넣는다.
        // (이미 있는 카드는 건드리지 않으므로 앱을 지우고 재설치할 필요가 없다.)
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.seedMissingCards(db)
        }
    }
}
