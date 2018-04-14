package com.paralect.citiesquiz.presenter

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
interface IDataView<in DATA> {

    fun onDataReceived(data: DATA)
}
