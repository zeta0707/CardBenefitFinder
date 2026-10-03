package com.zeta0707.cardbenefit

import android.app.Application
import com.zeta0707.cardbenefit.data.AppDatabase
import com.zeta0707.cardbenefit.data.CardRepository

class CardBenefitApp : Application() {

    lateinit var repository: CardRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(this)
        repository = CardRepository(db)
    }
}
