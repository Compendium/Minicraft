# Minicraft
## By Notch (http://notch.tumblr.com/)

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
### by Alexander Wiens (DerZaubererVonOz, Twitter: @BDSGHTJ, github.com/Compendium/)

* Replaced all that button monkey-business with a analog style pseudo joystick
* Working on adding more sounds, since the original lacks a bit in this aspect

### TODO
	* Multi-touch is more or less done, maybe add some more graphical feedback where the interface is and its status.
	* Fix 'key-repeat' so you can hold attack like on the pc and get continous attacking.
	* Saving/Loading game state, something lightweight on androids '/tmp/' -> not preserved on reboot, but easy to implement?
	* Loading screen
	* Optimizations generally and for android.
		-Huge memory wastes about 1mb per second collected by the GC
	* Make APK available somehow. (Is the market an option?, ask Notch and all co-authors / check licenses!)
