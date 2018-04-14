package com.paralect.citiesquiz.presenter

import android.util.Log
import com.paralect.citiesquiz.App
import com.paralect.citiesquiz.R
import com.paralect.citiesquiz.data.DataUtil
import com.paralect.citiesquiz.data.model.CapitalsPack
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import android.location.Geocoder
import com.paralect.citiesquiz.data.model.City
import java.util.*

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
class CitiesPresenter() : IDataPresenter<List<City>, Unit> {

    private var mDataView: IDataView<List<City>>? = null

    override fun setDataView(view: IDataView<List<City>>) {
        mDataView = view
    }

    override fun requestData(parameter: Unit) {
        loadCities()
    }

    private fun loadCities() {
        DataUtil().getResponseFromResObservable(App.app, CapitalsPack::class.java, R.raw.capital_cities)
                .toFlowable()
                .flatMapIterable{it.pack}
                .map {
                    val geocoder = Geocoder(App.app, Locale.getDefault())
                    val addresses = geocoder.getFromLocationName(it.capitalCity, 1)
                    val address = addresses[0]
                    it.latitude = address.latitude
                    it.longitude = address.longitude
                  it
                }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val pack = it
                    Log.d("", "")
                    mDataView?.onDataReceived(it)
                }, {
                    it.printStackTrace()
                })
    }

}