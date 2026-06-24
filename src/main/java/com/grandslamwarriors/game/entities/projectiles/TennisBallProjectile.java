package com.grandslamwarriors.game.entities.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class TennisBallProjectile {

    public Vector2 pos;
    public Vector2 vel;
    public Rectangle bounds;
    public boolean active;

    private final Texture texture;

    private static final float WIDTH = 10f;
    private static final float HEIGHT = 10f;
    private static final float SPEED_X = 420f;
    private static final float MAX_DISTANCE = 900f;

    private final float startX;

    public TennisBallProjectile(Texture texture, float x, float y, float direction) {
        this.texture = texture;

        this.pos = new Vector2(x, y);
        this.vel = new Vector2(SPEED_X * direction, 0f);

        this.bounds = new Rectangle(x, y, WIDTH, HEIGHT);
        this.active = true;

        this.startX = x;
    }

    public void update(float delta) {
        if (!active) return;

        pos.x += vel.x * delta;
        pos.y += vel.y * delta;

        bounds.setPosition(pos.x, pos.y);

        if (Math.abs(pos.x - startX) > MAX_DISTANCE) {
            active = false;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!active || texture == null) return;
        batch.draw(texture, pos.x, pos.y, WIDTH, HEIGHT);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }
}
