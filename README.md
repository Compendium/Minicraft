# Minicraft
## By Notch (http://notch.tumblr.com/)

For the Ludum Dare 22 competition!

This is the originally released official source code, unofficially hosted on GitHub. If you want to play around with it,
please fork this one! Pull requests accepted.

## Android Port
## by Rich Jones (http://gun.io)

I'm going to try to spend the weekend porting this game to Android. This will be done on the 'android' branch of this
git repository. Let's see how it goes (and no, I won't be screencasting it :( )


## Playable Android Port
## by Chris Lott (http://www.chrislott.net)

I forked this from Rich Jones.  Made it playable.  I included an icon found here: http://kaishinchan.deviantart.com/art/Minecraft-Icon-187560800 

## Changes to Android Port
### by Sven Reul (Twitter: @hefferniceday)

Based on the fork from Chris Lott, I managed to implement the following extras:

* Replaced text buttons for movement by semi-transparent arrows
* Sound is working
* Game thread is stopped when game is closed
* Different display sizes are supported (tested with resolution of 800x480 on SGS2 and 320x240 on Vodafone845)
* Game is started full screen (without title bar)

But still more things left to do:

* Multi-Touch (it is currently not possible to move AND interact/attack simultaneously)
* Save/Load game state
* "Loading screen" (it takes some time for the app to load/start, user should be notified about this)
* Optimizations for Android (Game is unplayable slow on low-end devices like Vodafone845)
* Put APK into Android market, if Notch is OK with it
* ... (ideas?)

