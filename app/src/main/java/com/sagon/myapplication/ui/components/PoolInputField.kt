package com.sagon.myapplication.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PoolInputField(
    label: String,
    value: String,
    icon: ImageVector,
    onValueChange: (String) -> Unit,
    onHelpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, bottom = 1.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = Color(0xFF2196F3),
                    spotColor = Color(0xFF2196F3)
                )
                .combinedClickable(
                    onClick = { /* Acción normal al tocar el borde */ },
                    onLongClick = onHelpClick
                ),
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.95f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(24.dp)
                )
                
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0D47A1)
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(Color(0xFF2196F3))
                )

                IconButton(onClick = onHelpClick) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = "Ayuda",
                        tint = Color(0xFF2196F3).copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
