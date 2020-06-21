package com.martinrgb.animer.core.interpolator.AndroidNative;

import android.graphics.Path;

import com.martinrgb.animer.core.interpolator.AnInterpolator;

public class PathInterpolator extends AnInterpolator {
    private static final float PRECISION = 0.002f;

    private float[] mX; // x coordinates in the line

    private float[] mY; // y coordinates in the line

    private float x1,y1,x2,y2;

    public PathInterpolator(float controlX1, float controlY1, float controlX2, float controlY2) {
        x1= controlX1;
        y1 = controlY1;
        x2 = controlX2;
        y2 = controlY2;
        initCubic(x1, y1, x2, y2);

        initArgData(0,x1,"x1",0,1);
        initArgData(1,y1,"y1",0,1);
        initArgData(2,x2,"x2",0,1);
        initArgData(3,y2,"y2",0,1);
    }

    @Override
    public void resetArgValue(int i, float value){
        setArgValue(i,value);
        if(i == 0){
            x1 = value;
        }
        if(i == 1){
            y1 = value;
        }
        if(i == 2){
            x2 = value;
        }
        if(i == 3){
            y2 = value;
        }
        initCubic(x1, y1, x2, y2);
    }


    private void initCubic(float x1, float y1, float x2, float y2) {
        Path path = new Path();
        path.moveTo(0, 0);
        path.cubicTo(x1, y1, x2, y2, 1f, 1f);
        initPath(path);
    }

    private void initPath(Path path) {
        float[] pointComponents = path.approximate(PRECISION);

        int numPoints = pointComponents.length / 3;
        if (pointComponents[1] != 0 || pointComponents[2] != 0
                || pointComponents[pointComponents.length - 2] != 1
                || pointComponents[pointComponents.length - 1] != 1) {
            throw new IllegalArgumentException("The Path must start at (0,0) and end at (1,1)");
        }

        mX = new float[numPoints];
        mY = new float[numPoints];
        float prevX = 0;
        float prevFraction = 0;
        int componentIndex = 0;
        for (int i = 0; i < numPoints; i++) {
            float fraction = pointComponents[componentIndex++];
            float x = pointComponents[componentIndex++];
            float y = pointComponents[componentIndex++];
            if (fraction == prevFraction && x != prevX) {
                throw new IllegalArgumentException(
                        "The Path cannot have discontinuity in the X axis.");
            }
            if (x < prevX) {
                throw new IllegalArgumentException("The Path cannot loop back on itself.");
            }
            mX[i] = x;
            mY[i] = y;
            prevX = x;
            prevFraction = fraction;
        }
    }

    @Override
    public float getInterpolation(float t) {
        if (t <= 0) {
            return 0;
        } else if (t >= 1) {
            return 1;
        }
        // Do a binary search for the correct x to interpolate between.
        int startIndex = 0;
        int endIndex = mX.length - 1;

        while (endIndex - startIndex > 1) {
            int midIndex = (startIndex + endIndex) / 2;
            if (t < mX[midIndex]) {
                endIndex = midIndex;
            } else {
                startIndex = midIndex;
            }
        }

        float xRange = mX[endIndex] - mX[startIndex];
        if (xRange == 0) {
            return mY[startIndex];
        }

        float tInRange = t - mX[startIndex];
        float fraction = tInRange / xRange;

        float startY = mY[startIndex];
        float endY = mY[endIndex];
        return startY + (fraction * (endY - startY));
    }

}