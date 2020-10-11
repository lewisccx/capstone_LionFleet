package com.sit.capstone_lionfleet.utils

import android.app.Activity
import android.content.Intent
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.function.BinaryOperator

fun<A: Activity> Activity.startNewActivity(activity: Class<A>){
    Intent(this, activity).also{
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}
fun isFetchNeeded(saveTimeStamp: LocalDateTime, apiModuleLimit: Int): Boolean{

    return ChronoUnit.MINUTES.between(saveTimeStamp,LocalDateTime.now()) > apiModuleLimit
}

