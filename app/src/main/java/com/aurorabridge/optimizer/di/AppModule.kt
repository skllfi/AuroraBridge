package com.aurorabridge.optimizer.di

import android.content.Context
import com.aurorabridge.optimizer.optimizer.BrandAutoOptimizer
import com.aurorabridge.optimizer.repository.SettingsRepository
import com.aurorabridge.optimizer.utils.AdbCommander
import com.aurorabridge.optimizer.utils.BackupManager
import com.aurorabridge.optimizer.utils.IAdbCommander
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAdbCommander(): IAdbCommander {
        return AdbCommander
    }

    @Provides
    @Singleton
    fun provideBrandAutoOptimizer(
        @ApplicationContext context: Context,
        adbCommander: IAdbCommander
    ): BrandAutoOptimizer {
        return BrandAutoOptimizer(context, adbCommander)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepository(context)
    }

    @Provides
    @Singleton
    fun provideBackupManager(): BackupManager {
        return BackupManager
    }
}
