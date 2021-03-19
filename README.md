# moose-game

Tournament to test different strategies in a specific [game](#rules-of-the-game), that demonstrates "Tragedy of the Commons" concept.





## The competition

Originally, it was the solution to an assignment on Game Theory course in Innopolis University (Spring 2021, 3rd year).<br>
The original report with more info can be found [here](https://drive.google.com/file/d/1KQo8gaGQo72y2SikgQ3bGXgYxMxP9pHK).

As a part of the assignment, `CoopPlayer` strategy was submitted to the competition. It took 28th place out of 99.





## Rules of the game

In each game, there are two players.

There are three fields. Each of them start with the X value of 1.

There are multiple rounds in each game. On each round, player makes a move by choosing one of three fields.

If both players select the same field, both of them get payoff of zero and X value is reduced by 1.<br>

If players select different fields, then:
* these fields has their X value decreased by 1 (but it can not become negative);
* each player gets a payoff of `f(X) - f(0)`, where `X` is the X value of the field, and `f(X)` is defined in the code as `Tournament#vegetationAmount(int)`.

When noone selects some field, it inecreases its X value by 1.
