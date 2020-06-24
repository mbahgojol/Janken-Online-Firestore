package com.blank.firestorefirebase.ui.battle

import android.util.Log

class Controller(private val navigatorBattle: NavigatorBattle) {
    private val suit = mutableListOf("gunting", "batu", "kertas")
    private val TAG = Controller::class.java.simpleName

    fun checkPemenang(player1: String, player2: String) {
        if (player1 == suit[1] && player2 == suit[0] ||
            player1 == suit[0] && player2 == suit[2] ||
            player1 == suit[2] && player2 == suit[1]
        ) {
            navigatorBattle.onResult("You Win!")
            Log.d(TAG, "Pemain 1 MENANG!")
        } else if (
            player1 == suit[1] && player2 == suit[2] ||
            player1 == suit[0] && player2 == suit[1] ||
            player1 == suit[2] && player2 == suit[0]
        ) {
            navigatorBattle.onResult("You Lose!")
            Log.d(TAG, "Pemain 2 MENANG!")
        } else {
            navigatorBattle.onResult("DRAW")
            Log.d(TAG, "DRAW")
        }
    }
}