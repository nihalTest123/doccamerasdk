package com.example.dummyandroidsdk

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2ccustomcamera.MainActivity.Companion.SCAN_TYPE_AADHAAR
import com.example.b2ccustomcamera.XtracapCustomCamera
import com.example.dummyandroidsdk.ui.theme.DummyAndroidSDKTheme

var selectedType= SCAN_TYPE_AADHAAR;
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedType= SCAN_TYPE_AADHAAR
        enableEdgeToEdge()
        setContent {
            DummyAndroidSDKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DashboardScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dashboard Screen",
            fontSize = 18.sp,
            fontFamily = FontFamily.Serif
        )
        Spacer(modifier = Modifier.height(16.dp))
        MainBody()
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
               println("Selected Type is $selectedType")
                XtracapCustomCamera.with(context)
                    .setScanType(selectedType)
                    .start()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(alpha = 0.4f)),
        ) {
           Text("Continue")
        }

    }
}


@Composable
fun MainBody() {
    LazyColumn(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp))
            .background(Color.DarkGray.copy(alpha = 0.3f))
            .padding(16.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                text = "Hello, Select any one option",
                color = Color.White,
                modifier = Modifier.fillMaxSize()
            )
            SelectCameraType()
        }
    }
}


@Composable
fun SelectCameraType() {
    val radioOptions = listOf("PAN", "Aadhaar", "Cheque")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[1]) }
    Column(
        // we are using column to align our
        // radio buttons to center of the screen.
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {

                            onOptionSelected(text) }
                    )
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start,

                verticalAlignment = Alignment.CenterVertically
            ) {
                val context = LocalContext.current
                RadioButton(
                    selected = (text == selectedOption),

                    onClick = {
                        selectedType=text.uppercase()
                        onOptionSelected(text)
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                    },

                    modifier = Modifier.padding(8.dp),

                    enabled = true,
                    colors = RadioButtonDefaults.colors(
                        Color.Green,
                        Color.Yellow
                    ),

                    interactionSource = remember { MutableInteractionSource() }
                )

                Text(
                    text = text,
                    modifier = Modifier.padding(start = 16.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
//}