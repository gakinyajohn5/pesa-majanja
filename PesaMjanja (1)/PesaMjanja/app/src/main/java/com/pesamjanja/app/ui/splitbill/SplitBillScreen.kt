package com.pesamjanja.app.ui.splitbill

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.compose.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.pesamjanja.app.data.entity.SplitBill
import com.pesamjanja.app.data.entity.SplitBillParticipant
import com.pesamjanja.app.ui.common.rememberPesaApp

@Composable
fun SplitBillScreen() {
    val app = rememberPesaApp()
    val viewModel: SplitBillViewModel = viewModel(
        factory = viewModelFactory {
            initializer { SplitBillViewModel(app.splitBillRepository) }
        }
    )
    val bills by viewModel.bills.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var totalAmount by remember { mutableStateOf("") }
    var namesInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Split a bill", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("What's it for? (e.g. House rent)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = totalAmount,
                        onValueChange = { totalAmount = it },
                        label = { Text("Total amount (KES)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = namesInput,
                        onValueChange = { namesInput = it },
                        label = { Text("Names, comma separated") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val names = namesInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                            val amount = totalAmount.toDoubleOrNull()
                            if (names.isNotEmpty() && amount != null && amount > 0) {
                                viewModel.createBill(title.ifBlank { "Bill" }, amount, names) {
                                    title = ""
                                    totalAmount = ""
                                    namesInput = ""
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create split")
                    }
                }
            }
        }

        item {
            Text("Your splits", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        items(bills, key = { it.id }) { bill ->
            BillCard(bill = bill, viewModel = viewModel)
        }
    }
}

@Composable
private fun BillCard(bill: SplitBill, viewModel: SplitBillViewModel) {
    val context = LocalContext.current
    val participants by viewModel.participantsFor(bill.id).collectAsStateWithLifecycle(initialValue = emptyList())

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(bill.title, fontWeight = FontWeight.Bold)
                    Text(
                        "KES ${"%,.0f".format(bill.totalAmount)} • ${bill.numberOfPeople} people • " +
                            "KES ${"%,.0f".format(bill.perPersonAmount)} each",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
                IconButton(onClick = { viewModel.deleteBill(bill.id) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete split")
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            participants.forEach { participant ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = participant.hasPaid,
                            onCheckedChange = { viewModel.setPaid(participant, it) }
                        )
                        Text(participant.name)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("KES ${"%,.0f".format(participant.amountOwed)}")
                        if (!participant.hasPaid) {
                            IconButton(onClick = {
                                val message = "Hey, you owe me KES ${"%,.0f".format(participant.amountOwed)} for ${bill.title}"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, message)
                                }
                                context.startActivity(Intent.createChooser(intent, "Send reminder"))
                            }) {
                                Icon(Icons.Filled.Send, contentDescription = "Send reminder")
                            }
                        }
                    }
                }
            }
        }
    }
}
