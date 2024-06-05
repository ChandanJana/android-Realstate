package com.realestate.utils

import android.opengl.ETC1.getHeight
import android.view.animation.Animation
import androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer.measure


/**
 * Created by Chandan on 18/3/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
class ExpandCollapse {
    /*companion object{
        fun expand() {
            val initialHeight = getHeight()
            measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            val targetHeight: Int = getMeasuredHeight()
            val distanceToExpand = targetHeight - initialHeight
            val a: Animation = object : Animation() {
                override fun applyTransformation(
                    interpolatedTime: Float,
                    t: Transformation?
                ) {
                    if (interpolatedTime == 1f) {
                        // Do this after expanded
                    }
                    getLayoutParams().height =
                        (initialHeight + distanceToExpand * interpolatedTime).toInt()
                    requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }
            a.duration = distanceToExpand.toLong()
            startAnimation(a)
        }

        fun collapse(collapsedHeight: Int) {
            val initialHeight: Int = getMeasuredHeight()
            val distanceToCollapse = (initialHeight - collapsedHeight)
            val a: Animation = object : Animation() {
                override fun applyTransformation(
                    interpolatedTime: Float,
                    t: Transformation?
                ) {
                    if (interpolatedTime == 1f) {
                        // Do this after collapsed
                    }
                    Log.i(TAG, "Collapse | InterpolatedTime = $interpolatedTime")
                    getLayoutParams().height =
                        (initialHeight - distanceToCollapse * interpolatedTime).toInt()
                    requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }
            a.duration = distanceToCollapse.toLong()
            startAnimation(a)
        }
    }*/
}