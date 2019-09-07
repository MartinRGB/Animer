# AndroidPhysicsEngine
for a better Android Experience

## Core concpet:

**Data**

- State machine concpet from [FramerJS](https://github.com/koenbok/Framer/tree/master/framer)
- Aniamtion converter from my [Animation Converter](https://github.com/MartinRGB/AndroidInterpolator_AE)
- Support external JSON to edit animation data

**Althogrim**

- Physics animation concept from [Rebound](https://github.com/facebook/rebound) & Android DynamicAnimation
- LookupTable Interpolation Animator + RK4 Solver + DHO Solver
- Physics simulation from [Flutter Physics](https://api.flutter.dev/flutter/physics/physics-library.html) & UIKit Dyanmic[https://developer.apple.com/documentation/uikit/animation_and_haptics/uikit_dynamics]
- Momentum

**Advanced Animation Setting**

- Addtive animation (compose mulitple animation)
- Chained animation (one by one)
- Parallax animation (same duration but differnt transition)
- Sequencing animation (same transition but different startDelay)

**User-Control**

- Gesture-Driven animation,you can interact with the animation even it is animating(Like iOS's `CADisplayLink` Or Rebound's `SetEndValue`)
- Package a gesture animator for interactive animation,attach gesture's velocity to animation system,make a flawess experience.
- Easy2used animation listener for controlling other element when the object is interacting or animating

**Performance**

- Use android framework native DyanmicAniamtion And TimingInterpolator
- Pre-save animation's data for less calculation

**Design Component**

- Scrollview|Scroller|PageViewer Component & Example
- Drag | DND Component & Example
- Button Component & Example
- Transition Component & Example(Maintain different element's property in state machine)
- Scroll-selector Component & Example(Scroll to fixed position)
- Swipe to delete Component & Example

**Dev Tools**

- Data-bind graph to modify and preview animation in application
- Data-bind selctor to change animation-type in application

**Utils**

- AE Plugin for converting curves & revealing codes

