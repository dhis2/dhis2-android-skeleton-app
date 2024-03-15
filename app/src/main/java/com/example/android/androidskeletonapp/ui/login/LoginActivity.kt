package com.example.android.androidskeletonapp.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.android.androidskeletonapp.R
import com.example.android.androidskeletonapp.databinding.ActivityLoginBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.disposables.Disposable
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.mobile.ui.designsystem.component.Button
import org.hisp.dhis.mobile.ui.designsystem.component.InputShellState
import org.hisp.dhis.mobile.ui.designsystem.component.InputText

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private var loginViewModel: LoginViewModel? = null
    private var disposable: Disposable? = null
    private var serverUrlEditText: TextInputEditText? = null
    private var usernameEditText: TextInputEditText? = null
    private var passwordEditText: TextInputEditText? = null
    private var loginButton: MaterialButton? = null
    private var loadingProgressBar: ProgressBar? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory()).get(
            LoginViewModel::class.java
        )
        binding.composeView.setContent {
            var serverUrlText by remember{ mutableStateOf(getString(R.string.auto_fill_url)) }
            var userName by remember{ mutableStateOf(getString(R.string.auto_fill_username)) }
            var password by remember{ mutableStateOf(getString(R.string.auto_fill_password)) }
            var isEnabled by remember{ mutableStateOf(true) }

            Column(modifier = Modifier.fillMaxHeight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {

                Spacer(modifier = Modifier.size(40.dp))
                InputText(
                    title =  getString(R.string.prompt_server_url),
                    state = InputShellState.UNFOCUSED,
                    inputText = serverUrlText,
                    onValueChanged = {newValue ->  serverUrlText = newValue ?: ""}
                )

                InputText(
                    title = getString(R.string.prompt_username),
                    state = InputShellState.UNFOCUSED,
                    inputText = userName,
                    onValueChanged = { newValue ->
                        userName = newValue ?: ""
                    })

                InputText(
                    title = getString(R.string.prompt_password),
                    state = InputShellState.UNFOCUSED,
                    inputText = password,
                    onValueChanged = { newValue ->
                        password = newValue ?: ""
                    },
                )

                Spacer(Modifier.size(20.dp))

                Button(
                    text = getString(R.string.action_sign_in_short),
                    onClick = {},
                            enabled =  isEnabled,
                )
            }


        }


        serverUrlEditText = binding.urlText
        usernameEditText = binding.usernameText
        passwordEditText = binding.passwordText
        loginButton = binding.loginButton
        loadingProgressBar = binding.loginProgressBar

        /*
        loginViewModel!!.loginFormState.observe(this) { loginFormState: LoginFormState? ->
            if (loginFormState == null) {
                return@observe
            }
            loginButton.setEnabled(loginFormState.isDataValid)
            if (loginFormState.serverUrlError != null) {
                serverUrlEditText.setError(getString(loginFormState.serverUrlError!!))
            }
            if (loginFormState.usernameError != null) {
                usernameEditText.setError(getString(loginFormState.usernameError!!))
            }
            if (loginFormState.passwordError != null) {
                passwordEditText.setError(getString(loginFormState.passwordError!!))
            }
        }
        loginViewModel!!.loginResult.observe(this) { loginResult: LoginResult? ->
            if (loginResult == null) {
                return@observe
            }
            loadingProgressBar.setVisibility(View.GONE)
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                if (Sdk.d2().programModule().programs().blockingCount() > 0) {
                    ActivityStarter.startActivity(
                        this,
                        ProgramsActivity.getProgramActivityIntent(this),
                        true
                    )
                } else {
                    ActivityStarter.startActivity(
                        this,
                        MainActivity.getMainActivityIntent(this),
                        true
                    )
                }
            }
            setResult(RESULT_OK)
        }

         */

        val afterTextChangedListener: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
              /*  loginViewModel!!.loginDataChanged(
                    serverUrlEditText.getText().toString(),
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString()
                )

               */
            }
        }
       /*
        serverUrlEditText.addTextChangedListener(afterTextChangedListener)
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener(OnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login()
            }
            false
        })
        loginButton.setOnClickListener(View.OnClickListener { v: View? -> login() })

        */
    }

    private fun login() {
        loadingProgressBar!!.visibility = View.VISIBLE
        loginButton!!.visibility = View.INVISIBLE
        val username = usernameEditText!!.getText().toString()
        val password = passwordEditText!!.getText().toString()
        val serverUrl = serverUrlEditText!!.getText().toString()
        disposable = loginViewModel
            ?.login(username, password, serverUrl)
            ?.doOnTerminate { loginButton!!.visibility = View.VISIBLE }
            ?.subscribe(
                { u: User? -> }
            ) { t: Throwable? -> }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (disposable != null) {
            disposable!!.dispose()
        }
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
