package com.aistudio.shoppinghospitalar.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarClock
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aistudio.shoppinghospitalar.data.model.Reminder
import com.aistudio.shoppinghospitalar.data.model.ReminderDepartment
import com.aistudio.shoppinghospitalar.theme.*
import com.aistudio.shoppinghospitalar.viewmodel.ShoppingUiState

@Composable
fun RemindersScreen(
    state: ShoppingUiState,
    onToggleReminder: (String) -> Unit,
    onNewReminderClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(ReminderDepartment.ESTOQUE, ReminderDepartment.ADMINISTRATIVO)

    val currentDept = tabs[selectedTab]
    val departmentReminders = state.reminders.filter { it.department == currentDept }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewReminderClick,
                containerColor = PurplePrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_new_reminder")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo Lembrete")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PurplePrimary
            ) {
                tabs.forEachIndexed { index, dept ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = dept.label,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
            ) {
                item {
                    val pendingCount = departmentReminders.count { !it.completed }
                    Text(
                        text = "$pendingCount pendência(s) em aberto",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(departmentReminders, key = { it.id }) { reminder ->
                    ReminderItemCard(
                        reminder = reminder,
                        onToggle = { onToggleReminder(reminder.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReminderItemCard(
    reminder: Reminder,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.completed) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (reminder.completed) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (reminder.completed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (reminder.completed) "Concluído" else "Pendente",
                    tint = if (reminder.completed) EmeraldSuccess else TextSecondaryLight
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (reminder.completed) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (reminder.completed) TextSecondaryLight else MaterialTheme.colorScheme.onSurface
                )
                if (reminder.notes.isNotBlank()) {
                    Text(
                        text = reminder.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PurplePrimary.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "Vencimento: ${reminder.dueAt}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PurplePrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NewReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, notes: String, dept: ReminderDepartment, dueAt: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedDept by remember { mutableStateOf(ReminderDepartment.ESTOQUE) }
    var dueAt by remember { mutableStateOf("Hoje até 18h") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Novo Lembrete de Agenda",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PurplePrimary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título da Tarefa") },
                    placeholder = { Text("Ex: Conferência de entrada de lote") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas Adicionais") },
                    placeholder = { Text("Ex: Armazém 3, paletes B e C") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReminderDepartment.values().forEach { dept ->
                        FilterChip(
                            selected = selectedDept == dept,
                            onClick = { selectedDept = dept },
                            label = { Text(dept.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }

                OutlinedTextField(
                    value = dueAt,
                    onValueChange = { dueAt = it },
                    label = { Text("Prazo / Horário") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, notes, selectedDept, dueAt)
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Salvar", color = Color.White)
                    }
                }
            }
        }
    }
}
