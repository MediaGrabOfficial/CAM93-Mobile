package fr.cam93.mobile;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/** A small, real OpenGL ES runner model with a continuous running cycle. */
public final class Runner3DView extends GLSurfaceView {
    public Runner3DView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);
        setRenderer(new RunnerRenderer());
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setFocusable(false);
    }

    private static final class Mesh {
        final FloatBuffer vertices;
        final ShortBuffer indices;
        final int indexCount;

        Mesh(float[] data, short[] indexData) {
            vertices = ByteBuffer.allocateDirect(data.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            vertices.put(data).position(0);
            indices = ByteBuffer.allocateDirect(indexData.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
            indices.put(indexData).position(0);
            indexCount = indexData.length;
        }
    }

    private static final class RunnerRenderer implements GLSurfaceView.Renderer {
        private static final float[] SHIRT = rgb(17, 80, 132);
        private static final float[] SHORTS = rgb(5, 26, 53);
        private static final float[] YELLOW = rgb(255, 213, 30);
        private static final float[] SKIN = rgb(218, 153, 105);

        private int program, aPosition, aNormal, uColor, uMvp, uModel;
        private Mesh sphere, cylinder;
        private FloatBuffer orbit, track;
        private final float[] projection = new float[16];
        private final float[] view = new float[16];
        private final float[] vp = new float[16];
        private final float[] parent = new float[16];
        private final float[] local = new float[16];
        private final float[] model = new float[16];
        private final float[] mvp = new float[16];
        private long startTime;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.024f, 0.105f, 0.208f, 1f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            program = program(VERTEX, FRAGMENT);
            aPosition = GLES20.glGetAttribLocation(program, "aPosition");
            aNormal = GLES20.glGetAttribLocation(program, "aNormal");
            uColor = GLES20.glGetUniformLocation(program, "uColor");
            uMvp = GLES20.glGetUniformLocation(program, "uMvp");
            uModel = GLES20.glGetUniformLocation(program, "uModel");
            sphere = sphere(16, 24);
            cylinder = cylinder(20);
            orbit = orbitBuffer();
            track = trackBuffer();
            startTime = System.nanoTime();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float aspect = width / (float) Math.max(1, height);
            Matrix.perspectiveM(projection, 0, 37f, Math.max(0.8f, aspect), 0.1f, 40f);
            Matrix.setLookAtM(view, 0, 0f, 0.23f, 5.8f, 0f, 0.23f, 0f, 0f, 1f, 0f);
            Matrix.multiplyMM(vp, 0, projection, 0, view, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glUseProgram(program);
            float t = (System.nanoTime() - startTime) / 1_000_000_000f;
            float phase = (float) Math.sin(t * 7.2f);
            float bounce = (float) Math.abs(Math.sin(t * 14.4f)) * 0.035f;

            Matrix.setIdentityM(parent, 0);
            Matrix.translateM(parent, 0, 0.82f + (float) Math.sin(t * 0.8f) * 0.035f, -0.015f + bounce * 0.2f, 0f);
            Matrix.rotateM(parent, 0, 7f + (float) Math.sin(t * 0.65f) * 12f, 0f, 1f, 0f);

            drawLines(orbit, 72, new float[]{1f, 0.835f, 0.12f, 0.45f}, 1.3f);
            drawLines(track, 72, new float[]{1f, 0.835f, 0.12f, 0.23f}, 1.2f);
            drawRunner(phase, bounce);
        }

        private void drawRunner(float phase, float bounce) {
            float bodyY = 0.50f + bounce;
            drawEllipsoid(0f, 0.04f + bounce, 0f, 0.205f, 0.16f, 0.15f, -3f, SHORTS);
            drawEllipsoid(0f, bodyY, 0f, 0.22f, 0.405f, 0.145f, -7f, SHIRT);
            drawEllipsoid(0.015f, bodyY + 0.035f, 0.134f, 0.173f, 0.055f, 0.026f, -7f, YELLOW);

            drawEllipsoid(0.035f, 1.145f + bounce, 0.005f, 0.142f, 0.16f, 0.135f, -5f, SKIN);
            drawEllipsoid(0.025f, 1.252f + bounce, -0.002f, 0.143f, 0.067f, 0.132f, -4f, SHORTS);

            drawArm(-0.19f, 0.81f + bounce, phase * 0.70f + 0.08f);
            drawArm(0.19f, 0.81f + bounce, -phase * 0.70f + 0.08f);
            drawLeg(-0.09f, bounce, phase * 0.62f);
            drawLeg(0.09f, bounce, -phase * 0.62f);
        }

        private void drawArm(float sx, float sy, float angle) {
            float ex = sx + (float) Math.sin(angle) * 0.22f;
            float ey = sy - (float) Math.cos(angle) * 0.23f;
            float wx = ex + (float) Math.sin(angle + 0.16f) * 0.205f;
            float wy = ey - (float) Math.cos(angle + 0.16f) * 0.205f;
            drawLimb(sx, sy, 0f, ex, ey, 0.02f, 0.075f, SHIRT);
            drawLimb(ex, ey, 0.02f, wx, wy, 0.04f, 0.052f, SKIN);
            drawEllipsoid(wx, wy, 0.055f, 0.058f, 0.062f, 0.053f, 0f, SKIN);
            drawEllipsoid(sx, sy, 0f, 0.085f, 0.086f, 0.075f, 0f, SHIRT);
        }

        private void drawLeg(float hx, float hy, float angle) {
            float kx = hx + (float) Math.sin(angle) * 0.29f;
            float ky = hy - (float) Math.cos(angle) * 0.31f;
            float shankAngle = angle * 0.78f - 0.16f;
            float fx = kx + (float) Math.sin(shankAngle) * 0.25f;
            float fy = ky - 0.27f * Math.max(0.42f, (float) Math.cos(shankAngle));
            drawLimb(hx, hy, 0f, kx, ky, 0.025f, 0.078f, SKIN);
            drawEllipsoid(kx, ky, 0.025f, 0.073f, 0.073f, 0.065f, 0f, SKIN);
            drawLimb(kx, ky, 0.025f, fx, fy, 0.055f, 0.055f, SKIN);
            drawEllipsoid(fx + 0.055f, fy - 0.026f, 0.07f, 0.135f, 0.052f, 0.078f, -8f, YELLOW);
        }

        private void drawEllipsoid(float x, float y, float z, float sx, float sy, float sz, float rz, float[] color) {
            Matrix.setIdentityM(local, 0);
            Matrix.translateM(local, 0, x, y, z);
            Matrix.rotateM(local, 0, rz, 0f, 0f, 1f);
            Matrix.scaleM(local, 0, sx, sy, sz);
            drawMesh(sphere, local, color);
        }

        private void drawLimb(float ax, float ay, float az, float bx, float by, float bz, float radius, float[] color) {
            float dx = bx - ax, dy = by - ay, dz = bz - az;
            float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (len < 0.0001f) return;
            float cosine = Math.max(-1f, Math.min(1f, dy / len));
            float degrees = (float) Math.toDegrees(Math.acos(cosine));
            float rx = dz, rz = -dx;
            float axis = (float) Math.sqrt(rx * rx + rz * rz);

            Matrix.setIdentityM(local, 0);
            Matrix.translateM(local, 0, (ax + bx) * 0.5f, (ay + by) * 0.5f, (az + bz) * 0.5f);
            if (axis > 0.0001f) Matrix.rotateM(local, 0, degrees, rx / axis, 0f, rz / axis);
            else if (cosine < 0f) Matrix.rotateM(local, 0, 180f, 1f, 0f, 0f);
            Matrix.scaleM(local, 0, radius, len * 0.5f, radius);
            drawMesh(cylinder, local, color);
        }

        private void drawMesh(Mesh mesh, float[] localTransform, float[] color) {
            Matrix.multiplyMM(model, 0, parent, 0, localTransform, 0);
            Matrix.multiplyMM(mvp, 0, vp, 0, model, 0);
            GLES20.glUniformMatrix4fv(uMvp, 1, false, mvp, 0);
            GLES20.glUniformMatrix4fv(uModel, 1, false, model, 0);
            GLES20.glUniform4f(uColor, color[0], color[1], color[2], color[3]);
            mesh.vertices.position(0);
            GLES20.glEnableVertexAttribArray(aPosition);
            GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 24, mesh.vertices);
            mesh.vertices.position(3);
            GLES20.glEnableVertexAttribArray(aNormal);
            GLES20.glVertexAttribPointer(aNormal, 3, GLES20.GL_FLOAT, false, 24, mesh.vertices);
            mesh.vertices.position(0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.indexCount, GLES20.GL_UNSIGNED_SHORT, mesh.indices);
        }

        private void drawLines(FloatBuffer buf, int count, float[] color, float width) {
            Matrix.multiplyMM(mvp, 0, vp, 0, parent, 0);
            GLES20.glUniformMatrix4fv(uMvp, 1, false, mvp, 0);
            GLES20.glUniformMatrix4fv(uModel, 1, false, parent, 0);
            GLES20.glUniform4f(uColor, color[0], color[1], color[2], color[3]);
            buf.position(0);
            GLES20.glEnableVertexAttribArray(aPosition);
            GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 24, buf);
            buf.position(3);
            GLES20.glEnableVertexAttribArray(aNormal);
            GLES20.glVertexAttribPointer(aNormal, 3, GLES20.GL_FLOAT, false, 24, buf);
            buf.position(0);
            GLES20.glLineWidth(width);
            GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, count);
        }

        private static Mesh sphere(int stacks, int sectors) {
            float[] data = new float[(stacks + 1) * (sectors + 1) * 6];
            int p = 0;
            for (int i = 0; i <= stacks; i++) {
                double theta = Math.PI * i / stacks;
                float y = (float) Math.cos(theta);
                float ring = (float) Math.sin(theta);
                for (int j = 0; j <= sectors; j++) {
                    double phi = 2.0 * Math.PI * j / sectors;
                    float x = ring * (float) Math.cos(phi);
                    float z = ring * (float) Math.sin(phi);
                    data[p++] = x; data[p++] = y; data[p++] = z;
                    data[p++] = x; data[p++] = y; data[p++] = z;
                }
            }
            short[] idx = new short[stacks * sectors * 6];
            int k = 0;
            for (int i = 0; i < stacks; i++) {
                int row = i * (sectors + 1), next = row + sectors + 1;
                for (int j = 0; j < sectors; j++) {
                    int a = row + j, b = next + j;
                    idx[k++] = (short) a; idx[k++] = (short) b; idx[k++] = (short) (a + 1);
                    idx[k++] = (short) (a + 1); idx[k++] = (short) b; idx[k++] = (short) (b + 1);
                }
            }
            return new Mesh(data, idx);
        }

        private static Mesh cylinder(int sectors) {
            float[] data = new float[(sectors + 1) * 2 * 6];
            int p = 0;
            for (int i = 0; i <= sectors; i++) {
                double a = 2.0 * Math.PI * i / sectors;
                float x = (float) Math.cos(a), z = (float) Math.sin(a);
                data[p++] = x; data[p++] = -1f; data[p++] = z; data[p++] = x; data[p++] = 0f; data[p++] = z;
                data[p++] = x; data[p++] = 1f; data[p++] = z; data[p++] = x; data[p++] = 0f; data[p++] = z;
            }
            short[] idx = new short[sectors * 6];
            int k = 0;
            for (int i = 0; i < sectors; i++) {
                short a = (short) (i * 2), b = (short) (i * 2 + 1);
                short c = (short) ((i + 1) * 2), d = (short) ((i + 1) * 2 + 1);
                idx[k++] = a; idx[k++] = b; idx[k++] = c;
                idx[k++] = c; idx[k++] = b; idx[k++] = d;
            }
            return new Mesh(data, idx);
        }

        private static FloatBuffer orbitBuffer() {
            float[] data = new float[72 * 6];
            for (int i = 0; i < 72; i++) {
                double a = Math.PI * 2.0 * i / 72.0;
                int p = i * 6;
                data[p] = 0.70f * (float) Math.cos(a);
                data[p + 1] = 0.46f + 0.95f * (float) Math.sin(a);
                data[p + 2] = -0.31f;
                data[p + 3] = 0f; data[p + 4] = 0f; data[p + 5] = 1f;
            }
            return buffer(data);
        }

        private static FloatBuffer trackBuffer() {
            float[] data = new float[72 * 6];
            for (int i = 0; i < 72; i++) {
                double a = Math.PI * 2.0 * i / 72.0;
                int p = i * 6;
                data[p] = 0.68f * (float) Math.cos(a);
                data[p + 1] = -0.80f;
                data[p + 2] = 0.42f * (float) Math.sin(a);
                data[p + 3] = 0f; data[p + 4] = 1f; data[p + 5] = 0f;
            }
            return buffer(data);
        }

        private static FloatBuffer buffer(float[] data) {
            FloatBuffer buf = ByteBuffer.allocateDirect(data.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            buf.put(data).position(0);
            return buf;
        }

        private static float[] rgb(int r, int g, int b) {
            return new float[]{r / 255f, g / 255f, b / 255f, 1f};
        }

        private static int program(String vertex, String fragment) {
            int vs = shader(GLES20.GL_VERTEX_SHADER, vertex);
            int fs = shader(GLES20.GL_FRAGMENT_SHADER, fragment);
            int p = GLES20.glCreateProgram();
            GLES20.glAttachShader(p, vs);
            GLES20.glAttachShader(p, fs);
            GLES20.glLinkProgram(p);
            int[] status = new int[1];
            GLES20.glGetProgramiv(p, GLES20.GL_LINK_STATUS, status, 0);
            if (status[0] == 0) {
                String error = GLES20.glGetProgramInfoLog(p);
                GLES20.glDeleteProgram(p);
                throw new IllegalStateException("Runner3D shader link failed: " + error);
            }
            GLES20.glDeleteShader(vs);
            GLES20.glDeleteShader(fs);
            return p;
        }

        private static int shader(int type, String source) {
            int s = GLES20.glCreateShader(type);
            GLES20.glShaderSource(s, source);
            GLES20.glCompileShader(s);
            int[] status = new int[1];
            GLES20.glGetShaderiv(s, GLES20.GL_COMPILE_STATUS, status, 0);
            if (status[0] == 0) {
                String error = GLES20.glGetShaderInfoLog(s);
                GLES20.glDeleteShader(s);
                throw new IllegalStateException("Runner3D shader compile failed: " + error);
            }
            return s;
        }

        private static final String VERTEX =
                "attribute vec3 aPosition;\n" +
                "attribute vec3 aNormal;\n" +
                "uniform mat4 uMvp;\n" +
                "uniform mat4 uModel;\n" +
                "varying vec3 vNormal;\n" +
                "void main(){ gl_Position = uMvp * vec4(aPosition,1.0); vNormal = normalize(mat3(uModel) * aNormal); }\n";

        private static final String FRAGMENT =
                "precision mediump float;\n" +
                "uniform vec4 uColor;\n" +
                "varying vec3 vNormal;\n" +
                "void main(){ vec3 n=normalize(vNormal); float l=max(dot(n,normalize(vec3(-0.25,0.65,1.0))),0.0); float rim=pow(1.0-max(dot(n,vec3(0.0,0.0,1.0)),0.0),2.0); vec3 c=uColor.rgb*(0.46+0.54*l)+vec3(0.08,0.10,0.14)*rim; gl_FragColor=vec4(c,uColor.a); }\n";
    }
}
