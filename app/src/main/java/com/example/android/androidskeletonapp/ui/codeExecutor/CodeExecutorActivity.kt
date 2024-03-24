package com.example.android.androidskeletonapp.ui.codeExecutor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.android.androidskeletonapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CodeExecutorActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null
    private var executingNotificator: TextView? = null
    private var resultNotificator: TextView? = null
    private var disposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_executor)
        val toolbar = findViewById<Toolbar>(R.id.codeExecutorToolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        executingNotificator = findViewById(R.id.codeExecutorNotificator)
        resultNotificator = findViewById(R.id.resultNotificator)
        progressBar = findViewById(R.id.codeExecutorProgressBar)
        val codeExecutorButton = findViewById<FloatingActionButton>(R.id.codeExecutorButton)
        codeExecutorButton.setOnClickListener { view: View ->
            view.isEnabled = java.lang.Boolean.FALSE
            view.visibility = View.INVISIBLE
            Snackbar.make(view, "Executing...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            executingNotificator?.setVisibility(View.VISIBLE)
            progressBar?.setVisibility(View.VISIBLE)
            resultNotificator?.setVisibility(View.INVISIBLE)
            disposable = executeCode()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: String? ->
                        executingNotificator?.setVisibility(View.INVISIBLE)
                        progressBar?.setVisibility(View.INVISIBLE)
                        resultNotificator?.setText(result)
                        resultNotificator?.setVisibility(View.VISIBLE)
                        view.isEnabled = java.lang.Boolean.TRUE
                        view.visibility = View.VISIBLE
                    },
                ) { obj: Throwable -> obj.printStackTrace() }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (disposable != null) {
            disposable!!.dispose()
        }
    }

    private fun executeCode(): Single<String> {
        return Single.just("Execution done!")
    }

    companion object {
        fun getIntent(context: Context?): Intent {
            return Intent(context, CodeExecutorActivity::class.java)
        }
    }
}
