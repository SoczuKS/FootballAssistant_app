package com.soczuks.footballassistant.di

import com.google.gson.Gson
import com.soczuks.footballassistant.api.AuthInterceptor
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.HttpTokenRefreshGateway
import com.soczuks.footballassistant.api.TokenAuthenticator
import com.soczuks.footballassistant.api.TokenRefreshGateway
import com.soczuks.footballassistant.config.NetworkConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor).authenticator(tokenAuthenticator).build()
    }

    @Provides
    @Singleton
    @Named("tokenRefreshClient")
    fun provideTokenRefreshClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideTokenRefreshGateway(gateway: HttpTokenRefreshGateway): TokenRefreshGateway = gateway

    @Provides
    @Singleton
    fun provideFootballAssistantApi(okHttpClient: OkHttpClient): FootballAssistantApi {
        return Retrofit.Builder().baseUrl(NetworkConfig.BASE_URL).addConverterFactory(
            GsonConverterFactory.create()
        ).client(okHttpClient).build().create(FootballAssistantApi::class.java)
    }
}
