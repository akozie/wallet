package com.woleapp.netpos.qrgenerator.utils

import android.os.CountDownTimer

class MyCountDownTimer(totalTimeInMillis: Long, intervalInMillis: Long) :
    CountDownTimer(totalTimeInMillis, intervalInMillis) {

    override fun onTick(millisUntilFinished: Long) {
        // This method will be called regularly during the countdown
        // Here you can update your UI with the remaining time
        val secondsRemaining = millisUntilFinished / 1000
        // Update UI with secondsRemaining
    }

    override fun onFinish() {
        // This method will be called when the countdown is finished
        // Here you can perform any actions that should happen when the timer is finished
        // For example, display a message, start another task, etc.
    }
}