package com.zeta0707.cardbenefit.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zeta0707.cardbenefit.data.Benefit

@Composable
fun CardDetailScreen(
    cardTitle: String,
    benefits: List<Benefit>,
    onAddBenefit: () -> Unit,
    onEditBenefit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val filteredBenefits = if (selectedCategory == null) {
        benefits
    } else {
        benefits.filter { it.category == selectedCategory }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = cardTitle,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            CategoryFilterRow(
                selected = selectedCategory,
                onSelect = { selectedCategory = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                items(filteredBenefits, key = { it.id }) { benefit ->
                    BenefitCard(benefit, onClick = { onEditBenefit(benefit.id) })
                }
            }
        }

        FloatingActionButton(
            onClick = onAddBenefit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "혜택 추가")
        }
    }
}

@Composable
fun BenefitCard(benefit: Benefit, onClick: (() -> Unit)? = null) {
    val cardModifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)

    val body: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = benefit.category, style = MaterialTheme.typography.labelMedium)
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
            if (benefit.note.isNotBlank()) {
                Text(
                    text = benefit.note,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    if (onClick != null) {
        Card(
            modifier = cardModifier,
            onClick = onClick,
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            content = { body() }
        )
    } else {
        Card(
            modifier = cardModifier,
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            content = { body() }
        )
    }
}
