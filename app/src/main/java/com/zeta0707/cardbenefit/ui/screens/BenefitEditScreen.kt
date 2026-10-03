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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.zeta0707.cardbenefit.data.Benefit
import com.zeta0707.cardbenefit.data.BenefitCategories
import com.zeta0707.cardbenefit.ui.viewmodel.CardViewModel

/**
 * benefitId == null 이면 cardId에 새 혜택 추가, 아니면 해당 혜택 수정.
 */
@Composable
fun BenefitEditScreen(
    cardId: Long,
    benefitId: Long?,
    viewModel: CardViewModel,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    var loaded by remember { mutableStateOf(benefitId == null) }
    var existingId by remember { mutableStateOf(0L) }

    var category by remember { mutableStateOf(BenefitCategories.PRESET.first()) }
    var title by remember { mutableStateOf("") }
    var rateOrAmount by remember { mutableStateOf("") }
    var minSpend by remember { mutableStateOf("") }
    var conditionDetail by remember { mutableStateOf("") }
    var monthlyLimit by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(benefitId) {
        if (benefitId != null) {
            val benefit = viewModel.getBenefit(benefitId)
            if (benefit != null) {
                existingId = benefit.id
                category = benefit.category
                title = benefit.title
                rateOrAmount = benefit.rateOrAmount
                minSpend = benefit.minSpend?.toString() ?: ""
                conditionDetail = benefit.conditionDetail
                monthlyLimit = benefit.monthlyLimit ?: ""
                note = benefit.note
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

    fun buildBenefit(): Benefit = Benefit(
        id = existingId,
        cardId = cardId,
        category = category.trim().ifBlank { "기타" },
        title = title.trim(),
        rateOrAmount = rateOrAmount.trim(),
        minSpend = minSpend.toIntOrNull(),
        conditionDetail = conditionDetail.trim(),
        monthlyLimit = monthlyLimit.trim().ifBlank { null },
        note = note.trim()
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        CategoryDropdown(
            selected = category,
            onSelected = { category = it }
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("혜택명 (예: 스타벅스 할인)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = rateOrAmount,
            onValueChange = { rateOrAmount = it },
            label = { Text("적립/할인율 표기 (예: 10% 할인, 1000원당 1마일)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = minSpend,
            onValueChange = { minSpend = it.filter(Char::isDigit) },
            label = { Text("전월실적 최소금액 (원, 선택)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = conditionDetail,
            onValueChange = { conditionDetail = it },
            label = { Text("세부 조건 (전월실적 구간별 한도 등)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = monthlyLimit,
            onValueChange = { monthlyLimit = it },
            label = { Text("월 한도 (선택, 예: 월 1만원, 월 3회)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("참고사항 (선택)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { viewModel.saveBenefit(buildBenefit(), onDone) },
                enabled = title.isNotBlank() && rateOrAmount.isNotBlank()
            ) {
                Text("저장")
            }

            if (existingId != 0L) {
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = { showDeleteConfirm = true }) {
                    Text("혜택 삭제")
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("혜택 삭제") },
            text = { Text("이 혜택을 삭제할까요?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.deleteBenefit(buildBenefit(), onDone)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = onSelected,
            readOnly = false,
            singleLine = true,
            label = { Text("분류 (직접 입력도 가능)") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            BenefitCategories.PRESET.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
