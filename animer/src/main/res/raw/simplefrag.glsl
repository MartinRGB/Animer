#ifdef GL_ES
precision highp float;
#endif

#define PI 3.14159265359
#define E 2.718281828459045

uniform vec2 u_resolution;
uniform float u_time;

varying vec2 vFlingCalc;
varying float vSpringCalc;
varying float vDuration;
varying float vMode;
varying vec2 vUv;
varying float v_factor1;
varying float v_factor2;
varying float v_factor3;
varying float v_factor4;
varying float v_factor5;

const float lineJitter = 0.5;
const float lineWidth = 7.0;
const float gridWidth = 1.7;
const float scale = 0.0013;
const float Samples = 3.;

float timeProgress;
vec2 circlePos = vec2(0.);
float circleRadius = 0.0008;

bool reset = false;

int duration_mode = 0;

float FlingSimulator(in float time){
    float startVal = 0.;
    float deltaT = time * vDuration;
    float mRealFriction = v_factor2*(-4.2);
    float valTransition =  (0. - v_factor1/mRealFriction) + ( v_factor1/ mRealFriction) * (exp(mRealFriction * deltaT ) );
    float mLastVal = valTransition/vFlingCalc[1];
    return mLastVal/2.;
}


float SpringSimulator(in float time){
    float deltaT = time*1. * vDuration;
    float starVal = 0.;
    float endVal = 1.;
    float mNaturalFreq = sqrt(v_factor1);
    float mDampedFreq = mNaturalFreq*sqrt(1.0 - v_factor2* v_factor2);
    //TODO vSpringVelocity
    float lastVelocity =  0.;
    float lastDisplacement  = time*1. - endVal;
    float coeffB = 1.0 / mDampedFreq * (v_factor2 * mNaturalFreq * lastDisplacement + lastVelocity);
    float displacement = pow(E,-v_factor2 * mNaturalFreq * deltaT) * (lastDisplacement * cos(mDampedFreq * deltaT) + coeffB * sin(mDampedFreq * deltaT));
    float mValue = displacement + endVal;
    return mValue/2.+0.;
}


float CubicBezierSimulator(in float time,vec4 bezierPoint) {
    if (time > 1.0) {
        return 1.;
    } else if(time < 0.){
        return 0.;
    }
    float x = time;
    float z;
    vec2 c,b,a;
    for (int i = 1; i < 20; i++) {
        c.x = 3. * bezierPoint[0];
        b.x = 3. * (bezierPoint[2] - bezierPoint[0]) - c.x;
        a.x = 1. - c.x - b.x;
        z = x * (c.x + x * (b.x + x * a.x)) - time;
        if (abs(z) < 1e-3) {
            break;
        }
        x -= z / (c.x + x * (2. * b.x + 3. * a.x * x));
    }

    c.y = 3. * bezierPoint[1];
    b.y = 3. * (bezierPoint[3] - bezierPoint[1]) - c.y;
    a.y = 1. - c.y - b.y;
    float mValue = x * (c.y + x * (b.y + x * a.y));

    return mValue/1.;
}

float AccelerateInterpolator(in float time){
    if (time <= 0.0) {
        return 0.;
    }

    if (v_factor1 == 1.0) {
        return time * time;
    } else {
        return pow(time, 2.*v_factor1);
    }
}

float DecelerateInterpolator(in float time) {

    if(time >=1.0){
        return 1.;
    }
    if (v_factor1 == 1.0) {
        return 1.0 - (1.0 - time) * (1.0 - time);
    } else {
        return (1.0 - pow((1.0 - time), 2.0 * v_factor1));
    }
}

float LinearInterpolator(in float time){
    return time;
}

float AccelerateDecelerateInterpolator(in float time){
    return (cos((time + 1.) * PI) / 2.0) + 0.5;
}

float AnticipateInterpolator(in float time){
    return time * time * ((v_factor1 + 1.) * time - v_factor1);
}

float OvershootInterpolator(float time) {
    time -= 1.0;
    return time * time * ((v_factor1 + 1.) * time + v_factor1) + 1.0;
}

float AOSIA(float t, float s) {
    return t * t * ((s + 1.) * t - s);
}

float AOSIO(float t, float s) {
    return t * t * ((s + 1.) * t + s);
}


float AnticipateOvershootInterpolator(float time) {
    float t = time;
    if (t < 0.5) return 0.5 * AOSIA(t * 2.0, v_factor1*1.5);
    else return 0.5 * (AOSIO(t * 2.0 - 2.0, v_factor1*1.5) + 2.0);
}

float BounceInterpolator(in float time){
    float t= time;
    t *= 1.1226;
    if (t < 0.3535) return t * t * 8.0;
    else if (t < 0.7408) return (t - 0.54719)*(t - 0.54719)*8. + 0.7;
    else if (t < 0.9644) return (t - 0.8526)*(t - 0.8526)*8. + 0.9;
    else return (t - 1.0435)*(t - 1.0435)*8. + 0.95;
}

float CycleInterpolator(in float time) {
    float mValue = sin(2. * v_factor1 * PI * time);
    return mValue/2. + 0.5;
}

float FastOutLinearInInterpolator(in float time){
    return CubicBezierSimulator(time,vec4(0.40,0.00,1.00,1.00));
}

float FastOutSlowInInterpolator(in float time){
    return CubicBezierSimulator(time,vec4(0.40,0.00,0.20,1.00));
}

float LinearOutSlowInInterpolator(in float time){
    return CubicBezierSimulator(time,vec4(0.00,0.00,0.20,1.00));
}

float CustomSpringInterpolator(in float ratio) {
    if (ratio == 0.0 || ratio == 1.0)
        return ratio/2.;
    else {
        float value = (pow(2., -10. * ratio) * sin((ratio - v_factor1 / 4.0) * (2.0 * PI) / v_factor1) + 1.);
        return value/2.;
    }
}

float CustomBounceInterpolator(in float ratio){
    float amplitude = 1.;
    float phase = 0.;
    float originalStiffness = 12.;
    float originalFrictionMultipler = 0.3;
    float mass = 0.058;
    float maxStifness = 50.;
    float maxFrictionMultipler = 1.;

    float aTension = min(max(v_factor1,0.),100.) * (maxStifness- originalStiffness)/100.;
    float aFriction = min(max(v_factor2,0.),100.) * (maxFrictionMultipler - originalFrictionMultipler)/100.;

    float pulsation = sqrt((originalStiffness + aTension) / mass);
    float friction = (originalFrictionMultipler + aFriction) * pulsation;

    if (ratio == 0.0 || ratio == 1.0)
    return ratio/2.;
    else {
        float value = amplitude * exp(-friction * ratio) * cos(pulsation * ratio + phase) ;
        return (-abs(value)+1.)/2.;
    }
}

float CustomDampingInterpolator(in float ratio){
    float amplitude = 1.;
    float phase = 0.;
    float originalStiffness = 12.;
    float originalFrictionMultipler = 0.3;
    float mass = 0.058;
    float maxStifness = 50.;
    float maxFrictionMultipler = 1.;

    float aTension = min(max(v_factor1,0.),100.) * (maxStifness- originalStiffness)/100.;
    float aFriction = min(max(v_factor2,0.),100.) * (maxFrictionMultipler - originalFrictionMultipler)/100.;

    float pulsation = sqrt((originalStiffness + aTension) / mass);
    float friction = (originalFrictionMultipler + aFriction) * pulsation;

    if (ratio == 0.0 || ratio == 1.0)
    return ratio/2.;
    else {
        float value = amplitude * exp(-friction * ratio) * cos(pulsation * ratio + phase) ;
        return (-(value)+1.)/2.;
    }
}

float AndroidSpringInterpolator(float ratio) {
    if (ratio == 0.0 || ratio == 1.0)
        return ratio/2.;
    else {
        float deltaT = ratio * vDuration;
        float starVal = 0.;
        float endVal = 1.;

        float mNaturalFreq = sqrt(v_factor1);
        float mDampedFreq = (mNaturalFreq*sqrt(1.0 - v_factor2* v_factor2));
        //TODO vSpringVelocity
        float lastVelocity =  0.;
        //float lastDisplacement  = ratio - endVal* deltaT/60 - endVal;
        float lastDisplacement  = ratio -  endVal;
        float coeffB =  (1.0 / mDampedFreq * (v_factor2 * mNaturalFreq * lastDisplacement + lastVelocity));
        float displacement =  (pow(E,-v_factor2 * mNaturalFreq * deltaT) * (lastDisplacement * cos(mDampedFreq * deltaT) + coeffB * sin(mDampedFreq * deltaT)));
        float mValue = displacement + endVal;
        if(vDuration == 0.){
            return starVal/2.;
        }
        else{
            return mValue/2.;
        }
    }
}

float CustomMocosSpringInterpolator(in float ratio) {
    if (ratio >= 1.) {
        return 1./2.;
    }

    float tension = v_factor1;
    float damping = v_factor2;
    float velocity = v_factor3;

    float mEps = 0.001;
    float mGamma,mVDiv2,mB,mA,mMocosDuration;

    bool mOscilative = (4. * tension - damping * damping > 0.);
    if (mOscilative) {
        mGamma = sqrt(4. * tension - damping * damping) / 2.;
        mVDiv2 = damping / 2.;
        mB = atan(-mGamma / (velocity - mVDiv2));
        mA = -1. / sin(mB);
        mMocosDuration = log(abs(mA) / mEps) / mVDiv2;
    } else {
        mGamma = sqrt(damping * damping - 4. * tension) / 2.;
        mVDiv2 = damping / 2.;
        mA = (velocity - (mGamma + mVDiv2)) / (2. * mGamma);
        mB = -1. - mA;
        mMocosDuration = log(abs(mA) / mEps) / (mVDiv2 - mGamma);
    }

    float t = ratio * mMocosDuration;
    if(mOscilative){
        return (mA * exp(-mVDiv2 * t) * sin(mGamma * t + mB) + 1.)/2.;
    }
    else{
        return (mA * exp((mGamma - mVDiv2) * t) + mB * exp(-(mGamma + mVDiv2) * t) + 1.)/2.;
    }
}


vec4 plot2D(in vec2 _st, in float _width ,vec3 initColor) {
    const float samples = float(Samples);

    vec2 steping = _width*vec2(scale)/samples;

    float count = 0.0;
    float mySamples = 0.0;
    for (float i = 0.0; i < samples; i++) {
        for (float j = 0.0;j < samples; j++) {
            if (i*i+j*j>samples*samples)
            continue;
            mySamples++;
            float ii = i;
            float jj = j;
            float f = 0.;
            if(vMode == 0.0){
                f = FlingSimulator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 1.0){
                f = SpringSimulator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.0){
                f = CubicBezierSimulator((_st.x+ ii*steping.x),vec4(v_factor1,v_factor2,v_factor3,v_factor4))-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.01){
                f = LinearInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.02){
                f = AccelerateDecelerateInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.03){
                f = AccelerateInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.04){
                f = DecelerateInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.05){
                f = AnticipateInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.06){
                f = OvershootInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.07){
                f = AnticipateOvershootInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.08){
                f = BounceInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.09){
                f = CycleInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.10){
                f = FastOutSlowInInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.11){
                f = LinearOutSlowInInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.12){
                f = FastOutLinearInInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.13){
                f = CustomMocosSpringInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.14){
                f = CustomSpringInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.15){
                f = CustomBounceInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.16){
                f = CustomDampingInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            else if(vMode == 2.17){
                f = AndroidSpringInterpolator((_st.x+ ii*steping.x) )-(_st.y+ jj*steping.y);
            }
            count += (f>0.0) ? 1. : -1.0 ;
        }
    }

    if (abs(count)!=mySamples)
        return vec4( vec3(abs(float(count))/float(mySamples))*1000.*initColor   ,1.);
    return vec4(0.);
}

float circle(in vec2 _st, in float _radius){
    vec2 dist = _st-vec2(0.5);
    return 1.-smoothstep(_radius-(_radius*0.01),_radius+(_radius*0.01),dot(dist,dist)*4.0);
}

vec2 circle2D(in float progress){
    if(vMode == 0.0){
        return vec2(progress,FlingSimulator(progress));
    }
    else if(vMode == 1.0){
        return vec2(progress,SpringSimulator(progress));
    }
    else if(vMode == 2.0){
        return vec2(progress,CubicBezierSimulator(progress,vec4(v_factor1,v_factor2,v_factor3,v_factor4)));
    }
    else if(vMode == 2.01){
        return vec2(progress,LinearInterpolator(progress));
    }
    else if(vMode == 2.02){
        return vec2(progress,AccelerateDecelerateInterpolator(progress));
    }
    else if(vMode == 2.03){
        return vec2(progress,AccelerateInterpolator(progress));
    }
    else if(vMode == 2.04){
        return vec2(progress,DecelerateInterpolator(progress));
    }
    else if(vMode == 2.05){
        return vec2(progress,AnticipateInterpolator(progress));
    }
    else if(vMode == 2.06){
        return vec2(progress,OvershootInterpolator(progress));
    }
    else if(vMode == 2.07){
        return vec2(progress,AnticipateOvershootInterpolator(progress));
    }
    else if(vMode == 2.08){
        return vec2(progress,BounceInterpolator(progress));
    }
    else if(vMode == 2.09){
        return vec2(progress,CycleInterpolator(progress));
    }
    else if(vMode == 2.10){
        return vec2(progress,FastOutSlowInInterpolator(progress));
    }
    else if(vMode == 2.11){
        return vec2(progress,LinearOutSlowInInterpolator(progress));
    }
    else if(vMode == 2.12){
        return vec2(progress,FastOutLinearInInterpolator(progress));
    }
    else if(vMode == 2.13){
        return vec2(progress,CustomMocosSpringInterpolator(progress));
    }
    else if(vMode == 2.14){
        return vec2(progress,CustomSpringInterpolator(progress));
    }
    else if(vMode == 2.15){
        return vec2(progress,CustomBounceInterpolator(progress));
    }
    else if(vMode == 2.16){
        return vec2(progress,CustomDampingInterpolator(progress));
    }
    else if(vMode == 2.17){
        return vec2(progress,AndroidSpringInterpolator(progress));
    }
}

float when_gt(float x, float y) {
    return max(sign(x - y), 0.0);
}

float when_lt(float x, float y) {
    return max(sign(y - x), 0.0);
}

void main(){
    vec2 st = vUv;


    //#Scale
    float mScale = 0.85;
    st *= 1./mScale;
    st -= (1./mScale - 1.)/2.;

    timeProgress = min(u_time/vDuration,1.);

    vec4 color = vec4(0.,0.,0.,0.);

    if(st.x>-0.01 &&st.y>-0.5 && st.y<1.5 && st.x<1.01){
        color = plot2D(st,lineWidth,vec3(1.));
        if(st.x<timeProgress){
            color += plot2D(st,lineWidth,vec3(-0./255.,-255./255.,-255./255.));
        }
    }

    float step = 0.005;
    circlePos = circle2D(timeProgress + step);
    vec2 uv = vUv;
    uv += vec2(0.5) *mScale;
    uv *= 1./mScale;
    uv -= (1./mScale - 1.)/2.;

    color += vec4(1.,-1000.,-1000.,1.)*circle(uv - circlePos+vec2(0.001,0.004),circleRadius);

    gl_FragColor = color;
}
