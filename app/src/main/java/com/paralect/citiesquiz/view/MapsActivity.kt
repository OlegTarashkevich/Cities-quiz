package com.paralect.citiesquiz.view

import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
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

    // region Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> presenter.loadGame()
        }
        return super.onOptionsItemSelected(item)
    }
    // endregion

    // region Map
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // show Berlin
        val berlin = LatLng(52.520008, 13.404954)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(berlin))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(3.0f))
        
        try {
            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success)
                onError(RuntimeException("Style parsing failed."))
        } catch (e: Resources.NotFoundException) {
            onError(e)
        }

        binding.placeButton.setOnClickListener {
            if (presenter.isLoaded()) {
                mMap.clear()
                val realCoordinate = presenter.getRealCoordinate()
                realCoordinate?.let { mMap.addMarker(MarkerOptions().position(realCoordinate).title("Correct Place")) }
                val center = mMap.cameraPosition.target
                presenter.setUsersCoordinate(center)
            }
        }
    }
    // endregion

    // region IGameView
    override fun onLevelLoaded(level: GameLevel, result: GameResult) {
        binding.statusTextview.text = level.details
        binding.countTextview.text = result.correctCities.toString()
        binding.kmTextview.text = result.getRealDistance()
    }

    override fun onGameResult(result: GameResult) {
        binding.statusTextview.text = getString(R.string.finished)
        binding.countTextview.text = result.correctCities.toString()
        binding.kmTextview.text = result.getRealDistance()
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        Toast.makeText(this, e.message, LENGTH_SHORT).show()
        onShowProgress(false)
    }

    override fun onShowProgress(show: Boolean) {
        binding.progressBar.setVisibile(show)
    }

    override fun onMessage(message: String) {
        Toast.makeText(this, message, LENGTH_SHORT).show()
    }
    // endregion
}
