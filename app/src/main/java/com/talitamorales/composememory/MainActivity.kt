package com.talitamorales.composememory

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.talitamorales.composememory.ui.theme.ComposeMemoryTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeMemoryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MemoryGameScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MemoryGameScreenPreview() {
    ComposeMemoryTheme {
        MemoryGameScreen()
    }
}