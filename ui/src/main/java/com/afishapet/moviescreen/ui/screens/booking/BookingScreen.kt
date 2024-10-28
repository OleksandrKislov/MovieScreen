package com.afishapet.moviescreen.ui.screens.booking

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.afishapet.moviescreen.ui.R
import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.ui.components.AppCircularProgressIndicator
import com.afishapet.moviescreen.ui.components.BaseErrorHandler
import com.afishapet.moviescreen.ui.components.ErrorDialog
import com.afishapet.moviescreen.ui.utils.LocalTopAppBarState

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BookingScreen(
    state: BookingViewModel.ScreenState,
    onEvent: (BookingViewModel.UiEvent) -> Unit,
) {
    with(LocalTopAppBarState.current) {
        LaunchedEffect(state.movieName) {
            titleText.value = state.movieName
            actions.value = {}
        }
    }

    var webView: WebView? by remember { mutableStateOf(null) }
    val animationScale by animateFloatAsState(
        targetValue = if (state.isLoadingWebView) 0f else 1f,
        label = "wv scale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = state.posterImageUrl,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        when (state.bookingUrlAnswer) {
            is Answer.Loading -> AppCircularProgressIndicator()
            is Answer.Success -> {
                when {
                    state.webResourceError != null -> {
                        ErrorDialog(
                            modifier = Modifier.padding(5.dp),
                            title = stringResource(R.string.failed_to_open_web_page_title),
                            text = stringResource(
                                R.string.failed_to_open_web_page_text,
                                state.webResourceError.description
                            ),
                            icon = Icons.Default.Warning,
                            confirmButtonText = stringResource(R.string.retry),
                            onConfirmClick = {
                                onEvent(BookingViewModel.UiEvent.UpdateWebResourceError(null))
                                webView?.reload()
                            }
                        )
                    }

                    state.isLoadingWebView -> {
                        AppCircularProgressIndicator()
                    }

                    !state.isEventAvailable -> {
                        ElevatedCard(
                            modifier = Modifier.padding(20.dp),
                            colors = CardDefaults.elevatedCardColors().copy(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text(
                                modifier = Modifier.padding(5.dp),
                                text = stringResource(R.string.show_will_start_soon),
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (state.isEventAvailable && state.webResourceError == null) {
                    var webViewHeight by remember { mutableIntStateOf(0) }
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .run {
                                if (webViewHeight > 0)
                                    height(with(LocalDensity.current) { webViewHeight.toDp() })
                                else this
                            }
                            .graphicsLayer(
                                scaleY = animationScale,
                                alpha = if (state.isLoadingWebView) 0f else 1f
                            )
                            .onPlaced {
                                if (webViewHeight == 0) {
                                    webViewHeight = it.size.height
                                }
                            },
                        factory = { context ->
                            WebView(context).apply {

                                webView = this

                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )

                                settings.apply {
                                    javaScriptEnabled = true
                                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                    builtInZoomControls = true
                                    displayZoomControls = false
                                    useWideViewPort = true
                                    loadWithOverviewMode = true
                                }

                                webViewClient = object : WebViewClient() {

                                    override fun onPageCommitVisible(view: WebView, url: String) {
                                        super.onPageCommitVisible(view, url)
                                        if (url.contains("widget/site") || url.contains("widget/closed")) {
                                            onEvent(BookingViewModel.UiEvent.EventUnavailable)
                                        }
                                        onEvent(BookingViewModel.UiEvent.WebViewLoaded)
                                    }

                                    override fun shouldOverrideUrlLoading(
                                        view: WebView,
                                        request: WebResourceRequest
                                    ): Boolean {
                                        return request.url.toString().contains("widget/").not()
                                    }

                                    override fun onReceivedError(
                                        view: WebView?,
                                        request: WebResourceRequest?,
                                        error: WebResourceError?
                                    ) {
                                        super.onReceivedError(view, request, error)
                                        onEvent(BookingViewModel.UiEvent.UpdateWebResourceError(error))
                                    }
                                }

                                loadUrl(state.bookingUrlAnswer.data)
                            }
                        },
                        update = {
                            it.loadUrl(state.bookingUrlAnswer.data)
                        }
                    )
                }
            }

            is Answer.Error -> BaseErrorHandler(
                modifier = Modifier.padding(5.dp),
                exception = state.bookingUrlAnswer.exception,
                confirmAction = { onEvent(BookingViewModel.UiEvent.RetryGenerateMovieBookingUrl) }
            )
        }
    }
}