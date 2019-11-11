package com.martinrgb.animer.core.util;

// From Facebook Rebound
public class AnUtil {

    /**
     * Map a value within a given range to another range.
     * @param value the value to map
     * @param fromLow the low end of the range the value is within
     * @param fromHigh the high end of the range the value is within
     * @param toLow the low end of the range to map to
     * @param toHigh the high end of the range to map to
     * @return the mapped value
     */
    public static float mapValueFromRangeToRange(
            float value,
            float fromLow,
            float fromHigh,
            float toLow,
            float toHigh) {
        float fromRangeSize = fromHigh - fromLow;
        float toRangeSize = toHigh - toLow;
        float valueScale = (value - fromLow) / fromRangeSize;
        return toLow + (valueScale * toRangeSize);
    }

    public static float mapClampedValueFromRangeToRange(
            float value,
            float fromLow,
            float fromHigh,
            float toLow,
            float toHigh) {
        float fromRangeSize = fromHigh - fromLow;
        float toRangeSize = toHigh - toLow;
        float valueScale = (value - fromLow) / fromRangeSize;
        return toLow + (Math.max(0,Math.min(1,valueScale)) * toRangeSize);
    }

    /**
     * Clamp a value to be within the provided range.
     * @param value the value to clamp
     * @param low the low end of the range
     * @param high the high end of the range
     * @return the clamped value
     */
    public static double clamp(double value, double low, double high) {
        return Math.min(Math.max(value, low), high);
    }
}
