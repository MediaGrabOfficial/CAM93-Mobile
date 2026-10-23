package fr.cam93.mobile;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import android.view.animation.LinearInterpolator;

public final class TrackHeroView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final RectF oval = new RectF();
    private float motion = 0f;
    private ValueAnimator animator;

    public TrackHeroView(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(3600);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(value -> {
            motion = (float) value.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        if (w <= 0 || h <= 0) return;

        paint.setShader(new LinearGradient(0, 0, w, h,
                new int[]{0xFF07162C, 0xFF102C4F, 0xFF07111F},
                new float[]{0f, 0.56f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.78f, h * 0.22f, w * 0.82f,
                new int[]{0x554C83C2, 0x1C17385E, 0x0010213D},
                new float[]{0f, 0.48f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        // Large, cropped typographic ring gives the hero a branded sports-poster feel.
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(1.3f));
        paint.setColor(0x35FFFFFF);
        oval.set(w * 0.42f, -h * 0.30f, w * 1.24f, h * 0.56f);
        canvas.drawOval(oval, paint);
        paint.setStrokeWidth(dp(1f));
        oval.set(w * 0.47f, -h * 0.24f, w * 1.16f, h * 0.50f);
        canvas.drawOval(oval, paint);
        paint.setColor(0x20FFD31F);
        paint.setStrokeWidth(dp(1.6f));
        oval.set(w * 0.52f, -h * 0.19f, w * 1.08f, h * 0.44f);
        canvas.drawOval(oval, paint);

        // Track lanes sweep across the lower half in perspective.
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, h * 0.48f, w, h,
                new int[]{0x004E2332, 0xB54A2734, 0xEE4C2937},
                null, Shader.TileMode.CLAMP));
        path.reset();
        path.moveTo(0, h * 0.66f);
        path.cubicTo(w * 0.27f, h * 0.55f, w * 0.73f, h * 0.52f, w, h * 0.48f);
        path.lineTo(w, h);
        path.lineTo(0, h);
        path.close();
        canvas.drawPath(path, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(dp(1.7f));
        paint.setColor(0x4DFFD31F);
        for (int i = 0; i < 5; i++) {
            float offset = i * dp(18);
            path.reset();
            path.moveTo(-w * 0.17f, h * 1.12f - offset);
            path.cubicTo(w * 0.17f, h * 0.77f - offset,
                    w * 0.48f, h * 0.64f - offset * 0.72f,
                    w * 1.10f, h * 0.58f - offset * 0.46f);
            canvas.drawPath(path, paint);
        }

        // One brighter lane is the accent stroke.
        paint.setStrokeWidth(dp(2.4f));
        paint.setColor(0xC9FFD31F);
        path.reset();
        path.moveTo(-w * 0.08f, h * 1.04f);
        path.cubicTo(w * 0.15f, h * 0.83f, w * 0.48f, h * 0.70f, w * 1.08f, h * 0.60f);
        canvas.drawPath(path, paint);

        // Diagonal speed marks drift through the top-right without distracting from text.
        paint.setStrokeWidth(dp(2f));
        for (int i = 0; i < 6; i++) {
            float x = w * 0.61f + i * dp(17) + motion * dp(18);
            float y = h * 0.10f + i * dp(19);
            float alpha = 0.10f + (float) (0.12 * Math.sin((motion + i * 0.17) * Math.PI * 2));
            int a = (int) (Math.max(0.05f, alpha) * 255f);
            paint.setColor((a << 24) | 0x00FFD31F);
            canvas.drawLine(x, y, x + dp(24), y - dp(6), paint);
        }

        // Thin finish-line ticks near the bottom-right.
        paint.setColor(0x66FFFFFF);
        paint.setStrokeWidth(dp(1.2f));
        for (int i = 0; i < 7; i++) {
            float x = w * 0.70f + i * dp(12);
            canvas.drawLine(x, h * 0.88f, x + dp(10), h * 0.84f, paint);
        }

        // A graphic medal mark sits high at the right edge.
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFFFD31F);
        canvas.drawCircle(w * 0.86f, h * 0.17f, dp(22), paint);
        paint.setColor(0xFF07162C);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(android.graphics.Typeface.create("sans-serif-condensed", android.graphics.Typeface.BOLD));
        paint.setTextSize(dp(16));
        canvas.drawText("93", w * 0.86f, h * 0.17f + dp(5), paint);

        paint.setTextSize(dp(7));
        paint.setColor(0xFFFFD31F);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("VITESSE  /  COLLECTIF  /  MONTREUIL", w - dp(15), h - dp(16), paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.FILL);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
