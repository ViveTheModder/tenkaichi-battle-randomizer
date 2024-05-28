# tenkaichi-randomizer
A random match generator for Budokai Tenkaichi 3.

![image](https://github.com/ViveTheModder/tenkaichi-randomizer/assets/93151014/00a5737f-2bca-4c32-8fc7-292b84318b32)

![image](https://github.com/ViveTheModder/tenkaichi-randomizer/assets/93151014/a5a1e445-9a4e-4103-b383-6afc765c985c)

Not to be confused with the [Random DP Team Generator](https://github.com/ViveTheModder/bt3-dp-team-gen) I made for the same game.

Unlike that one, I included character transformation restrictions and repetition prevention (although it could occur... very rarely).

Then again, I did make sure to include a [release](https://github.com/ViveTheModder/tenkaichi-randomizer/releases/tag/v1.0), thanks to me using Swing rather than JavaFX like last time.

My original plan was to also have the number of teammates for both teams be random, but it somehow caused problems with the UI.

As a result, I made the user specify them upon running the program.

![image](https://github.com/ViveTheModder/tenkaichi-randomizer/assets/93151014/5b2a46fb-3aac-4bcb-a059-40f02ee64dc8)

Otherwise, the program will still run just fine, even in a command-line interpreter, like so:
```java -jar tenkaichi-randomizer.jar 4 5```

![image](https://github.com/ViveTheModder/tenkaichi-randomizer/assets/93151014/210b18a2-7c67-49f2-8b64-8a064d765da8)

The following are pretty much randomly generated:
* Characters (obviously)
* Equipped Costumes
* Duel Time (60. 90, 180, 240, Infinite)
* COM Difficulty (Very Weak, Weak, Average, Strong, Very Strong) 
* Referee (Ox King, Videl, Supreme Kai, Shenron, Announcer 1-3)
