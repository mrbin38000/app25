package com.example.ticketgenerator.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout

class AnimatedTicketBorderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val ANIMATION_DURATION = 1200L
        private const val DASH_COUNT = 4
        private const val SEGMENT_COUNT = 12  // Nombre de segments pour les trapèzes courbés
    }

    private var borderWidth = 16f
    private var dashStrokeWidth = 17f
    private var cornerRadius = 18f
    private var dashLength = 130f

    private val rectF = RectF()
    private val path = Path()
    private var dashOffset = 0f
    private var pathLength = 0f
    private var gapLength = 0f
    private var pathMeasure = PathMeasure()

    // Liste pour stocker les positions des trapèzes
    private val trapezoidPositions = mutableListOf<TrapezoidData>()

    // Peinture de fond grise claire
    private val backgroundBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#e7e7e7")
        strokeWidth = borderWidth
    }

    // Peinture des trapèzes animés
    private val trapezoidPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = ANIMATION_DURATION
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            val progress = it.animatedValue as Float
            dashOffset = progress * (dashLength + gapLength)
            invalidate()
        }
    }

    // Classe pour stocker les données d'un trapèze
    private data class TrapezoidData(
        val position: Float,
        val path: Path = Path()
    )

    init {
        setBackgroundColor(Color.TRANSPARENT)
        clipChildren = false
        setWillNotDraw(false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val padding = dashStrokeWidth / 2
        rectF.set(padding, padding, w - padding, h - padding)

        path.reset()
        path.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW)

        pathMeasure = PathMeasure(path, true)
        pathLength = pathMeasure.length
        gapLength = (pathLength / DASH_COUNT) - dashLength

        if (gapLength < dashLength) {
            dashLength = pathLength / (DASH_COUNT * 2)
            gapLength = dashLength
        }

        if (!animator.isRunning) animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Dessiner la bordure grise de fond
        canvas.drawPath(path, backgroundBorderPaint)

        // Dessiner les trapèzes à leurs positions calculées
        drawTrapezoidsAlongPath(canvas)
    }

    private fun drawTrapezoidsAlongPath(canvas: Canvas) {
        trapezoidPositions.clear()
        
        // Calculer la position de départ avec l'offset d'animation
        var distanceAlongPath = dashOffset % (dashLength + gapLength)
        
        // Ajouter des trapèzes tout au long du chemin
        while (distanceAlongPath < pathLength) {
            trapezoidPositions.add(TrapezoidData(distanceAlongPath))
            distanceAlongPath += dashLength + gapLength
        }
        
        // Dessiner chaque trapèze en suivant la courbure du chemin
        for (trapezoid in trapezoidPositions) {
            // Dessiner un trapèze qui suit la courbe du chemin
            drawCurvedTrapezoid(canvas, trapezoid.position)
        }
    }
    
    private fun drawCurvedTrapezoid(canvas: Canvas, startDistance: Float) {
        // Créer un nouveau chemin pour le trapèze courbé
        val trapezoidPath = Path()
        
        // Nombre de segments pour approximer la courbe
        val segments = SEGMENT_COUNT
        val segmentLength = dashLength / segments
        
        // Liste pour stocker temporairement les points
        val topPoints = mutableListOf<Pair<Float, Float>>()
        val bottomPoints = mutableListOf<Pair<Float, Float>>()
        
        // Points pour la partie supérieure du trapèze
        for (i in 0..segments) {
            // Gérer le cas où la distance dépasse la longueur du chemin (wraparound)
            var distance = startDistance + i * segmentLength
            while (distance >= pathLength) {
                distance -= pathLength
            }
            
            val pos = FloatArray(2)
            val tan = FloatArray(2)
            pathMeasure.getPosTan(distance, pos, tan)
            
            // Calculer l'offset perpendiculaire pour la largeur du trapèze
            val perpX = -tan[1] * (dashStrokeWidth / 2)
            val perpY = tan[0] * (dashStrokeWidth / 2)
            
            // Ajuster le facteur de décalage pour créer une forme quasi-rectangulaire
            // Très légère inclinaison (0.1f au lieu de 0.4f)
            val skewFactor = if (i == 0) 0.12f else if (i == segments) 0.12f else 0f
            val skewX = tan[0] * dashStrokeWidth * skewFactor
            val skewY = tan[1] * dashStrokeWidth * skewFactor
            
            topPoints.add(Pair(pos[0] + perpX + skewX, pos[1] + perpY + skewY))
        }
        
        // Points pour la partie inférieure du trapèze (en sens inverse)
        for (i in segments downTo 0) {
            // Gérer le cas où la distance dépasse la longueur du chemin (wraparound)
            var distance = startDistance + i * segmentLength
            while (distance >= pathLength) {
                distance -= pathLength
            }
            
            val pos = FloatArray(2)
            val tan = FloatArray(2)
            pathMeasure.getPosTan(distance, pos, tan)
            
            // Calculer l'offset perpendiculaire pour l'autre côté du trapèze
            val perpX = tan[1] * (dashStrokeWidth / 2)
            val perpY = -tan[0] * (dashStrokeWidth / 2)
            
            // Ajuster pour une forme quasi-rectangulaire avec une très légère inclinaison
            // Réduction considérable des facteurs d'inclinaison
            val skewFactor = if (i == 0) -0.3f else if (i == segments) -0.1f else -0.15f
            val skewX = tan[0] * dashStrokeWidth * skewFactor * 0.1f
            val skewY = tan[1] * dashStrokeWidth * skewFactor * 0.1f
            
            bottomPoints.add(Pair(pos[0] + perpX + skewX, pos[1] + perpY + skewY))
        }
        
        // Construire le chemin avec tous les points calculés
        // La partie supérieure
        if (topPoints.isNotEmpty()) {
            trapezoidPath.moveTo(topPoints.first().first, topPoints.first().second)
            for (i in 1 until topPoints.size) {
                trapezoidPath.lineTo(topPoints[i].first, topPoints[i].second)
            }
        }
        
        // La partie inférieure
        for (point in bottomPoints) {
            trapezoidPath.lineTo(point.first, point.second)
        }
        
        trapezoidPath.close()
        canvas.drawPath(trapezoidPath, trapezoidPaint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        stopAnimation()
        super.onDetachedFromWindow()
    }

    fun startAnimation() {
        if (!animator.isRunning) animator.start()
    }

    fun stopAnimation() {
        if (animator.isRunning) animator.cancel()
    }
}