# CS175 Homework 7: Camera
CS175_Homework7

Author: HUANG Siyuan (519030910095)

## Overview

Features:

+ *Click the shutter to take a picture; Hold the shutter to record a video.*

  I use a combination of ``onClickListener``, ``onLongClickListener`` and ``onTouchListener`` to realize this.

+ *A 128 x 128 thumbnail preview of the latest taken image/ video, with the filename and resolution displayed.*

  For the video, I use ``MediaMetadataRetriever().frameAtTime`` to capture a frame from the video as the thumbnail.

+ *Name the media file with the input box on the top.*

+ *Switch between front & back camera.*

## Demo

The demo is run in the AVM, 'cuz I don't have an available Android phone by my hand.

<p align="center">
<img src="demo/demo.gif" alt="img" style="zoom:33%;" />



