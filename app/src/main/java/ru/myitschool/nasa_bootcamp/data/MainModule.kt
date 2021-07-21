package ru.myitschool.nasa_bootcamp.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.myitschool.nasa_bootcamp.data.api.NasaApi
import ru.myitschool.nasa_bootcamp.data.api.SpaceXApi
import ru.myitschool.nasa_bootcamp.data.model.nasa.asteroids.AsteroidDao
import ru.myitschool.nasa_bootcamp.data.model.nasa.asteroids.AsteroidRepository
import ru.myitschool.nasa_bootcamp.utils.NASA_BASE_URL
import ru.myitschool.nasa_bootcamp.utils.SPACEX_BASE_URL
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    @Named("NASA")
    fun getNasaRetrofit(): Retrofit{
        val okHttpBuiler: OkHttpClient.Builder = OkHttpClient.Builder()

        return Retrofit.Builder()
            .baseUrl(NASA_BASE_URL)
            .addConverterFactory( MoshiConverterFactory.create(
                Moshi.Builder().add(
                KotlinJsonAdapterFactory()
            ).build()))
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpBuiler.build())
            .build()
    }

    @Provides
    @Singleton
    @Named("SPACEX")
    fun getSpaceXRetrofit(): Retrofit{
        val okHttpBuiler: OkHttpClient.Builder = OkHttpClient.Builder()

        return Retrofit.Builder()
            .baseUrl(SPACEX_BASE_URL)
            .addConverterFactory( MoshiConverterFactory.create(
                Moshi.Builder().add(
                    KotlinJsonAdapterFactory()
                ).build()))
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpBuiler.build())
            .build()
    }

    @Provides
    @Singleton
    fun getNasaApi(@Named("NASA") retrofit: Retrofit): NasaApi{
        return retrofit.create(NasaApi::class.java)
    }

    @Provides
    @Singleton
    fun getSpaceXApi(@Named("SPACEX") retrofit: Retrofit): SpaceXApi{
        return retrofit.create(SpaceXApi::class.java)
    }

    @Provides
    @Singleton
    fun getAsteroidRepository(asteroidDao: AsteroidDao, nasaApi: NasaApi): AsteroidRepository{
        return AsteroidRepository(asteroidDao, nasaApi)
    }
}