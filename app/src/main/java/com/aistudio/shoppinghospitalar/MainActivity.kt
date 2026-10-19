package com.aistudio.shoppinghospitalar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aistudio.shoppinghospitalar.theme.ShoppingHospitalarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppingHospitalarTheme {
                ShoppingHospitalarApp()
            }
        }
    }
}
