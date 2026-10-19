package com.aistudio.shoppinghospitalar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.window.Dialog
import com.aistudio.shoppinghospitalar.data.model.Meeting
import com.aistudio.shoppinghospitalar.theme.*
import com.aistudio.shoppinghospitalar.viewmodel.ShoppingUiState

@Composable
fun MeetingsScreen(
    state: ShoppingUiState,
    onNewMeetingClick: () -> Unit,
    onJoinMeeting: (Meeting) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewMeetingClick,
                containerColor = PurplePrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_new_meeting")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agendar Reunião")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            item {
                Text(
                    text = "Próximos Encontros & Alinhamentos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Sessões virtuais e presenciais do Shopping Hospitalar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(state.meetings, key = { it.id }) { meeting ->
                DetailedMeetingCard(meeting = meeting, onJoin = { onJoinMeeting(meeting) })
            }
        }
    }
}

@Composable
fun DetailedMeetingCard(
    meeting: Meeting,
    onJoin: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AmberWarning.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(14.dp))
                        Text(
                            text = meeting.startsAt,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AmberWarning
                        )
                    }
                }
                Text(
                    text = "${meeting.durationMinutes} minutos",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryLight
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = meeting.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Place, contentDescription = null, tint = TextSecondaryLight, modifier = Modifier.size(16.dp))
                Text(
                    text = meeting.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onJoin,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("button_join_meeting_${meeting.id}"),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.VideoCall, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Entrar na Sala Virtual")
            }
        }
    }
}

@Composable
fun NewMeetingDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, startsAt: String, duration: Int, location: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var startsAt by remember { mutableStateOf("Hoje às 16:00") }
    var durationStr by remember { mutableStateOf("45") }
    var location by remember { mutableStateOf("Online / Google Meet") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Agendar Reunião",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PurplePrimary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título da Reunião") },
                    placeholder = { Text("Ex: Alinhamento de Fornecedores") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = startsAt,
                    onValueChange = { startsAt = it },
                    label = { Text("Horário / Data") },
                    placeholder = { Text("Ex: Amanhã às 15h") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = durationStr,
                    onValueChange = { durationStr = it },
                    label = { Text("Duração (minutos)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Local ou Link Virtual") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            val duration = durationStr.toIntOrNull() ?: 30
                            if (title.isNotBlank()) {
                                onConfirm(title, startsAt, duration, location)
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Agendar", color = Color.White)
                    }
                }
            }
        }
    }
}
