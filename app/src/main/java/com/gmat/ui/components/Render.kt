package com.gmat.ui.components


import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun RenderPainterIcon(
    id: Int,
    desc: String="",
    modifier: Modifier,
    tint: Color
){
    Icon(
        painter = painterResource(id),
        contentDescription = desc,
        modifier = modifier,
        tint=tint
    )
}
