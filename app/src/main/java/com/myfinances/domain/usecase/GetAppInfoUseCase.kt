package com.myfinances.domain.usecase

import com.myfinances.BuildConfig
import com.myfinances.domain.entity.AppInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetAppInfoUseCase @Inject constructor() {
    operator fun invoke(): AppInfo {
        val buildDate = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale.getDefault())
            .format(Date(BuildConfig.BUILD_TIME))

        return AppInfo(
            version = BuildConfig.VERSION_NAME,
            buildDate = buildDate
        )
    }
}