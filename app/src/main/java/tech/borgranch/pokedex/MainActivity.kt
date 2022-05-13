package tech.borgranch.pokedex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import tech.borgranch.pokedex.ui.main.MainFragment
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        try {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
            }
        } catch (ex: Exception) {
            Timber.e(ex, "Error while creating MainActivity")
            ex.printStackTrace()
        }
    }
}
