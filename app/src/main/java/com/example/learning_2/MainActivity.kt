package com.example.learning_2

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.learning_2.components.VacationApp
import com.example.learning_2.core.ui.theme.Learning_2Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Learning_2Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VacationApp()
                }
            }
        }
    }
}
