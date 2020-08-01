package com.example.articleslist.presentation.helpers

import android.animation.Animator
import android.content.Context
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import com.example.articleslist.R


object AnimUtils {
    private var fastOutSlowIn: Interpolator? = null
    private var fastOutLinearIn: Interpolator? = null
    private var linearOutSlowIn: Interpolator? = null
    private var linear: Interpolator? = null

    fun getFastOutSlowInInterpolator(context: Context?): Interpolator? {
        if (fastOutSlowIn == null) {
            fastOutSlowIn = AnimationUtils.loadInterpolator(
                context, R.anim.fast_out_slow_in
            )
        }
        return fastOutSlowIn
    }


    private class AnimatorListenerWrapper internal constructor(
        private val mAnimator: Animator,
        private val mListener: Animator.AnimatorListener
    ) :
        Animator.AnimatorListener {
        override fun onAnimationStart(animator: Animator) {
            mListener.onAnimationStart(mAnimator)
        }

        override fun onAnimationEnd(animator: Animator) {
            mListener.onAnimationEnd(mAnimator)
        }

        override fun onAnimationCancel(animator: Animator) {
            mListener.onAnimationCancel(mAnimator)
        }

        override fun onAnimationRepeat(animator: Animator) {
            mListener.onAnimationRepeat(mAnimator)
        }

    }
}
