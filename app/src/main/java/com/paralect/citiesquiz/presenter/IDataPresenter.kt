package com.paralect.citiesquiz.presenter

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
interface IDataPresenter<out DATA, in P> {

    fun setDataView(view: IDataView<DATA>)

    /**
     * perform a query, use [IDataView.onDataReceived] for notifying that the data is retrieved
     */
    fun requestData(parameter: P)
}
