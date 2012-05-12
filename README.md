# Minicraft #
### By Notch (http://notch.tumblr.com/)

For the Ludum Dare 22 competition!

This is the originally released official source code, unofficially hosted on GitHub. If you want to play around with it,
please fork this one! Pull requests accepted.

## Android Port
### by Rich Jones (http://gun.io)

I'm going to try to spend the weekend porting this game to Android. This will be done on the 'android' branch of this
git repository. Let's see how it goes (and no, I won't be screencasting it :( )


## Playable Android Port
### by Chris Lott (http://www.chrislott.net)

I forked this from Rich Jones.  Made it playable.  I included an icon found here: http://kaishinchan.deviantart.com/art/Minecraft-Icon-187560800 

## Changes to Android Port
### by Sven Reul (Twitter: @hefferniceday)

Based on the fork from Chris Lott, I managed to implement the following extras:

* Replaced text buttons for movement by semi-transparent arrows
* Sound is working
* Game thread is stopped when game is closed
* Different display sizes are supported (tested with resolution of 800x480 on SGS2 and 320x240 on Vodafone845)
* Game is started full screen (without title bar)

## Changes to the changes of the Android Port ;)
### by Alexander Wiens (DerZaubererVonOz, Twitter: [@BDSGHTJ](https://twitter.com/BDSGHTJ), [github.com/Compendium/](http://github.com/Compendium)

* Fixed build-setup on eclipse (ant didn't work for me)
* Replaced all that button monkey-business with a analog style pseudo joystick
* Working on adding more sounds/music, since the original lacks a bit in this aspect
* Added really fast saving and loading :D  (but need to test this on other devices, maybe it's just my phone?)
* Re-enabled frame-limiter, to conserve battery live on mobile devices.

### TODO
	* Fix the sometimes flaky controls
	* Make APK available somehow. (Is the market an option?, ask Notch and all co-authors / check licenses!)
	* Add a credits screen, with attribution.

# Attribution
##(in no specific order)
* Alexander Wiens (DerZaubererVonOz, [@BDSGHTJ](https://twitter.com/BDSGHTJ), [github.com/Compendium](http://github.com/Compendium))
* Chris Lott ([@ChrisLott](https://twitter.com/ChrisLott), [http://www.chrislott.net](http://www.chrislott.net))
* George Schneeloch ([GMail](mailto:bostonbusmap@gmail.com))
* Sven Reul ([@hefferniceday](https://twitter.com/hefferniceday))
* Rich Jones ([http://www.gun.io](http://www.gun.io))

* With special thanks to Markus 'Notch' Persson ([@notch](https://twitter.com/notch), [http://mojang.com](http://www.mojang.com)) for creating Minicraft

## Music
* [Dark Skies, by MaestroRage](http://www.newgrounds.com/audio/view.php?id=1714459&sub=70107)
* [Temple In the Storm, by BlazingDragon](http://www.newgrounds.com/audio/listen/165200)
* [timid girl, by vibe-newgrounds](http://www.newgrounds.com/audio/listen/217741)
