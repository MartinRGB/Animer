<img src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/logo.png?token=ABVV6IRDJX54663FGBF3NAC5633SY" alt="" data-canonical-src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/logo.png?token=ABVV6IRDJX54663FGBF3NAC5633SY" width="264"/>

## About

<img src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/gifs.gif" alt="" data-canonical-src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/gifs.gif"/>

Animer is a java library which designed for a better Android animation experience.（Currently is more like a view animation controller)

It contains animation curves in `Android` `iOS` `Origami(POP or Rebound in Client)` `Principle` `Protopie` `FramerJS`

All these animation algorithm will be translated into Android's native implementation like DynamicAnimation & TimingInterpolator,which can improve the performance of animation.

It also provides a real-time controller & graph UI for tweaking parameters. 

Web version(Convert Any Animation tools' parameters to Android's) —— [Animator List](http://www.martinrgb.com/Animator_List/#)

AE plugin(only scripts currently) —— [Animator_List_AE_OpenSource](https://github.com/MartinRGB/Animator_List_AE_OpenSource)

## Download

[ ![Download](https://api.bintray.com/packages/martinrgb/animer/animer/images/download.svg?version=0.1.5.7) ](https://bintray.com/martinrgb/animer/animer/0.1.5.7/link)

```
dependencies {
    implementation 'com.martinrgb:animer:0.1.5.7'
}
```

[Simple Demo 1](https://github.com/MartinRGB/Animer/files/3948871/app-debug_2.zip)

[Simple Demo 2](https://github.com/MartinRGB/Animer/files/3948863/app-debug.zip)

## Usage

Animer supports multiple ways of animating:

### Android Native Style
```java
  
// equal to Android's TimingInterpolator
Animer.AnimerSolver solver  = Animer.interpolatorDroid(new AccelerateDecelerateInterpolator(),600)

// similar to ObjectAnimator 
Animer animer = new Animer(myView,solver,Animer.TRANSLATION_Y,0,200);

animer.start();

// animer.cancel();

// animer.end();

```

### FramerJS State Machine Style
```java
// equal to Android's SpringAnimation
Animer.AnimerSolver solver  = Animer.springDroid(1000,0.5f);

Animer animer = new Animer();

// add a solver to Animer; 
animer.setSolver(solver);

// add value to different state;
animer.setStateValue("stateA",300);
animer.setStateValue("stateB",700);
animer.setStateValue("stateC",200);

// add a listener to observe the motion of the Animation
animer.setUpdateListener(new Animer.UpdateListener() {
    @Override
    public void onUpdate(float value, float velocity, float progress) {
      myView1.setTranslationX(value);
      myView2.setScaleX(1.f+value/100);
      myView2.setScaleY(1.f+value/100);
    }
});

// switch immediately
animer.switchToState("stateA");

// or animate to state value
// animer.animateToState("stateB");
```

### Facebook Rebound Style
```java
// equal to Facebook Origami POP Animation
Animer.AnimerSolver solver  = Animer.springOrigamiPOP(5,10);

Animer animer = new Animer(myView,solver,Animer.SCALE);

// setup a listener to add everything you want
animer.setUpdateListener(new Animer.UpdateListener() {
    @Override
    public void onUpdate(float value, float velocity, float progress) 
      myView.setScaleX(value);
      myView.setScaleY(value);
    }
});

animer.setCurrentValue(1.f);

boolean isScaled = false;

myView.setOnClickListener(view -> {

    if(!isScaled){
        animer.setEndValue(0.5);

    }
    else{
        animer.setEndValue(1);
    }
    isScaled = !isScaled;
});
  
```

### Add animers to configUI

<img src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/configui.jpg?token=ABVV6IQRHX6MK3KK4RIFMLS564BKG" alt="" data-canonical-src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/configui.jpg?token=ABVV6IQRHX6MK3KK4RIFMLS564BKG" width="360"  />

init in xml

```xml
<com.martinrgb.animer.monitor.AnConfigView
    android:id="@+id/an_configurator"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

add animers' config in java

```java
AnConfigView mAnimerConfiguratorView = (AnConfigView) findViewById(R.id.an_configurator);
AnConfigRegistry.getInstance().addAnimer("Card Scale Animation",cardScaleAnimer);
AnConfigRegistry.getInstance().addAnimer("Card TranslationX Animation",cardTransXAnimer);
mAnimerConfiguratorView.refreshAnimerConfigs();
```


### Supported View Propertys:

```
Animer.TRANSLATION_X
Animer.TRANSLATION_Y
Animer.TRANSLATION_Z
Animer.SCALE // equal to SCALE_X + SCALE_Y
Animer.SCALE_X
Animer.SCALE_Y
Animer.ROTATION
Animer.ROTATION_X
Animer.ROTATION_Y
Animer.X
Animer.Y
Animer.Z
Animer.ALPHA
```

### Supported Animators(as AnimerSolver):

```java

Animer.springDroid(stiffness,dampingratio)                              // Android Dynamic SpringAnimation
Animer.flingDroid(velocity,friction)                                    // Android Dynamic FlingAnimation
Animer.springiOSUIView(dampingratio,duration)                           // iOS UIView SPring
Animer.springiOSCoreAnimation(stiffness,damping)                        // iOS CASpringAnimation
Animer.springOrigamiPOP(bounciness,speed)                               // Origami POP
Animer.springRK4(tension,friction)                                      // Framer-RK4
Animer.springDHO(stiffness,damping)                                     // Framer-DHO
Animer.springProtopie(tension,friction)                                 // Protopie
Animer.springPrinciple(tension,friction)                                // Principle

// Custom Bounce Interpolator(Romain Guy's DropInMotion)
Animer.interpolatorDroid(new CustomBounceInterpolator(),duration)       

// Custom Damping Interpolator(Romain Guy's DropInMotion)
Animer.interpolatorDroid(new CustomDampingInterpolator(),duration)      

// MocosSpring Interpolator (https://github.com/marcioapaiva/mocos-controlator)
Animer.interpolatorDroid(new CustomMocosSpringInterpolator(),duration)  

// Custom Spring Interpolator(https://inloop.github.io/interpolator/)
Animer.interpolatorDroid(new CustomSpringInterpolator(),duration)       

// Android Native Interpolator Below
Animer.interpolatorDroid(new PathInterpolator(),duration)               // Cubic Bezier Interpolator
...
Animer.interpolatorDroid(new DecelerateInterpolator(),duration)         // Android Decelerate Interpolator
...

```

## TODO

- Redesign the API
- Rewrite the glsl shader
- Consider scences in activity/fragment transition
- Hook mechanism 

## Core concpet:

<img src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/concept.jpg" alt="" data-canonical-src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/concept.jpg" width="900" />

**Data**

- State machine concpet from [FramerJS](https://github.com/koenbok/Framer/tree/master/framer) ✅
- Aniamtion converter from my [Animation Converter](https://github.com/MartinRGB/AndroidInterpolator_AE) ✅
- Support external JSON to edit animation data

**Althogrim**

- Physics animation concept from [Rebound](https://github.com/facebook/rebound) & Android DynamicAnimation ✅
- LookupTable Interpolation Animator + RK4 Solver + DHO Solver ✅
- Physics simulation from [Flutter Physics](https://api.flutter.dev/flutter/physics/physics-library.html) & [UIKit Dyanmic](https://developer.apple.com/documentation/uikit/animation_and_haptics/uikit_dynamics)
- Momentum

**Advanced Animation Setting**

- Addtive animation (compose mulitple animation)
- Chained animation (one by one)
- Parallax animation (same duration but differnt transition)
- Sequencing animation (same transition but different startDelay)

**User-Control**

- Gesture-Driven animation,you can interact with the animation even it is animating(Like iOS's `CADisplayLink` Or Rebound's `SetEndValue`) ✅
- Package a gesture animator for interactive animation,attach gesture's velocity to animation system,make a flawess experience.
- Easy2use animation listener for controlling other element when the object is interacting or animating ✅

**Performance**

- Use android framework native DyanmicAniamtion And TimingInterpolator ✅
- Pre-save animation's data for less calculation
- Hardware Acceleration ✅

**Design Component**

- Scrollview|Scroller|PageViewer Component & Example
- Drag | DND Component & Example
- Button Component & Example
- Transition Component & Example(Maintain different element's property in state machine)
- Scroll-selector Component & Example(Scroll to fixed position)
- Swipe to delete Component & Example

**Dev Tools**

- Data-bind graph to modify and preview animation in application ✅
- Data-bind selctor to change animation-type in application ✅（Still has some bugs)

**Utils**

- AE Plugin for converting curves & revealing codes ✅（Will update GUI later)

## License 

See Apache License [here](https://github.com/MartinRGB/Animer/blob/master/LICENSE)
