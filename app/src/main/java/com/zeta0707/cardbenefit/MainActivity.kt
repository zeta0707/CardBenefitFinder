package com.zeta0707.cardbenefit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zeta0707.cardbenefit.ui.screens.BenefitEditScreen
import com.zeta0707.cardbenefit.ui.screens.CardDetailScreen
import com.zeta0707.cardbenefit.ui.screens.CardEditScreen
import com.zeta0707.cardbenefit.ui.screens.CardListScreen
import com.zeta0707.cardbenefit.ui.screens.SearchScreen
import com.zeta0707.cardbenefit.ui.theme.CardBenefitFinderTheme
import com.zeta0707.cardbenefit.ui.viewmodel.CardViewModel

private const val ROUTE_CARDS = "cards"
private const val ROUTE_SEARCH = "search"
private const val ROUTE_CARD_DETAIL = "cardDetail/{cardId}"
private const val ROUTE_CARD_EDIT = "cardEdit?cardId={cardId}"
private const val ROUTE_BENEFIT_EDIT = "benefitEdit/{cardId}?benefitId={benefitId}"
private const val NO_ID = -1L

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as CardBenefitApp
        val viewModelFactory = CardViewModel.Factory(app.repository)

        setContent {
            CardBenefitFinderTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val viewModel: CardViewModel = viewModel(factory = viewModelFactory)
                    CardBenefitFinderApp(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardBenefitFinderApp(viewModel: CardViewModel) {
    val navController = rememberNavController()
    val cards by viewModel.cards.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isTopLevel = currentRoute == ROUTE_CARDS || currentRoute == ROUTE_SEARCH

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle(currentRoute)) },
                navigationIcon = {
                    if (!isTopLevel) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (isTopLevel) {
                BottomBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_CARDS,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ROUTE_CARDS) {
                CardListScreen(
                    cards = cards,
                    onCardClick = { cardId -> navController.navigate("cardDetail/$cardId") },
                    onEditCard = { cardId -> navController.navigate("cardEdit?cardId=$cardId") },
                    onAddCard = { navController.navigate("cardEdit") }
                )
            }
            composable(ROUTE_SEARCH) {
                SearchScreen(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    selectedCategory = selectedCategory,
                    onCategorySelect = viewModel::onCategoryFilterChange,
                    results = searchResults
                )
            }
            composable(ROUTE_CARD_DETAIL) { entry ->
                val id = entry.arguments?.getString("cardId")?.toLongOrNull() ?: NO_ID
                val benefits by viewModel.benefitsForCard(id).collectAsState(initial = emptyList())
                val card = cards.find { it.id == id }
                CardDetailScreen(
                    cardTitle = card?.let { "${it.issuer} ${it.name}" } ?: "카드 상세",
                    benefits = benefits,
                    onAddBenefit = { navController.navigate("benefitEdit/$id") },
                    onEditBenefit = { benefitId -> navController.navigate("benefitEdit/$id?benefitId=$benefitId") }
                )
            }
            composable(
                route = ROUTE_CARD_EDIT,
                arguments = listOf(navArgument("cardId") { type = NavType.LongType; defaultValue = NO_ID })
            ) { entry ->
                val id = entry.arguments?.getLong("cardId") ?: NO_ID
                CardEditScreen(
                    cardId = if (id == NO_ID) null else id,
                    viewModel = viewModel,
                    onDone = { navController.popBackStack() }
                )
            }
            composable(
                route = ROUTE_BENEFIT_EDIT,
                arguments = listOf(
                    navArgument("cardId") { type = NavType.LongType },
                    navArgument("benefitId") { type = NavType.LongType; defaultValue = NO_ID }
                )
            ) { entry ->
                val cardId = entry.arguments?.getLong("cardId") ?: NO_ID
                val benefitId = entry.arguments?.getLong("benefitId") ?: NO_ID
                BenefitEditScreen(
                    cardId = cardId,
                    benefitId = if (benefitId == NO_ID) null else benefitId,
                    viewModel = viewModel,
                    onDone = { navController.popBackStack() }
                )
            }
        }
    }
}

private fun topBarTitle(route: String?): String {
    return when (route) {
        ROUTE_CARDS -> "내 카드"
        ROUTE_SEARCH -> "혜택 검색"
        ROUTE_CARD_DETAIL -> "카드 상세"
        ROUTE_CARD_EDIT -> "카드 추가/수정"
        ROUTE_BENEFIT_EDIT -> "혜택 추가/수정"
        else -> "내 카드 혜택"
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    val items = listOf(
        Triple(ROUTE_CARDS, "내 카드", Icons.Filled.CreditCard),
        Triple(ROUTE_SEARCH, "혜택 검색", Icons.Filled.Search)
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { (route, label, icon) ->
            val selected = currentDestination?.hierarchy?.any { it.route == route } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}
