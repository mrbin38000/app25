package com.example.ticketgenerator.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import kotlin.math.*

class AnimatedTicketBorderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val ANIMATION_DURATION = 1600L
        private const val TOTAL_FRAMES = 45
    }

    private var borderWidth = 6f
    private var cornerRadius = 10f
    private var currentFrame = 0
    private var animator: ValueAnimator? = null
    private val path = Path()
    private val clipPath = Path()

    private val referenceWidth = 823f
    private val referenceHeight = 296f

    private val trapezoidPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    // Coordonnées des trapèzes pour chaque frame (toutes les 46 frames)
    private val frameData = arrayOf(
        // Frame 1
        arrayOf(
            floatArrayOf(368f, 0f, 464f, 0f, 464f, 15f, 368f, 15f),
            floatArrayOf(0f, 204f, 0f, 108f, 15f, 108f, 15f, 204f),
            floatArrayOf(464f, 296f, 368f, 296f, 368f, 281f, 464f, 281f),
            floatArrayOf(823f, 108f, 823f, 204f, 808f, 204f, 808f, 108f)
        ),
        // Frame 2
        arrayOf(
            floatArrayOf(358f, 0f, 454f, 0f, 454f, 15f, 358f, 15f),
            floatArrayOf(0f, 231f, 0f, 135f, 15f, 135f, 15f, 231f),
            floatArrayOf(470f, 296f, 374f, 296f, 374f, 281f, 470f, 281f),
            floatArrayOf(823f, 81f, 823f, 177f, 808f, 177f, 808f, 81f)
        ),
        // Frame 3
        arrayOf(
            floatArrayOf(347f, 0f, 443f, 0f, 445f, 15f, 349f, 15f),
            floatArrayOf(0f, 259f, 0f, 163f, 15f, 163f, 15f, 259f),
            floatArrayOf(476f, 296f, 380f, 296f, 378f, 281f, 474f, 281f),
            floatArrayOf(823f, 53f, 823f, 149f, 808f, 149f, 808f, 53f)
        ),
        // Frame 4
        arrayOf(
            floatArrayOf(336f, 0f, 436f, 0f, 439f, 15f, 339f, 15f),
            floatArrayOf(0f, 286f, 0f, 190f, 15f, 190f, 15f, 286f),
            floatArrayOf(487f, 296f, 384f, 296f, 381f, 281f, 484f, 281f),
            floatArrayOf(823f, 26f, 823f, 122f, 808f, 122f, 808f, 26f)
        ),
        // Frame 5
        arrayOf(
            floatArrayOf(322f, 0f, 421f, 0f, 426f, 15f, 327f, 15f),
            floatArrayOf(90f, 296f, 0f, 217f, 15f, 217f, 106f, 281f),
            floatArrayOf(501f, 296f, 402f, 296f, 397f, 281f, 496f, 281f),
            floatArrayOf(733f, 0f, 823f, 95f, 808f, 95f, 717f, 15f)
        ),
        // Frame 6
        arrayOf(
            floatArrayOf(309f, 0f, 408f, 0f, 415f, 15f, 314f, 15f),
            floatArrayOf(157f, 296f, 0f, 244f, 15f, 244f, 173f, 281f),
            floatArrayOf(514f, 296f, 415f, 296f, 410f, 281f, 509f, 281f),
            floatArrayOf(666f, 0f, 823f, 68f, 808f, 68f, 650f, 15f)
        ),
        // Frame 7
        arrayOf(
            floatArrayOf(299f, 0f, 404f, 0f, 410f, 15f, 305f, 15f),
            floatArrayOf(211f, 296f, 0f, 271f, 15f, 271f, 227f, 281f),
            floatArrayOf(524f, 296f, 419f, 296f, 415f, 281f, 518f, 281f),
            floatArrayOf(612f, 0f, 823f, 41f, 808f, 41f, 596f, 15f)
        ),
        // Frame 8
        arrayOf(
            floatArrayOf(284f, 0f, 392f, 0f, 399f, 15f, 292f, 15f),
            floatArrayOf(243f, 296f, 42f, 296f, 58f, 281f, 259f, 281f),
            floatArrayOf(539f, 296f, 431f, 296f, 424f, 281f, 532f, 281f),
            floatArrayOf(580f, 0f, 781f, 0f, 765f, 15f, 564f, 15f)
        ),
        // Frame 9
        arrayOf(
            floatArrayOf(267f, 0f, 382f, 0f, 389f, 15f, 274f, 15f),
            floatArrayOf(273f, 296f, 94f, 296f, 109f, 281f, 288f, 281f),
            floatArrayOf(556f, 296f, 441f, 296f, 434f, 281f, 549f, 281f),
            floatArrayOf(550f, 0f, 729f, 0f, 714f, 15f, 535f, 15f)
        ),
        // Frame 10
        arrayOf(
            floatArrayOf(255f, 0f, 372f, 0f, 380f, 15f, 263f, 15f),
            floatArrayOf(302f, 296f, 140f, 296f, 155f, 281f, 317f, 281f),
            floatArrayOf(568f, 296f, 451f, 296f, 443f, 281f, 560f, 281f),
            floatArrayOf(521f, 0f, 683f, 0f, 668f, 15f, 506f, 15f)
        ),
        // Frame 11
        arrayOf(
            floatArrayOf(230f, 0f, 356f, 0f, 365f, 15f, 239f, 15f),
            floatArrayOf(317f, 296f, 169f, 296f, 183f, 281f, 331f, 281f),
            floatArrayOf(593f, 296f, 467f, 296f, 458f, 281f, 584f, 281f),
            floatArrayOf(506f, 0f, 654f, 0f, 640f, 15f, 492f, 15f)
        ),
        // Frame 12
        arrayOf(
            floatArrayOf(207f, 0f, 342f, 0f, 352f, 15f, 217f, 15f),
            floatArrayOf(331f, 296f, 195f, 296f, 208f, 281f, 344f, 281f),
            floatArrayOf(616f, 296f, 481f, 296f, 471f, 281f, 606f, 281f),
            floatArrayOf(492f, 0f, 628f, 0f, 615f, 15f, 479f, 15f)
        ),
        // Frame 13
        arrayOf(
            floatArrayOf(181f, 0f, 328f, 0f, 339f, 15f, 192f, 15f),
            floatArrayOf(346f, 296f, 221f, 296f, 231f, 281f, 356f, 281f),
            floatArrayOf(642f, 296f, 495f, 296f, 484f, 281f, 631f, 281f),
            floatArrayOf(477f, 0f, 602f, 0f, 592f, 15f, 467f, 15f)
        ),
        // Frame 14
        arrayOf(
            floatArrayOf(150f, 0f, 308f, 0f, 320f, 15f, 162f, 15f),
            floatArrayOf(360f, 296f, 241f, 296f, 249f, 281f, 368f, 281f),
            floatArrayOf(673f, 296f, 515f, 296f, 503f, 281f, 661f, 281f),
            floatArrayOf(463f, 0f, 582f, 0f, 574f, 15f, 455f, 15f)
        ),
        // Frame 15
        arrayOf(
            floatArrayOf(114f, 0f, 288f, 0f, 303f, 15f, 129f, 15f),
            floatArrayOf(377f, 296f, 263f, 296f, 270f, 281f, 384f, 281f),
            floatArrayOf(709f, 296f, 535f, 296f, 520f, 281f, 694f, 281f),
            floatArrayOf(446f, 0f, 560f, 0f, 553f, 15f, 439f, 15f)
        ),
        // Frame 16
        arrayOf(
            floatArrayOf(63f, 0f, 256f, 0f, 272f, 15f, 79f, 15f),
            floatArrayOf(384f, 296f, 271f, 296f, 277f, 281f, 390f, 281f),
            floatArrayOf(760f, 296f, 567f, 296f, 551f, 281f, 744f, 281f),
            floatArrayOf(439f, 0f, 552f, 0f, 549f, 15f, 433f, 15f)
        ),
        // Frame 17
        arrayOf(
            floatArrayOf(14f, 0f, 220f, 0f, 237f, 15f, 14f, 15f),
            floatArrayOf(390f, 296f, 281f, 296f, 288f, 281f, 395f, 281f),
            floatArrayOf(809f, 296f, 603f, 296f, 586f, 281f, 809f, 281f),
            floatArrayOf(433f, 0f, 540f, 0f, 535f, 15f, 428f, 15f)
        ),
        // Frame 18
        arrayOf(
            floatArrayOf(0f, 38f, 177f, 0f, 193f, 15f, 15f, 38f),
            floatArrayOf(407f, 296f, 304f, 296f, 309f, 281f, 412f, 281f),
            floatArrayOf(823f, 258f, 647f, 296f, 631f, 281f, 808f, 258f),
            floatArrayOf(416f, 0f, 519f, 0f, 514f, 15f, 411f, 15f)
        ),
        // Frame 19
        arrayOf(
            floatArrayOf(0f, 68f, 115f, 0f, 129f, 15f, 15f, 68f),
            floatArrayOf(416f, 296f, 314f, 296f, 318f, 281f, 420f, 281f),
            floatArrayOf(823f, 228f, 710f, 296f, 694f, 281f, 808f, 228f),
            floatArrayOf(407f, 0f, 509f, 0f, 505f, 15f, 403f, 15f)
        ),
        // Frame 20
        arrayOf(
            floatArrayOf(0f, 98f, 34f, 0f, 50f, 15f, 15f, 98f),
            floatArrayOf(424f, 296f, 326f, 296f, 329f, 281f, 427f, 281f),
            floatArrayOf(823f, 198f, 789f, 296f, 773f, 281f, 808f, 198f),
            floatArrayOf(399f, 0f, 497f, 0f, 494f, 15f, 396f, 15f)
        ),
        // Frame 21
        arrayOf(
            floatArrayOf(0f, 128f, 0f, 32f, 15f, 32f, 15f, 128f),
            floatArrayOf(438f, 296f, 339f, 296f, 342f, 281f, 441f, 281f),
            floatArrayOf(823f, 171f, 823f, 267f, 808f, 267f, 808f, 171f),
            floatArrayOf(385f, 0f, 484f, 0f, 481f, 15f, 382f, 15f)
        ),
        // Frame 22
        arrayOf(
            floatArrayOf(0f, 155f, 0f, 59f, 15f, 59f, 15f, 155f),
            floatArrayOf(445f, 296f, 348f, 296f, 350f, 281f, 447f, 281f),
            floatArrayOf(823f, 144f, 823f, 240f, 808f, 240f, 808f, 144f),
            floatArrayOf(378f, 0f, 475f, 0f, 473f, 15f, 376f, 15f)
        ),
        // Frame 23
        arrayOf(
            floatArrayOf(0f, 184f, 0f, 88f, 15f, 88f, 15f, 184f),
            floatArrayOf(455f, 296f, 358f, 296f, 359f, 281f, 456f, 281f),
            floatArrayOf(823f, 117f, 823f, 215f, 808f, 215f, 808f, 117f),
            floatArrayOf(367f, 0f, 465f, 0f, 464f, 15f, 367f, 15f)
        ),
        // Frame 24
        arrayOf(
            floatArrayOf(0f, 214f, 0f, 118f, 15f, 118f, 15f, 214f),
            floatArrayOf(464f, 296f, 368f, 296f, 368f, 281f, 464f, 281f),
            floatArrayOf(823f, 87f, 823f, 183f, 808f, 183f, 808f, 87f),
            floatArrayOf(368f, 0f, 464f, 0f, 464f, 15f, 368f, 15f)
        ),
        // Frame 25
        arrayOf(
            floatArrayOf(0f, 242f, 0f, 146f, 15f, 146f, 15f, 242f),
            floatArrayOf(470f, 296f, 374f, 296f, 374f, 281f, 470f, 281f),
            floatArrayOf(823f, 60f, 823f, 156f, 808f, 156f, 808f, 60f),
            floatArrayOf(358f, 0f, 454f, 0f, 454f, 15f, 358f, 15f)
        ),
        // Frame 26
        arrayOf(
            floatArrayOf(0f, 269f, 0f, 173f, 15f, 173f, 15f, 269f),
            floatArrayOf(476f, 296f, 380f, 296f, 378f, 281f, 474f, 281f),
            floatArrayOf(823f, 31f, 823f, 127f, 808f, 127f, 808f, 31f),
            floatArrayOf(347f, 0f, 443f, 0f, 445f, 15f, 349f, 15f)
        ),
        // Frame 27
        arrayOf(
            floatArrayOf(90f, 296f, 0f, 203f, 15f, 203f, 106f, 281f),
            floatArrayOf(487f, 296f, 384f, 296f, 381f, 281f, 484f, 281f),
            floatArrayOf(733f, 0f, 823f, 97f, 808f, 97f, 717f, 15f),
            floatArrayOf(336f, 0f, 436f, 0f, 439f, 15f, 339f, 15f)
        ),
        // Frame 28
        arrayOf(
            floatArrayOf(157f, 296f, 0f, 233f, 15f, 233f, 173f, 281f),
            floatArrayOf(501f, 296f, 402f, 296f, 397f, 281f, 496f, 281f),
            floatArrayOf(666f, 0f, 823f, 67f, 808f, 67f, 650f, 15f),
            floatArrayOf(322f, 0f, 421f, 0f, 426f, 15f, 327f, 15f)
        ),
        // Frame 29
        arrayOf(
            floatArrayOf(211f, 296f, 0f, 263f, 15f, 263f, 227f, 281f),
            floatArrayOf(514f, 296f, 415f, 296f, 410f, 281f, 509f, 281f),
            floatArrayOf(612f, 0f, 823f, 33f, 808f, 33f, 596f, 15f),
            floatArrayOf(309f, 0f, 408f, 0f, 415f, 15f, 314f, 15f)
        ),
        // Frame 30
        arrayOf(
            floatArrayOf(243f, 296f, 42f, 296f, 58f, 281f, 259f, 281f),
            floatArrayOf(524f, 296f, 419f, 296f, 415f, 281f, 518f, 281f),
            floatArrayOf(580f, 0f, 781f, 0f, 765f, 15f, 564f, 15f),
            floatArrayOf(299f, 0f, 404f, 0f, 410f, 15f, 305f, 15f)
        ),
        // Frame 31
        arrayOf(
            floatArrayOf(273f, 296f, 94f, 296f, 109f, 281f, 288f, 281f),
            floatArrayOf(539f, 296f, 431f, 296f, 424f, 281f, 532f, 281f),
            floatArrayOf(550f, 0f, 729f, 0f, 714f, 15f, 535f, 15f),
            floatArrayOf(284f, 0f, 392f, 0f, 399f, 15f, 292f, 15f)
        ),
        // Frame 32
        arrayOf(
            floatArrayOf(302f, 296f, 140f, 296f, 155f, 281f, 317f, 281f),
            floatArrayOf(556f, 296f, 441f, 296f, 434f, 281f, 549f, 281f),
            floatArrayOf(521f, 0f, 683f, 0f, 668f, 15f, 506f, 15f),
            floatArrayOf(267f, 0f, 382f, 0f, 389f, 15f, 274f, 15f)
        ),
        // Frame 33
        arrayOf(
            floatArrayOf(317f, 296f, 169f, 296f, 183f, 281f, 331f, 281f),
            floatArrayOf(568f, 296f, 451f, 296f, 443f, 281f, 560f, 281f),
            floatArrayOf(506f, 0f, 654f, 0f, 640f, 15f, 492f, 15f),
            floatArrayOf(255f, 0f, 372f, 0f, 380f, 15f, 263f, 15f)
        ),
        // Frame 34
        arrayOf(
            floatArrayOf(331f, 296f, 195f, 296f, 208f, 281f, 344f, 281f),
            floatArrayOf(593f, 296f, 467f, 296f, 458f, 281f, 584f, 281f),
            floatArrayOf(492f, 0f, 628f, 0f, 615f, 15f, 479f, 15f),
            floatArrayOf(230f, 0f, 356f, 0f, 365f, 15f, 239f, 15f)
        ),
        // Frame 35
        arrayOf(
            floatArrayOf(346f, 296f, 221f, 296f, 231f, 281f, 356f, 281f),
            floatArrayOf(616f, 296f, 481f, 296f, 471f, 281f, 606f, 281f),
            floatArrayOf(477f, 0f, 602f, 0f, 592f, 15f, 467f, 15f),
            floatArrayOf(207f, 0f, 342f, 0f, 352f, 15f, 217f, 15f)
        ),
        // Frame 36
        arrayOf(
            floatArrayOf(360f, 296f, 241f, 296f, 249f, 281f, 368f, 281f),
            floatArrayOf(642f, 296f, 495f, 296f, 484f, 281f, 631f, 281f),
            floatArrayOf(463f, 0f, 582f, 0f, 574f, 15f, 455f, 15f),
            floatArrayOf(181f, 0f, 328f, 0f, 339f, 15f, 192f, 15f)
        ),
        // Frame 37
        arrayOf(
            floatArrayOf(377f, 296f, 263f, 296f, 270f, 281f, 384f, 281f),
            floatArrayOf(673f, 296f, 515f, 296f, 503f, 281f, 661f, 281f),
            floatArrayOf(446f, 0f, 560f, 0f, 553f, 15f, 439f, 15f),
            floatArrayOf(150f, 0f, 308f, 0f, 320f, 15f, 162f, 15f)
        ),
        // Frame 38
        arrayOf(
            floatArrayOf(384f, 296f, 271f, 296f, 277f, 281f, 390f, 281f),
            floatArrayOf(709f, 296f, 535f, 296f, 520f, 281f, 694f, 281f),
            floatArrayOf(439f, 0f, 552f, 0f, 549f, 15f, 433f, 15f),
            floatArrayOf(114f, 0f, 288f, 0f, 303f, 15f, 129f, 15f)
        ),
        // Frame 39
        arrayOf(
            floatArrayOf(390f, 296f, 281f, 296f, 288f, 281f, 395f, 281f),
            floatArrayOf(760f, 296f, 567f, 296f, 551f, 281f, 744f, 281f),
            floatArrayOf(433f, 0f, 540f, 0f, 535f, 15f, 428f, 15f),
            floatArrayOf(63f, 0f, 256f, 0f, 272f, 15f, 79f, 15f)
        ),
        // Frame 40
        arrayOf(
            floatArrayOf(407f, 296f, 304f, 296f, 309f, 281f, 412f, 281f),
            floatArrayOf(809f, 269f, 603f, 296f, 586f, 281f, 809f, 269f),
            floatArrayOf(416f, 0f, 519f, 0f, 514f, 15f, 411f, 15f),
            floatArrayOf(14f, 0f, 220f, 0f, 237f, 15f, 14f, 14f)
        ),
        // Frame 41
        arrayOf(
            floatArrayOf(416f, 296f, 314f, 296f, 318f, 281f, 420f, 281f),
            floatArrayOf(823f, 242f, 647f, 296f, 631f, 281f, 808f, 242f),
            floatArrayOf(407f, 0f, 509f, 0f, 505f, 15f, 403f, 15f),
            floatArrayOf(0f, 62f, 177f, 0f, 193f, 15f, 15f, 62f)
        ),
        // Frame 42
        arrayOf(
            floatArrayOf(424f, 296f, 326f, 296f, 329f, 281f, 427f, 281f),
            floatArrayOf(823f, 215f, 710f, 296f, 694f, 281f, 808f, 215f),
            floatArrayOf(399f, 0f, 497f, 0f, 494f, 15f, 396f, 15f),
            floatArrayOf(0f, 91f, 115f, 0f, 129f, 15f, 15f, 91f)
        ),
        // Frame 43
        arrayOf(
            floatArrayOf(438f, 296f, 339f, 296f, 342f, 281f, 441f, 281f),
            floatArrayOf(823f, 188f, 789f, 284f, 773f, 284f, 808f, 188f),
            floatArrayOf(385f, 0f, 484f, 0f, 481f, 15f, 382f, 15f),
            floatArrayOf(0f, 120f, 34f, 24f, 50f, 24f, 15f, 120f)
        ),
        // Frame 44
        arrayOf(
            floatArrayOf(445f, 296f, 348f, 296f, 350f, 281f, 447f, 281f),
            floatArrayOf(823f, 161f, 823f, 257f, 808f, 257f, 808f, 161f),
            floatArrayOf(378f, 0f, 475f, 0f, 473f, 15f, 376f, 15f),
            floatArrayOf(0f, 149f, 0f, 53f, 15f, 53f, 15f, 149f)
        ),
        // Frame 45
        arrayOf(
            floatArrayOf(455f, 296f, 358f, 296f, 359f, 281f, 456f, 281f),
            floatArrayOf(823f, 134f, 823f, 230f, 808f, 230f, 808f, 134f),
            floatArrayOf(367f, 0f, 465f, 0f, 464f, 15f, 367f, 15f),
            floatArrayOf(0f, 178f, 0f, 82f, 15f, 82f, 15f, 178f)
        )
    )


    init {
        val density = context.resources.displayMetrics.density
        borderWidth *= density
        cornerRadius *= density
        setWillNotDraw(false)
        startAnimation()
    }

    private fun startAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofInt(0, TOTAL_FRAMES - 1).apply {
            duration = ANIMATION_DURATION
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener {
                currentFrame = it.animatedValue as Int
                invalidate()
            }
        }
        animator?.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        clipPath.reset()
        clipPath.addRoundRect(
            0f, 0f, w.toFloat(), h.toFloat(),
            cornerRadius, cornerRadius,
            Path.Direction.CW
        )
    }

    override fun dispatchDraw(canvas: Canvas) {
        val saveCount = canvas.save()
        canvas.clipPath(clipPath)
        drawAnimatedTrapezoids(canvas)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(saveCount)
    }

    private fun drawAnimatedTrapezoids(canvas: Canvas) {
        if (currentFrame >= frameData.size || frameData[currentFrame].size < 4) return

        val currentFrameData = frameData[currentFrame]
        val scaleX = width / referenceWidth
        val scaleY = height / referenceHeight

        for (trapezoid in currentFrameData) {
            if (trapezoid.size >= 8) {
                val scaledCoords = FloatArray(8).also { arr ->
                    for (i in arr.indices step 2) {
                        arr[i]     = trapezoid[i]     * scaleX
                        arr[i + 1] = trapezoid[i + 1] * scaleY
                    }
                }

                val x1 = scaledCoords[0]; val y1 = scaledCoords[1]
                val x2 = scaledCoords[2]; val y2 = scaledCoords[3]

                val isHorizontalEdge = abs(y1 - y2) < 5f
                val isVerticalEdge   = abs(x1 - x2) < 5f

                if (isHorizontalEdge || isVerticalEdge) {
                    drawSimpleTrapezoid(canvas, scaledCoords)
                } else {
                    drawBorderFill(canvas, scaledCoords)
                }

                // Triangles pour trapèze 1 aux frames 18-20 (index 17..19)
                if (currentFrame in 17..19 && trapezoid === currentFrameData[0]) {
                    val tx1 = scaledCoords[0]; val ty1 = scaledCoords[1]
                    val tx2 = scaledCoords[2]; val ty2 = scaledCoords[3]
                    val tx3 = scaledCoords[4]; val ty3 = scaledCoords[5]
                    val tx4 = scaledCoords[6]; val ty4 = scaledCoords[7]
                    val apexX = 15f * scaleX;     val apexY = 15f * scaleY

                    val triangle1 = Path().apply {
                        moveTo(tx2, ty2)
                        lineTo(tx3, ty3)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle1, trapezoidPaint)

                    val triangle2 = Path().apply {
                        moveTo(tx1, ty1)
                        lineTo(tx4, ty4)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle2, trapezoidPaint)
                }

                // Triangles pour trapèze 4 aux frames 40-42 (index 39..41)
                if (currentFrame in 39..41 && trapezoid === currentFrameData[3]) {
                    val tx1 = scaledCoords[0]; val ty1 = scaledCoords[1]
                    val tx2 = scaledCoords[2]; val ty2 = scaledCoords[3]
                    val tx3 = scaledCoords[4]; val ty3 = scaledCoords[5]
                    val tx4 = scaledCoords[6]; val ty4 = scaledCoords[7]
                    val apexX = 15f * scaleX;     val apexY = 15f * scaleY

                    val triangle3 = Path().apply {
                        moveTo(tx2, ty2)
                        lineTo(tx3, ty3)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle3, trapezoidPaint)

                    val triangle4 = Path().apply {
                        moveTo(tx1, ty1)
                        lineTo(tx4, ty4)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle4, trapezoidPaint)
                }

                // Triangles pour trapèze 1 aux frames 27-29 (index 26..28) - coin inférieur gauche
                if (currentFrame in 26..28 && trapezoid === currentFrameData[0]) {
                    val tx1 = scaledCoords[0]; val ty1 = scaledCoords[1]
                    val tx2 = scaledCoords[2]; val ty2 = scaledCoords[3]
                    val tx3 = scaledCoords[4]; val ty3 = scaledCoords[5]
                    val tx4 = scaledCoords[6]; val ty4 = scaledCoords[7]
                    val apexX = 15f * scaleX;     val apexY = 281f * scaleY

                    val triangle5 = Path().apply {
                        moveTo(tx2, ty2)
                        lineTo(tx3, ty3)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle5, trapezoidPaint)

                    val triangle6 = Path().apply {
                        moveTo(tx1, ty1)
                        lineTo(tx4, ty4)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle6, trapezoidPaint)
                }

                // Triangles pour trapèze 2 aux frames 5-7 (index 4..6) - coin inférieur gauche
                if (currentFrame in 4..6 && trapezoid === currentFrameData[1]) {
                    val tx1 = scaledCoords[0]; val ty1 = scaledCoords[1]
                    val tx2 = scaledCoords[2]; val ty2 = scaledCoords[3]
                    val tx3 = scaledCoords[4]; val ty3 = scaledCoords[5]
                    val tx4 = scaledCoords[6]; val ty4 = scaledCoords[7]
                    val apexX = 15f * scaleX;     val apexY = 281f * scaleY

                    val triangle7 = Path().apply {
                        moveTo(tx2, ty2)
                        lineTo(tx3, ty3)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle7, trapezoidPaint)

                    val triangle8 = Path().apply {
                        moveTo(tx1, ty1)
                        lineTo(tx4, ty4)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle8, trapezoidPaint)
                }

                // Triangles pour trapèze 3 aux frames 18-20 (index 17..19) - coin inférieur droite
                if (currentFrame in 17..19 && trapezoid === currentFrameData[2]) {
                    val tx1 = scaledCoords[0]; val ty1 = scaledCoords[1]
                    val tx2 = scaledCoords[2]; val ty2 = scaledCoords[3]
                    val tx3 = scaledCoords[4]; val ty3 = scaledCoords[5]
                    val tx4 = scaledCoords[6]; val ty4 = scaledCoords[7]
                    val apexX = 808f * scaleX;     val apexY = 281f * scaleY

                    val triangle9 = Path().apply {
                        moveTo(tx2, ty2)
                        lineTo(tx3, ty3)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle9, trapezoidPaint)

                    val triangle10 = Path().apply {
                        moveTo(tx1, ty1)
                        lineTo(tx4, ty4)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle10, trapezoidPaint)
                }

                // Triangles pour trapèze 2 aux frames 40-42 (index 39..41) - coin inférieur droite
                if (currentFrame in 39..41 && trapezoid === currentFrameData[1]) {
                    val tx1 = scaledCoords[0]; val ty1 = scaledCoords[1]
                    val tx2 = scaledCoords[2]; val ty2 = scaledCoords[3]
                    val tx3 = scaledCoords[4]; val ty3 = scaledCoords[5]
                    val tx4 = scaledCoords[6]; val ty4 = scaledCoords[7]
                    val apexX = 808f * scaleX;     val apexY = 281f * scaleY

                    val triangle11 = Path().apply {
                        moveTo(tx2, ty2)
                        lineTo(tx3, ty3)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle11, trapezoidPaint)

                    val triangle12 = Path().apply {
                        moveTo(tx1, ty1)
                        lineTo(tx4, ty4)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle12, trapezoidPaint)
                }

                // Triangles pour trapèze 4 aux frames 5-7 (index 4..6) - coin supérieur droite
                if (currentFrame in 4..6 && trapezoid === currentFrameData[3]) {
                    val tx1 = scaledCoords[0]; val ty1 = scaledCoords[1]
                    val tx2 = scaledCoords[2]; val ty2 = scaledCoords[3]
                    val tx3 = scaledCoords[4]; val ty3 = scaledCoords[5]
                    val tx4 = scaledCoords[6]; val ty4 = scaledCoords[7]
                    val apexX = 808f * scaleX;     val apexY = 15f * scaleY

                    val triangle13 = Path().apply {
                        moveTo(tx2, ty2)
                        lineTo(tx3, ty3)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle13, trapezoidPaint)

                    val triangle14 = Path().apply {
                        moveTo(tx1, ty1)
                        lineTo(tx4, ty4)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle14, trapezoidPaint)
                }

                // Triangles pour trapèze 3 aux frames 27-29 (index 26..28) - coin supérieur droite
                if (currentFrame in 26..28 && trapezoid === currentFrameData[2]) {
                    val tx1 = scaledCoords[0]; val ty1 = scaledCoords[1]
                    val tx2 = scaledCoords[2]; val ty2 = scaledCoords[3]
                    val tx3 = scaledCoords[4]; val ty3 = scaledCoords[5]
                    val tx4 = scaledCoords[6]; val ty4 = scaledCoords[7]
                    val apexX = 808f * scaleX;     val apexY = 15f * scaleY

                    val triangle15 = Path().apply {
                        moveTo(tx2, ty2)
                        lineTo(tx3, ty3)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle15, trapezoidPaint)

                    val triangle16 = Path().apply {
                        moveTo(tx1, ty1)
                        lineTo(tx4, ty4)
                        lineTo(apexX, apexY)
                        close()
                    }
                    canvas.drawPath(triangle16, trapezoidPaint)
                }

                // Triangles pour trapèze 2 à la frame 42 (index 41) - coin inférieur droite
                if (currentFrame == 42 && trapezoid === currentFrameData[1]) {
                    val tx1 = scaledCoords[0]; val ty1 = scaledCoords[1]
                    val tx2 = scaledCoords[2]; val ty2 = scaledCoords[3]
                    val tx3 = scaledCoords[4]; val ty3 = scaledCoords[5]
                    val tx4 = scaledCoords[6]; val ty4 = scaledCoords[7]

                    // Point de référence pour le coin inférieur droit
                    val cornerX = 808f * scaleX
                    val cornerY = 281f * scaleY

                    // Triangle entre point 2, point 3 et (808,281)
                    val triangle17 = Path().apply {
                        moveTo(tx2, ty2)
                        lineTo(tx3, ty3)
                        lineTo(cornerX, cornerY)
                        close()
                    }
                    canvas.drawPath(triangle17, trapezoidPaint)

                    // Triangle entre point 1, point 4 et (808,281)
                    val triangle18 = Path().apply {
                        moveTo(tx1, ty1)
                        lineTo(tx4, ty4)
                        lineTo(cornerX, cornerY)
                        close()
                    }
                    canvas.drawPath(triangle18, trapezoidPaint)
                }

                // Triangle pour trapèze 4 à la frame 42 (index 41) - coin supérieur gauche
                if (currentFrame == 42 && trapezoid === currentFrameData[3]) {
                    // Triangle noir entre les points (0,91), (15,91) et (15,15)
                    val point1X = 0f * scaleX
                    val point1Y = 91f * scaleY
                    val point2X = 15f * scaleX
                    val point2Y = 91f * scaleY
                    val point3X = 15f * scaleX
                    val point3Y = 15f * scaleY

                    val triangle19 = Path().apply {
                        moveTo(point1X, point1Y)
                        lineTo(point2X, point2Y)
                        lineTo(point3X, point3Y)
                        close()
                    }
                    canvas.drawPath(triangle19, trapezoidPaint)
                }
            }
        }
    }

    private fun drawSimpleTrapezoid(canvas: Canvas, coords: FloatArray) {
        path.reset()
        path.moveTo(coords[0], coords[1])
        path.lineTo(coords[2], coords[3])
        path.lineTo(coords[4], coords[5])
        path.lineTo(coords[6], coords[7])
        path.close()
        canvas.drawPath(path, trapezoidPaint)
    }

    private fun drawBorderFill(canvas: Canvas, coords: FloatArray) {
        val fillPath = Path()
        val pts = listOf(
            coords[0] to coords[1],
            coords[2] to coords[3],
            coords[4] to coords[5],
            coords[6] to coords[7]
        )
        val avgX = pts.map { it.first }.average().toFloat()
        val avgY = pts.map { it.second }.average().toFloat()
        val corner = when {
            avgX < width/2f && avgY < height/2f -> Corner.TOP_LEFT
            avgX > width/2f && avgY < height/2f -> Corner.TOP_RIGHT
            avgX < width/2f && avgY > height/2f -> Corner.BOTTOM_LEFT
            else                                -> Corner.BOTTOM_RIGHT
        }
        val (targetX, targetY) = when (corner) {
            Corner.TOP_LEFT     -> 0f to 0f
            Corner.TOP_RIGHT    -> width.toFloat() to 0f
            Corner.BOTTOM_LEFT  -> 0f to height.toFloat()
            Corner.BOTTOM_RIGHT -> width.toFloat() to height.toFloat()
        }
        var start = pts.minByOrNull { abs(it.first - targetX) }!!
        var end   = pts.minByOrNull { abs(it.second - targetY) }!!
        if (corner == Corner.TOP_RIGHT || corner == Corner.BOTTOM_LEFT) {
            val tmp = start; start = end; end = tmp
        }
        val outer = when (corner) {
            Corner.TOP_LEFT     -> RectF(0f, 0f, 2*cornerRadius, 2*cornerRadius)
            Corner.TOP_RIGHT    -> RectF(width-2*cornerRadius, 0f, width.toFloat(), 2*cornerRadius)
            Corner.BOTTOM_LEFT  -> RectF(0f, height-2*cornerRadius, 2*cornerRadius, height.toFloat())
            Corner.BOTTOM_RIGHT -> RectF(width-2*cornerRadius, height-2*cornerRadius, width.toFloat(), height.toFloat())
        }
        val inner = RectF(
            outer.left   + borderWidth,
            outer.top    + borderWidth,
            outer.right  - borderWidth,
            outer.bottom - borderWidth
        )
        fillPath.moveTo(start.first, start.second)
        fillPath.lineTo(targetX, targetY)
        val (startAngle, sweep) = when (corner) {
            Corner.TOP_LEFT     -> 180f to  90f
            Corner.TOP_RIGHT    -> 270f to  90f
            Corner.BOTTOM_RIGHT ->   0f to  90f
            Corner.BOTTOM_LEFT  ->  90f to  90f
        }
        fillPath.arcTo(outer, startAngle, sweep, false)
        fillPath.lineTo(end.first, end.second)
        fillPath.arcTo(inner, startAngle + sweep, -sweep, false)
        fillPath.close()
        canvas.drawPath(fillPath, trapezoidPaint)
    }

    private enum class Corner {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (animator?.isRunning != true) startAnimation()
    }

    fun pauseAnimation() {
        animator?.pause()
    }

    fun resumeAnimation() {
        animator?.resume()
    }

    fun setAnimationSpeed(speedMultiplier: Float) {
        val progress = animator?.animatedFraction ?: 0f
        animator?.cancel()
        animator = ValueAnimator.ofInt(0, TOTAL_FRAMES - 1).apply {
            duration = (ANIMATION_DURATION / speedMultiplier).toLong()
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener {
                currentFrame = it.animatedValue as Int
                invalidate()
            }
        }
        animator?.start()
    }

    fun setTrapezoidColor(color: Int) {
        trapezoidPaint.color = color
        invalidate()
    }
}