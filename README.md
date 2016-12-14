# https---github.com-BigArmy-GameLearning
game
Overall I find your code quite good as it stands. :) You've got sensible classes with sane, short methods. However, there were a few points that you could improve. Let's look at your solution class by class:

Pong class:

When you turn on strict enough compiler warnings in your IDE, you'll notice it warns about the fields WIDTH and HEIGHT hiding other fields. This is indeed true: public static final int WIDTH and HEIGHT are defined in the ImageObserver interface high up in the inheritance hierarchy of your class. You'll notice you get no errors even if you delete the line where you define those constants. You should come up with unique names for those variables, perhaps something like PLAYING_AREA_WIDTH. That'd also be more descriptive than just plain "WIDTH", which could be the width of the window, or that of the playing area if it wasn't as big as the window, but as of now nobody can know it for sure without inspecting the code more closely and trying the program.

Constructors should also be used just for light weight initialization stuff, but here it starts the entire game by initializing the PongPanel class. This is as much a fault of the PongPanel class, though. I'd add a start() method into both and change the main method of the Pong class to say "Pong game = new Pong(); game.start();". Ignoring new instances of classes, like the main method currently does, is quite confusing. Did the developer just forget to assign it into a variable? What is supposed to happen? game.start() would make it explicit.

PongPanel class:

Here my attention first turned to the actionPerformed, keyPressed, keyReleased and keyTyped methods, as they are all missing an @Override annotation. It's really recommendable to use one, because then you know right away that the method signature is enforced elsewhere. Interfaces are more forgiving with this, as you always get a warning if you lack a required method, but suppose you wanted to override a method that already has an implementation in a super class. Then, without an @Override annotation, someone might change the signature of the method and everything would appear to be just fine, but something would still break somewhere when the subclass's implementation wouldn't be used anymore.

My second observation was the int parameter of the getPlayer, increaseScore and getScore methods. In Pong you aren't likely to have Integer.MAX_VALUE amount of players, or even more than two, so I find an enum would be pretty good here. Init it like enum PlayerId {ONE, TWO}; next to the class fields, and change the method signatures from something like getPlayer(int playerNo) to getPlayer(PlayerId player).

This would also make the increaseScore method more readable, as currently increaseScore(2) looks like you'd increase the score by two points, whereas actually you are increasing the score for player two. Contrast that with increaseScore(PlayerId.TWO), or perhaps with even renaming the method: increaseScoreForPlayer(PlayerId.TWO). Remember, bits and bytes aren't expensive to store, you can use as many of them as is required to get clear and self explaining method names. :)

At the end of the class there's the paintComponent method that correctly does have the @Override annotation. The problem with the method is that it asks the ball and the players for them to paint themselves. This is against the principle of separating the business logic and the presentation logic. The ball and the rackets should not need to know how they are rendered, the user interface should take care of that. The logic of the game stays the same no matter how fancy or crude the game actually looks, and this should be reflected in the design so that the classes with the logic should not change when the appearance of the game changes. I won't go into this with any more detail, but you should think this through and read about it.

Ball class:

In this class the update method looks rather confusing with all the if-statements and the math in their conditions. I'd create methods of the conditions: instead of writing else if (y < 0 || y > game.getHeight() - HEIGHT - 29), I'd write else if (hasHitTopOrBottom()) and then define method like this:

private boolean hasHitTopOrBottom() {
    return y < 0 || y > game.getHeight() - HEIGHT - 29;
}
Do that for all the conditions and now, when you read the if-statement, you do not need to stop for a second to immediately understand what's happening in which branch.

Also note that the first two branches of the if-statement, the ones that check the ball hitting the left or right side of the playing area, have two lines of duplicate code, which you should refactor into a new method named, say, resetToMiddle().

After that if - else if -set the game checks whether either player has won. I find this should happen in a separate method, say, checkVictoryConditions(). Now it's there as if it was comparable to the action of checking if the ball hit the walls. The final method call in update(),  checkCollision(), also has a misleading name, as collisions with walls have already been checked and now it's time to only check collisions with the rackets. Actually, your update() method should probably just look like this:

public void update() {
    updateLocation();
    checkCollisionWithSides();
    checkVictoryConditions();
    checkCollisionWithRackets();
}
Also, this class fails to reset the score when the game ends, so after either player wins, the game gets stuck showing the message box on every tick of the timer, even if you try to dismiss it. With this I also recommend you start using curly braces with if-statements, because then it's explicit where the branch block starts and ends. Currently someone who didn't use an IDE with auto formatting might break the code by trying to add the score resets within the if-statements that did not have curly braces.

Racket class:

This class has the same issue as the Ball class: the update() method looks confusing. Turning the conditions into methods and giving them descriptive names will help a lot.

Also, giving the Racket the entire Pong object in the constructor looks suspicious. A Racket implemented by a malicious player could, for example, do game.getPanel().increaseScore(1) calls every time its update() method was called. Just pass the racket its maximum and minimum height and you also avoid doing those "game.getHeight() - HEIGHT - 29" calculations in this class. All in all this would then add two parameters into the constructor and remove one, and externalize some calculations into the PongPanel class that creates those Rackets. Also, isn't it the Panel's responsibility to know the magic number "29", rather than the Racket's? You should also introduce new constants with descriptive names for magic numbers like that.
