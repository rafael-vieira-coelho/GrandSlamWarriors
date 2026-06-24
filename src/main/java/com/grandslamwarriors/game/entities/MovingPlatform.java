package com.grandslamwarriors.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MovingPlatform {
    private Rectangle bounds;
    private Vector2 startPos;
    private Vector2 endPos;
    private Vector2 velocity;
    private float speed;
    private boolean movingToEnd = true;

    public MovingPlatform(float x1, float y1, float x2, float y2, float width, float height, float speed) {
        this.startPos = new Vector2(x1, y1);
        this.endPos = new Vector2(x2, y2);
        this.speed = speed;
        this.bounds = new Rectangle(x1, y1, width, height);
        this.velocity = new Vector2();
        updateVelocity();
    }

    private void updateVelocity() {
        Vector2 dir = new Vector2(endPos).sub(startPos);
        float len = dir.len();
        if (len == 0) return;
        dir.nor().scl(speed);
        velocity.set(dir);
    }

    public void update(float delta) {
        float step = speed * delta;
        if (movingToEnd) {
            float dx = endPos.x - bounds.x;
            float dy = endPos.y - bounds.y;
            float dist = (float) Math.sqrt(dx*dx + dy*dy);
            if (dist <= step) {
                bounds.x = endPos.x;
                bounds.y = endPos.y;
                movingToEnd = false;
                velocity.scl(-1);
            } else {
                bounds.x += velocity.x * delta;
                bounds.y += velocity.y * delta;
            }
        } else {
            float dx = startPos.x - bounds.x;
            float dy = startPos.y - bounds.y;
            float dist = (float) Math.sqrt(dx*dx + dy*dy);
            if (dist <= step) {
                bounds.x = startPos.x;
                bounds.y = startPos.y;
                movingToEnd = true;
                velocity.scl(-1);
            } else {
                bounds.x += velocity.x * delta;
                bounds.y += velocity.y * delta;
            }
        }
    }

    public Rectangle getBounds() { return bounds; }
    public float getVelocityX() { return velocity.x; }
    public float getVelocityY() { return velocity.y; }
}
