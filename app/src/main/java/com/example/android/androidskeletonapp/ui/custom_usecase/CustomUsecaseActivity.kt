package com.example.android.androidskeletonapp.ui.custom_usecase

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.ui.Modifier
import androidx.databinding.DataBindingUtil
import com.example.android.androidskeletonapp.R
import io.reactivex.disposables.CompositeDisposable
import org.hisp.dhis.mobile.ui.designsystem.theme.SurfaceColor
import org.hisp.dhis.mobile.ui.designsystem.theme.TextColor

class CustomUsecaseActivity : AppCompatActivity() {

    private var disposable = CompositeDisposable()
    @OptIn(ExperimentalFoundationApi::class)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(
                Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(getString(R.string.your_usecase), color = TextColor.OnPrimary)
                        },
                        backgroundColor = SurfaceColor.Primary,
                        navigationIcon = {
                            IconButton(onClick = {
                            finish()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Localized description",
                                    tint = TextColor.OnPrimary
                                )
                            } },

                    )
                },

            ) { contentPadding ->

                val pagerState = rememberPagerState {
                    10
                }
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.padding(contentPadding)
                ) { /* Page contents */
                    Column (Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center){
                        Text(text = "testing compose view without xml")
                    }
                }
            }
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
