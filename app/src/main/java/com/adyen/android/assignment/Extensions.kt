package com.adyen.android.assignment

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}

fun Fragment.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context!!, msg, duration).show()
}

fun Activity.log(msg: String, tag: String = "ffnet") {
    Log.d(tag, msg)
}

fun ContextWrapper.log(msg: String, tag: String = "ffnet") {
    Log.d(tag, msg)
}

fun Fragment.log(msg: String, tag: String = "ffnet") {
    Log.d(tag, msg)
}

fun log(msg: String, tag: String = "ffnet") {
    Log.d(tag, msg)
}

fun CoroutineScope.log(msg: String, tag: String = "ffnet") {
    Log.d(tag, msg)
}