package com.zeta0707.cardbenefit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zeta0707.cardbenefit.data.BenefitCategories

/**
 * "전체" + [BenefitCategories.PRESET] 을 가로 스크롤 칩으로 보여주고,
 * 하나만 선택할 수 있는 카테고리 필터. selected == null 이면 "전체"가 선택된 상태.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterRow(
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf<String?>(null) + BenefitCategories.PRESET

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options) { category ->
            FilterChip(
                selected = selected == category,
                onClick = { onSelect(category) },
                label = { Text(category ?: BenefitCategories.ALL_LABEL) }
            )
        }
    }
}
