package com.talitamorales.composememory.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.talitamorales.composememory.R
import java.util.Calendar

@Composable
fun AudienceSelectionScreen(
    onContinue: (Int?, Int) -> Unit,
    onShowPrivacy: () -> Unit
) {
    // Do not persist the entered year or prefill an age that could influence the answer.
    var birthYearText by remember { mutableStateOf("") }
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    val birthYear = birthYearText.toIntOrNull()
    val isValid = birthYearText.length == 4 &&
        birthYear != null && birthYear in (currentYear - 125)..currentYear

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF7FF))
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge)
        Text(stringResource(R.string.audience_title), style = MaterialTheme.typography.headlineSmall)
        Text(
            stringResource(R.string.audience_explanation),
            modifier = Modifier.widthIn(max = 480.dp)
        )
        OutlinedTextField(
            value = birthYearText,
            onValueChange = { value -> birthYearText = value.filter { it in '0'..'9' }.take(4) },
            label = { Text(stringResource(R.string.audience_birth_year)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth(),
            isError = birthYearText.length == 4 && !isValid,
            supportingText = {
                if (birthYearText.length == 4 && !isValid) {
                    Text(stringResource(R.string.audience_invalid_year))
                }
            }
        )
        Button(onClick = { onContinue(birthYear, currentYear) }, enabled = isValid) {
            Text(stringResource(R.string.audience_continue))
        }
        TextButton(onClick = { onContinue(null, currentYear) }) {
            Text(stringResource(R.string.audience_skip))
        }
        TextButton(onClick = onShowPrivacy) {
            Text(stringResource(R.string.privacy_policy_title))
        }
    }
}
