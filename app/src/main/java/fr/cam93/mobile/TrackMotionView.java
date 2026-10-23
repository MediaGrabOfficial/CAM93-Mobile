package fr.cam93.mobile;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Quiet, animated athletics-track artwork for the club hero.
 * The motion is deliberately restrained: no mascot, neon, or bouncing sticker.
 */
public final class TrackMotionView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF oval = new RectF();
    private final Path track = new Path();
    private final Path segment = new Path();
    private ValueAnimator animator;
    private float phase;
    private final float density;

    private static final int NAVY = Color.rgb(6, 30, 61);
    private static final int DEEP_BLUE = Color.rgb(11, 48, 86);
    private static final int GOLD = Color.rgb(211, 177, 97);
    private static final int PALE_GOLD = Color.rgb(240, 218, 164);

    public TrackMotionView(Context context) {
        super(context);
        density = getResources().getDisplayMetrics().density;
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        if (w <= 0 || h <= 0) return;

        // A muted pool of stadium light in the right-hand side of the composition.
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(
                w * 0.82f, h * 0.39f, w * 0.82f,
                new int[]{0xFF163B61, 0xFF0D2A4B, NAVY},
                new float[]{0f, 0.52f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        int save = canvas.save();
        canvas.clipRect(w * 0.39f, 0, w, h);

        float cx = w * 0.79f;
        float cy = h * 0.54f;

        int transform = canvas.save();
        canvas.rotate(-17f, cx, cy);
        canvas.scale(1f, 0.67f, cx, cy);

        // Fine track lanes, inspired by a stadium viewed from high in the stands.
        for (int i = 0; i < 6; i++) {
            float rx = w * (0.36f + i * 0.061f);
            float ry = h * (0.27f + i * 0.057f);
            oval.set(cx - rx, cy - ry, cx + rx, cy + ry);
            track.reset();
            track.addOval(oval, Path.Direction.CW);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth((i == 2 ? 1.4f : 0.85f) * density);
            paint.setColor(i == 2 ? 0x3DCDB77C : 0x249BB0C5);
            canvas.drawPath(track, paint);
        }

        // A single warm highlight travels slowly around one lane: elegant, not flashy.
        oval.set(cx - w * 0.535f, cy - h * 0.433f,
                cx + w * 0.535f, cy + h * 0.433f);
        track.reset();
        track.addOval(oval, Path.Direction.CW);
        PathMeasure measure = new PathMeasure(track, false);
        float length = measure.getLength();
        if (length > 0f) {
            float start = phase * length;
            float end = start + length * 0.115f;
            segment.reset();
            if (end <= length) {
                measure.getSegment(start, end, segment, true);
            } else {
                measure.getSegment(start, length, segment, true);
                measure.getSegment(0f, end - length, segment, true);
            }
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(2.5f * density);
            paint.setShader(new LinearGradient(
                    cx - w * 0.42f, cy - h * 0.24f,
                    cx + w * 0.42f, cy + h * 0.24f,
                    new int[]{0x08D3B161, 0xE6E0C28C, 0x60D3B161},
                    null, Shader.TileMode.CLAMP));
            canvas.drawPath(segment, paint);
            paint.setShader(null);
        }

        // Subtle, stationary reference lines ground the abstract track in the layout.
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f * density);
        paint.setColor(0x2BFFFFFF);
        Path horizon = new Path();
        horizon.moveTo(w * 0.48f, h * 0.88f);
        horizon.cubicTo(w * 0.71f, h * 0.73f, w * 0.78f, h * 0.75f, w * 1.04f, h * 0.63f);
        canvas.drawPath(horizon, paint);

        canvas.restoreToCount(transform);
        canvas.restoreToCount(save);

        // A low-contrast edge darkens the left panel so the headline stays readable.
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(
                0, 0, w * 0.74f, 0,
                new int[]{NAVY, 0xF2061E3D, 0xB7061E3D, 0x00061E3D},
                new float[]{0f, 0.47f, 0.76f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        // Thin gold hairline used as a quiet signature of the club colours.
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(GOLD);
        canvas.drawRect(0, h - 2f * density, w, h, paint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (animator == null) {
            animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(15000L);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(a -> {
                phase = (Float) a.getAnimatedValue();
                invalidate();
            });
        }
        if (!animator.isStarted()) animator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (animator != null) animator.cancel();
        super.onDetachedFromWindow();
    }
}
