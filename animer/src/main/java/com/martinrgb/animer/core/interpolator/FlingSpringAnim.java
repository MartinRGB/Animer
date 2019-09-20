/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.launcher3.anim;

import androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.oppo.launcher.annotation.ColorLauncherHook;

import static com.android.launcher3.ColorLauncherAppTransitionManagerImpl.NORMAL_SPEED_FLAG;
import static com.android.launcher3.ColorLauncherAppTransitionManagerImpl.QUICK_SPEED_FLAG;
import static com.android.launcher3.ColorLauncherAppTransitionManagerImpl.SLOW_SPEED_FLAG;

/**
 * Given a property to animate and a target value and starting velocity, first apply friction to
 * the fling until we pass the target, then apply a spring force to pull towards the target.
 */
public class FlingSpringAnim {

    private static final float FLING_FRICTION = 1.5f;
    private static final float SPRING_STIFFNESS = 400f;
    private static final float SPRING_DAMPING = 0.8f;

    //#ifdef COLOROS_EDIT 80254194 add on 2019/8/12
    private static final float FLING_FRICTION_QUICK = 3.5f;
    private static final float FLING_FRICTION_NORMAL = 2f;
    private static final float FLING_FRICTION_SLOW = 4.5f;
    private static final float SPRING_STIFFNESS_QUICK = 800f;
    private static final float SPRING_STIFFNESS_NORMAL = 400f;
    private static final float SPRING_STIFFNESS_SLOW = 150f;
    private static final float SPRING_DAMPING_QUICK = 0.85f;
    private static final float SPRING_DAMPING_NORMAL = 0.8f;
    private static final float SPRING_DAMPING_SLOW = 0.95f;
    //#endif COLOROS_EDIT
    private final FlingAnimation mFlingAnim;
    private SpringAnimation mSpringAnim;

    private float mTargetPosition;

    public <K> FlingSpringAnim(K object, FloatPropertyCompat<K> property, float startPosition,
            float targetPosition, float startVelocity, float minVisChange, float minValue,
            float maxValue, float springVelocityFactor, OnAnimationEndListener onEndListener) {
        mFlingAnim = new FlingAnimation(object, property)
                .setFriction(FLING_FRICTION)
                // Have the spring pull towards the target if we've slowed down too much before
                // reaching it.
                .setMinimumVisibleChange(minVisChange)
                .setStartVelocity(startVelocity)
                .setMinValue(minValue)
                .setMaxValue(maxValue);
        mTargetPosition = targetPosition;

        mFlingAnim.addEndListener(((animation, canceled, value, velocity) -> {
            mSpringAnim = new SpringAnimation(object, property)
                    .setStartValue(value)
                    .setStartVelocity(velocity * springVelocityFactor)
                    .setSpring(new SpringForce(mTargetPosition)
                            .setStiffness(SPRING_STIFFNESS)
                            .setDampingRatio(SPRING_DAMPING));
            mSpringAnim.addEndListener(onEndListener);
            mSpringAnim.animateToFinalPosition(mTargetPosition);
        }));
    }

    public float getTargetPosition() {
        return mTargetPosition;
    }

    public void updatePosition(float startPosition, float targetPosition) {
        mFlingAnim.setMinValue(Math.min(startPosition, targetPosition))
                .setMaxValue(Math.max(startPosition, targetPosition));
        mTargetPosition = targetPosition;
        if (mSpringAnim != null) {
            mSpringAnim.animateToFinalPosition(mTargetPosition);
        }
    }

    public void start() {
        mFlingAnim.start();
    }

    public void end() {
        mFlingAnim.cancel();
        if (mSpringAnim.canSkipToEnd()) {
            mSpringAnim.skipToEnd();
        }
    }
}
