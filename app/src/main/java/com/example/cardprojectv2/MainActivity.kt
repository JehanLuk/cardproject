package com.example.cardprojectv2

import org.threeten.bp.LocalDateTime
import com.jakewharton.threetenabp.AndroidThreeTen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LembreteViewModel(private val dao: LembreteDAO) : ViewModel() {
    var lembretes = mutableStateListOf<Lembrete>()
        private set

    init {
        viewModelScope.launch {
            val todos = dao.obterTodosLembretes()
            lembretes.addAll(todos)
        }
    }

    fun inserir(lembrete: Lembrete) {
        viewModelScope.launch {
            dao.inserirLembrete(lembrete)
            val todos = dao.obterTodosLembretes()
            lembretes.clear()
            lembretes.addAll(todos)
        }
    }

    fun remover(lembrete: Lembrete) {
        viewModelScope.launch {
            dao.deletarLembrete(lembrete)
            val todos = dao.obterTodosLembretes()
            lembretes.clear()
            lembretes.addAll(todos)
        }
    }
}

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase
    private lateinit var lembreteDao: LembreteDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidThreeTen.init(this)

        enableEdgeToEdge()

        db = AppDatabase.Companion.getDatabase(this)
        lembreteDao = db.lembreteDao()

        val factory = LembreteViewModelFactory(lembreteDao)
        val viewModel = ViewModelProvider(this, factory)[LembreteViewModel::class.java]

        setContent {
            MainScreen(viewModel)
        }
    }
}

@Composable
fun MainScreen(viewModel: LembreteViewModel) {
    val showDialog = remember { mutableStateOf(false) }

    Scaffold { pad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
        ) {
            Column {
                Button(
                    onClick = { showDialog.value = true },
                    modifier = Modifier
                        .offset(220.dp, 10.dp)
                        .size(150.dp, 50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF69B4))
                ) {
                    Text("Criar Lembrete")
                }
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Gray,
                    modifier = Modifier.offset(0.dp, 15.dp)
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(viewModel.lembretes) { lembrete ->
                        Cards(lembrete.mensagem, lembrete.dataCriacao, onDelete = { viewModel.remover(lembrete) })
                    }
                }
            }

            AnimatedVisibility(
                visible = showDialog.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
            }

            if (showDialog.value) {
                CriarCard(
                    onConfirmation = { mensagem ->
                        viewModel.inserir(
                            Lembrete(
                                mensagem = mensagem,

                                dataCriacao = LocalDateTime.now()
                            )
                        )
                        showDialog.value = false
                    },
                    onDismissRequest = { showDialog.value = false }
                )
            }
        }
    }
}

@Composable
fun CriarCard(
    onConfirmation: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var mensagem by remember { mutableStateOf("") }

    AlertDialog(
        icon = {
            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Ícone de Card")
        },
        title = {
            Text(text = "Insira as informações")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = mensagem,
                    onValueChange = { mensagem = it },
                    label = { Text("Mensagem do lembrete") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        containerColor = Color.White,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    if (mensagem.isNotBlank()) {
                        onConfirmation(mensagem)
                    }
                }
            ) {
                Text("Criar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}