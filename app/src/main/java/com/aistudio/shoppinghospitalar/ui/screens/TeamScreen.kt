package com.aistudio.shoppinghospitalar.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aistudio.shoppinghospitalar.data.model.Profile
import com.aistudio.shoppinghospitalar.data.model.UserStatus
import com.aistudio.shoppinghospitalar.theme.*
import com.aistudio.shoppinghospitalar.viewmodel.ShoppingUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(
    state: ShoppingUiState,
    onStartChat: (Profile) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedDepartment by remember { mutableStateOf("Todos") }

    val departments = remember {
        listOf("Todos", "Vendas", "Diretoria", "Estoque", "Financeiro", "Administrativo", "RH", "Operacional")
    }

    val filteredMembers = remember(state.team, searchQuery, selectedDepartment) {
        state.team.filter { member ->
            val matchesSearch = member.fullName.contains(searchQuery, ignoreCase = true) ||
                    member.role.contains(searchQuery, ignoreCase = true) ||
                    member.department.contains(searchQuery, ignoreCase = true) ||
                    member.phone.contains(searchQuery, ignoreCase = true) ||
                    member.email.contains(searchQuery, ignoreCase = true)
            val matchesDept = selectedDepartment == "Todos" || member.department.equals(selectedDepartment, ignoreCase = true)
            matchesSearch && matchesDept
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Agenda de Contatos & Equipe",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Todos os ${state.team.size} colaboradores e contatos cadastrados no banco de dados.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_team_input"),
                placeholder = { Text("Buscar colaborador, cargo, setor...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpar")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                departments.forEach { dept ->
                    FilterChip(
                        selected = selectedDepartment == dept,
                        onClick = { selectedDepartment = dept },
                        label = { Text(dept) }
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Colaboradores (${filteredMembers.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items(filteredMembers, key = { it.id }) { member ->
            TeamMemberCard(
                member = member,
                onChatClick = { onStartChat(member) },
                onCallClick = {
                    if (member.phone.isNotEmpty()) {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${member.phone.replace("[^0-9+]".toRegex(), "")}")
                        }
                        context.startActivity(intent)
                    }
                }
            )
        }
    }
}

@Composable
fun TeamMemberCard(
    member: Profile,
    onChatClick: () -> Unit,
    onCallClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PurpleDark),
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = member.fullName.split(" ")
                            .take(2)
                            .mapNotNull { it.firstOrNull()?.toString() }
                            .joinToString("")
                        Text(
                            text = initials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    val dotColor = when (member.status) {
                        UserStatus.ONLINE -> EmeraldSuccess
                        UserStatus.AUSENTE -> AmberWarning
                        UserStatus.OCUPADO -> RoseDanger
                        UserStatus.OFFLINE -> Color.Gray
                    }
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = member.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = member.role,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = PurplePrimary.copy(alpha = 0.1f),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = member.department,
                            color = PurplePrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (member.phone.isNotEmpty()) {
                        IconButton(
                            onClick = onCallClick,
                            modifier = Modifier.testTag("call_${member.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Ligar para ${member.fullName}",
                                tint = EmeraldSuccess
                            )
                        }
                    }
                    IconButton(
                        onClick = onChatClick,
                        modifier = Modifier.testTag("chat_with_${member.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Enviar mensagem",
                            tint = PurplePrimary
                        )
                    }
                }
            }

            // Contact details row
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = member.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (member.phone.isNotEmpty()) {
                    Text(
                        text = member.phone,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
