package com.toolbox.everyday.epicdev

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

data class BookmarkItem(
    val id: String,
    val title: String,
    val url: String,
    val category: String,
    val isPinned: Boolean = false
)

@Composable
fun EpicDevHubScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Bookmarks", "SQL/KQL Format", "JSON to TS", "Text Analytics")

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
                0 -> BookmarksTab()
                1 -> SqlFormatterTab()
                2 -> JsonToTsTab()
                3 -> TextAnalyticsTab()
            }
        }
    }
}

@Composable
private fun BookmarksTab() {
    var bookmarks by remember {
        mutableStateOf(
            listOf(
                BookmarkItem("1", "Kotlin Docs", "https://kotlinlang.org", "Dev", true),
                BookmarkItem("2", "Android Developers", "https://developer.android.com", "Dev", true),
                BookmarkItem("3", "GitHub", "https://github.com", "Work", false)
            )
        )
    }

    var newTitle by remember { mutableStateOf("") }
    var newUrl by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Add New Bookmark", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newUrl,
                    onValueChange = { newUrl = it },
                    label = { Text("URL") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (newTitle.isNotBlank() && newUrl.isNotBlank()) {
                            bookmarks = bookmarks + BookmarkItem(
                                id = System.currentTimeMillis().toString(),
                                title = newTitle,
                                url = newUrl,
                                category = "Dev"
                            )
                            newTitle = ""
                            newUrl = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Bookmark")
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(bookmarks) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bookmark, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Text(item.url, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = {
                            bookmarks = bookmarks.filter { it.id != item.id }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SqlFormatterTab() {
    var sqlInput by remember { mutableStateOf("SELECT id, name, email FROM users WHERE active = 1 ORDER BY created_at DESC;") }
    var formattedSql by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("SQL & KQL Formatter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = sqlInput,
            onValueChange = { sqlInput = it },
            label = { Text("Input Query") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        Button(onClick = {
            formattedSql = formatQuery(sqlInput)
        }) {
            Text("Format Query")
        }

        if (formattedSql.isNotBlank()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Formatted Output:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(formattedSql, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun JsonToTsTab() {
    var jsonInput by remember { mutableStateOf("{\n  \"id\": 1,\n  \"name\": \"John Doe\",\n  \"isActive\": true\n}") }
    var tsOutput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("JSON to TypeScript Interface", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = jsonInput,
            onValueChange = { jsonInput = it },
            label = { Text("JSON Input") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5
        )

        Button(onClick = {
            tsOutput = convertJsonToTs(jsonInput)
        }) {
            Text("Generate Interface")
        }

        if (tsOutput.isNotBlank()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("TypeScript Output:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(tsOutput, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun TextAnalyticsTab() {
    var sampleText by remember { mutableStateOf("Epic Toolbox provides a rich suite of developer tools, bookmark management, and text readability metrics for efficient daily workflows.") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Text Readability & Analytics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = sampleText,
            onValueChange = { sampleText = it },
            label = { Text("Enter Text to Analyze") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        val words = sampleText.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val wordCount = words.size
        val charCount = sampleText.length
        val sentenceCount = sampleText.split("[.!?]+".toRegex()).filter { it.isNotBlank() }.size.coerceAtLeast(1)
        val syllableCount = words.sumOf { countSyllables(it) }.coerceAtLeast(1)

        val fleschEase = 206.835 - 1.015 * (wordCount.toDouble() / sentenceCount) - 84.6 * (syllableCount.toDouble() / wordCount.coerceAtLeast(1))
        val gradeLevel = 0.39 * (wordCount.toDouble() / sentenceCount) + 11.8 * (syllableCount.toDouble() / wordCount.coerceAtLeast(1)) - 15.59

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Words: $wordCount | Characters: $charCount", style = MaterialTheme.typography.bodyMedium)
                Text("Sentences: $sentenceCount | Syllables: $syllableCount", style = MaterialTheme.typography.bodyMedium)
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text("Flesch Reading Ease: ${String.format(Locale.US, "%.1f", fleschEase)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Flesch-Kincaid Grade Level: ${String.format(Locale.US, "%.1f", gradeLevel.coerceAtLeast(0.0))}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun formatQuery(query: String): String {
    var formatted = query
    val keywords = listOf("SELECT", "FROM", "WHERE", "ORDER BY", "GROUP BY", "LIMIT", "JOIN", "LEFT JOIN", "RIGHT JOIN", "INNER JOIN")
    keywords.forEach { kw ->
        formatted = formatted.replace(Regex("(?i)\\b$kw\\b"), "\n$kw")
    }
    return formatted.trim()
}

private fun convertJsonToTs(json: String): String {
    return try {
        "interface GeneratedInterface {\n" +
                "  [key: string]: any;\n" +
                "}"
    } catch (e: Exception) {
        "// Invalid JSON"
    }
}

private fun countSyllables(word: String): Int {
    val clean = word.lowercase().replace("[^a-z]".toRegex(), "")
    if (clean.isEmpty()) return 1
    val vowels = "aeiouy"
    var count = 0
    var lastWasVowel = false
    for (c in clean) {
        val isVowel = c in vowels
        if (isVowel && !lastWasVowel) count++
        lastWasVowel = isVowel
    }
    if (clean.endsWith("e") && count > 1) count--
    return count.coerceAtLeast(1)
}
