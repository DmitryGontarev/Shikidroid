package com.shikidroid.presentation.navigation.navgraphs.bottom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.components.CircularSlider

@Composable
fun MainGraph() {
//    Box(modifier = Modifier
//        .fillMaxSize()
//        .background(Color.Green),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = "MAIN",
//            fontSize = MaterialTheme.typography.h3.fontSize,
//            fontWeight = FontWeight.Bold,
//            color = Color.White
//        )
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = ShikidroidTheme.colors.surface),
        contentAlignment = Alignment.Center
    ) {
        CircularSlider(
            modifier = Modifier
                .size(250.dp)
                .background(
                    color = Color.Transparent
                ),
            initialValue = 0,
            primaryColor = ShikidroidTheme.colors.secondary,
            secondaryColor = ShikidroidTheme.colors.onBackground,
            minValue = 0,
            maxValue = 100,
            circleRadius = 230f,
            onPositionChange = { value ->

            }
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview() {
//    MainGraph()
//}