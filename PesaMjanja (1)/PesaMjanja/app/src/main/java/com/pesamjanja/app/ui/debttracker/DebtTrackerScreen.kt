package com.pesamjanja.app.ui.debttracker

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.compose.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.pesamjanja.app.data.entity.Debt
import com.pesamjanja.app.domain.model.DebtDirection
import com.pesamjanja.app.ui.common.rememberPesaApp

@Composable
fun DebtTrackerScreen() {
    val app = rememberPesaApp()
    val viewModel: DebtTrackerViewModel = viewModel(
        factory = viewModelFactory {
            initializer { DebtTrackerViewModel(app.debtRepository) }
        }
    )

    var tab by remember { mutableIntStateOf(0) }
    val owedToMe by viewModel.owedToMe.collectAsStateWithLifecycle()
    val iOwe by viewModel.iOwe.collectAsStateWithLifecycle()

    var personName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("The Den", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(
            "Who owes who",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(16.dp))

        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Owed to me") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("I owe") })
        }

        Spacer(Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = personName,
                    onValueChange = { personName = it },
                    label = { Text("Person") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount (KES)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val parsedAmount = amount.toDoubleOrNull()
                        if (personName.isNotBlank() && parsedAmount != null) {
                            val direction = if (tab == 0) DebtDirection.OWED_TO_ME else DebtDirection.I_OWE
                            viewModel.addDebt(personName, parsedAmount, direction, note)
                            personName = ""
                            amount = ""
                            note = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (tab == 0) "Add who owes me" else "Add who I owe")
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        val list = if (tab == 0) owedToMe else iOwe
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(list, key = { it.id }) { debt ->
                DebtRow(debt = debt, onSettle = { viewModel.markSettled(it) }, onDelete = { viewModel.delete(it) })
            }
        }
    }
}

@Composable
private fun DebtRow(debt: Debt, onSettle: (Long) -> Unit, onDelete: (Long) -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (debt.isSettled) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(debt.personName, fontWeight = FontWeight.Medium)
                Text(
                    "KES ${"%,.0f".format(debt.amount)}" + (debt.note?.let { " • $it" } ?: ""),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }
            Row {
                if (!debt.isSettled) {
                    IconButton(onClick = {
                        val message = if (debt.direction == DebtDirection.OWED_TO_ME)
                            "Hey, you owe me KES ${"%,.0f".format(debt.amount)}" + (debt.note?.let { " for $it" } ?: "")
                        else
                            "Hey, just confirming I owe you KES ${"%,.0f".format(debt.amount)}" + (debt.note?.let { " for $it" } ?: "")
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, message)
                        }
                        context.startActivity(Intent.createChooser(intent, "Send reminder"))
                    }) {
                        Icon(Icons.Filled.Send, contentDescription = "Send reminder")
                    }
                    IconButton(onClick = { onSettle(debt.id) }) {
                        Icon(Icons.Filled.Check, contentDescription = "Mark settled")
                    }
                } else {
                    TextButton(onClick = { onDelete(debt.id) }) {
                        Text("Remove")
                    }
                }
            }
        }
    }
}
