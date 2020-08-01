package com.example.articleslist.presentation.helpers

import android.app.Activity
import android.graphics.Color
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.example.articleslist.utils.isKeyboardAppeared


typealias OnSystemInsetsChangedListener = (statusBarSize: Int, navigationBarSize: Int) -> Unit

fun Activity.applyInsets(listener: OnSystemInsetsChangedListener = { _, _ -> }) = with(window) {
    removeSystemInsets(
        decorView,
        listener
    )
    navigationBarColor = Color.TRANSPARENT
    statusBarColor = Color.TRANSPARENT
}

fun Fragment.applyInsets(listener: OnSystemInsetsChangedListener = { _, _ -> }) = activity?.let {
    with (it.window) {
        removeSystemInsets(decorView, listener)
        navigationBarColor = Color.TRANSPARENT
        statusBarColor = Color.TRANSPARENT
    }
}

private fun removeSystemInsets(view: View, listener: OnSystemInsetsChangedListener) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->

        val desiredBottomInset =
            calculateDesiredBottomInset(
                view, insets.systemWindowInsetTop,
                insets.systemWindowInsetBottom, listener
            )

        ViewCompat.onApplyWindowInsets(
            view, insets.replaceSystemWindowInsets(0, 0, 0, desiredBottomInset)
        )
    }
}

private fun calculateDesiredBottomInset(
    view: View, topInset: Int, bottomInset: Int,
    listener: OnSystemInsetsChangedListener
): Int {
    val hasKeyboard = isKeyboardAppeared(view, bottomInset)
    val desiredBottomInset = if (hasKeyboard) bottomInset else 0
    listener(topInset, if (hasKeyboard) 0 else bottomInset)
    return desiredBottomInset
}
