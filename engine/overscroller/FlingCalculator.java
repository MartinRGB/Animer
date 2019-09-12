/*
 * This file provided by Facebook is for non-commercial testing and evaluation purposes only.
 * Facebook reserves all rights not expressly granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * FACEBOOK BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.martinrgb.dynamic_scroll.component;

import android.util.Log;

public class FlingCalculator {

  private float mFriction;
  private float mVelocity;
  private float mDuration;
  private float mTransiton;

  public FlingCalculator(float friction,float velocity) {
    mFriction = friction*-4.2f;
    mVelocity = velocity;
    mDuration = calculate()[0];
    mTransiton = calculate()[1];
  }

  private float[] calculate() {
    float sampleScale = 1.5f;
    float maxItertation = 0;
    float maxValue = 0;
    float sampeScale = 1.5f;

    for (float i = 1 / (60 * sampleScale); i < 20.; i += 1 / (60 * sampleScale)) {

      float currentVelocity = mVelocity * (float) Math.exp(i * mFriction);
      float currentTransition = (mVelocity / mFriction) * (float) (Math.exp(mFriction * i) - 1);
      float speedThereshold = 2.3f;

      if (Math.abs(currentVelocity) <= speedThereshold) {

        maxItertation = i;
        maxValue = (currentTransition);

      }
      else{

      }

    }

    return new float[]{maxItertation, maxValue};
  }

  public float getDuration() {
    return mDuration;
  }

  public float getTransiton() {
    return mTransiton;
  }
}
