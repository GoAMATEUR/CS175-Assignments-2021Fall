# CS175_Homework6
CS175_Homework6: Multimedia Viewer. 

Author: HUANG Siyuan (519030910095)

## Overview

Here's what I've done so far.

**Image Viewer**:

+ *Swipe left or right to switch pictures*

  The images has a template ``image_template``, and are put into a ``ViewPager``.

+ *load online images and gifs*

  I use ``Glide`` to load online images and cache them.

**Video Viewer**:

+ *Basic functions including start/pause and replay*

+ *A customized seek bar, with a play/pause button, a replay button, and a seek bar with time indication*

  For time indication, I create another thread to update the current playing position every other second, if the video is playing.

+ *Slide the seek bar to jump to specific location of the video*

  ``OnSeekBarChangeListener`` is used to listen seek bar change from user, and relocate the playing position.

I'm still working on the optional tasks.

## DEMO

Image viewer:

<p align="center">
<img src="demo/image_demo.gif" alt="img" style="zoom:33%;" />

Video viewer:

<p align="center">
<img src="demo/v_demo.gif" alt="img" style="zoom:33%;" />
