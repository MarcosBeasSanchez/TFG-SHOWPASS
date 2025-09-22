package com.example.appmovilshowpass.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun BarraCarga()= CircularProgressIndicator(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(),
        color = Color.Gray
    )