package com.example.JeffBikes.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.JeffBikes.models.Cliente
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

@Composable
fun ClientesScreen(navController: NavHostController) {
    var clientes by remember { mutableStateOf(listOf<Cliente>()) }
    var cpf by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var instagram by remember { mutableStateOf("") }
    var searchCpf by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<Cliente?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    val firestore = Firebase.firestore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Clientes",
                fontSize = 24.sp,
                modifier = Modifier
                    .alignByBaseline()
                    .weight(1f)
            )

            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .padding(end = 16.dp)
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        }

        // Campos para adicionar ou editar um cliente
        Column(modifier = Modifier.padding(8.dp)) {
            TextField(
                value = cpf,
                onValueChange = { cpf = it },
                label = { Text("CPF") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            TextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            TextField(
                value = instagram,
                onValueChange = { instagram = it },
                label = { Text("Instagram") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botão de cadastro ou edição
                Button(
                    onClick = {
                        if (isEditing) {
                            // Realiza a atualização do cliente existente
                            firestore.collection("clientes").document(cpf)
                                .set(
                                    Cliente(
                                        cpf = cpf,
                                        nome = nome,
                                        email = email,
                                        instagram = instagram
                                    ).toMap()
                                )
                                .addOnSuccessListener {
                                    Log.d(TAG, "Cliente atualizado com sucesso!")
                                    clientes = clientes.map { cliente ->
                                        if (cliente.cpf == cpf) {
                                            cliente.copy(
                                                nome = nome,
                                                email = email,
                                                instagram = instagram
                                            )
                                        } else {
                                            cliente
                                        }
                                    }
                                    // Limpa os campos após a edição
                                    cpf = ""
                                    nome = ""
                                    email = ""
                                    instagram = ""
                                    isEditing = false
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Erro ao atualizar cliente", e)
                                    // Tratar falha na atualização, se necessário
                                }
                        } else {
                            // Realiza o cadastro de um novo cliente
                            if (cpf.isNotEmpty() && nome.isNotEmpty() && email.isNotEmpty() && instagram.isNotEmpty()) {
                                val novoCliente = Cliente(
                                    cpf = cpf,
                                    nome = nome,
                                    email = email,
                                    instagram = instagram
                                )
                                firestore.collection("clientes").document(cpf)
                                    .set(novoCliente.toMap())
                                    .addOnSuccessListener {
                                        clientes = clientes + novoCliente
                                        cpf = ""
                                        nome = ""
                                        email = ""
                                        instagram = ""
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "Erro ao cadastrar cliente", e)
                                        // Tratar falha no cadastro, se necessário
                                    }
                            } else {
                                // Lógica para lidar com campos obrigatórios não preenchidos
                                Log.e(TAG, "Campos obrigatórios não preenchidos")
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp)
                ) {
                    if (isEditing) {
                        Text("Salvar Alterações")
                    } else {
                        Text("Cadastrar")
                    }
                }

                // Botão de cancelar edição
                if (isEditing) {
                    Button(
                        onClick = {
                            // Limpa os campos e cancela a edição
                            cpf = ""
                            nome = ""
                            email = ""
                            instagram = ""
                            isEditing = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp, start = 8.dp)
                    ) {
                        Text("Cancelar Edição")
                    }
                }
            }
        }

        // Campo para buscar cliente pelo CPF
        Row(modifier = Modifier.padding(8.dp)) {
            TextField(
                value = searchCpf,
                onValueChange = { searchCpf = it },
                label = { Text("Buscar pelo CPF") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val clienteEncontrado = clientes.find { it.cpf == searchCpf }
                    searchResult = clienteEncontrado
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
            ) {
                Text("Buscar")
            }
        }

        // Exibe o resultado da busca se houver
        searchResult?.let { cliente ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("CPF: ${cliente.cpf}")
                    Text("Nome: ${cliente.nome}")
                    Text("Email: ${cliente.email}")
                    Text("Instagram: ${cliente.instagram}")
                    Row {
                        // Botão para editar o cliente encontrado
                        Button(
                            onClick = {
                                // Preenche os campos com os dados do cliente para edição
                                cpf = cliente.cpf
                                nome = cliente.nome
                                email = cliente.email
                                instagram = cliente.instagram
                                isEditing = true
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Editar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        // Botão para deletar o cliente
                        Button(
                            onClick = {
                                cliente.cpf.let { clientId ->
                                    firestore.collection("clientes").document(clientId)
                                        .delete()
                                        .addOnSuccessListener {
                                            clientes = clientes.filter { it.cpf != cliente.cpf } // Remove da lista local
                                            searchResult = null
                                        }
                                        .addOnFailureListener { e ->
                                            // Tratar falha na deleção, se necessário
                                            Log.e(TAG, "Erro ao deletar cliente", e)
                                        }
                                }
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Deletar")
                        }
                    }
                }
            }
        }

        // Lista todos os clientes cadastrados
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            items(clientes.distinctBy { it.cpf }) { cliente ->
                if (searchResult == null || cliente.cpf == searchCpf) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("CPF: ${cliente.cpf}")
                            Text("Nome: ${cliente.nome}")
                            Text("Email: ${cliente.email}")
                            Text("Instagram: ${cliente.instagram}")
                            Row {
                                // Botão para editar o cliente
                                Button(
                                    onClick = {
                                        // Preenche os campos com os dados do cliente para edição
                                        cpf = cliente.cpf
                                        nome = cliente.nome
                                        email = cliente.email
                                        instagram = cliente.instagram
                                        isEditing = true
                                    },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Editar")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                // Botão para deletar o cliente
                                Button(
                                    onClick = {
                                        cliente.cpf.let { clientId ->
                                            firestore.collection("clientes").document(clientId)
                                                .delete()
                                                .addOnSuccessListener {
                                                    clientes = clientes.filter { it.cpf != cliente.cpf } // Remove da lista local
                                                }
                                                .addOnFailureListener { e ->
                                                    // Tratar falha na deleção, se necessário
                                                    Log.e(TAG, "Erro ao deletar cliente", e)
                                                }
                                        }
                                    },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Deletar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Busca todos os clientes no Firestore ao iniciar a tela
    LaunchedEffect(Unit) {
        firestore.collection("clientes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Erro ao buscar clientes", error)
                    // Tratar erro aqui
                    return@addSnapshotListener
                }

                snapshot?.let {
                    clientes = it.documents.mapNotNull { doc ->
                        doc.toObject<Cliente>()?.copy(cpf = doc.id)
                    }
                }
            }
    }
}

// Constante para TAG de log
private const val TAG = "ClientesScreen"
