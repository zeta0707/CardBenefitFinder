package com.zeta0707.cardbenefit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zeta0707.cardbenefit.data.Card as CardEntity

@Composable
fun CardListScreen(
    cards: List<CardEntity>,
    onCardClick: (Long) -> Unit,
    onEditCard: (Long) -> Unit,
    onAddCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (cards.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("등록된 카드가 없습니다. 오른쪽 아래 + 버튼으로 추가해보세요.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cards, key = { it.id }) { card ->
                    CardRow(
                        card = card,
                        onClick = { onCardClick(card.id) },
                        onEditClick = { onEditCard(card.id) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onAddCard,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "카드 추가")
        }
    }
}

@Composable
private fun CardRow(card: CardEntity, onClick: () -> Unit, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(text = card.issuer, style = MaterialTheme.typography.labelMedium)
                Text(text = card.name, style = MaterialTheme.typography.titleMedium)
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    val feeText = buildString {
                        if (card.annualFeeDomestic != null) append("국내전용 ${"%,d".format(card.annualFeeDomestic)}원")
                        if (card.annualFeeOverseas != null) {
                            if (isNotEmpty()) append(" · ")
                            append("해외겸용 ${"%,d".format(card.annualFeeOverseas)}원")
                        }
                        if (isEmpty()) append("연회비 정보 없음")
                    }
                    Text(text = feeText, style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onEditClick) {
                Icon(Icons.Filled.Edit, contentDescription = "카드 정보 수정")
            }
        }
    }
}
