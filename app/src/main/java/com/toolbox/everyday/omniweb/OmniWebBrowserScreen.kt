package com.toolbox.everyday.omniweb

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.io.ByteArrayInputStream

@Composable
fun OmniWebBrowserScreen() {
    var urlInput by remember { mutableStateOf("https://wikipedia.org") }
    var currentUrl by remember { mutableStateOf("https://wikipedia.org") }
    var webView: WebView? by remember { mutableStateOf(null) }
    var adBlockEnabled by remember { mutableStateOf(true) }
    var viewMode by remember { mutableStateOf("Normal") } // Normal, Reader, Source
    var pageSource by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val adDomains = remember { setOf("doubleclick.net", "googleadservices.com", "adnxs.com", "pagead2.googlesyndication.com") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Address & Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = { webView?.goBack() },
                enabled = webView?.canGoBack() == true
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            IconButton(
                onClick = { webView?.goForward() },
                enabled = webView?.canGoForward() == true
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward")
            }
            IconButton(onClick = { webView?.reload() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }

            OutlinedTextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = {
                    val target = if (!urlInput.startsWith("http://") && !urlInput.startsWith("https://")) {
                        "https://" + urlInput
                    } else urlInput
                    currentUrl = target
                    webView?.loadUrl(target)
                })
            )

            IconButton(onClick = {
                val target = if (!urlInput.startsWith("http://") && !urlInput.startsWith("https://")) {
                    "https://" + urlInput
                } else urlInput
                currentUrl = target
                webView?.loadUrl(target)
            }) {
                Icon(Icons.Default.Search, contentDescription = "Go")
            }
        }

        // Feature Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("AdBlocker", style = MaterialTheme.typography.bodySmall)
                Switch(
                    checked = adBlockEnabled,
                    onCheckedChange = { adBlockEnabled = it }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FilterChip(
                    selected = viewMode == "Normal",
                    onClick = { viewMode = "Normal" },
                    label = { Text("Web") }
                )
                FilterChip(
                    selected = viewMode == "Reader",
                    onClick = { viewMode = "Reader" },
                    label = { Text("Reader") }
                )
                FilterChip(
                    selected = viewMode == "Source",
                    onClick = {
                        viewMode = "Source"
                        webView?.evaluateJavascript("(function() { return document.documentElement.outerHTML; })();") { html ->
                            pageSource = html ?: "No source available."
                        }
                    },
                    label = { Text("Source") }
                )
            }
        }

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Box(modifier = Modifier.weight(1f)) {
            when (viewMode) {
                "Normal", "Reader" -> {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.loadWithOverviewMode = true
                                settings.useWideViewPort = true

                                webViewClient = object : WebViewClient() {
                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        super.onPageFinished(view, url)
                                        isLoading = false
                                        if (url != null) {
                                            currentUrl = url
                                            urlInput = url
                                        }
                                        if (viewMode == "Reader") {
                                            view?.evaluateJavascript(
                                                "document.querySelectorAll('header, footer, nav, sidebar, .ad, .ads').forEach(e => e.remove());" +
                                                        "document.body.style.fontSize='18px';" +
                                                        "document.body.style.lineHeight='1.6';" +
                                                        "document.body.style.maxWidth='800px';" +
                                                        "document.body.style.margin='0 auto';" +
                                                        "document.body.style.padding='16px';", null
                                            )
                                        }
                                    }

                                    override fun shouldInterceptRequest(
                                        view: WebView?,
                                        request: WebResourceRequest?
                                    ): WebResourceResponse? {
                                        if (adBlockEnabled && request != null) {
                                            val host = request.url.host ?: ""
                                            if (adDomains.any { host.contains(it) }) {
                                                return WebResourceResponse("text/plain", "UTF-8", ByteArrayInputStream("".toByteArray()))
                                            }
                                        }
                                        return super.shouldInterceptRequest(view, request)
                                    }
                                }
                                webView = this
                                loadUrl(currentUrl)
                            }
                        },
                        update = { wv ->
                            webView = wv
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                "Source" -> {
                    val scrollState = rememberScrollState()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState)
                    ) {
                        Text(
                            text = pageSource.ifBlank { "Loading page source..." },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
