package rd.zhang.aio.kotlin.core.function

import android.animation.ObjectAnimator
import android.support.v4.view.ViewCompat
import android.view.View

/**
 * Created by Richard on 2017/9/5.
 */
fun <T : View> T.alphaAnimate(value: Float) {
    var anim = ObjectAnimator.ofFloat(this, "alpha", this.alpha, value)
    anim.duration = 150
    anim.start()
}

fun <T : View> T.scaleAnimate(value: Float) {
    ViewCompat.animate(this)
            .scaleX(value)
            .scaleY(value)
            .setDuration(150)
            .start()
}

fun <T : View> T.rotateAnimate(value: Float) {
    var anim = ObjectAnimator.ofFloat(this, "rotation", this.rotation, value)
    anim.duration = 150
    anim.start()
}