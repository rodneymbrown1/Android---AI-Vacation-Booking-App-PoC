package com.example.learning_2

import VacationApp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.learning_2.ui.theme.Learning_2Theme
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.example.learning_2.components.VacationDetailView
import com.example.learning_2.components.VacationManager
import com.example.learning_2.components.ExcursionListView
import com.example.learning_2.database.AppDatabase
import com.example.learning_2.database.AppDatabaseProvider

class MainActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the database
        database = AppDatabaseProvider.getDatabase(this)

        setContent {
            Learning_2Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VacationApp(database)
//                    VacationDetailView(database) // Pass the database to VacationDetailView
                }
            }
        }
    }

    //==========================================================================================
    //COMPOSABLE
    @Composable
    fun GreetingCard(msg: Message) {
        Row(modifier = Modifier.padding(all = 8.dp)) {
            // Profile Picture
            Image(
                painter = painterResource(R.drawable.profile_picture),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Column for Text and VacationManager
            Column(modifier = Modifier.fillMaxWidth()) {
                // Author Name
                Text(
                    text = msg.author,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Message Body
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth(0.8f) // Keep message body compact
                ) {
                    Text(
                        text = msg.body,
                        modifier = Modifier.padding(all = 4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // VacationManager aligned lower and left
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    VacationApp(database)
//                    VacationDetailView()
//                      ExcursionListView()
                }
            }
        }
    }

    //==========================================================================================
    //DATA TYPE
    data class Message(val author: String, val body: String)

    //==========================================================================================
    //PREVIEWS
    @Preview
    @Composable
    fun PreviewMessageCard() {
        Learning_2Theme {
            Surface {
                GreetingCard(
                    msg = Message("Rodney Brown", "Mobile Development")
                )

            }
        }
    }
}