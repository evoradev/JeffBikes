package com.example.JeffBikes.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.JeffBikes.models.Bicicleta
import com.example.JeffBikes.models.Cliente
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File

@Composable
fun BicicletaScreen(navController: NavHostController) {
    // Instância do Firestore
    val db = Firebase.firestore

    // Estado para elementos da UI
    var bicicletas by remember { mutableStateOf(listOf<Bicicleta>()) }
    var clientes by remember { mutableStateOf(listOf<Cliente>()) }
    var codigo by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var materialDoChassi by remember { mutableStateOf("") }
    var aro by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidadeDeMarchas by remember { mutableStateOf("") }
    var searchCodigo by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<Bicicleta?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var editingCodigo by remember { mutableStateOf("") }
    var selectedCliente by remember { mutableStateOf<Cliente?>(null) }
    var expandedCliente by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var fileContent by remember { mutableStateOf("") }

    // Contexto local para exibir Toast
    val context = LocalContext.current

    // Função para buscar bicicletas no Firestore
    fun fetchBicicletas() {
        db.collection("bicicletas")
            .get()
            .addOnSuccessListener { result ->
                bicicletas = result.toObjects(Bicicleta::class.java)
            }
            .addOnFailureListener { exception ->
                // Tratar erros
                println("Erro ao buscar bicicletas: $exception")
            }
    }

    // Função para buscar clientes no Firestore
    fun fetchClientes() {
        db.collection("clientes")
            .get()
            .addOnSuccessListener { result ->
                clientes = result.toObjects(Cliente::class.java)
            }
            .addOnFailureListener { exception ->
                // Tratar erros
                println("Erro ao buscar clientes: $exception")
            }
    }

    // Busca inicial de bicicletas e clientes
    LaunchedEffect(Unit) {
        fetchBicicletas()
        fetchClientes()
    }

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
                text = "Bicicletas",
                fontSize = 24.sp,
                modifier = Modifier
                    .alignByBaseline()
                    .weight(1f)
            )

            // Botão para salvar em TXT
            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .padding(end = 16.dp) // Ajustar o padding conforme necessário
            ) {
                IconButton(
                    onClick = {
                        saveToTxtFile(context, bicicletas) // Chamar função para salvar em arquivo TXT com contexto
                        showToast("Dados salvos em TXT", context) // Exibir Toast
                    }
                ) {
                    Icon(Icons.Default.MailOutline, contentDescription = "Salvar em TXT")
                }
            }

            // Botão para visualizar o conteúdo do arquivo TXT
            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .padding(end = 16.dp) // Ajustar o padding conforme necessário
            ) {
                IconButton(
                    onClick = {
                        fileContent = readFromFile(context, "bicicletas_data.txt") // Ler conteúdo do arquivo
                        showDialog = true // Mostrar diálogo com o conteúdo do arquivo
                    }
                ) {
                    Icon(Icons.Default.Email, contentDescription = "Ver Dados em TXT")
                }
            }

            // Botão para voltar
            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .padding(end = 16.dp) // Ajustar o padding conforme necessário
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                }
            }
        }

        // Campos de entrada para adicionar ou editar uma bicicleta
        TextField(
            value = codigo,
            onValueChange = { codigo = it },
            label = { Text("Código") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        TextField(
            value = modelo,
            onValueChange = { modelo = it },
            label = { Text("Modelo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        TextField(
            value = materialDoChassi,
            onValueChange = { materialDoChassi = it },
            label = { Text("Material do Chassi") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        TextField(
            value = aro,
            onValueChange = { aro = it },
            label = { Text("Aro") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        TextField(
            value = preco,
            onValueChange = { preco = it },
            label = { Text("Preço") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        TextField(
            value = quantidadeDeMarchas,
            onValueChange = { quantidadeDeMarchas = it },
            label = { Text("Quantidade de Marchas") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Dropdown para selecionar proprietário (cliente)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            TextField(
                value = selectedCliente?.nome ?: "Selecionar Proprietário",
                onValueChange = { },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.clickable {
                            expandedCliente = !expandedCliente
                        }
                    )
                },
                label = { Text("Proprietário") }
            )

            DropdownMenu(
                expanded = expandedCliente,
                onDismissRequest = { expandedCliente = false }
            ) {
                clientes.forEach { cliente ->
                    DropdownMenuItem(
                        text = { Text(cliente.nome) },
                        onClick = {
                            selectedCliente = cliente
                            expandedCliente = false
                        }
                    )
                }
            }
        }

        // Botão de adicionar ou editar
        Button(
            onClick = {
                val bicicleta = Bicicleta(
                    codigo = codigo,
                    modelo = modelo,
                    materialDoChassi = materialDoChassi,
                    aro = aro.toInt(),
                    preco = preco.toDouble(),
                    quantidadeDeMarchas = quantidadeDeMarchas.toInt(),
                    proprietario = selectedCliente?.nome ?: ""
                )

                if (isEditing) {
                    // Atualizar bicicleta existente
                    db.collection("bicicletas").document(editingCodigo)
                        .set(bicicleta)
                        .addOnSuccessListener {
                            // Limpar campos após atualização bem-sucedida
                            codigo = ""
                            modelo = ""
                            materialDoChassi = ""
                            aro = ""
                            preco = ""
                            quantidadeDeMarchas = ""
                            selectedCliente = null
                            isEditing = false
                            fetchBicicletas() // Atualizar lista de bicicletas
                        }
                } else {
                    // Adicionar nova bicicleta
                    db.collection("bicicletas").document(codigo)
                        .set(bicicleta)
                        .addOnSuccessListener {
                            // Limpar campos após adição bem-sucedida
                            codigo = ""
                            modelo = ""
                            materialDoChassi = ""
                            aro = ""
                            preco = ""
                            quantidadeDeMarchas = ""
                            selectedCliente = null
                            fetchBicicletas() // Atualizar lista de bicicletas
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(if (isEditing) "Editar Bicicleta" else "Adicionar Bicicleta")
        }

        // Campo de busca
        TextField(
            value = searchCodigo,
            onValueChange = { searchCodigo = it },
            label = { Text("Buscar por Código") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                db.collection("bicicletas")
                    .whereEqualTo("codigo", searchCodigo)
                    .get()
                    .addOnSuccessListener { result ->
                        searchResult = result.documents.firstOrNull()?.toObject(Bicicleta::class.java)
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Buscar")
        }

        searchResult?.let { bicicleta ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Código: ${bicicleta.codigo}")
                Text("Modelo: ${bicicleta.modelo}")
                Text("Material do Chassi: ${bicicleta.materialDoChassi}")
                Text("Aro: ${bicicleta.aro}")
                Text("Preço: ${bicicleta.preco}")
                Text("Quantidade de Marchas: ${bicicleta.quantidadeDeMarchas}")
                Text("Proprietário: ${bicicleta.proprietario}")

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = {
                        isEditing = true
                        editingCodigo = bicicleta.codigo
                        codigo = bicicleta.codigo
                        modelo = bicicleta.modelo
                        materialDoChassi = bicicleta.materialDoChassi
                        aro = bicicleta.aro.toString()
                        preco = bicicleta.preco.toString()
                        quantidadeDeMarchas = bicicleta.quantidadeDeMarchas.toString()
                        selectedCliente = clientes.find { it.id == bicicleta.proprietario }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }

                    IconButton(onClick = {
                        db.collection("bicicletas").document(bicicleta.codigo)
                            .delete()
                            .addOnSuccessListener {
                                fetchBicicletas() // Atualizar lista de bicicletas após deletar
                            }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir")
                    }
                }
            }
        }

        // Lista de bicicletas
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ) {
            items(bicicletas) { bicicleta ->
                BicicletaItem(
                    bicicleta = bicicleta,
                    onEditClick = {
                        codigo = bicicleta.codigo
                        modelo = bicicleta.modelo
                        materialDoChassi = bicicleta.materialDoChassi
                        aro = bicicleta.aro.toString()
                        preco = bicicleta.preco.toString()
                        quantidadeDeMarchas = bicicleta.quantidadeDeMarchas.toString()
                        selectedCliente = clientes.find { it.nome == bicicleta.proprietario }
                        isEditing = true
                        editingCodigo = bicicleta.codigo
                    },
                    onDeleteClick = {
                        db.collection("bicicletas").document(bicicleta.codigo)
                            .delete()
                            .addOnSuccessListener {
                                fetchBicicletas() // Atualizar lista de bicicletas após deletar
                            }
                    }
                )
            }
        }
    }

    // Diálogo para exibir o conteúdo do arquivo TXT
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Fechar")
                }
            },
            title = { Text("Conteúdo do Arquivo TXT") },
            text = {
                Text(fileContent)
            }
        )
    }
}

// Composable para exibir cada item de bicicleta na lista
@Composable
fun BicicletaItem(
    bicicleta: Bicicleta,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Código: ${bicicleta.codigo}")
            Text("Modelo: ${bicicleta.modelo}")
            Text("Material do Chassi: ${bicicleta.materialDoChassi}")
            Text("Aro: ${bicicleta.aro}")
            Text("Preço: ${bicicleta.preco}")
            Text("Quantidade de Marchas: ${bicicleta.quantidadeDeMarchas}")
            Text("Proprietário: ${bicicleta.proprietario}")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onEditClick
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }

                IconButton(
                    onClick = onDeleteClick
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir")
                }
            }
        }
    }
}

// Função para salvar bicicletas em um arquivo TXT
fun saveToTxtFile(context: Context, bicicletas: List<Bicicleta>) {
    val fileName = "bicicletas_data.txt"
    val file = File(context.filesDir, fileName)
    file.writeText("Dados das Bicicletas:\n\n")

    bicicletas.forEach { bicicleta ->
        file.appendText("Código: ${bicicleta.codigo}\n")
        file.appendText("Modelo: ${bicicleta.modelo}\n")
        file.appendText("Material do Chassi: ${bicicleta.materialDoChassi}\n")
        file.appendText("Aro: ${bicicleta.aro}\n")
        file.appendText("Preço: ${bicicleta.preco}\n")
        file.appendText("Quantidade de Marchas: ${bicicleta.quantidadeDeMarchas}\n")
        file.appendText("Proprietário: ${bicicleta.proprietario}\n\n")
    }

    // Opcional: Mostrar Toast de confirmação
    showToast("Dados salvos em TXT", context)
}

// Função para ler o conteúdo de um arquivo
fun readFromFile(context: Context, fileName: String): String {
    val file = File(context.filesDir, fileName)
    return if (file.exists()) {
        file.readText()
    } else {
        "Arquivo não encontrado."
    }
}

// Função para exibir um Toast na tela
fun showToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
