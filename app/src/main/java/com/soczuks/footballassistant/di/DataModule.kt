package com.soczuks.footballassistant.di

import com.soczuks.footballassistant.auth.ApiSessionValidator
import com.soczuks.footballassistant.auth.SessionValidator
import com.soczuks.footballassistant.data.DataStoreManager
import com.soczuks.footballassistant.data.SessionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindSessionManager(dataStoreManager: DataStoreManager): SessionManager

    @Binds
    @Singleton
    abstract fun bindSessionValidator(validator: ApiSessionValidator): SessionValidator
}
