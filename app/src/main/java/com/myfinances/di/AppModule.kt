package com.myfinances.di

import android.content.Context
import com.myfinances.BuildConfig
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.data.manager.HapticFeedbackManager
import com.myfinances.data.manager.LocaleManager
import com.myfinances.data.manager.SnackbarManager
import com.myfinances.data.manager.SyncUpdateManager
import com.myfinances.data.network.RetryInterceptor
import com.myfinances.domain.repository.SessionRepository
import com.myfinances.ui.util.ResourceProvider
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
object AppModule {
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideRetryInterceptor(): RetryInterceptor {
        return RetryInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        retryInterceptor: RetryInterceptor
    ): OkHttpClient {
        val authInterceptor = okhttp3.Interceptor { chain ->
            val originalRequest = chain.request()
            val token = "Bearer ${BuildConfig.API_KEY}"
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", token)
                .build()
            chain.proceed(newRequest)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(retryInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideAccountUpdateManager(): AccountUpdateManager {
        return AccountUpdateManager()
    }

    @Provides
    @Singleton
    fun provideResourceProvider(context: Context): ResourceProvider {
        return ResourceProvider(context)
    }

    @Provides
    @Singleton
    fun provideSyncUpdateManager(sessionRepository: SessionRepository): SyncUpdateManager {
        return SyncUpdateManager(sessionRepository)
    }

    @Provides
    @Singleton
    fun provideSnackbarManager(): SnackbarManager {
        return SnackbarManager()
    }

    @Provides
    @Singleton
    fun provideHapticFeedbackManager(
        context: Context,
        sessionRepository: SessionRepository
    ): HapticFeedbackManager {
        return HapticFeedbackManager(context, sessionRepository)
    }

    @Provides
    @Singleton
    fun provideLocaleManager(): LocaleManager {
        return LocaleManager()
    }
}