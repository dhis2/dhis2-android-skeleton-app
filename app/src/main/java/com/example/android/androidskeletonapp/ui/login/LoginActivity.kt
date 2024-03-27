package com.example.android.androidskeletonapp.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Login
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.android.androidskeletonapp.R
import com.example.android.androidskeletonapp.data.Sdk
import com.example.android.androidskeletonapp.data.service.ActivityStarter
import com.example.android.androidskeletonapp.databinding.ActivityLoginBinding
import com.example.android.androidskeletonapp.ui.main.MainActivity
import com.example.android.androidskeletonapp.ui.programs.ProgramsActivity
import io.reactivex.disposables.CompositeDisposable
import org.hisp.dhis.mobile.ui.designsystem.component.Button
import org.hisp.dhis.mobile.ui.designsystem.component.ButtonStyle
import org.hisp.dhis.mobile.ui.designsystem.component.InputShellState
import org.hisp.dhis.mobile.ui.designsystem.component.ProgressIndicator
import org.hisp.dhis.mobile.ui.designsystem.component.ProgressIndicatorType
import org.hisp.dhis.mobile.ui.designsystem.theme.Spacing
import org.hisp.dhis.mobile.ui.designsystem.theme.TextColor

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private var loginViewModel: LoginViewModel? = null
    private var disposable = CompositeDisposable()
    private var isLoading: MutableState<Boolean> = mutableStateOf(false)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory()).get(
            LoginViewModel::class.java,
        )

        binding.composeView.setContent {
            val loginUiState by loginViewModel!!.loginUiState.collectAsState()
            var serverUrlText by remember { mutableStateOf(TextFieldValue(getString(R.string.auto_fill_url), TextRange(getString(R.string.auto_fill_url).length))) }
            var userName by remember { mutableStateOf(TextFieldValue(getString(R.string.auto_fill_username), TextRange(getString(R.string.auto_fill_username).length))) }
            var password by remember { mutableStateOf(TextFieldValue(getString(R.string.auto_fill_password), TextRange(getString(R.string.auto_fill_password).length))) }
            var showProgress by remember(isLoading) { mutableStateOf(isLoading) }
            loginViewModel!!.initLoginDefaultValues(
                serverUrlText.text,
                userName.text,
                password.text,
            )
            val isLoginEnabled by remember(loginUiState) { mutableStateOf(loginUiState.isLoginEnabled()) }

            LoginScreen(
                title = getString(R.string.app_name),
                modifier = Modifier,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dhis2_icon_white),
                        contentDescription = "DHIS2 icon",
                        tint = TextColor.OnPrimary,
                    )
                },
                content = {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(1f)
                                .padding(horizontal = Spacing.Spacing24),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            ProvideInputQRCode(
                                title = getString(R.string.prompt_server_url),
                                state = if (!loginUiState.isServerUrlValid()) InputShellState.ERROR else InputShellState.UNFOCUSED,
                                inputTextFieldValue = serverUrlText,
                                onValueChanged = {
                                    serverUrlText = it
                                    loginViewModel!!.setServer(it.text)
                                },
                                onQRButtonClicked = {},
                                autoCompleteList = getServerAutoCompleteList(),
                            )

                            ProvideInputUser(
                                title = getString(R.string.prompt_username),
                                state = if (!loginUiState.isUserNameValid()) InputShellState.ERROR else InputShellState.UNFOCUSED,
                                inputTextFieldValue = userName,
                                onValueChanged =
                                {
                                    userName = it
                                    loginViewModel!!.setUserName(it.text)
                                },

                            )

                            ProvideInputPassword(
                                title = getString(R.string.prompt_password),
                                state = if (!loginUiState.isPasswordValid()) InputShellState.ERROR else InputShellState.UNFOCUSED,
                                inputTextFieldValue = password,
                                onValueChanged = {
                                    password = it
                                    loginViewModel!!.setPassword(it.text)
                                },

                            )

                            Button(
                                style = ButtonStyle.FILLED,
                                onClick = {
                                    isLoading.value = true
                                    loginViewModel!!.login(loginUiState)
                                },
                                text = getString(R.string.action_sign_in_short),
                                icon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Login,
                                        contentDescription = "Login button",
                                        tint = TextColor.OnPrimary,
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = isLoginEnabled,
                            )
                        }
                        if (showProgress.value) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(1f)
                                    .background(color = Color.Black.copy(0.1f)),
                            ) {
                                ProgressIndicator(
                                    type = ProgressIndicatorType.CIRCULAR,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .align(Alignment.Center),
                                )
                            }
                        }
                    }
                },
            )
        }
        observeLoginResult()
    }

    private fun observeLoginResult() {
        loginViewModel!!.loginResult.observe(this) { loginResult: LoginResult? ->
            if (loginResult == null) {
                return@observe
            }
            isLoading.value = false
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                if (Sdk.d2().programModule().programs().blockingCount() > 0) {
                    ActivityStarter.startActivity(
                        this,
                        ProgramsActivity.getProgramActivityIntent(this),
                        true,
                    )
                } else {
                    ActivityStarter.startActivity(
                        this,
                        MainActivity.getMainActivityIntent(this),
                        true,
                    )
                }
            }
            setResult(RESULT_OK)
        }
    }

    private fun getServerAutoCompleteList(): List<String> {
        return listOf(
            getString(R.string.auto_fill_url),
            getString(R.string.auto_fill_url2),
            getString(
                R.string.auto_fill_url3,
            ),
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private fun showLoginFailed(errorString: String?) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun getLoginActivityIntent(context: Context?): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}
