package com.mudasir.nexacvai.presentation.ui.templates.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.presentation.ui.components.NexaAlertDialog
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField

@Composable
fun CustomTemplateImportDialog(
    onDismissRequest: () -> Unit,
    onImportJson: (String) -> Unit
) {
    var jsonText by remember { mutableStateOf("") }

    NexaAlertDialog(
        onDismissRequest = onDismissRequest,
        title = "Import Custom Template",
        confirmLabel = "Import Template",
        onConfirm = {
            if (jsonText.isNotBlank()) {
                onImportJson(jsonText)
                onDismissRequest()
            }
        },
        dismissLabel = "Cancel",
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Paste a custom template JSON schema file to register a new dynamic layout."
                )
                NexaTextField(
                    value = jsonText,
                    onValueChange = { jsonText = it },
                    label = "Template JSON Schema*",
                    placeholder = "{\n  \"name\": \"Custom Dark\",\n  \"category\": \"IMPORTED\",\n  \"primaryColorHex\": \"#0F172A\"\n}",
                    singleLine = false,
                    minLines = 4,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
