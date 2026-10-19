package com.aistudio.shoppinghospitalar.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aistudio.shoppinghospitalar.data.model.DavCategory
import com.aistudio.shoppinghospitalar.theme.PurplePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDavDialog(
    onDismiss: () -> Unit,
    onConfirm: (client: String, description: String, value: Double, category: DavCategory, dueAt: String) -> Unit
) {
    var clientName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var valueStr by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(DavCategory.GERAL) }
    var dueAt by remember { mutableStateOf("2026-10-30") }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("dialog_new_dav"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cadastrar Novo DAV",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PurplePrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Nome do Cliente / Hospital") },
                    placeholder = { Text("Ex: Hospital Santa Casa") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_client_name"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição dos Insumos / Equipamentos") },
                    placeholder = { Text("Ex: Lote de agulhas e bombas de infusão") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_description"),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = valueStr,
                    onValueChange = { valueStr = it },
                    label = { Text("Valor Estimado (R$)") },
                    placeholder = { Text("Ex: 15400.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_dav_value"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Category selection
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isCategoryDropdownExpanded = true },
                        shape = RoundedCornerShape(12.dp)
                    )
                    DropdownMenu(
                        expanded = isCategoryDropdownExpanded,
                        onDismissRequest = { isCategoryDropdownExpanded = false }
                    ) {
                        DavCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.label) },
                                onClick = {
                                    selectedCategory = cat
                                    isCategoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = dueAt,
                    onValueChange = { dueAt = it },
                    label = { Text("Data de Vencimento") },
                    placeholder = { Text("AAAA-MM-DD") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            val parsedValue = valueStr.toDoubleOrNull() ?: 0.0
                            if (clientName.isNotBlank()) {
                                onConfirm(clientName, description, parsedValue, selectedCategory, dueAt)
                            }
                        },
                        enabled = clientName.isNotBlank(),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("button_confirm_new_dav"),
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Salvar DAV", color = Color.White)
                    }
                }
            }
        }
    }
}
