package com.example.android.androidskeletonapp.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.android.androidskeletonapp.R
import com.example.android.androidskeletonapp.data.Sdk
import com.example.android.androidskeletonapp.data.service.ActivityStarter
import com.example.android.androidskeletonapp.databinding.ActivitySplashBinding
import com.example.android.androidskeletonapp.ui.login.LoginActivity
import com.example.android.androidskeletonapp.ui.main.MainActivity
import io.reactivex.disposables.CompositeDisposable
import org.hisp.dhis.android.core.D2Manager.blockingInstantiateD2

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding

    private var disposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.SplashTheme)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        blockingInstantiateD2(Sdk.getD2Configuration(this))?.userModule()?.isLogged()
            ?.subscribe(
                { isLogged: Boolean ->
                    if (isLogged) {
                        ActivityStarter.startActivity(
                            this,
                            MainActivity.getMainActivityIntent(this),
                            true,
                        )
                    } else {
                        ActivityStarter.startActivity(
                            this,
                            LoginActivity.getLoginActivityIntent(this),
                            true,
                        )
                    }
                },
                { throwable: Throwable ->
                    throwable.printStackTrace()
                    ActivityStarter.startActivity(
                        this,
                        LoginActivity.getLoginActivityIntent(this),
                        true,
                    )
                },
            )?.let {
                disposable.add(
                    it,
                )
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
