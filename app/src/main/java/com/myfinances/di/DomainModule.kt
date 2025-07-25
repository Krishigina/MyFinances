package com.myfinances.di

import android.content.Context
import com.myfinances.data.manager.HapticFeedbackManager
import com.myfinances.data.manager.LocaleManager
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.repository.PinRepository
import com.myfinances.domain.repository.SessionRepository
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.usecase.DeletePinUseCase
import com.myfinances.domain.usecase.GetAccountUseCase
import com.myfinances.domain.usecase.GetActiveAccountIdUseCase
import com.myfinances.domain.usecase.GetAnalysisDataUseCase
import com.myfinances.domain.usecase.GetAppInfoUseCase
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.usecase.GetColorPaletteUseCase
import com.myfinances.domain.usecase.GetCurrentLanguageUseCase
import com.myfinances.domain.usecase.GetHapticSettingsUseCase
import com.myfinances.domain.usecase.GetLastSyncTimeUseCase
import com.myfinances.domain.usecase.GetSyncFrequencyUseCase
import com.myfinances.domain.usecase.GetThemeUseCase
import com.myfinances.domain.usecase.GetTransactionsUseCase
import com.myfinances.domain.usecase.IsPinSetUseCase
import com.myfinances.domain.usecase.PreviewHapticEffectUseCase
import com.myfinances.domain.usecase.SaveColorPaletteUseCase
import com.myfinances.domain.usecase.SaveHapticEffectUseCase
import com.myfinances.domain.usecase.SaveHapticsEnabledUseCase
import com.myfinances.domain.usecase.SaveLanguageUseCase
import com.myfinances.domain.usecase.SavePinUseCase
import com.myfinances.domain.usecase.SaveSyncFrequencyUseCase
import com.myfinances.domain.usecase.SaveThemeUseCase
import com.myfinances.domain.usecase.SetupPeriodicSyncUseCase
import com.myfinances.domain.usecase.VerifyPinUseCase
import dagger.Module
import dagger.Provides

@Module
object DomainModule {

    @Provides
    fun provideGetAccountUseCase(
        repository: AccountsRepository,
        sessionRepository: SessionRepository
    ): GetAccountUseCase {
        return GetAccountUseCase(repository, sessionRepository)
    }

    @Provides
    fun provideGetCategoriesUseCase(repository: CategoriesRepository): GetCategoriesUseCase {
        return GetCategoriesUseCase(repository)
    }

    @Provides
    fun provideGetTransactionsUseCase(
        transactionsRepository: TransactionsRepository,
        categoriesRepository: CategoriesRepository,
        accountsRepository: AccountsRepository,
        getActiveAccountIdUseCase: GetActiveAccountIdUseCase
    ): GetTransactionsUseCase {
        return GetTransactionsUseCase(
            transactionsRepository,
            categoriesRepository,
            accountsRepository,
            getActiveAccountIdUseCase
        )
    }

    @Provides
    fun provideGetAnalysisDataUseCase(
        transactionsRepository: TransactionsRepository,
        categoriesRepository: CategoriesRepository,
        accountsRepository: AccountsRepository,
        getActiveAccountIdUseCase: GetActiveAccountIdUseCase
    ): GetAnalysisDataUseCase {
        return GetAnalysisDataUseCase(
            transactionsRepository,
            categoriesRepository,
            accountsRepository,
            getActiveAccountIdUseCase
        )
    }

    @Provides
    fun provideGetLastSyncTimeUseCase(sessionRepository: SessionRepository): GetLastSyncTimeUseCase {
        return GetLastSyncTimeUseCase(sessionRepository)
    }

    @Provides
    fun provideGetThemeUseCase(sessionRepository: SessionRepository): GetThemeUseCase {
        return GetThemeUseCase(sessionRepository)
    }

    @Provides
    fun provideSaveThemeUseCase(sessionRepository: SessionRepository): SaveThemeUseCase {
        return SaveThemeUseCase(sessionRepository)
    }

    @Provides
    fun provideGetColorPaletteUseCase(sessionRepository: SessionRepository): GetColorPaletteUseCase {
        return GetColorPaletteUseCase(sessionRepository)
    }

    @Provides
    fun provideSaveColorPaletteUseCase(sessionRepository: SessionRepository): SaveColorPaletteUseCase {
        return SaveColorPaletteUseCase(sessionRepository)
    }

    @Provides
    fun provideGetHapticSettingsUseCase(sessionRepository: SessionRepository): GetHapticSettingsUseCase {
        return GetHapticSettingsUseCase(sessionRepository)
    }

    @Provides
    fun provideSaveHapticsEnabledUseCase(sessionRepository: SessionRepository): SaveHapticsEnabledUseCase {
        return SaveHapticsEnabledUseCase(sessionRepository)
    }

    @Provides
    fun provideSaveHapticEffectUseCase(sessionRepository: SessionRepository): SaveHapticEffectUseCase {
        return SaveHapticEffectUseCase(sessionRepository)
    }

    @Provides
    fun providePreviewHapticEffectUseCase(hapticFeedbackManager: HapticFeedbackManager): PreviewHapticEffectUseCase {
        return PreviewHapticEffectUseCase(hapticFeedbackManager)
    }

    @Provides
    fun provideGetCurrentLanguageUseCase(): GetCurrentLanguageUseCase {
        return GetCurrentLanguageUseCase()
    }

    @Provides
    fun provideSaveLanguageUseCase(localeManager: LocaleManager): SaveLanguageUseCase {
        return SaveLanguageUseCase(localeManager)
    }

    @Provides
    fun provideIsPinSetUseCase(pinRepository: PinRepository): IsPinSetUseCase {
        return IsPinSetUseCase(pinRepository)
    }

    @Provides
    fun provideSavePinUseCase(pinRepository: PinRepository): SavePinUseCase {
        return SavePinUseCase(pinRepository)
    }

    @Provides
    fun provideVerifyPinUseCase(pinRepository: PinRepository): VerifyPinUseCase {
        return VerifyPinUseCase(pinRepository)
    }

    @Provides
    fun provideDeletePinUseCase(pinRepository: PinRepository): DeletePinUseCase {
        return DeletePinUseCase(pinRepository)
    }

    @Provides
    fun provideGetSyncFrequencyUseCase(sessionRepository: SessionRepository): GetSyncFrequencyUseCase {
        return GetSyncFrequencyUseCase(sessionRepository)
    }

    @Provides
    fun provideSaveSyncFrequencyUseCase(
        sessionRepository: SessionRepository,
        setupPeriodicSyncUseCase: SetupPeriodicSyncUseCase
    ): SaveSyncFrequencyUseCase {
        return SaveSyncFrequencyUseCase(sessionRepository, setupPeriodicSyncUseCase)
    }

    @Provides
    fun provideSetupPeriodicSyncUseCase(
        context: Context,
        sessionRepository: SessionRepository
    ): SetupPeriodicSyncUseCase {
        return SetupPeriodicSyncUseCase(context, sessionRepository)
    }

    @Provides
    fun provideGetAppInfoUseCase(): GetAppInfoUseCase {
        return GetAppInfoUseCase()
    }
}