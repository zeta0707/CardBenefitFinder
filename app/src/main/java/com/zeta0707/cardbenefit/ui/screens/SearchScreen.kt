package com.zeta0707.cardbenefit.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zeta0707.cardbenefit.data.BenefitWithCard

@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedCategory: String?,
    onCategorySelect: (String?) -> Unit,
    results: List<BenefitWithCard>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            placeholder = { Text("카드명, 가맹점, 혜택 키워드로 검색 (예: 스타벅스, 공항라운지)") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true
        )

        CategoryFilterRow(
            selected = selectedCategory,
            onSelect = onCategorySelect,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        if (results.isEmpty()) {
            Text(
                text = "검색 결과가 없습니다.",
                modifier = Modifier.padding(16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
        ) {
            items(results, key = { it.benefit.id }) { item ->
                SearchResultCard(item)
            }
        }
    }
}

@Composable
private fun SearchResultCard(item: BenefitWithCard) {
    val benefit = item.benefit
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${item.cardIssuer} · ${item.cardName}",
                style = MaterialTheme.typography.labelMedium
            )
            Text(text = benefit.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = benefit.rateOrAmount,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )

            val minSpendText = benefit.minSpend?.let { "전월실적 ${"%,d".format(it)}원 이상 필요" }
                ?: "전월실적 조건 없음(또는 확인 필요)"
            Text(
                text = minSpendText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(text = benefit.conditionDetail, style = MaterialTheme.typography.bodySmall)

            benefit.monthlyLimit?.let {
                Text(
                    text = "한도: $it",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
