package tech.borgranch.pokedex.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import tech.borgranch.pokedex.databinding.MainActivityBinding
import tech.borgranch.pokedex.ui.main.list.ListFragment
import java.net.URI

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ListFragment.OnFragmentInteractionListener {

    private var _binding: MainActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onFragmentInteraction(uri: URI) {
        // This intentionally left blank
    }

    fun toolbarHide() {
        binding.mainToolbar.toolbar.visibility = View.GONE
    }

    fun toolbarShow() {
        binding.mainToolbar.toolbar.visibility = View.VISIBLE
    }
}
