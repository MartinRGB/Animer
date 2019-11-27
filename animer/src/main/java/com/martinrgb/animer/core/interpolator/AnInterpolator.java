package com.martinrgb.animer.core.interpolator;


import android.animation.TimeInterpolator;

/**
 * An interpolator defines the rate of change of an animation. This allows
 * the basic animation effects (alpha, scale, translate, rotate) to be
 * accelerated, decelerated, repeated, etc.
 */
public abstract class AnInterpolator implements TimeInterpolator {
    // A new interface, TimeInterpolator, was introduced for the new android.animation
    // package. This older Interpolator interface extends TimeInterpolator so that users of
    // the new Animator-based animations can use either the old Interpolator implementations or
    // new classes that implement TimeInterpolator directly.

    public float arg1,arg2,arg3,arg4 = -1f;
    public String string1,string2,string3,string4 = "NULL";
    public float min1,min2,min3,min4,max1,max2,max3,max4 = -1f;
    public int argNum = 0;

    public void setArgData(int i, float val, String name, float min, float max){
        if(i == 0){
            //arg1 = val;
            string1 = name;
            min1 = min;
            max1 = max;
        }
        else if(i == 1){
            //arg2 = val;
            string2 = name;
            min2 = min;
            max2 = max;
        }
        else if(i == 2){
            //arg3 = val;
            string3 = name;
            min3 = min;
            max3 = max;
        }
        else if(i == 3){
            //arg4 = val;
            string4 = name;
            min4 = min;
            max4 = max;
        }
        setArgValue(i,val);
        argNum++;
    }

    public void resetData(int i,float value){

    }

    public void setArgValue(int i,float val){
        if(i == 0){
            arg1 = val;
        }
        else if(i == 1){
            arg2 = val;
        }
        else if(i == 2){
            arg3 = val;
        }
        else if(i == 3){
            arg4 = val;
        }
    }

    public int getArgNum(){
        return argNum;
    }

    public float getArgValue(int i) {
        if(i == 0){
            if(arg1 != -1)
                return (float) arg1;
            else
                return -1;
        }
        else if(i == 1){
            if(arg2 != -1)
                return (float) arg2;
            else
                return -1;
        }
        else if(i == 2){
            if(arg3 != -1)
                return (float) arg3;
            else
                return -1;
        }
        else if(i == 3){
            if(arg4 != -1)
                return (float) arg4;
            else
                return -1;
        }
        return -1;
    }

    public String getArgString(int i) {
        if(i == 0){
            if(string1 != "NULL")
                return  string1;
            else
                return "NULL";
        }
        else if(i == 1){
            if(string2 != "NULL")
                return  string2;
            else
                return "NULL";
        }
        else if(i == 2){
            if(string3 != "NULL")
                return  string3;
            else
                return "NULL";
        }
        else if(i == 3){
            if(string4 != "NULL")
                return  string4;
            else
                return "NULL";
        }
        return "NULL";
    }

    public float getArgMin(int i) {
        if(i == 0){
            if(min1 != -1)
                return  min1;
            else
                return -1;
        }
        else if(i == 1){
            if(min2 != -1)
                return  min2;
            else
                return -1;
        }
        else if(i == 2){
            if(min3 != -1)
                return  min3;
            else
                return -1;
        }
        else if(i == 3){
            if(min4 != -1)
                return  min4;
            else
                return -1;
        }
        return -1;
    }

    public float getArgMax(int i) {
        if(i == 0){
            if(max1 != -1)
                return  max1;
            else
                return -1;
        }
        else if(i == 1){
            if(max2 != -1)
                return  max2;
            else
                return -1;
        }
        else if(i == 2){
            if(max3 != -1)
                return  max3;
            else
                return -1;
        }
        else if(i == 3){
            if(max4 != -1)
                return  max4;
            else
                return -1;
        }
        return -1;
    }
}
