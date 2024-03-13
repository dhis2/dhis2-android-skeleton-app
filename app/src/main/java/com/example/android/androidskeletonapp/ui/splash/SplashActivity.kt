package com.example.android.androidskeletonapp.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.androidskeletonapp.R
import com.example.android.androidskeletonapp.data.Sdk
import com.example.android.androidskeletonapp.data.service.ActivityStarter
import com.example.android.androidskeletonapp.ui.login.LoginActivity
import com.example.android.androidskeletonapp.ui.main.MainActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.Exceptions
import io.reactivex.internal.functions.ObjectHelper
import io.reactivex.plugins.RxJavaPlugins
import org.hisp.dhis.android.core.D2Manager.blockingInstantiateD2

class SplashActivity : AppCompatActivity() {
    private var disposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        try {
            blockingInstantiateD2(Sdk.getD2Configuration(this))?.userModule()?.isLogged()
                ?.subscribe(
                    {isLogged: Boolean ->
                        if (isLogged) {
                            ActivityStarter.startActivity(
                                this,
                                MainActivity.getMainActivityIntent(this),
                                true
                            )
                        } else {
                            ActivityStarter.startActivity(
                                this,
                                LoginActivity.getLoginActivityIntent(this),
                                true
                            )
                        }},
                    {throwable: Throwable ->
                        throwable.printStackTrace()
                        ActivityStarter.startActivity(
                            this,
                            LoginActivity.getLoginActivityIntent(this),
                            true
                        )}
                )?.let {
                    disposable.add(
                        it
                    )
                }

        } catch (ex: Throwable) {
            ex.printStackTrace()

            ActivityStarter.startActivity(
                this,
                LoginActivity.getLoginActivityIntent(this),
                true
            )
        }


    }

    override fun onDestroy() {
        super.onDestroy()
            disposable.dispose()

    }
}