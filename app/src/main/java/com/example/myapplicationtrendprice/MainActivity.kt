package com.example.myapplicationtrendprice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplicationtrendprice.receipt.ReceiptResultScreen
import com.example.myapplicationtrendprice.scanner.ScannerScreen
import com.example.myapplicationtrendprice.ui.AdminScreen
import com.example.myapplicationtrendprice.ui.AnalyticsScreen
import com.example.myapplicationtrendprice.ui.BasketScreen
import com.example.myapplicationtrendprice.ui.HomeScreen
import com.example.myapplicationtrendprice.ui.ProfileScreen
import com.example.myapplicationtrendprice.ui.TrendPriceTheme
import com.example.myapplicationtrendprice.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.White.toArgb()

        setContent {
            val viewModel: ShopViewModel = viewModel()
            val navController = rememberNavController()

            TrendPriceTheme {
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            currentRoute = "home",
                            onOpenBasket = {
                                navController.navigate("basket")
                            },
                            onOpenScanner = {
                                navController.navigate("scanner")
                            },
                            onOpenAnalytics = { productId ->
                                navController.navigate("analytics/$productId")
                            },
                            onNavigate = { route ->
                                navController.navigateSingleTop(route)
                            }
                        )
                    }

                    composable(
                        route = "analytics/{productId}",
                        arguments = listOf(
                            navArgument("productId") {
                                type = NavType.StringType
                            }
                        )
                    ) { entry ->
                        AnalyticsScreen(
                            viewModel = viewModel,
                            selectedProductId = entry.arguments?.getString("productId"),
                            currentRoute = "analytics",
                            onBack = {
                                navController.popBackStack()
                            },
                            onNavigate = { route ->
                                navController.navigateSingleTop(route)
                            }
                        )
                    }

                    composable("analytics") {
                        AnalyticsScreen(
                            viewModel = viewModel,
                            selectedProductId = null,
                            currentRoute = "analytics",
                            onBack = {
                                navController.popBackStack()
                            },
                            onNavigate = { route ->
                                navController.navigateSingleTop(route)
                            }
                        )
                    }

                    composable("basket") {
                        BasketScreen(
                            viewModel = viewModel,
                            currentRoute = "basket",
                            onBack = {
                                navController.popBackStack()
                            },
                            onNavigate = { route ->
                                navController.navigateSingleTop(route)
                            }
                        )
                    }

                    composable("profile") {
                        ProfileScreen(
                            currentRoute = "profile",
                            onNavigate = { route ->
                                navController.navigateSingleTop(route)
                            }
                        )
                    }

                    composable("admin") {
                        AdminScreen(
                            viewModel = viewModel,
                            currentRoute = "admin",
                            onNavigate = { route ->
                                navController.navigateSingleTop(route)
                            }
                        )
                    }

                    composable("scanner") {
                        ScannerScreen(
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onQrScanned = { qrText ->
                                viewModel.analyzeReceiptQr(qrText)
                                navController.navigate("receipt_result")
                            }
                        )
                    }

                    composable("receipt_result") {
                        ReceiptResultScreen(
                            analysis = viewModel.receiptAnalysis.value,
                            onBackClick = {
                                navController.popBackStack("home", inclusive = false)
                            },
                            onScanAgainClick = {
                                navController.navigate("scanner")
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun androidx.navigation.NavController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true

        popUpTo("home") {
            saveState = true
        }
    }
}