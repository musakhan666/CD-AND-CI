package com.codingwithjks.composeyt.todo.data.ui

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.codingwithjks.composeyt.todo.data.Todo
import com.codingwithjks.composeyt.ui.theme.ComposeYtTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoActivity : ComponentActivity() {

    private val todoViewModel: TodoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeYtTheme {
                Surface(color = MaterialTheme.colors.background) {
                    AddToolbar()
                }
            }
        }
    }

    @Composable
    fun AddToolbar() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "ToDo App")
                    }
                )
            },
            floatingActionButton = {
                val openDialog = remember { mutableStateOf(false) }
                FloatingActionButton(onClick = {
                    openDialog.value = true
                }) {
                    AlertDialogBox(openDialog = openDialog)
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) {
            Recyclerview(todoViewModel = todoViewModel)
        }
    }

    @Composable
    fun AlertDialogBox(openDialog: MutableState<Boolean>) {
        val title = remember { mutableStateOf("") }
        val description = remember { mutableStateOf("") }

        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                }, title = {
                    Text(
                        text = "Todo",
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )


                }, text = {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 15.dp, horizontal = 10.dp)
                            .fillMaxWidth()
                    ) {
                        OutlinedTextField(value = title.value, onValueChange = {
                            title.value = it
                        }, placeholder = {
                            Text(text = "Enter title")
                        }, label = {
                            Text(text = "Enter title")
                        }, modifier = Modifier.padding(10.dp))

                        OutlinedTextField(value = description.value, onValueChange = {
                            description.value = it
                        }, placeholder = {
                            Text(text = "Enter description")
                        }, label = {
                            Text(text = "Enter description")
                        }, modifier = Modifier.padding(10.dp))
                    }
                }, confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedButton(
                            onClick = {
                                insert(title, description)

                            },
                            modifier = Modifier
                                .padding(10.dp)
                                .size(width = 100.dp, height = 40.dp),
                        ) {
                            Text(text = "save", textAlign = TextAlign.Center)

                        }
                    }

                })
        }


    }


    private fun insert(title: MutableState<String>, description: MutableState<String>) {
        lifecycleScope.launchWhenStarted {
            if (!TextUtils.isEmpty(title.value) && !TextUtils.isEmpty(description.value)) {
                todoViewModel.insert(
                    Todo(
                        title.value,
                        description.value
                    )
                )

                Toast.makeText(this@TodoActivity, "Inserted..", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@TodoActivity, "Empty..", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Composable
    fun EachRow(todo: Todo) {

        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth(),
            elevation = 2.dp,
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = todo.title, fontWeight = FontWeight.ExtraBold)
                Text(text = todo.description, fontStyle = FontStyle.Italic)
            }
        }
    }

    @Composable
    fun Recyclerview(todoViewModel: TodoViewModel) {
        LazyColumn {
            items(todoViewModel.response.value) { todo ->
                EachRow(todo = todo)
            }
        }
    }
}