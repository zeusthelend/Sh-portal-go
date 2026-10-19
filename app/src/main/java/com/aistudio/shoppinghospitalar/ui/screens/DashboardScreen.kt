package com.aistudio.shoppinghospitalar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.shoppinghospitalar.data.model.Announcement
import com.aistudio.shoppinghospitalar.data.model.DavStatus
import com.aistudio.shoppinghospitalar.data.model.Meeting
import com.aistudio.shoppinghospitalar.theme.*
import com.aistudio.shoppinghospitalar.ui.components.KpiCard
import com.aistudio.shoppinghospitalar.ui.components.OverdueAlertBanner
import com.aistudio.shoppinghospitalar.ui.navigation.Screen
import com.aistudio.shoppinghospitalar.viewmodel.ShoppingUiState

@Composable
fun DashboardScreen(
    state: ShoppingUiState,
    onNavigate: (Screen) -> Unit,
    onNewDavClick: () -> Unit
) {
    val onlineCount = state.team.count { it.status == com.aistudio.shoppinghospitalar.data.model.UserStatus.ONLINE }
    val overdueCount = state.davs.count { it.status != DavStatus.CONCLUIDO && it.status != DavStatus.CANCELADO && it.dueAt < "2026-10-19" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Hero Greeting Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PurpleDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CoralHighlight
                        ) {
                            Text(
                                text = "PORTAL CORPORATIVO",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Sejam bem-vindas(a) ao SHOPPING HOSPITALAR!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val firstName = state.currentUser.fullName.split(" ").firstOrNull() ?: "Colaborador"
                    Text(
                        text = "Olá, $firstName. Bom plantão corporativo!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Overdue Alert
        item {
            OverdueAlertBanner(count = overdueCount) {
                onNavigate(Screen.Davs)
            }
        }

        // KPI Cards Grid (2x2)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        title = "Equipe online",
                        value = "$onlineCount colaboradores",
                        icon = Icons.Default.People,
                        iconBgColor = EmeraldSuccess.copy(alpha = 0.15f),
                        iconTintColor = EmeraldSuccess,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "DAVs em aberto",
                        value = "${state.salesSummary.openCount + state.salesSummary.inProgressCount}",
                        icon = Icons.Default.Assignment,
                        iconBgColor = PurplePrimary.copy(alpha = 0.15f),
                        iconTintColor = PurplePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        title = "Reuniões hoje",
                        value = "${state.meetings.size}",
                        icon = Icons.Default.CalendarMonth,
                        iconBgColor = AmberWarning.copy(alpha = 0.15f),
                        iconTintColor = AmberWarning,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Lembretes pendentes",
                        value = "${state.reminders.count { !it.completed }}",
                        icon = Icons.Default.Checklist,
                        iconBgColor = CoralHighlight.copy(alpha = 0.15f),
                        iconTintColor = CoralHighlight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Actions
        item {
            Text(
                text = "Ações Rápidas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNewDavClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_new_dav"),
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Novo DAV", fontSize = 13.sp)
                }
                OutlinedButton(
                    onClick = { onNavigate(Screen.Chat) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_open_chat"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Abrir Chat", fontSize = 13.sp)
                }
            }
        }

        // Avisos Importantes
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = CoralHighlight)
                    Text(
                        text = "Avisos Importantes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(state.announcements) { announcement ->
            AnnouncementCard(announcement = announcement)
        }

        // Próximas Reuniões
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = AmberWarning)
                    Text(
                        text = "Próximas Reuniões",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(onClick = { onNavigate(Screen.Meetings) }) {
                    Text("Ver todas", color = PurplePrimary)
                }
            }
        }

        items(state.meetings) { meeting ->
            MeetingCard(meeting = meeting)
        }
    }
}

@Composable
fun AnnouncementCard(announcement: Announcement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (announcement.important) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = RoseDanger.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "IMPORTANTE",
                            color = RoseDanger,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PurplePrimary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "INFORMATIVO",
                            color = PurplePrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = announcement.createdAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = announcement.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Por: ${announcement.authorName}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryLight
            )
        }
    }
}

@Composable
fun MeetingCard(meeting: Meeting) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AmberWarning.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VideoCall,
                    contentDescription = null,
                    tint = AmberWarning,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meeting.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${meeting.startsAt} • ${meeting.durationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = PurplePrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = meeting.location,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
