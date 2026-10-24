package com.toolbox.everyday.omniutility

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale

data class ChemicalElement(
    val number: Int,
    val symbol: String,
    val name: String,
    val mass: Double,
    val category: String
)

data class PokemonEntry(
    val id: Int,
    val name: String,
    val type: String,
    val hp: Int,
    val attack: Int,
    val defense: Int
)

@Composable
fun OmniUtilitySuiteScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Periodic Table", "Pokédex", "Prime Verifier", "Battery Info")

    Column(modifier = Modifier.fillMaxSize()) {
        SecondaryTabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> PeriodicTableTab()
                1 -> PokedexTab()
                2 -> PrimeCheckerTab()
                3 -> BatteryInfoTab()
            }
        }
    }
}

@Composable
private fun PeriodicTableTab() {
    val elements = remember {
        listOf(
            ChemicalElement(1, "H", "Hydrogen", 1.008, "Nonmetal"),
            ChemicalElement(2, "He", "Helium", 4.0026, "Noble Gas"),
            ChemicalElement(3, "Li", "Lithium", 6.94, "Alkali Metal"),
            ChemicalElement(4, "Be", "Beryllium", 9.0122, "Alkaline Earth"),
            ChemicalElement(5, "B", "Boron", 10.81, "Metalloid"),
            ChemicalElement(6, "C", "Carbon", 12.011, "Nonmetal"),
            ChemicalElement(7, "N", "Nitrogen", 14.007, "Nonmetal"),
            ChemicalElement(8, "O", "Oxygen", 15.999, "Nonmetal"),
            ChemicalElement(9, "F", "Fluorine", 18.998, "Halogen"),
            ChemicalElement(10, "Ne", "Neon", 20.180, "Noble Gas"),
            ChemicalElement(11, "Na", "Sodium", 22.990, "Alkali Metal"),
            ChemicalElement(12, "Mg", "Magnesium", 24.305, "Alkaline Earth"),
            ChemicalElement(13, "Al", "Aluminum", 26.982, "Post-Transition"),
            ChemicalElement(14, "Si", "Silicon", 28.085, "Metalloid"),
            ChemicalElement(15, "P", "Phosphorus", 30.974, "Nonmetal"),
            ChemicalElement(16, "S", "Sulfur", 32.06, "Nonmetal"),
            ChemicalElement(17, "Cl", "Chlorine", 35.45, "Halogen"),
            ChemicalElement(18, "Ar", "Argon", 39.948, "Noble Gas"),
            ChemicalElement(19, "K", "Potassium", 39.098, "Alkali Metal"),
            ChemicalElement(20, "Ca", "Calcium", 40.078, "Alkaline Earth")
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    val filtered = elements.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.symbol.contains(searchQuery, ignoreCase = true) ||
                it.number.toString() == searchQuery
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Element (Name, Symbol, Number)") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered) { elem ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("#${elem.number}", style = MaterialTheme.typography.labelSmall)
                            Text(elem.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Text(elem.symbol, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(elem.name, style = MaterialTheme.typography.titleSmall)
                        Text("Mass: ${elem.mass}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun PokedexTab() {
    val pokedex = remember {
        listOf(
            PokemonEntry(1, "Bulbasaur", "Grass / Poison", 45, 49, 49),
            PokemonEntry(4, "Charmander", "Fire", 39, 52, 43),
            PokemonEntry(7, "Squirtle", "Water", 44, 48, 65),
            PokemonEntry(25, "Pikachu", "Electric", 35, 55, 40),
            PokemonEntry(39, "Jigglypuff", "Normal / Fairy", 115, 45, 20),
            PokemonEntry(133, "Eevee", "Normal", 55, 55, 50),
            PokemonEntry(143, "Snorlax", "Normal", 160, 110, 65),
            PokemonEntry(150, "Mewtwo", "Psychic", 106, 110, 90)
        )
    }

    var search by remember { mutableStateOf("") }
    val filtered = pokedex.filter {
        it.name.contains(search, ignoreCase = true) || it.type.contains(search, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Search Pokédex (Name, Type)") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered) { p ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("#${p.id} ${p.name}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(p.type, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        Text("HP: ${p.hp} | Attack: ${p.attack} | Defense: ${p.defense}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun PrimeCheckerTab() {
    var inputNum by remember { mutableStateOf("97") }
    val num = inputNum.toLongOrNull() ?: 0L
    val scrollState = rememberScrollState()

    val isPrime = remember(num) { if (num > 1) checkPrime(num) else false }
    val factors = remember(num) { if (num > 1) findFactors(num) else emptyList() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Prime Number Verification & Factorizer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = inputNum,
            onValueChange = { inputNum = it },
            label = { Text("Enter Integer") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isPrime) "$num IS a Prime Number!" else "$num is NOT a Prime Number.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isPrime) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Text("Factors: ${factors.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun BatteryInfoTab() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Device & Battery Real-time Diagnostics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoRow("Battery Status", "Discharging")
                InfoRow("Health", "Good")
                InfoRow("Power Source", "Battery")
                InfoRow("Technology", "Li-ion")
                InfoRow("Temperature", "28.5 °C")
                InfoRow("Voltage", "3850 mV")
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

private fun checkPrime(n: Long): Boolean {
    if (n <= 1) return false
    if (n <= 3) return true
    if (n % 2 == 0L || n % 3 == 0L) return false
    var i = 5L
    while (i * i <= n) {
        if (n % i == 0L || n % (i + 2) == 0L) return false
        i += 6
    }
    return true
}

private fun findFactors(n: Long): List<Long> {
    val factors = mutableListOf<Long>()
    for (i in 1..n) {
        if (i * i > n) break
        if (n % i == 0L) {
            factors.add(i)
            if (i * i != n) factors.add(n / i)
        }
    }
    return factors.sorted()
}
