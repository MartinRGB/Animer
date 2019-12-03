attribute vec4  a_position;

uniform vec2 u_resolution;
uniform float u_time;
uniform mat4 u_MVPMatrix;
uniform float u_mode;
uniform float u_factor1;
uniform float u_factor2;
uniform float u_factor3;
uniform float u_factor4;
uniform float u_factor5;
uniform float u_duration;


varying vec2 vFlingCalc;
varying float vSpringCalc;
varying float vMode;
varying float vDuration;
varying vec2 vUv;
varying float v_factor1;
varying float v_factor2;
varying float v_factor3;
varying float v_factor4;
varying float v_factor5;

// ############################# compute functions #############################

vec2 computeFling(in float velocity,in float friction){
    float mRealFriction = friction*-4.2;

    for (float i = 1./60.;i < 4.;i += 1./60.){
        float currentVelocity = velocity * exp(i * mRealFriction) ;
        float currentTransition = (velocity/ mRealFriction) * (exp(mRealFriction * i ) - 1.);
        float speedThereshold = 2.3;

        if(abs(currentVelocity) <=  speedThereshold){
            return vec2(i,currentTransition);
        }
        else{
            continue;
        }

    }
}

float computeDampingRatio(in float tension,in float friction) {
    float mass = 1.0;
    return friction / (2. * sqrt(mass * tension));
}

float computeTFSpringDuration(in float tension,in float friction) {
    float durationFactor = 2.;
    float epsilon = 0.001;
    float velocity = 0.0;
    float mass = 1.0;
    float dampingRatio = computeDampingRatio(tension, friction);
    float undampedFrequency = sqrt(tension / mass);
    if (dampingRatio < 1.) {
        float a = sqrt(1. - pow(dampingRatio, 2.));
        float b = velocity / (a * undampedFrequency);
        float c = dampingRatio / a;
        float d = -((b - c) / epsilon);
        if (d <= 0.) {
            return 0.0;
        }
        return log(d) / (dampingRatio * undampedFrequency);
    } else {
        return 0.0;
    }
}

float computeSDSpringDuration(in float stiffness,in float dampingRatio) {
    float durationFactor = 2.;
    float epsilon = 0.0004;
    float velocity = 0.0;
    float mass = 1.0;
    float undampedFrequency = sqrt(stiffness / mass);
    if (dampingRatio < 1.) {
        float a = sqrt(1. - pow(dampingRatio, 2.));
        float b = velocity / (a * undampedFrequency);
        float c = dampingRatio / a;
        float d = -((b - c) / epsilon);
        if (d <= 0.) {
            return 0.0;
        }
        return log(d) / (dampingRatio * undampedFrequency);
    } else {
        return 0.0;
    }
}

//https://stackoverflow.com/questions/23910020/equivalent-to-gl-fragcoord-in-glsl-vertex-shader
vec2 computeFakeUV(){
    vec4 fake_frag_coord  = (u_MVPMatrix * a_position);     // Range:   [-w,w]^4
    // Vertex in NDC-space
    fake_frag_coord.xyz /= fake_frag_coord.w;       // Rescale: [-1,1]^3
    fake_frag_coord.w    = 1.0 / fake_frag_coord.w; // Invert W
    // Vertex in window-space
    // Rescale: [0,1]^3
    fake_frag_coord.xyz *= vec3(0.5);
    fake_frag_coord.xyz += vec3(0.5);

    return fake_frag_coord.xy;
}


void main() {

    vMode = u_mode;

    v_factor1 = u_factor1;
    v_factor2 = u_factor2;
    v_factor3 = u_factor3;
    v_factor4 = u_factor4;
    v_factor5 = u_factor5;


    if(u_mode == 0.0){
        vDuration = computeFling(u_factor1,u_factor2)[0];
    }
    else if(u_mode == 1.0){
        vDuration = computeSDSpringDuration(u_factor1,u_factor2);
    }
    else{
        vDuration = u_duration;
    }
    // #Fling
    vFlingCalc = computeFling(u_factor1,u_factor2);

    // #Spring
    vSpringCalc = computeSDSpringDuration(u_factor1,u_factor2);

    vUv = computeFakeUV();

    gl_Position = a_position*u_MVPMatrix;
    //gl_Position = vec4(a_position.xy,1.,0.);
}
