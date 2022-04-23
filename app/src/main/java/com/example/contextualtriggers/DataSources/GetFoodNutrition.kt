package com.example.contextualtriggers.DataSources

import java.util.*


fun getFoodNutrition(cal: Double): Any? {
    val foodMap = TreeMap<Double, String>()
    foodMap[285.0] = "Pizza"
    foodMap[540.0] = "Burger"
    foodMap[275.0] = "Icecream"
    foodMap[200.0] = "Pasta"
    foodMap[270.0] = "Cake"
    foodMap[40.0] = "Chocolate"
    foodMap[300.0] = "Pie"
    foodMap[600.0] = "Lasagna"
    val low: MutableMap.MutableEntry<Double, String>? = foodMap.floorEntry(cal)
    val high: MutableMap.MutableEntry<Double, String>? = foodMap.ceilingEntry(cal)
    var res: Any? = null
    if (low != null && high != null) {
        res = if (Math.abs(cal - low.key) < Math.abs(cal - high.key)) low.value else high.value
    } else if (low != null || high != null) {
        res = low?.value ?: high!!.value
    }
    return res
}