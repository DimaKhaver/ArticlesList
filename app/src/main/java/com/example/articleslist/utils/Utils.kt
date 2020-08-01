package com.example.articleslist.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.articleslist.R
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.koin.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import java.lang.System.currentTimeMillis


inline fun <reified VM : ViewModel> Fragment.sharedGraphViewModel(
    @IdRes navGraphId: Int,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = lazy {
    val owner = findNavController().getViewModelStoreOwner(navGraphId)
    getKoin().getViewModel(owner, VM::class, qualifier, parameters)
}

fun displaySnackbar(view: View, message: CharSequence) = Snackbar.make(view, message, Snackbar.LENGTH_LONG)

fun Snackbar.withColor(
    @ColorInt backgroundColor: Int? = null,
    @ColorInt textColor: Int = context.getColor(R.color.colorWhite),
    backgroundDrawable: Drawable? = context.getDrawable(R.drawable.snackbar_background)): Snackbar
{
    with (this.view) {
        backgroundColor?.let { setBackgroundColor(it) }
        backgroundDrawable?.let { background = backgroundDrawable }
        findViewById<TextView>(R.id.snackbar_text).setTextColor(textColor)
    }
    return this
}

fun <T> MutableLiveData<T>.forceRefresh() {
    this.value = this.value
}

fun Fragment.hideKeyboard() = view?.let { activity?.hideKeyboard(it) }

fun Activity.hideKeyboard() = hideKeyboard(currentFocus ?: View(this))

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun isKeyboardAppeared(view: View, bottomInset: Int) =
    bottomInset / view.resources.displayMetrics.heightPixels.toDouble() > .25

fun View.onSingleClick(onClick: () -> Unit) {
    var lastClickTime = 0L
    setOnClickListener {
        if (currentTimeMillis() > lastClickTime + 750) onClick()
        lastClickTime = currentTimeMillis()
    }
}

internal inline fun <T> T.safeRun(TAG: String = "", block: T.() -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        //ignore but log it
        Log.e(TAG, e.toString())
    }
}

fun Context?.getConnectivityServiceManager() = this?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
