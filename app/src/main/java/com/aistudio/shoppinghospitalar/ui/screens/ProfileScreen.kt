package com.aistudio.shoppinghospitalar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aistudio.shoppinghospitalar.data.model.UserStatus
import com.aistudio.shoppinghospitalar.theme.*
import com.aistudio.shoppinghospitalar.viewmodel.ShoppingUiState

@Composable
fun ProfileScreen(
    state: ShoppingUiState,
    onUpdateProfile: (name: String, status: UserStatus, phone: String) -> Unit
) {
    var isEditDialogOpen by remember { mutableStateOf(false) }
    var pushNotificationsEnabled by remember { mutableStateOf(true) }
    val user = state.currentUser

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // User Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(PurpleDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.fullName.split(" ").take(2).mapNotNull { it.firstOrNull()?.toString() }.joinToString(""),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = user.fullName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${user.role} • ${user.department}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PurplePrimary,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { isEditDialogOpen = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        modifier = Modifier.testTag("button_edit_profile")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Editar Perfil")
                    }
                }
            }
        }

        // Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Status de Presença",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    UserStatus.values().forEach { status ->
                        val isSelected = user.status == status
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUpdateProfile(user.fullName, status, user.phone) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) PurplePrimary.copy(alpha = 0.1f) else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val dotColor = when (status) {
                                    UserStatus.ONLINE -> EmeraldSuccess
                                    UserStatus.AUSENTE -> AmberWarning
                                    UserStatus.OCUPADO -> RoseDanger
                                    UserStatus.OFFLINE -> Color.Gray
                                }
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(dotColor)
                                )
                                Text(
                                    text = status.label,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = PurplePrimary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Settings / Preferences
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Preferências e Notificações",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Notificações de DAVs e Chat", fontWeight = FontWeight.SemiBold)
                            Text("Alertas de vencimento e mensagens", style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                        }
                        Switch(
                            checked = pushNotificationsEnabled,
                            onCheckedChange = { pushNotificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PurplePrimary)
                        )
                    }

                    HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Empresa", fontWeight = FontWeight.SemiBold)
                            Text("Shopping Hospitalar Ltda.", style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                        }
                        Icon(Icons.Default.LocalHospital, contentDescription = null, tint = CoralHighlight)
                    }

                    HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Versão do App", fontWeight = FontWeight.SemiBold)
                        Text("1.0.0 (Android Native)", style = MaterialTheme.typography.bodySmall, color = PurplePrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (isEditDialogOpen) {
        var editName by remember { mutableStateOf(user.fullName) }
        var editPhone by remember { mutableStateOf(user.phone) }

        Dialog(onDismissRequest = { isEditDialogOpen = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Editar Meu Perfil", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nome Completo") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Telefone / WhatsApp") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = { isEditDialogOpen = false }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                onUpdateProfile(editName, user.status, editPhone)
                                isEditDialogOpen = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Salvar", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
