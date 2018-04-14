package com.paralect.citiesquiz.utils

import android.view.View
import java.util.*

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */

fun <E> List<E>.random(): E? = if (size > 0) get(Random().nextInt(size)) else null

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.setVisibile(visible: Boolean) {
    if (visible)
        this.visible()
    else
        this.gone()
}