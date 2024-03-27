package com.example.android.androidskeletonapp.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import org.hisp.dhis.mobile.ui.designsystem.component.InputPassword
import org.hisp.dhis.mobile.ui.designsystem.component.InputQRCode
import org.hisp.dhis.mobile.ui.designsystem.component.InputShellState
import org.hisp.dhis.mobile.ui.designsystem.component.InputUser
import org.hisp.dhis.mobile.ui.designsystem.component.model.InputPasswordModel
import org.hisp.dhis.mobile.ui.designsystem.component.model.InputUserModel
import org.hisp.dhis.mobile.ui.designsystem.theme.Spacing
import org.hisp.dhis.mobile.ui.designsystem.theme.SurfaceColor
import org.hisp.dhis.mobile.ui.designsystem.theme.TextColor

@Composable
fun LoginScreen(
    title: String,
    content: @Composable (() -> Unit),
    icon: @Composable (() -> Unit),
    modifier: Modifier,
) {
    Scaffold(
        modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier =
                        Modifier.padding(vertical = Spacing.Spacing8),
                    ) {
                        icon()
                        Text(title, color = TextColor.OnPrimary, modifier = Modifier.padding(start = Spacing.Spacing8))
                    }
                },
                backgroundColor = SurfaceColor.Primary,
                navigationIcon = {
                },

            )
        },

    ) { contentPadding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
        }
    }
}

@Composable
fun ProvideInputQRCode(
    title: String,
    state: InputShellState,
    onQRButtonClicked: (() -> Unit)?,
    inputTextFieldValue: TextFieldValue? = null,
    autoCompleteList: List<String>? = null,
    onValueChanged: (TextFieldValue) -> Unit,
) {
    InputQRCode(
        title = title,
        state = state,
        inputTextFieldValue = inputTextFieldValue,
        onValueChanged = { onValueChanged.invoke(it ?: TextFieldValue()) },
        onQRButtonClicked = { onQRButtonClicked?.invoke() },
        modifier = Modifier.padding(bottom = Spacing.Spacing16, top = Spacing.Spacing40),
        autoCompleteList = autoCompleteList,
        autoCompleteItemSelected = {
            it?.let {
                onValueChanged.invoke(TextFieldValue(it, TextRange(it.length)))
            }
        },

    )
}

@Composable
fun ProvideInputUser(
    title: String,
    state: InputShellState,
    inputTextFieldValue: TextFieldValue? = null,
    onValueChanged: (TextFieldValue) -> Unit,
) {
    InputUser(
        InputUserModel(
            title = title,
            state = state,
            inputTextFieldValue = inputTextFieldValue,
            onValueChanged =
            {
                onValueChanged.invoke(it ?: TextFieldValue())
            },
        ),
        modifier = Modifier.padding(bottom = Spacing.Spacing16),

    )
}

@Composable
fun ProvideInputPassword(
    title: String,
    state: InputShellState,
    inputTextFieldValue: TextFieldValue? = null,
    onValueChanged: (TextFieldValue) -> Unit,
) {
    InputPassword(
        InputPasswordModel(
            title = title,
            state = state,
            inputTextFieldValue = inputTextFieldValue,
            onValueChanged = {
                onValueChanged.invoke(it ?: TextFieldValue())
            },

        ),
        modifier = Modifier.padding(bottom = Spacing.Spacing16),

    )
}
