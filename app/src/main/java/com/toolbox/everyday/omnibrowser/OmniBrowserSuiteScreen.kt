package com.toolbox.everyday.omnibrowser

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.security.MessageDigest
import java.util.Base64

@Composable
fun OmniBrowserSuiteScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Crypto & Hash", "Network Tools", "Markdown Editor")

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
                0 -> CryptoTab()
                1 -> NetworkTab()
                2 -> MarkdownEditorTab()
            }
        }
    }
}

@Composable
private fun CryptoTab() {
    var textInput by remember { mutableStateOf("Omni Tool Suite Password") }
    val scrollState = rememberScrollState()

    val md5 = remember(textInput) { hashString("MD5", textInput) }
    val sha1 = remember(textInput) { hashString("SHA-1", textInput) }
    val sha256 = remember(textInput) { hashString("SHA-256", textInput) }
    val base64Encoded = remember(textInput) {
        try { Base64.getEncoder().encodeToString(textInput.toByteArray()) } catch (e: Exception) { "" }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Cryptography & Hashing Engine", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Input Text / Secret") },
            modifier = Modifier.fillMaxWidth()
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CryptoResultRow("MD5 Digest", md5)
                CryptoResultRow("SHA-1 Digest", sha1)
                CryptoResultRow("SHA-256 Digest", sha256)
                CryptoResultRow("Base64 Encoded", base64Encoded)
            }
        }
    }
}

@Composable
private fun NetworkTab() {
    var hostInput by remember { mutableStateOf("192.168.1.1") }
    var subnetInput by remember { mutableStateOf("24") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Network Diagnostics & Subnet Calculator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = hostInput,
            onValueChange = { hostInput = it },
            label = { Text("Target Host / IP") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = subnetInput,
            onValueChange = { subnetInput = it },
            label = { Text("Subnet Mask (CIDR, e.g., 24)") },
            modifier = Modifier.fillMaxWidth()
        )

        val cidr = subnetInput.toIntOrNull() ?: 24
        val totalHosts = (1 shl (32 - cidr)) - 2

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Subnet Details (/ $cidr):", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                CryptoResultRow("Available Usable Hosts", totalHosts.coerceAtLeast(0).toString())
                CryptoResultRow("Network Type", if (cidr >= 24) "Class C / Local LAN" else "Class A/B / Large Network")
                CryptoResultRow("Status", "Local Interface Active")
            }
        }
    }
}

@Composable
private fun MarkdownEditorTab() {
    var markdownText by remember { mutableStateOf("# Omni Markdown Editor\n\n- Real-time preview\n- Local formatting\n- Clean Jetpack Compose UI") }
    var isPreviewMode by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Markdown & Code Editor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            FilterChip(
                selected = isPreviewMode,
                onClick = { isPreviewMode = !isPreviewMode },
                label = { Text(if (isPreviewMode) "Edit Mode" else "Preview") }
            )
        }

        if (isPreviewMode) {
            val scrollState = rememberScrollState()
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(markdownText, style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            OutlinedTextField(
                value = markdownText,
                onValueChange = { markdownText = it },
                modifier = Modifier.fillMaxSize(),
                label = { Text("Write Markdown...") }
            )
        }
    }
}

@Composable
private fun CryptoResultRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

private fun hashString(algorithm: String, input: String): String {
    return try {
        val digest = MessageDigest.getInstance(algorithm)
        val bytes = digest.digest(input.toByteArray())
        bytes.joinToString("") { "%02x".format(it) }
    } catch (e: Exception) {
        "Error calculating hash"
    }
}
