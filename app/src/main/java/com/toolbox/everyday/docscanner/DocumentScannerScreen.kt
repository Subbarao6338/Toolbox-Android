package com.toolbox.everyday.docscanner

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun DocumentScannerScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var working by remember { mutableStateOf(false) }
    var resultFile by remember { mutableStateOf<File?>(null) }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        working = true
        error = null
        resultFile = null
        scope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                }
                if (bitmap == null) {
                    error = "Could not load selected image."
                } else {
                    previewBitmap = bitmap
                    val pdfFile = withContext(Dispatchers.IO) { createPdfFromBitmap(context, bitmap) }
                    resultFile = pdfFile
                }
            } catch (e: Exception) {
                error = e.message ?: "Failed to convert image to PDF"
            } finally {
                working = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Select a document image or photo to format and convert it into a standard PDF document offline.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            enabled = !working,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Select Document Image")
        }

        if (working) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(Modifier.width(20.dp).height(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Generating PDF…")
            }
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        previewBitmap?.let { bmp ->
            Text("Preview:", fontWeight = FontWeight.Bold)
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Document Preview",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        resultFile?.let { f ->
            Text("Scan ready!", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            Button(
                onClick = { sharePdf(context, f) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Share PDF")
            }
        }
    }
}

private fun createPdfFromBitmap(context: Context, bitmap: Bitmap): File {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    page.canvas.drawBitmap(bitmap, 0f, 0f, null)
    pdfDocument.finishPage(page)

    val outDir = File(context.cacheDir, "pdf_out").apply { mkdirs() }
    val outFile = File(outDir, "scan_${System.currentTimeMillis()}.pdf")
    FileOutputStream(outFile).use { out ->
        pdfDocument.writeTo(out)
    }
    pdfDocument.close()
    return outFile
}

private fun sharePdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share PDF"))
}
