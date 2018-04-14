package com.paralect.citiesquiz.utils

import java.util.*

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */

fun <E> List<E>.random(): E? = if (size > 0) get(Random().nextInt(size)) else null