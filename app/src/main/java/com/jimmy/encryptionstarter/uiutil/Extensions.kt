
package com.jimmy.encryptionstarter.uiutil

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import kotlin.reflect.KClass

fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(color) }
}


fun Context.showToast( message: String, length: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, message, length).show()
}

fun @receiver:StringRes Int.errorDialog(activity: Activity) {
    AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert)
        .setTitle("Error")
        .setMessage(this@errorDialog)
        .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
        .setIcon(android.R.drawable.ic_dialog_alert).show()
}


fun <T : Activity> KClass<T>.start(activity: Activity, extras : Bundle = Bundle(), finish: Boolean = false) {
    Intent(activity, this.java).apply {
        putExtras(extras)
        activity.startActivity(this)
    }
    if (finish) {
        activity.finish()
    }
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}