package tech.borgranch.pokedex

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import tech.borgranch.pokedex.utils.HyperlinkDebugTree
import timber.log.Timber
import java.util.UUID

@HiltAndroidApp
class PokedexApp : Application() {
    companion object {
        lateinit var instance: PokedexApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG) {
            // Have coroutines log out state changes
            System.setProperty("kotlinx.coroutines.debug", "on")
            // See explanation for HyperlinkDebugTree
            Timber.plant(HyperlinkDebugTree())
        }
    }

    fun getImageFile(): String {
        return "pokeart${UUID.randomUUID()}.jpg"
    }
}
