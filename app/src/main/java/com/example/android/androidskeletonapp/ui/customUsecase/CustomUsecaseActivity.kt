package com.example.android.androidskeletonapp.ui.customUsecase

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.example.android.androidskeletonapp.R
import io.reactivex.disposables.CompositeDisposable

class CustomUsecaseActivity : AppCompatActivity() {

    private var disposable = CompositeDisposable()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomUseCaseScreen(
                title = getString(R.string.your_usecase),
                navigationAction = { finish() },
                modifier = Modifier,
                content = {
                    Text(text = getString(R.string.custom_usecase))
                },
            )
        }
    }

    companion object {
        fun getIntent(context: Context?): Intent {
            return Intent(context, CustomUsecaseActivity::class.java)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
