package com.grandslamwarriors.game.screens;

import com.badlogic.gdx.math.Rectangle;

public enum TouristPoint {
    SYDNEY_OPERA_HOUSE(720, 300, 60, 80),
    EIFFEL_TOWER(720, 320, 50, 100),
    BUCKINGHAM_PALACE(720, 280, 80, 90),
    STATUE_OF_LIBERTY(720, 310, 55, 110);

    public final Rectangle area;

    TouristPoint(float x, float y, float w, float h) {
        this.area = new Rectangle(x, y, w, h);
    }
}
