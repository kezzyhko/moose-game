package gametheory.assignment2.students2021;

import gametheory.assignment2.Player;

public class SergeySemushinCode implements Player {
    private static final String THE_EMAIL = "s.semushin@innopolis.university";

    @Override
    public void reset() {
    }

    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {
        return 0;
    }

    @Override
    public String getEmail() {
        return THE_EMAIL;
    }
}


