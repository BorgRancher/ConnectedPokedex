package tech.borgranch.pokedex.di

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import tech.borgranch.pokedex.BuildConfig
import tech.borgranch.pokedex.network.HttpRequestInterceptor
import javax.inject.Singleton

/**
 * @author Shaun McDonald
 * @version 1.2
 * Determines the dependencies for the network module.
 */

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    /**
     * @return OkHttpClient - The OkHttpClient used for the network module.
     */
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpRequestInterceptor())
            .build()
    }

    @Provides
    @Singleton
    /**
     * @return ApolloClient
     * Provides the ApolloClient for the network module.
     * Since we're running a vanilla graphql client, Apollo is all we need,
     * though we are using an OkHttpClient for the interceptor.
     */
    fun providePokedexClient(okHttpClient: OkHttpClient): ApolloClient {
        return ApolloClient.Builder()
            .okHttpClient(okHttpClient)
            .serverUrl(BuildConfig.BASE_URL)
            .build()
    }
}
