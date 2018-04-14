package com.paralect.citiesquiz.view

import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.paralect.citiesquiz.R
import com.paralect.citiesquiz.data.model.GameLevel
import com.paralect.citiesquiz.data.model.GameResult
import com.paralect.citiesquiz.databinding.ActivityMapsBinding
import com.paralect.citiesquiz.presenter.GamePresenter
import com.paralect.citiesquiz.presenter.IGameView
import com.paralect.citiesquiz.utils.setVisibile


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, IGameView {

    private val presenter = GamePresenter()

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMapsBinding>(this, R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        presenter.setGameView(this)
        presenter.loadGame()
    }

    // region Map
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMaxZoomPreference(15f)

        try {
            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success)
                onError(RuntimeException("Style parsing failed."))
        } catch (e: Resources.NotFoundException) {
            onError(e)
        }

        binding.placeButton.setOnClickListener {
            if (presenter.isLoaded()) {
                val center = mMap.cameraPosition.target
                mMap.clear()
                presenter.setUsersCoordinate(center)
            }
        }
    }
    // endregion

    // region IGameView
    override fun onLevelLoaded(level: GameLevel) {
        binding.taskTextview.text = level.details
    }

    override fun onGameResult(result: GameResult) {
        binding.taskTextview.text = result.toString()
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        Toast.makeText(this, e.message, LENGTH_SHORT).show()
        onShowProgress(false)
    }

    override fun onShowProgress(show: Boolean) {
        binding.progressBar.setVisibile(show)
    }
    // endregion
}
