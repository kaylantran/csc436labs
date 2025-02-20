package com.zybooks.pizzaparty.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.zybooks.pizzaparty.calculateNumPizzas

class PizzaPartyViewModel : ViewModel() {
    var totalPizzas by mutableStateOf(0)
        private set

    var numPeopleInput by mutableStateOf("")

    var hungerLevel by mutableStateOf("Medium")

    fun calculatePizzas() {
        totalPizzas = calculateNumPizzas(numPeopleInput.toIntOrNull() ?: 0, hungerLevel)
    }
}