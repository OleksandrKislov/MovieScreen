package com.afishapet.moviescreen.ui.utils

import android.app.Activity
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistryOwner
import dagger.hilt.android.EntryPointAccessors

/**
* used for @AssistedInject before Dagger Hilt 2.49
 *
 * Usage:
 * in the  ViewModel
 *  ```kotlin
 * @AssistedFactory
 * interface MovieInfoViewModelFactory :
 *     AssistedSavedStateViewModelFactory<MovieInfoViewModel, MovieInfoViewModelArgs>
 *
 * data class MovieInfoViewModelArgs(
 *     val movieId: String,
 * ) : AssistedViewModelArguments
 *
 * class MovieInfoViewModel @AssistedInject constructor(
 *     @Assisted override val savedStateHandle: SavedStateHandle,
 *     @Assisted override val assistedArguments: MovieInfoViewModelArgs,
 *     private val getMovieInfoUseCase: GetMovieInfoUseCase,
 *     private val getMovieScheduleUseCase: GetMovieScheduleUseCase,
 * ) : AssistedSavedStateViewModel<MovieInfoViewModelArgs>() {}
 *```
 *
 * in the MainActivity
 * ```kotlin
 * @EntryPoint
 * @InstallIn(ActivityComponent::class)
 * interface MyProvider : AssistedViewModelFactoryProvider {
 *     fun movieInfoViewModelFactory(): MovieInfoViewModelFactory
 * }
 *```
 * in the NavRoot
 * ```kotlin
 * val movieInfoViewModelFactory = MainActivity.MyProvider::movieInfoViewModelFactory
 * val args = MovieInfoViewModelArgs(movieId = movieId)
 * val movieInfoViewModel = assistedViewModel(movieInfoViewModelFactory, args)
 * ```
 * */

abstract class AssistedSavedStateViewModel<Args : AssistedViewModelArguments> : AssistedViewModel<Args>() {
    protected abstract val savedStateHandle: SavedStateHandle
}

abstract class AssistedViewModel<Args : AssistedViewModelArguments> : ViewModel() {
    protected abstract val assistedArguments: Args
}

interface AssistedSavedStateViewModelFactory<AVM : AssistedViewModel<Args>, Args : AssistedViewModelArguments> {
    fun create(
        handle: SavedStateHandle,
        assistedArgs: Args,
    ): AVM
}

interface AssistedViewModelFactory<AVM : AssistedViewModel<Args>, Args : AssistedViewModelArguments> {
    fun create(assistedArgs: Args): AVM
}

interface AssistedViewModelArguments

interface AssistedViewModelFactoryProvider

@Composable
inline fun <
        Args : AssistedViewModelArguments,
        reified AVM : AssistedSavedStateViewModel<Args>,
        reified Provider : AssistedViewModelFactoryProvider,
        > assistedViewModel(
    getAssistedFactory: Provider.() -> AssistedSavedStateViewModelFactory<AVM, Args>,
    assistedArgs: Args,
    savedStateHandleDefaultArgs: Bundle? = null,
): AVM {
    val assistedFactory = getProvider<Provider>().getAssistedFactory()

    val lifecycleOwner = LocalLifecycleOwner.current as SavedStateRegistryOwner

    return viewModel(
        factory = object : AbstractSavedStateViewModelFactory(
            owner = lifecycleOwner,
            defaultArgs = savedStateHandleDefaultArgs
        ) {
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle,
            ): T {
                @Suppress("UNCHECKED_CAST")
                return assistedFactory.create(handle, assistedArgs) as T
            }
        }
    )
}

@Composable
inline fun <
        Args : AssistedViewModelArguments,
        reified AVM : AssistedViewModel<Args>,
        reified Provider : AssistedViewModelFactoryProvider,
        > assistedViewModel(
    getAssistedFactory: Provider.() -> AssistedViewModelFactory<AVM, Args>,
    assistedArgs: Args,
): AVM {
    val assistedFactory = getProvider<Provider>().getAssistedFactory()

    return viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return assistedFactory.create(assistedArgs) as T
            }
        }
    )
}

@Composable
inline fun <reified Provider : AssistedViewModelFactoryProvider> getProvider(): Provider {
    return EntryPointAccessors.fromActivity(
        activity = LocalContext.current as Activity,
        entryPoint = Provider::class.java
    )
}
