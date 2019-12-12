<img src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/logo.png?token=ABVV6IRDJX54663FGBF3NAC5633SY" alt="" data-canonical-src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/logo.png?token=ABVV6IRDJX54663FGBF3NAC5633SY" width="264"/>

## 关于

<img src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/gifs.gif" alt="" data-canonical-src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/gifs.gif"/>

Launguage:[English](https://github.com/MartinRGB/Animer)

Animer 是一款致力于提升 Android 动画体验的 Java 库，目前主要功能是基于 `View 动画`的控制器和调试器

Animer 封装了:

* Android 平台的 DynamicAnimation 和 Interpolator 的曲线
* iOS 平台的 CASpringAnimation 和 UIViewSpring 的曲线
* 贝塞尔函数曲线
* Principle、Origami、Protopie、FramerJS 等动画工具的曲线

Animer 并没有像 Rebound 那样，通过 Choreographer 或者自构建 Looper ，从头构建一套动画器，而是将上述曲线的算法通过转换器，最终会转换成 Android 原生的 DynamicAnimation 或者 TimingInterpolator，进而提高动画执行性能。

Animer 提供了可实时调节的控制UI和曲线图表，以便设计师和开发者调节参数，节省编译时间。

网页版本(该网页主要功能是可以将其他平台、工具的参数转化为安卓原生动画类的参数) —— [Animator List](http://www.martinrgb.com/Animator_List/#)

AE 插件(目前仅仅是脚本形式，而且建议使用英文版 AE) —— [Animator_List_AE_OpenSource](https://github.com/MartinRGB/Animator_List_AE_OpenSource)

## 下载

[ ![Download](https://api.bintray.com/packages/martinrgb/animer/animer/images/download.svg?version=0.1.5.7) ](https://bintray.com/martinrgb/animer/animer/0.1.5.7/link)

```
dependencies {
    implementation 'com.martinrgb:animer:0.1.5.7'
}
```

[View 动画 Demo 1](https://github.com/MartinRGB/Animer/files/3948871/app-debug_2.zip)

[View 动画 Demo 2](https://github.com/MartinRGB/Animer/files/3948863/app-debug.zip)

## 使用方法

Animer 支持多种风格的动画方法，如果写过 FramerJS、用过 Rebound 库的朋友会非常熟悉，也模拟了安卓原生的动画方法。

### Android 原生风格
```java
  
// 创建一个 Animer 解算器对象，采用了原生的插值动画类
Animer.AnimerSolver solver  = Animer.interpolatorDroid(new AccelerateDecelerateInterpolator(),600)

// 模仿 ObjectAnimator 的构造
Animer animer = new Animer(myView,solver,Animer.TRANSLATION_Y,0,200);

animer.start();

// animer.cancel();

// animer.end();

```

### FramerJS 状态机风格（ FramerJS 的状态机动画机制 非常便于组织、整理页面动画）
```java
// 创建一个 Animer 解算器对象，采用了原生的 DynamicAnimation 动画器
Animer.AnimerSolver solver  = Animer.springDroid(1000,0.5f);

Animer animer = new Animer();

// 给 Animer 对象添加 solver
animer.setSolver(solver);

// 设置动画的几种可能状态（入场、退场、点击）
animer.setStateValue("stateA",300);
animer.setStateValue("stateB",700);
animer.setStateValue("stateC",200);

// 给动画添加监听，观察数值变化
animer.setUpdateListener(new Animer.UpdateListener() {
    @Override
    public void onUpdate(float value, float velocity, float progress) {
      myView1.setTranslationX(value);
      myView2.setScaleX(1.f+value/100);
      myView2.setScaleY(1.f+value/100);
    }
});

// 立即切换到状态
animer.switchToState("stateA");

// 动画运动到状态
// animer.animateToState("stateB");
```

### Facebook Rebound 风格
```java
// 创建一个 Animer 解算器对象，采用了 Facebook 的 POP 弹性动画器
Animer.AnimerSolver solver  = Animer.springOrigamiPOP(5,10);

Animer animer = new Animer(myView,solver,Animer.SCALE);

// 给动画添加监听，观察数值变化，这里将对象 view 的缩放和动画器数值绑定
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

### 将 Animer 对象添加到 config UI 中

<img src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/configui.jpg?token=ABVV6IQRHX6MK3KK4RIFMLS564BKG" alt="" data-canonical-src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/configui.jpg?token=ABVV6IQRHX6MK3KK4RIFMLS564BKG" width="360"  />

添加 XML

```xml
<com.martinrgb.animer.monitor.AnConfigView
    android:id="@+id/an_configurator"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

在 Java中， 将 Animer 对象添加到 AnConfigRegistry 实例中，然后刷新 UI

```java
AnConfigView mAnimerConfiguratorView = (AnConfigView) findViewById(R.id.an_configurator);
AnConfigRegistry.getInstance().addAnimer("Card Scale Animation",cardScaleAnimer);
AnConfigRegistry.getInstance().addAnimer("Card TranslationX Animation",cardTransXAnimer);
mAnimerConfiguratorView.refreshAnimerConfigs();
```


### 支持的 View 属性

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

### 支持的动画曲线

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

- 重新设计 API，重新编写文档，提高可用性
- 重写绘制图表的 shader，目前使用了太多条件分歧，参考[如何在shader中避免使用if else](https://www.bilibili.com/read/cv1469216/)
- 考虑转场的使用场景
- 考虑 Hook 机制

## Animer 设计的核心理念和一些想法

下图是 Animer 大致的原理和设计思路

<img src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/concept.jpg" alt="" data-canonical-src="https://raw.githubusercontent.com/MartinRGB/Animer/master/art/concept.jpg" width="900" />

**数据**

- [FramerJS]的动画状态机概念(https://github.com/koenbok/Framer/tree/master/framer) ✅
- [Animation Converter]中的多平台、工具动画器转换函数(https://github.com/MartinRGB/AndroidInterpolator_AE) ✅
- 支持读取外部 JSON 编辑动画(???)

**算法**

- [Rebound]的物理动画概念(https://github.com/facebook/rebound) & Android DynamicAnimation ✅
- Android 原生的 查找表插值器（LookupTable Interpolator） + RK4 弹性解算器 + DHO 弹性解算器 ✅
- [Flutter Physics]的物理模拟(https://api.flutter.dev/flutter/physics/physics-library.html) & [UIKit Dyanmic]的物理模拟(https://developer.apple.com/documentation/uikit/animation_and_haptics/uikit_dynamics)
- 动量传递与保存（通过状态机实现）

**高级动画**

- 叠加动画(Addtive animation，将多个动画同时影响到一个对象)
- 链式动画(Chained animation,动画一个接一个开始)
- 视差效果(Parallax animation,相同动画触发，不同动画曲线、时间、延迟)
- 序列动画(Sequencing animation，动画逐次开始)

**交互动画**

- 支持手势驱动的动画，动画可中断，可在动画过程中重新交互(参考 iOS `CADisplayLink` 或 Rebound 的 `SetEndValue`) ✅
- 内置封装一个手势动画器，提供手势速度自动保留，以便手势交互时动画体验更物理、更流畅。（完成一半）
- 提供易用的动画监听，在动画监听中控制多个对象元素 ✅

**性能**

- 所有动画最终被转换为 Android 框架原生的 DyanmicAniamtion 和 TimingInterpolator，可调试完解除依赖然后使用原生写法 ✅
- 可考虑将动画数据转换为预存数组，以便节省实时计算开销
- 硬件加速 ✅

**设计组建**

- Scrollview|Scroller|PageViewer 组件跟案例，提供更好的 Overscroll 和 Fling 效果
- Drag | 拖拽组件跟案例，提供更符合物理直觉的甩手感
- Button 组件和案例
- Transition 组件和案例(考虑如何在不同 Activity 中维护一个元素的属性以便转场)
- Scroll-selector 滑动选择器组件和案例（类似 iOS 的日期选择器，提供平缓的衰减且定位的滚动体验）
- Swipe to delete 滑动删除组件和案例（更自然的滑动删除效果）

**开发工具**

- 提供 GLSL 异步绘制的图表，展示动画曲线本身 ✅
- 提供 GLSL 异步绘制的图表，展示实时属性变化曲线
- 提供数据与 View 属性绑定的控制 UI，实时切换、修改动画 ✅（目前仍有些许 bugs)

**实用工具**

- AE 插件，通过赋予关键帧表达式模拟上述曲线 ✅（目前仅为脚本，后面提供 Extension 级插件，增加 UI)

## 协议 

采用了 Apache 许可协议，[详细](https://github.com/MartinRGB/Animer/blob/master/LICENSE)
