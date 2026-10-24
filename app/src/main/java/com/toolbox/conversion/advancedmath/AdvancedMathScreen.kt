package com.toolbox.conversion.advancedmath

import androidx.compose.foundation.layout.*
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
import kotlin.math.PI

@Composable
fun AdvancedMathScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Geometry", "Health", "Fuel & Mileage", "Ohm's Law")

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
                0 -> GeometryTab()
                1 -> HealthTab()
                2 -> FuelTab()
                3 -> OhmsLawTab()
            }
        }
    }
}

@Composable
private fun GeometryTab() {
    var shape by remember { mutableStateOf("Circle") }
    val shapes = listOf("Circle", "Triangle", "Trapezoid", "Cylinder", "Sphere")

    var val1 by remember { mutableStateOf("") }
    var val2 by remember { mutableStateOf("") }
    var val3 by remember { mutableStateOf("") }

    val v1 = val1.toDoubleOrNull() ?: 0.0
    val v2 = val2.toDoubleOrNull() ?: 0.0
    val v3 = val3.toDoubleOrNull() ?: 0.0

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Select Shape", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            shapes.take(3).forEach { s ->
                FilterChip(
                    selected = shape == s,
                    onClick = { shape = s; val1 = ""; val2 = ""; val3 = "" },
                    label = { Text(s) }
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            shapes.drop(3).forEach { s ->
                FilterChip(
                    selected = shape == s,
                    onClick = { shape = s; val1 = ""; val2 = ""; val3 = "" },
                    label = { Text(s) }
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (shape) {
                    "Circle" -> {
                        OutlinedTextField(
                            value = val1,
                            onValueChange = { val1 = it },
                            label = { Text("Radius (r)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        val area = PI * v1 * v1
                        val perimeter = 2 * PI * v1
                        ResultRow("Area (A)", String.format(Locale.US, "%.4f", area))
                        ResultRow("Circumference (C)", String.format(Locale.US, "%.4f", perimeter))
                    }
                    "Triangle" -> {
                        OutlinedTextField(
                            value = val1,
                            onValueChange = { val1 = it },
                            label = { Text("Base (b)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = val2,
                            onValueChange = { val2 = it },
                            label = { Text("Height (h)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        val area = 0.5 * v1 * v2
                        ResultRow("Area (A)", String.format(Locale.US, "%.4f", area))
                    }
                    "Trapezoid" -> {
                        OutlinedTextField(
                            value = val1,
                            onValueChange = { val1 = it },
                            label = { Text("Base 1 (a)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = val2,
                            onValueChange = { val2 = it },
                            label = { Text("Base 2 (b)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = val3,
                            onValueChange = { val3 = it },
                            label = { Text("Height (h)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        val area = 0.5 * (v1 + v2) * v3
                        ResultRow("Area (A)", String.format(Locale.US, "%.4f", area))
                    }
                    "Cylinder" -> {
                        OutlinedTextField(
                            value = val1,
                            onValueChange = { val1 = it },
                            label = { Text("Radius (r)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = val2,
                            onValueChange = { val2 = it },
                            label = { Text("Height (h)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        val volume = PI * v1 * v1 * v2
                        val surfaceArea = 2 * PI * v1 * (v1 + v2)
                        ResultRow("Volume (V)", String.format(Locale.US, "%.4f", volume))
                        ResultRow("Surface Area (S)", String.format(Locale.US, "%.4f", surfaceArea))
                    }
                    "Sphere" -> {
                        OutlinedTextField(
                            value = val1,
                            onValueChange = { val1 = it },
                            label = { Text("Radius (r)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        val volume = (4.0 / 3.0) * PI * v1 * v1 * v1
                        val surfaceArea = 4 * PI * v1 * v1
                        ResultRow("Volume (V)", String.format(Locale.US, "%.4f", volume))
                        ResultRow("Surface Area (S)", String.format(Locale.US, "%.4f", surfaceArea))
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthTab() {
    var height by remember { mutableStateOf("175") }
    var weight by remember { mutableStateOf("70") }
    var age by remember { mutableStateOf("25") }

    val h = height.toDoubleOrNull() ?: 0.0
    val w = weight.toDoubleOrNull() ?: 0.0
    val a = age.toDoubleOrNull() ?: 0.0

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Health & Fitness Calculator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Height (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age (years)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        val bmi = if (h > 0) w / ((h / 100.0) * (h / 100.0)) else 0.0
        val bmr = if (h > 0 && w > 0 && a > 0) 10 * w + 6.25 * h - 5 * a + 5 else 0.0
        val idealWeightMin = if (h > 0) 18.5 * (h / 100.0) * (h / 100.0) else 0.0
        val idealWeightMax = if (h > 0) 24.9 * (h / 100.0) * (h / 100.0) else 0.0

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ResultRow("BMI Index", String.format(Locale.US, "%.1f", bmi))
                ResultRow("BMR (Basal Metabolic Rate)", String.format(Locale.US, "%.0f kcal/day", bmr))
                ResultRow("Ideal Weight Range", String.format(Locale.US, "%.1f - %.1f kg", idealWeightMin, idealWeightMax))
            }
        }
    }
}

@Composable
private fun FuelTab() {
    var distance by remember { mutableStateOf("500") }
    var fuelUsed by remember { mutableStateOf("40") }
    var fuelPrice by remember { mutableStateOf("1.5") }

    val d = distance.toDoubleOrNull() ?: 0.0
    val f = fuelUsed.toDoubleOrNull() ?: 0.0
    val p = fuelPrice.toDoubleOrNull() ?: 0.0

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Fuel & Mileage Consumption", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = distance,
            onValueChange = { distance = it },
            label = { Text("Distance (km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = fuelUsed,
            onValueChange = { fuelUsed = it },
            label = { Text("Fuel Used (Liters)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = fuelPrice,
            onValueChange = { fuelPrice = it },
            label = { Text("Price per Liter ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        val lPer100km = if (d > 0) (f / d) * 100.0 else 0.0
        val mpgUS = if (lPer100km > 0) 235.215 / lPer100km else 0.0
        val totalCost = f * p
        val costPerKm = if (d > 0) totalCost / d else 0.0

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ResultRow("Consumption (L/100km)", String.format(Locale.US, "%.2f", lPer100km))
                ResultRow("Economy (US MPG)", String.format(Locale.US, "%.2f", mpgUS))
                ResultRow("Total Trip Fuel Cost", String.format(Locale.US, "$%.2f", totalCost))
                ResultRow("Cost per km", String.format(Locale.US, "$%.3f", costPerKm))
            }
        }
    }
}

@Composable
private fun OhmsLawTab() {
    var voltage by remember { mutableStateOf("12") }
    var current by remember { mutableStateOf("2") }
    var resistance by remember { mutableStateOf("") }

    val v = voltage.toDoubleOrNull()
    val i = current.toDoubleOrNull()
    val r = resistance.toDoubleOrNull()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Ohm's Law & Electronics Calculator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = voltage,
            onValueChange = { voltage = it },
            label = { Text("Voltage (V in Volts)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = current,
            onValueChange = { current = it },
            label = { Text("Current (I in Amps)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = resistance,
            onValueChange = { resistance = it },
            label = { Text("Resistance (R in Ohms - leave blank to solve)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        val calcR = if (v != null && i != null && i != 0.0) v / i else r ?: 0.0
        val calcV = if (i != null && r != null) i * r else v ?: 0.0
        val calcI = if (v != null && r != null && r != 0.0) v / r else i ?: 0.0
        val power = calcV * calcI

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ResultRow("Voltage (V)", String.format(Locale.US, "%.2f V", calcV))
                ResultRow("Current (I)", String.format(Locale.US, "%.2f A", calcI))
                ResultRow("Resistance (R)", String.format(Locale.US, "%.2f Ω", calcR))
                ResultRow("Power (P)", String.format(Locale.US, "%.2f Watts", power))
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}
