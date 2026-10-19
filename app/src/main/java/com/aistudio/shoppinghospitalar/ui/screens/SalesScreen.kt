package com.aistudio.shoppinghospitalar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.shoppinghospitalar.data.model.DavStatus
import com.aistudio.shoppinghospitalar.data.repository.ShoppingRepository
import com.aistudio.shoppinghospitalar.theme.*
import com.aistudio.shoppinghospitalar.viewmodel.ShoppingUiState

@Composable
fun SalesScreen(state: ShoppingUiState) {
    val summary = state.salesSummary

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PurpleDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Faturamento Concluído em DAVs",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = ShoppingRepository.formatCurrency(summary.totalRevenue),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess,
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Conversão", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                            Text(
                                text = "%.1f%%".format(summary.conversionRate),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Column {
                            Text("DAVs Concluídos", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                            Text(
                                text = "${summary.completedCount} de ${summary.totalCount}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Desempenho por Status",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SalesStatusRow(
                    label = "Concluídos",
                    count = summary.completedCount,
                    total = summary.totalCount,
                    color = EmeraldSuccess
                )
                SalesStatusRow(
                    label = "Em andamento / negociação",
                    count = summary.inProgressCount,
                    total = summary.totalCount,
                    color = BlueInfo
                )
                SalesStatusRow(
                    label = "Abertos / potenciais",
                    count = summary.openCount,
                    total = summary.totalCount,
                    color = AmberWarning
                )
                SalesStatusRow(
                    label = "Desistências",
                    count = summary.cancelledCount,
                    total = summary.totalCount,
                    color = RoseDanger
                )
            }
        }

        item {
            Text(
                text = "Maiores Clientes Hospitalares",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        val completedDavs = state.davs.filter { it.status == DavStatus.CONCLUIDO }
        items(completedDavs.size) { index ->
            val dav = completedDavs[index]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = dav.clientName, fontWeight = FontWeight.Bold)
                        Text(text = dav.davNumber, style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                    }
                    Text(
                        text = ShoppingRepository.formatCurrency(dav.davValue),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess
                    )
                }
            }
        }
    }
}

@Composable
fun SalesStatusRow(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    val progress = if (total > 0) count.toFloat() / total.toFloat() else 0f
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text(text = "$count ($total)", style = MaterialTheme.typography.bodyMedium, color = color, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.15f)
            )
        }
    }
}
