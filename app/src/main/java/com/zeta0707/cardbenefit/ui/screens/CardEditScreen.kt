package com.zeta0707.cardbenefit.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zeta0707.cardbenefit.data.Card
import com.zeta0707.cardbenefit.ui.viewmodel.CardViewModel

/**
 * cardId == null 이면 새 카드 추가, 아니면 해당 카드 수정.
 */
@Composable
fun CardEditScreen(
    cardId: Long?,
    viewModel: CardViewModel,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    var loaded by remember { mutableStateOf(cardId == null) }
    var existingId by remember { mutableStateOf(0L) }

    var issuer by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var annualFeeDomestic by remember { mutableStateOf("") }
    var annualFeeOverseas by remember { mutableStateOf("") }
    var homepageUrl by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(cardId) {
        if (cardId != null) {
            val card = viewModel.getCard(cardId)
            if (card != null) {
                existingId = card.id
                issuer = card.issuer
                name = card.name
                annualFeeDomestic = card.annualFeeDomestic?.toString() ?: ""
                annualFeeOverseas = card.annualFeeOverseas?.toString() ?: ""
                homepageUrl = card.homepageUrl
                memo = card.memo
            }
            loaded = true
        }
    }

    if (!loaded) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = issuer,
            onValueChange = { issuer = it },
            label = { Text("카드사 (예: 삼성카드)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("카드명") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = annualFeeDomestic,
            onValueChange = { annualFeeDomestic = it.filter(Char::isDigit) },
            label = { Text("국내전용 연회비 (원, 선택)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = annualFeeOverseas,
            onValueChange = { annualFeeOverseas = it.filter(Char::isDigit) },
            label = { Text("해외겸용 연회비 (원, 선택)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = homepageUrl,
            onValueChange = { homepageUrl = it },
            label = { Text("카드 상세 페이지 URL (선택)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = memo,
            onValueChange = { memo = it },
            label = { Text("메모 (선택)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    val card = Card(
                        id = existingId,
                        issuer = issuer.trim(),
                        name = name.trim(),
                        annualFeeDomestic = annualFeeDomestic.toIntOrNull(),
                        annualFeeOverseas = annualFeeOverseas.toIntOrNull(),
                        homepageUrl = homepageUrl.trim(),
                        memo = memo.trim()
                    )
                    viewModel.saveCard(card, onDone)
                },
                enabled = issuer.isNotBlank() && name.isNotBlank()
            ) {
                Text("저장")
            }

            if (existingId != 0L) {
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = { showDeleteConfirm = true }) {
                    Text("카드 삭제")
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("카드 삭제") },
            text = { Text("이 카드를 삭제하면 연결된 혜택도 모두 함께 삭제됩니다. 삭제할까요?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    val card = Card(
                        id = existingId,
                        issuer = issuer.trim(),
                        name = name.trim(),
                        annualFeeDomestic = annualFeeDomestic.toIntOrNull(),
                        annualFeeOverseas = annualFeeOverseas.toIntOrNull(),
                        homepageUrl = homepageUrl.trim(),
                        memo = memo.trim()
                    )
                    viewModel.deleteCard(card, onDone)
                }) {
                    Text("삭제")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("취소")
                }
            }
        )
    }
}
