package com.zeta0707.cardbenefit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zeta0707.cardbenefit.data.Benefit
import com.zeta0707.cardbenefit.data.BenefitWithCard
import com.zeta0707.cardbenefit.data.Card
import com.zeta0707.cardbenefit.data.CardRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CardViewModel(private val repository: CardRepository) : ViewModel() {

    val cards: StateFlow<List<Card>> = repository.getAllCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // null이면 "전체" (필터 없음)
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val queriedBenefits: Flow<List<BenefitWithCard>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAllBenefitsWithCard()
            else repository.searchBenefits(query.trim())
        }

    val searchResults: StateFlow<List<BenefitWithCard>> = combine(
        queriedBenefits,
        _selectedCategory
    ) { list, category ->
        if (category == null) list else list.filter { it.benefit.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategoryFilterChange(category: String?) {
        _selectedCategory.value = category
    }

    fun benefitsForCard(cardId: Long): Flow<List<Benefit>> =
        repository.getBenefitsForCard(cardId)

    suspend fun getCard(cardId: Long): Card? = repository.getCardById(cardId)

    suspend fun getBenefit(benefitId: Long): Benefit? = repository.getBenefitById(benefitId)

    fun saveCard(card: Card, onDone: () -> Unit) {
        viewModelScope.launch {
            if (card.id == 0L) repository.addCard(card) else repository.updateCard(card)
            onDone()
        }
    }

    fun deleteCard(card: Card, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.deleteCard(card)
            onDone()
        }
    }

    fun saveBenefit(benefit: Benefit, onDone: () -> Unit) {
        viewModelScope.launch {
            if (benefit.id == 0L) repository.addBenefit(benefit) else repository.updateBenefit(benefit)
            onDone()
        }
    }

    fun deleteBenefit(benefit: Benefit, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.deleteBenefit(benefit)
            onDone()
        }
    }

    class Factory(private val repository: CardRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CardViewModel::class.java)) {
                return CardViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
