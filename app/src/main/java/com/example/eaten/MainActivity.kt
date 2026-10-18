package com.example.eaten

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eaten.ui.theme.EatenTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EatenTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CanteenApp()
                }
            }
        }
    }
}

@Composable
fun CanteenApp() {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) }
    var userRole by remember { mutableStateOf("Student") }
    var username by remember { mutableStateOf("") }
    
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                navController = navController,
                isLoggedIn = isLoggedIn,
                userRole = userRole,
                username = username,
                onLogout = {
                    isLoggedIn = false
                    userRole = "Student"
                    username = ""
                }
            )
        }
        composable("login") {
            LoginScreen(
                navController = navController,
                onLogin = { user, role ->
                    isLoggedIn = true
                    userRole = role
                    username = user
                    navController.navigate("home")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                navController = navController,
                onRegister = { user, role ->
                    isLoggedIn = true
                    userRole = role
                    username = user
                    navController.navigate("home")
                }
            )
        }
        composable("canteens") {
            CanteensScreen(navController = navController)
        }
        composable("dishes") {
            DishesScreen(navController = navController)
        }
        composable("manager") {
            if (userRole == "Manager") {
                ManagerScreen(navController = navController)
            } else {
                AccessDeniedScreen(navController = navController)
            }
        }
        composable("reviews") {
            ReviewsScreen(navController = navController)
        }
        composable("add_review") {
            AddReviewScreen(navController = navController)
        }
        composable("add_dish") {
            AddDishScreen(navController = navController)
        }
        composable("edit_dish") {
            EditDishScreen(navController = navController)
        }
        composable("manage_reviews") {
            ManageReviewsScreen(navController = navController)
        }
    }
}

data class Dish(
    val name: String,
    val price: String,
    val spiciness: String,
    val sweetness: String,
    val ingredientTags: Set<String>,
    val flavorStyle: String,
    val specialNeeds: Set<String>,
    val supplyPeriods: Set<SupplyPeriod>
)

data class ServiceWindow(
    val id: String,
    val name: String,
    val dish: Dish,
    val popularity: WindowPopularity
)

data class Canteen(
    val id: String,
    val name: String,
    val description: String,
    val windows: List<ServiceWindow>
)

enum class WindowPopularity { POPULAR, REGULAR }

data class QueueTime(
    val baseMinutes: Int,
    val peakMinutes: Int? = null
)

data class PeakPeriod(
    val start: LocalTime,
    val end: LocalTime
)

data class QueuePreset(
    val peakPeriod: PeakPeriod?,
    val popular: QueueTime,
    val regular: QueueTime
)

enum class SupplyPeriod(
    val displayName: String,
    val timeRange: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val order: Int,
    val referenceTime: LocalTime
) {
    BREAKFAST("Breakfast", "6:30-9:00", LocalTime.of(6, 30), LocalTime.of(9, 0), 0, LocalTime.of(7, 30)),
    LUNCH("Lunch", "11:00-13:30", LocalTime.of(11, 0), LocalTime.of(13, 30), 1, LocalTime.of(12, 0)),
    DINNER("Dinner", "17:00-19:30", LocalTime.of(17, 0), LocalTime.of(19, 30), 2, LocalTime.of(18, 0)),
    ALL_DAY("All Day", "6:30-19:30", LocalTime.of(6, 30), LocalTime.of(19, 30), 99, LocalTime.of(12, 0));

    val displayLabel: String
        get() = "$displayName ($timeRange)"

    fun contains(time: LocalTime): Boolean {
        val afterStart = !time.isBefore(startTime)
        val beforeEnd = !time.isAfter(endTime)
        return afterStart && beforeEnd
    }

    companion object {
        private val orderedPeriods = listOf(BREAKFAST, LUNCH, DINNER)

        fun currentFor(time: LocalTime): SupplyPeriod? {
            return orderedPeriods.firstOrNull { it.contains(time) }
        }

        fun defaultFor(time: LocalTime): SupplyPeriod {
            return currentFor(time) ?: ALL_DAY
        }
    }
}

private val queuePresets = mapOf(
    SupplyPeriod.BREAKFAST to QueuePreset(
        peakPeriod = PeakPeriod(LocalTime.of(7, 0), LocalTime.of(8, 0)),
        popular = QueueTime(baseMinutes = 10, peakMinutes = 15),
        regular = QueueTime(baseMinutes = 5, peakMinutes = 8)
    ),
    SupplyPeriod.LUNCH to QueuePreset(
        peakPeriod = PeakPeriod(LocalTime.of(11, 30), LocalTime.of(12, 30)),
        popular = QueueTime(baseMinutes = 9, peakMinutes = 18),
        regular = QueueTime(baseMinutes = 5, peakMinutes = 10)
    ),
    SupplyPeriod.DINNER to QueuePreset(
        peakPeriod = PeakPeriod(LocalTime.of(17, 30), LocalTime.of(18, 30)),
        popular = QueueTime(baseMinutes = 8, peakMinutes = 15),
        regular = QueueTime(baseMinutes = 4, peakMinutes = 8)
    ),
    SupplyPeriod.ALL_DAY to QueuePreset(
        peakPeriod = PeakPeriod(LocalTime.of(12, 0), LocalTime.of(13, 0)),
        popular = QueueTime(baseMinutes = 6, peakMinutes = 9),
        regular = QueueTime(baseMinutes = 3, peakMinutes = 6)
    )
)

private val sampleCanteens = listOf(
    Canteen(
        id = "main",
        name = "Main Campus Canteen",
        description = "Traditional hot dishes and classic flavors",
        windows = listOf(
            ServiceWindow(
                id = "main_breakfast_bun",
                name = "Window 1 - Steamed Bun & Soy Milk Combo",
                dish = Dish(
                    name = "Steamed Bun & Soy Milk Combo",
                    price = "£7.00",
                    spiciness = "Not Spicy",
                    sweetness = "Slightly Sweet",
                    ingredientTags = setOf("Contains Meat", "Contains Eggs/Dairy"),
                    flavorStyle = "Savory",
                    specialNeeds = setOf("Customizable"),
                    supplyPeriods = setOf(SupplyPeriod.BREAKFAST)
                ),
                popularity = WindowPopularity.POPULAR
            ),
            ServiceWindow(
                id = "main_red_braised_pork",
                name = "Window 2 - Red-Braised Pork Rice",
                dish = Dish(
                    name = "Red-Braised Pork Rice",
                    price = "£12.50",
                    spiciness = "Mildly Spicy",
                    sweetness = "Not Sweet",
                    ingredientTags = setOf("Contains Meat"),
                    flavorStyle = "Soy Sauce",
                    specialNeeds = setOf("Customizable"),
                    supplyPeriods = setOf(SupplyPeriod.LUNCH, SupplyPeriod.DINNER)
                ),
                popularity = WindowPopularity.POPULAR
            ),
            ServiceWindow(
                id = "main_kung_pao",
                name = "Window 3 - Kung Pao Chicken",
                dish = Dish(
                    name = "Kung Pao Chicken",
                    price = "£14.00",
                    spiciness = "Medium Spicy",
                    sweetness = "Not Sweet",
                    ingredientTags = setOf("Contains Meat", "Contains Eggs/Dairy"),
                    flavorStyle = "Sour & Spicy",
                    specialNeeds = setOf("Customizable"),
                    supplyPeriods = setOf(SupplyPeriod.LUNCH, SupplyPeriod.DINNER)
                ),
                popularity = WindowPopularity.REGULAR
            ),
            ServiceWindow(
                id = "main_sweet_sour",
                name = "Window 4 - Sweet & Sour Pork",
                dish = Dish(
                    name = "Sweet & Sour Pork",
                    price = "£13.50",
                    spiciness = "Not Spicy",
                    sweetness = "Sweet",
                    ingredientTags = setOf("Contains Meat"),
                    flavorStyle = "Sweet & Spicy",
                    specialNeeds = setOf("Customizable"),
                    supplyPeriods = setOf(SupplyPeriod.LUNCH, SupplyPeriod.DINNER)
                ),
                popularity = WindowPopularity.REGULAR
            ),
            ServiceWindow(
                id = "main_mapo_tofu",
                name = "Window 5 - Mapo Tofu",
                dish = Dish(
                    name = "Mapo Tofu",
                    price = "£11.00",
                    spiciness = "Medium Spicy",
                    sweetness = "Not Sweet",
                    ingredientTags = setOf("Contains Meat"),
                    flavorStyle = "Numbing & Spicy",
                    specialNeeds = setOf("Customizable"),
                    supplyPeriods = setOf(SupplyPeriod.DINNER)
                ),
                popularity = WindowPopularity.POPULAR
            ),
            ServiceWindow(
                id = "main_yuxiang_pork",
                name = "Window 6 - Yu Xiang Pork Shreds",
                dish = Dish(
                    name = "Yu Xiang Pork Shreds",
                    price = "£13.00",
                    spiciness = "Mildly Spicy",
                    sweetness = "Slightly Sweet",
                    ingredientTags = setOf("Contains Meat"),
                    flavorStyle = "Sweet & Spicy",
                    specialNeeds = setOf("Customizable"),
                    supplyPeriods = setOf(SupplyPeriod.LUNCH, SupplyPeriod.DINNER)
                ),
                popularity = WindowPopularity.POPULAR
            ),
            ServiceWindow(
                id = "main_soup",
                name = "Window 7 - Seaweed Egg Drop Soup",
                dish = Dish(
                    name = "Seaweed Egg Drop Soup",
                    price = "£5.00",
                    spiciness = "Not Spicy",
                    sweetness = "Not Sweet",
                    ingredientTags = setOf("Contains Eggs/Dairy"),
                    flavorStyle = "Light",
                    specialNeeds = setOf("Customizable"),
                    supplyPeriods = setOf(SupplyPeriod.ALL_DAY)
                ),
                popularity = WindowPopularity.REGULAR
            )
        )
    ),
    Canteen(
        id = "north",
        name = "North Campus Canteen",
        description = "Light meals and international flavors",
        windows = listOf(
            ServiceWindow(
                id = "north_breakfast_sandwich",
                name = "Window 1 - Breakfast Sandwich",
                dish = Dish(
                    name = "Breakfast Sandwich",
                    price = "£9.00",
                    spiciness = "Not Spicy",
                    sweetness = "Not Sweet",
                    ingredientTags = setOf("Contains Meat", "Contains Eggs/Dairy"),
                    flavorStyle = "Light",
                    specialNeeds = setOf("Customizable", "Low Salt/Sugar"),
                    supplyPeriods = setOf(SupplyPeriod.BREAKFAST)
                ),
                popularity = WindowPopularity.REGULAR
            ),
            ServiceWindow(
                id = "north_burger",
                name = "Window 2 - Beef Burger",
                dish = Dish(
                    name = "Beef Burger",
                    price = "£15.50",
                    spiciness = "Mildly Spicy",
                    sweetness = "Not Sweet",
                    ingredientTags = setOf("Contains Meat"),
                    flavorStyle = "Savory",
                    specialNeeds = setOf("Customizable", "Low Salt/Sugar"),
                    supplyPeriods = setOf(SupplyPeriod.LUNCH, SupplyPeriod.DINNER)
                ),
                popularity = WindowPopularity.POPULAR
            ),
            ServiceWindow(
                id = "north_salad",
                name = "Window 3 - Caesar Salad",
                dish = Dish(
                    name = "Caesar Salad",
                    price = "£8.50",
                    spiciness = "Not Spicy",
                    sweetness = "Not Sweet",
                    ingredientTags = setOf("Contains Eggs/Dairy"),
                    flavorStyle = "Light",
                    specialNeeds = setOf("Low Salt/Sugar"),
                    supplyPeriods = setOf(SupplyPeriod.LUNCH, SupplyPeriod.DINNER)
                ),
                popularity = WindowPopularity.REGULAR
            ),
            ServiceWindow(
                id = "north_korean_bbq",
                name = "Window 4 - Korean BBQ",
                dish = Dish(
                    name = "Korean BBQ",
                    price = "£16.00",
                    spiciness = "Very Spicy",
                    sweetness = "Not Sweet",
                    ingredientTags = setOf("Contains Meat"),
                    flavorStyle = "Numbing & Spicy",
                    specialNeeds = setOf("Customizable"),
                    supplyPeriods = setOf(SupplyPeriod.DINNER)
                ),
                popularity = WindowPopularity.POPULAR
            ),
            ServiceWindow(
                id = "north_ramen",
                name = "Window 5 - Japanese Ramen",
                dish = Dish(
                    name = "Japanese Ramen",
                    price = "£15.00",
                    spiciness = "Mildly Spicy",
                    sweetness = "Not Sweet",
                    ingredientTags = setOf("Contains Meat", "Contains Eggs/Dairy"),
                    flavorStyle = "Savory",
                    specialNeeds = setOf("Customizable"),
                    supplyPeriods = setOf(SupplyPeriod.LUNCH, SupplyPeriod.DINNER)
                ),
                popularity = WindowPopularity.POPULAR
            ),
            ServiceWindow(
                id = "north_pasta",
                name = "Window 6 - Italian Pasta",
                dish = Dish(
                    name = "Italian Pasta",
                    price = "£14.50",
                    spiciness = "Not Spicy",
                    sweetness = "Not Sweet",
                    ingredientTags = setOf("Contains Meat", "Contains Eggs/Dairy"),
                    flavorStyle = "Savory",
                    specialNeeds = setOf("Customizable"),
                    supplyPeriods = setOf(SupplyPeriod.LUNCH, SupplyPeriod.DINNER)
                ),
                popularity = WindowPopularity.REGULAR
            ),
            ServiceWindow(
                id = "north_coffee",
                name = "Window 7 - Coffee & Drinks",
                dish = Dish(
                    name = "Coffee & Drinks",
                    price = "£10.00",
                    spiciness = "Not Spicy",
                    sweetness = "Slightly Sweet",
                    ingredientTags = setOf("Contains Eggs/Dairy"),
                    flavorStyle = "Light",
                    specialNeeds = setOf("Customizable", "Low Salt/Sugar"),
                    supplyPeriods = setOf(SupplyPeriod.ALL_DAY)
                ),
                popularity = WindowPopularity.REGULAR
            )
        )
    ),
    Canteen(
        id = "south",
        name = "South Campus Canteen",
        description = "Vegetarian, seafood, and all-day beverages",
        windows = listOf(
            ServiceWindow(
                id = "south_breakfast_combo",
                name = "Window 1 - Soy Milk & Fried Dough Combo",
                dish = Dish(
                    name = "Soy Milk & Fried Dough Combo",
                    price = "£6.50",
                    spiciness = "Not Spicy",
                    sweetness = "Slightly Sweet",
                    ingredientTags = setOf("Contains Eggs/Dairy"),
                    flavorStyle = "Savory",
                    specialNeeds = setOf("Customizable"),
                    supplyPeriods = setOf(SupplyPeriod.BREAKFAST)
                ),
                popularity = WindowPopularity.REGULAR
            ),
            ServiceWindow(
                id = "south_vegetable",
                name = "Window 2 - Stir-Fried Vegetables",
                dish = Dish(
                    name = "Stir-Fried Vegetables",
                    price = "£9.00",
                    spiciness = "Not Spicy",
                    sweetness = "Not Sweet",
                    ingredientTags = setOf("Vegetarian"),
                    flavorStyle = "Light",
                    specialNeeds = setOf("Low Salt/Sugar", "No Onion/Garlic"),
                    supplyPeriods = setOf(SupplyPeriod.LUNCH, SupplyPeriod.DINNER)
                ),
                popularity = WindowPopularity.REGULAR
            ),
            ServiceWindow(
                id = "south_spicy_shrimp",
                name = "Window 3 - Spicy Shrimp",
                dish = Dish(
                    name = "Spicy Shrimp",
                    price = "£18.00",
                    spiciness = "Medium Spicy",
                    sweetness = "Not Sweet",
                    ingredientTags = setOf("Contains Seafood"),
                    flavorStyle = "Numbing & Spicy",
                    specialNeeds = setOf("Customizable"),
                    supplyPeriods = setOf(SupplyPeriod.DINNER)
                ),
                popularity = WindowPopularity.POPULAR
            ),
            ServiceWindow(
                id = "south_pumpkin",
                name = "Window 4 - Steamed Pumpkin",
                dish = Dish(
                    name = "Steamed Pumpkin",
                    price = "£7.00",
                    spiciness = "Not Spicy",
                    sweetness = "Slightly Sweet",
                    ingredientTags = setOf("Vegetarian"),
                    flavorStyle = "Light",
                    specialNeeds = setOf("No Onion/Garlic", "Low Salt/Sugar"),
                    supplyPeriods = setOf(SupplyPeriod.LUNCH, SupplyPeriod.DINNER)
                ),
                popularity = WindowPopularity.REGULAR
            ),
            ServiceWindow(
                id = "south_sour_potato",
                name = "Window 5 - Sour & Spicy Potato Shreds",
                dish = Dish(
                    name = "Sour & Spicy Potato Shreds",
                    price = "£8.00",
                    spiciness = "Medium Spicy",
                    sweetness = "Not Sweet",
                    ingredientTags = setOf("Vegetarian"),
                    flavorStyle = "Sour & Spicy",
                    specialNeeds = setOf("Customizable"),
                    supplyPeriods = setOf(SupplyPeriod.LUNCH, SupplyPeriod.DINNER)
                ),
                popularity = WindowPopularity.REGULAR
            ),
            ServiceWindow(
                id = "south_brown_sugar_milk_tea",
                name = "Window 6 - Brown Sugar Bubble Tea",
                dish = Dish(
                    name = "Brown Sugar Bubble Tea",
                    price = "£12.00",
                    spiciness = "Not Spicy",
                    sweetness = "Sweet",
                    ingredientTags = setOf("Contains Eggs/Dairy"),
                    flavorStyle = "Light",
                    specialNeeds = setOf("Customizable"),
                    supplyPeriods = setOf(SupplyPeriod.ALL_DAY)
                ),
                popularity = WindowPopularity.REGULAR
            )
        )
    )
)

private fun calculateQueueWait(
    period: SupplyPeriod,
    popularity: WindowPopularity,
    referenceTime: LocalTime
): Int {
    val effectivePeriod = if (period == SupplyPeriod.ALL_DAY) {
        SupplyPeriod.currentFor(referenceTime) ?: SupplyPeriod.ALL_DAY
    } else {
        period
    }

    val preset = queuePresets[effectivePeriod] ?: queuePresets[SupplyPeriod.ALL_DAY] ?: return 0
    val queueTime = when (popularity) {
        WindowPopularity.POPULAR -> preset.popular
        WindowPopularity.REGULAR -> preset.regular
    }

    val isPeak = preset.peakPeriod?.let { peak ->
        isWithin(referenceTime, peak.start, peak.end)
    } ?: false

    val peakMinutes = queueTime.peakMinutes
    return if (isPeak && peakMinutes != null) peakMinutes else queueTime.baseMinutes
}

private fun isWithin(time: LocalTime, start: LocalTime, end: LocalTime): Boolean {
    return !time.isBefore(start) && !time.isAfter(end)
}

private data class DishWaitResult(
    val window: ServiceWindow,
    val queueMinutes: Int,
    val totalMinutes: Int,
    val isCurrentlyAvailable: Boolean
) {
    val dish: Dish = window.dish
}

private enum class SortOption(val label: String) {
    ASCENDING("Wait Time: Shortest to Longest"),
    DESCENDING("Wait Time: Longest to Shortest");
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SortOptionSelector(
    selected: SortOption,
    onSelect: (SortOption) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Sort By",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SortOption.values().forEach { option ->
                FilterChip(
                    selected = option == selected,
                    onClick = { onSelect(option) },
                    label = { Text(option.label) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: androidx.navigation.NavController,
    isLoggedIn: Boolean = false,
    userRole: String = "Student",
    username: String = "",
    onLogout: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (isLoggedIn) "Welcome, $username ($userRole)" 
                        else "Campus Canteen Digital Menu"
                    )
                },
                actions = {
                    if (isLoggedIn) {
                        TextButton(onClick = onLogout) {
                            Text("Logout")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Canteen",
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Welcome to Campus Canteen Digital Menu",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "View menus, reviews, and more features",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            
            item {
    Text(
                    text = "Main Features",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            item {
                FeatureCard(
                    icon = Icons.Default.Home,
                    title = "View Canteens",
                    description = "Browse all campus canteens and window information",
                    onClick = { navController.navigate("canteens") }
                )
            }
            
            item {
                FeatureCard(
                    icon = Icons.Default.Menu,
                    title = "View Menu",
                    description = "View all dishes and price information",
                    onClick = { navController.navigate("dishes") }
                )
            }
            
            item {
                FeatureCard(
                    icon = Icons.Default.Star,
                    title = "Review System",
                    description = "View and post dish reviews",
                    onClick = { navController.navigate("reviews") }
                )
            }
            
            if (userRole == "Manager") {
                item {
                    FeatureCard(
                        icon = Icons.Default.Settings,
                        title = "Manager Portal",
                        description = "Canteen manager exclusive features",
                        onClick = { navController.navigate("manager") }
                    )
                }
            }
            
            if (!isLoggedIn) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { navController.navigate("login") }
                        ) {
                            Text("Login")
                        }
                        Button(
                            onClick = { navController.navigate("register") }
                        ) {
                            Text("Register")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Go to $title"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: androidx.navigation.NavController,
    onLogin: (String, String) -> Unit = { _, _ -> }
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "User Login",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { 
                    if (username.isNotBlank() && password.isNotBlank()) {
                        onLogin(username, "Student") // Default student role
                    }
                },
                enabled = username.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = { navController.navigate("register") }
            ) {
                Text("Don't have an account? Register now")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: androidx.navigation.NavController,
    onRegister: (String, String) -> Unit = { _, _ -> }
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Student") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "User Registration",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == "Student",
                        onClick = { role = "Student" }
                    )
                    Text("Student")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == "Manager",
                        onClick = { role = "Manager" }
                    )
                    Text("Manager")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { 
                    if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        onRegister(username, role)
                    }
                },
                enabled = username.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text("Already have an account? Login now")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanteensScreen(navController: androidx.navigation.NavController) {
    val canteens = sampleCanteens

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campus Canteens") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(canteens) { canteen ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                            Text(
                            text = canteen.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                            text = canteen.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        Spacer(modifier = Modifier.height(8.dp))
                            Text(
                            text = "Windows:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        canteen.windows.forEach { window ->
                            Text(
                                text = "- ${window.name} (Dish: ${window.dish.name})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DishesScreen(navController: androidx.navigation.NavController) {
    val canteens = sampleCanteens
    val spicinessOptions = listOf("Not Spicy", "Mildly Spicy", "Medium Spicy", "Very Spicy")
    val sweetnessOptions = listOf("Not Sweet", "Slightly Sweet", "Sweet")
    val ingredientOptions = listOf("Contains Meat", "Vegetarian", "Contains Seafood", "Contains Eggs/Dairy")
    val flavorOptions = listOf("Savory", "Sour & Spicy", "Sweet & Spicy", "Light", "Soy Sauce", "Numbing & Spicy")
    val specialNeedsOptions = listOf("No Onion/Garlic", "Low Salt/Sugar", "Customizable")
    val supplyPeriods = listOf(SupplyPeriod.BREAKFAST, SupplyPeriod.LUNCH, SupplyPeriod.DINNER, SupplyPeriod.ALL_DAY)

    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val now = remember { LocalTime.now() }
    val currentSupplyPeriod = remember(now) { SupplyPeriod.currentFor(now) }
    val defaultSupplyPeriod = remember(now) { SupplyPeriod.defaultFor(now) }

    var selectedCanteenId by remember { mutableStateOf(canteens.firstOrNull()?.id ?: "") }
    val selectedCanteen = remember(selectedCanteenId) {
        canteens.firstOrNull { it.id == selectedCanteenId }
    }
    val windowsForCanteen = selectedCanteen?.windows ?: emptyList()
    var selectedSupplyPeriod by remember { mutableStateOf<SupplyPeriod?>(null) }

    var travelMinutesInput by remember { mutableStateOf("") }
    val travelMinutesRaw = travelMinutesInput.toIntOrNull()
    val travelInputError = travelMinutesRaw != null && travelMinutesRaw !in 0..60
    val travelMinutesValue = if (!travelInputError && travelMinutesRaw != null) travelMinutesRaw else 0

    var selectedSpiciness by remember { mutableStateOf<String?>(null) }
    var selectedSweetness by remember { mutableStateOf<String?>(null) }
    var selectedFlavor by remember { mutableStateOf<String?>(null) }
    var selectedIngredientTypes by remember { mutableStateOf(setOf<String>()) }
    var selectedSpecialNeeds by remember { mutableStateOf(setOf<String>()) }
    var sortOption by remember { mutableStateOf(SortOption.ASCENDING) }

    val effectiveSupplyPeriod = selectedSupplyPeriod ?: defaultSupplyPeriod
    val queueReferenceTime = when {
        selectedSupplyPeriod != null -> effectiveSupplyPeriod.referenceTime
        currentSupplyPeriod != null -> now
        else -> effectiveSupplyPeriod.referenceTime
    }

    val filteredWindows = remember(
        windowsForCanteen,
        selectedSpiciness,
        selectedSweetness,
        selectedFlavor,
        effectiveSupplyPeriod,
        selectedIngredientTypes,
        selectedSpecialNeeds
    ) {
        windowsForCanteen.filter { window ->
            val dish = window.dish
            val spicinessMatch = selectedSpiciness?.let { dish.spiciness == it } ?: true
            val sweetnessMatch = selectedSweetness?.let { dish.sweetness == it } ?: true
            val flavorMatch = selectedFlavor?.let { dish.flavorStyle == it } ?: true
            val supplyTimeMatch = when (effectiveSupplyPeriod) {
                SupplyPeriod.ALL_DAY -> SupplyPeriod.ALL_DAY in dish.supplyPeriods
                else -> effectiveSupplyPeriod in dish.supplyPeriods || SupplyPeriod.ALL_DAY in dish.supplyPeriods
            }
            val ingredientMatch = if (selectedIngredientTypes.isEmpty()) {
                true
            } else {
                selectedIngredientTypes.any { it in dish.ingredientTags }
            }
            val specialNeedsMatch = if (selectedSpecialNeeds.isEmpty()) {
                true
            } else {
                selectedSpecialNeeds.all { it in dish.specialNeeds }
            }

            spicinessMatch &&
                sweetnessMatch &&
                flavorMatch &&
                supplyTimeMatch &&
                ingredientMatch &&
                specialNeedsMatch
        }
    }

    val manualPeriod = selectedSupplyPeriod
    val dishWaitResults = filteredWindows.map { window ->
        val dish = window.dish
        val queueMinutes = calculateQueueWait(effectiveSupplyPeriod, window.popularity, queueReferenceTime)
        val isCurrentlyAvailable = when {
            manualPeriod != null -> manualPeriod in dish.supplyPeriods || SupplyPeriod.ALL_DAY in dish.supplyPeriods
            SupplyPeriod.ALL_DAY in dish.supplyPeriods -> isWithin(now, SupplyPeriod.BREAKFAST.startTime, SupplyPeriod.DINNER.endTime)
            currentSupplyPeriod != null -> currentSupplyPeriod in dish.supplyPeriods || SupplyPeriod.ALL_DAY in dish.supplyPeriods
            else -> false
        }
        val totalMinutes = travelMinutesValue + queueMinutes
        DishWaitResult(window, queueMinutes, totalMinutes, isCurrentlyAvailable)
    }

    val sortedDishResults = when (sortOption) {
        SortOption.ASCENDING -> dishWaitResults.sortedWith(compareBy({ it.totalMinutes }, { it.dish.name }))
        SortOption.DESCENDING -> dishWaitResults.sortedWith(compareByDescending<DishWaitResult> { it.totalMinutes }.thenBy { it.dish.name })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dish Menu") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Filter Options",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Select Canteen",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        canteens.forEach { canteen ->
                            FilterChip(
                                selected = canteen.id == selectedCanteenId,
                                onClick = { selectedCanteenId = canteen.id },
                                label = { Text(canteen.name) }
                            )
                        }
                    }

                    selectedCanteen?.let {
                        Text(
                            text = it.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedTextField(
                        value = travelMinutesInput,
                        onValueChange = { raw ->
                            if (raw.isBlank() || (raw.all { it.isDigit() } && raw.length <= 2)) {
                                travelMinutesInput = raw
                            }
                        },
                        label = { Text("Travel Time (minutes)") },
                        placeholder = { Text("0-60") },
                        supportingText = {
                            Text("Enter travel time from your current location to ${selectedCanteen?.name ?: "selected canteen"}")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = travelInputError,
                    modifier = Modifier.fillMaxWidth()
                    )

                    if (travelInputError) {
                        Text(
                            text = "Please enter an integer value between 0-60",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    SingleSelectFilterSection(
                        title = "Spiciness",
                        options = spicinessOptions,
                        selectedOption = selectedSpiciness,
                        onOptionSelected = { option -> selectedSpiciness = option }
                    )

                    SingleSelectFilterSection(
                        title = "Sweetness",
                        options = sweetnessOptions,
                        selectedOption = selectedSweetness,
                        onOptionSelected = { option -> selectedSweetness = option }
                    )

                    MultiSelectFilterSection(
                        title = "Ingredient Type",
                        options = ingredientOptions,
                        selectedOptions = selectedIngredientTypes,
                        onSelectionChange = { updated -> selectedIngredientTypes = updated }
                    )

                    SingleSelectFilterSection(
                        title = "Flavor Style",
                        options = flavorOptions,
                        selectedOption = selectedFlavor,
                        onOptionSelected = { option -> selectedFlavor = option }
                    )

                    MultiSelectFilterSection(
                        title = "Special Needs",
                        options = specialNeedsOptions,
                        selectedOptions = selectedSpecialNeeds,
                        onSelectionChange = { updated -> selectedSpecialNeeds = updated }
                    )

                    SupplyTimeFilterSection(
                        periods = supplyPeriods,
                        selectedPeriod = selectedSupplyPeriod,
                        defaultPeriod = defaultSupplyPeriod,
                        currentPeriod = currentSupplyPeriod,
                        currentTimeLabel = now.format(timeFormatter),
                        onPeriodSelected = { period -> selectedSupplyPeriod = period }
                    )

                    SortOptionSelector(
                        selected = sortOption,
                        onSelect = { sortOption = it }
                    )
                }
            }

            if (sortedDishResults.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        tonalElevation = 2.dp,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                        modifier = Modifier
                            .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "No results",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "No dishes match the filter criteria",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Please try adjusting the filter options",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(sortedDishResults) { result ->
                    val dish = result.dish
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (result.isCurrentlyAvailable) 1f else 0.6f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                        Text(
                                text = dish.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = result.window.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = dish.price,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "Flavor: ${dish.flavorStyle} | Spiciness: ${dish.spiciness} | Sweetness: ${dish.sweetness}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Ingredients: ${dish.ingredientTags.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Supply Period: ${dish.supplyPeriods
                                    .sortedBy { it.order }
                                    .joinToString(", ") { it.displayLabel }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (dish.specialNeeds.isNotEmpty()) {
                                Text(
                                    text = "Special Needs: ${dish.specialNeeds.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "Queue Wait: ${result.queueMinutes} minutes",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Total Wait: ${result.totalMinutes} minutes (Travel ${travelMinutesValue} min + Queue ${result.queueMinutes} min)",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )

                            if (!result.isCurrentlyAvailable) {
                                AssistChip(
                                    onClick = {},
                                    enabled = false,
                                    label = { Text("Currently Unavailable") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SingleSelectFilterSection(
    title: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selectedOption == option,
                    onClick = {
                        onOptionSelected(if (selectedOption == option) null else option)
                    },
                    label = { Text(option) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MultiSelectFilterSection(
    title: String,
    options: List<String>,
    selectedOptions: Set<String>,
    onSelectionChange: (Set<String>) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = option in selectedOptions
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val updated = if (isSelected) {
                            selectedOptions - option
                        } else {
                            selectedOptions + option
                        }
                        onSelectionChange(updated)
                    },
                    label = { Text(option) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SupplyTimeFilterSection(
    periods: List<SupplyPeriod>,
    selectedPeriod: SupplyPeriod?,
    defaultPeriod: SupplyPeriod,
    currentPeriod: SupplyPeriod?,
    currentTimeLabel: String,
    onPeriodSelected: (SupplyPeriod?) -> Unit
) {
    val effectivePeriod = selectedPeriod ?: defaultPeriod
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Supply Period",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = buildString {
                append("Current Time: $currentTimeLabel")
                append(" | Current Supply: ")
                append(currentPeriod?.displayLabel ?: "Non-Supply Period")
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            periods.sortedBy { it.order }.forEach { period ->
                FilterChip(
                    selected = effectivePeriod == period,
                    onClick = {
                        if (selectedPeriod == period) {
                            onPeriodSelected(null)
                        } else {
                            onPeriodSelected(period)
                        }
                    },
                    label = { Text(period.displayLabel) }
                )
            }
        }

        val selectionLabel = if (selectedPeriod == null) {
            "Current Filter: ${effectivePeriod.displayLabel} (Auto)"
        } else {
            "Current Filter: ${effectivePeriod.displayLabel}"
        }

        Text(
            text = selectionLabel,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (selectedPeriod != null) {
            AssistChip(
                onClick = { onPeriodSelected(null) },
                label = { Text("Reset to Current Period") }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerScreen(navController: androidx.navigation.NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manager Panel") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Manager Functions",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            item {
                FeatureCard(
                    icon = Icons.Default.Add,
                    title = "Add Dish",
                    description = "Add new dishes to the menu",
                    onClick = { navController.navigate("add_dish") }
                )
            }
            
            item {
                FeatureCard(
                    icon = Icons.Default.Edit,
                    title = "Edit Dish",
                    description = "Modify existing dish information",
                    onClick = { navController.navigate("edit_dish") }
                )
            }
            
            item {
                FeatureCard(
                    icon = Icons.Default.Star,
                    title = "Manage Reviews",
                    description = "View student reviews and replies",
                    onClick = { navController.navigate("manage_reviews") }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(navController: androidx.navigation.NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review System") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Dish Reviews",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Red-Braised Pork Rice",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "⭐⭐⭐⭐⭐ 5.0 stars",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = "Delicious! Tender meat with rich flavor.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Reviewer: John • 2 hours ago",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Kung Pao Chicken",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "⭐⭐⭐⭐ 4.0 stars",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = "Good taste, but a bit salty.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Reviewer: Jane • 1 day ago",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            
            item {
                Button(
                    onClick = { navController.navigate("add_review") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add New Review")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessDeniedScreen(navController: androidx.navigation.NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Access Restricted") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Access Restricted",
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Access Restricted",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "This feature is only available to managers",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Back")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewScreen(navController: androidx.navigation.NavController) {
    var selectedDish by remember { mutableStateOf("Red-Braised Pork Rice") }
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Review") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Review Dish",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            item {
                OutlinedTextField(
                    value = selectedDish,
                    onValueChange = { selectedDish = it },
                    label = { Text("Select Dish") },
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = "Select") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                Text("Rating:")
                Row {
                    repeat(5) { index ->
                        IconButton(
                            onClick = { rating = index + 1 }
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Star ${index + 1}",
                                tint = if (index < rating) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            item {
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Review Content") },
                    placeholder = { Text("Please enter your review...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }
            
            item {
                Button(
                    onClick = { showSuccessDialog = true },
                    enabled = comment.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Review")
                }
            }
        }
    }
    
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Review Submitted") },
            text = { Text("Your review has been submitted. Thank you for your feedback!") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showSuccessDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDishScreen(navController: androidx.navigation.NavController) {
    var dishName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Chinese") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Dish") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Add New Dish",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            item {
                OutlinedTextField(
                    value = dishName,
                    onValueChange = { dishName = it },
                    label = { Text("Dish Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    placeholder = { Text("e.g., 12.50") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Dish Description") },
                    placeholder = { Text("Please enter dish description...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }
            
            item {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Dish Category") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                Button(
                    onClick = { showSuccessDialog = true },
                    enabled = dishName.isNotBlank() && price.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Dish")
                }
            }
        }
    }
    
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Added Successfully") },
            text = { Text("Dish has been successfully added to the menu!") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showSuccessDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDishScreen(navController: androidx.navigation.NavController) {
    val dishes = sampleCanteens.flatMap { it.windows }.map { it.dish }
    var selectedDish by remember { mutableStateOf(dishes[0]) }
    var price by remember { mutableStateOf("12.50") }
    var description by remember { mutableStateOf("Classic Chinese Dish") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Dish") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Edit Dish Information",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            item {
                OutlinedTextField(
                    value = selectedDish.name,
                    onValueChange = { selectedDish = selectedDish.copy(name = it) },
                    label = { Text("Select Dish to Edit") },
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = "Select") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Dish Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }
            
            item {
                Button(
                    onClick = { showSuccessDialog = true },
                    enabled = price.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
    
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Updated Successfully") },
            text = { Text("Dish information has been successfully updated!") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showSuccessDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageReviewsScreen(navController: androidx.navigation.NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Reviews") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Student Review Management",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Red-Braised Pork Rice - John",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "⭐⭐⭐⭐⭐ 5.0 stars",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        Text(
                            text = "Delicious! Tender meat with rich flavor.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Button(
                            onClick = { /* Reply to Review */ },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Reply to Review")
                        }
                    }
                }
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kung Pao Chicken - Jane",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "⭐⭐ 2.0 stars",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        Text(
                            text = "Too salty, and the chicken is tough.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Button(
                            onClick = { /* Reply to Review */ },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Reply to Review")
                        }
                    }
                }
            }
        }
    }
}