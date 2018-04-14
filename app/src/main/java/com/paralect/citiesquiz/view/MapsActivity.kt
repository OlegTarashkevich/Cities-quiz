package com.paralect.citiesquiz.view

import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.paralect.citiesquiz.R
import com.paralect.citiesquiz.data.model.GameLevel
import com.paralect.citiesquiz.presenter.GamePresenter
import com.paralect.citiesquiz.presenter.IGameView

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, IGameView {

    companion object {
        val TAG = MapsActivity::class.java.simpleName
    }

    private val presenter = GamePresenter()

    private lateinit var mMap: GoogleMap
    private lateinit var mStatusTextView: TextView
    private lateinit var taskTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mStatusTextView = findViewById(R.id.status_textview)
        taskTextView = findViewById(R.id.task_textview)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        presenter.setGameView(this)
        presenter.loadGame()
    }

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

        mMap.setOnMapClickListener {
            mMap.addMarker(MarkerOptions().position(it).title("Your choice"))
            presenter.setUsersCoordinate(it)
        }
    }

    // region IGameView
    override fun onLevelLoaded(level: GameLevel) {
        taskTextView.text = level.details
    }

    override fun onGameOver() {
        taskTextView.text = "Game Over"
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        Toast.makeText(this, e.toString(), LENGTH_SHORT).show()
    }
    // endregion
}