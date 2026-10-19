package com.aistudio.shoppinghospitalar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.shoppinghospitalar.data.model.Dav
import com.aistudio.shoppinghospitalar.data.model.DavCategory
import com.aistudio.shoppinghospitalar.data.model.DavStatus
import com.aistudio.shoppinghospitalar.data.repository.ShoppingRepository
import com.aistudio.shoppinghospitalar.theme.*
import com.aistudio.shoppinghospitalar.ui.components.StatusBadge
import com.aistudio.shoppinghospitalar.viewmodel.ShoppingUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DavsScreen(
    state: ShoppingUiState,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onStatusChange: (String, DavStatus) -> Unit,
    onDeleteDav: (String) -> Unit,
    onNewDavClick: () -> Unit
) {
    var selectedDavForStatus by remember { mutableStateOf<Dav?>(null) }
    var davToDelete by remember { mutableStateOf<Dav?>(null) }

    val categories = listOf("TODOS" to "Todos", "GERAL" to "Geral", "PREFEITURA" to "Prefeitura", "RESERVADO" to "Reservado")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewDavClick,
                containerColor = PurplePrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_new_dav")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Cadastrar DAV")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            // Search Input
            item {
                OutlinedTextField(
                    value = state.davSearchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dav_search_input"),
                    placeholder = { Text("Buscar por número, cliente ou vendedor...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = TextSecondaryLight)
                    },
                    trailingIcon = {
                        if (state.davSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpar")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = BorderLight
                    )
                )
            }

            // Category Filter Tabs
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { (key, label) ->
                        val isSelected = state.selectedDavCategory == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategoryChange(key) },
                            label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PurplePrimary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            // Summary Stats Pill Bar
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${state.filteredDavs.size} documento(s) encontrado(s)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val totalVal = state.filteredDavs.sumOf { it.davValue }
                        Text(
                            text = "Total: ${ShoppingRepository.formatCurrency(totalVal)}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = PurplePrimary
                        )
                    }
                }
            }

            // Dav Items
            if (state.filteredDavs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = TextSecondaryLight,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Nenhum DAV encontrado.",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSecondaryLight
                            )
                        }
                    }
                }
            } else {
                items(state.filteredDavs, key = { it.id }) { dav ->
                    DavCard(
                        dav = dav,
                        onStatusClick = { selectedDavForStatus = dav },
                        onDeleteClick = { davToDelete = dav }
                    )
                }
            }
        }
    }

    // Status Selection Dialog
    selectedDavForStatus?.let { dav ->
        AlertDialog(
            onDismissRequest = { selectedDavForStatus = null },
            title = { Text("Alterar Status do DAV") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "${dav.davNumber} — ${dav.clientName}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    DavStatus.values().forEach { status ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onStatusChange(dav.id, status)
                                    selectedDavForStatus = null
                                }
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (dav.status == status) PurplePrimary.copy(alpha = 0.12f) else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(status.label, fontWeight = if (dav.status == status) FontWeight.Bold else FontWeight.Normal)
                                if (dav.status == status) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = PurplePrimary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDavForStatus = null }) {
                    Text("Fechar")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    davToDelete?.let { dav ->
        AlertDialog(
            onDismissRequest = { davToDelete = null },
            title = { Text("Excluir DAV") },
            text = { Text("Tem certeza que deseja excluir o documento ${dav.davNumber}? Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteDav(dav.id)
                        davToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseDanger)
                ) {
                    Text("Excluir", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { davToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun DavCard(
    dav: Dav,
    onStatusClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Number + Category Badge + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = dav.davNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PurplePrimary
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (dav.category) {
                            DavCategory.PREFEITURA -> BlueInfo.copy(alpha = 0.15f)
                            DavCategory.RESERVADO -> CoralHighlight.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Text(
                            text = dav.category.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = when (dav.category) {
                                DavCategory.PREFEITURA -> BlueInfo
                                DavCategory.RESERVADO -> CoralHighlight
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row {
                    IconButton(onClick = onStatusClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Alterar status", tint = PurplePrimary, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Excluir", tint = RoseDanger, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dav.clientName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dav.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // Footer Row: Value, Due Date, Seller, Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = ShoppingRepository.formatCurrency(dav.davValue),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (dav.status == DavStatus.CONCLUIDO) EmeraldSuccess else PurpleDark
                    )
                    Text(
                        text = "Vencimento: ${dav.dueAt}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryLight
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    StatusBadge(status = dav.status)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dav.sellerName,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryLight
                    )
                }
            }
        }
    }
}
