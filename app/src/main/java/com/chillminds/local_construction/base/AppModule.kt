package com.chillminds.local_construction.base

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.chillminds.local_construction.BuildConfig
import com.chillminds.local_construction.common.SecurePreference
import com.chillminds.local_construction.models.CommonModel
import com.chillminds.local_construction.repositories.remote.RemoteRepository
import com.chillminds.local_construction.repositories.remote.service.ApiHelper
import com.chillminds.local_construction.repositories.remote.service.ApiService
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.chillminds.local_construction.view_models.SplashViewModel
import com.google.gson.GsonBuilder
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class AppModule {
    private val utilsModule = module {
        single { getSharedPreference(androidApplication(), getProperty("pref.name")) }
        singleOf(::SecurePreference)
    }

    fun getModules() =
        listOf(networkModule, utilsModule,viewModelsModule)

    private val viewModelsModule= module {
        singleOf(::CommonModel)
        factoryOf(::SplashViewModel)
        singleOf(::DashboardViewModel)
    }

    private val networkModule = module {

        singleOf(::provideOkHttpClient)
        singleOf(::RemoteRepository)

        single { provideRetrofit(get(), getProperty("base.url")) }
        single { provideApiService(get()) }

        single {
            ApiHelper(
                get(),
                Credentials.basic(getProperty("user.name"), getProperty("user.password"), StandardCharsets.UTF_8)
            )
        }
    }

    private fun getSharedPreference(
        application: Application,
        sharedPrefsFile: String
    ): SharedPreferences {
        return try {
            val mainKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            //MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

            EncryptedSharedPreferences.create(
                sharedPrefsFile,
                mainKey,
                application.applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e:Exception){
            e.printStackTrace()
            application.applicationContext.getSharedPreferences(sharedPrefsFile, Context.MODE_PRIVATE)
        }
    }

    private fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    private fun provideRetrofit(client: OkHttpClient, url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().serializeNulls().create()
                )
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    private fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder().connectTimeout(6000, TimeUnit.SECONDS)
            .readTimeout(6000, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        return builder.build()
    }

}