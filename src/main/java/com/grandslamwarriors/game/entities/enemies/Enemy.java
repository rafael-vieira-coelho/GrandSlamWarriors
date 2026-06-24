package com.grandslamwarriors.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.grandslamwarriors.game.entities.Player;

public abstract class Enemy {

    protected float difficultyFactor = 1f;

    protected Vector2 pos;
    protected Vector2 vel;
    protected Rectangle bounds;

    protected Texture texture;
    protected TextureRegion[] regions;
    protected float animTime;
    protected boolean dead;
    protected int health;

    protected Player playerRef;

    protected float spawnX;
    protected float spawnY;

    protected float patrolMinX;
    protected float patrolMaxX;

    protected boolean movingRight = true;

    protected float drawWidth;
    protected float drawHeight;

    protected float drawOffsetX;
    protected float drawOffsetY;

    protected Array<Rectangle> levelSolids;

    // Velocidade da animação (quadros por segundo)
    protected float animationSpeed = 8f;

    public Enemy(float x, float y, Texture texture) {
        this.texture = texture;
        if (texture != null) {
            // Divide a textura em 6 frames por padrão (pode ser sobrescrito)
            int frameWidth = texture.getWidth() / 6;
            int frameHeight = texture.getHeight();
            this.regions = TextureRegion.split(texture, frameWidth, frameHeight)[0];
        }
        this.animTime = 0f;

        this.pos = new Vector2(x, y);
        this.vel = new Vector2();

        this.bounds = new Rectangle(x, y, 32f, 32f);

        this.spawnX = x;
        this.spawnY = y;

        this.patrolMinX = x - 100f;
        this.patrolMaxX = x + 100f;

        this.drawWidth = 32f;
        this.drawHeight = 32f;
        this.drawOffsetX = 0f;
        this.drawOffsetY = 0f;

        this.health = 1;
        this.dead = false;
    }

    public abstract void update(float delta);

    public void draw(SpriteBatch batch) {
        if (dead || (texture == null && regions == null)) return;

        TextureRegion region = null;
        if (regions != null && regions.length > 0) {
            int frame = (int)(animTime * animationSpeed) % regions.length;
            region = regions[frame];
        }

        if (region != null) {
            drawRegion(batch, region);
        } else if (texture != null) {
            drawTexture(batch, texture);
        }
    }

    protected void drawRegion(SpriteBatch batch, TextureRegion region) {
        float drawX = pos.x + drawOffsetX;
        float drawY = pos.y + drawOffsetY;

        if (movingRight) {
            batch.draw(region, drawX, drawY, drawWidth, drawHeight);
        } else {
            batch.draw(region, drawX + drawWidth, drawY, -drawWidth, drawHeight);
        }
    }

    protected void drawTexture(SpriteBatch batch, Texture tex) {
        if (tex == null || dead) return;

        float drawX = pos.x + drawOffsetX;
        float drawY = pos.y + drawOffsetY;

        if (movingRight) {
            batch.draw(tex, drawX, drawY, drawWidth, drawHeight);
        } else {
            batch.draw(tex, drawX + drawWidth, drawY, -drawWidth, drawHeight);
        }
    }

    protected void setBoundsSize(float width, float height) {
        bounds.width = width;
        bounds.height = height;
    }

    protected void setDrawSize(float width, float height) {
        this.drawWidth = width;
        this.drawHeight = height;
    }

    protected void setDrawOffset(float offsetX, float offsetY) {
        this.drawOffsetX = offsetX;
        this.drawOffsetY = offsetY;
    }

    protected void setPatrolRange(float minX, float maxX) {
        this.patrolMinX = minX;
        this.patrolMaxX = maxX;
    }

    protected void setPatrolRangeSymmetric(float distance) {
        this.patrolMinX = spawnX - distance;
        this.patrolMaxX = spawnX + distance;
    }

    protected void patrol(float speed, float delta) {
        animTime += delta;

        if (!hasGroundAhead()) {
            movingRight = !movingRight;
        }

        vel.x = movingRight ? speed : -speed;
        pos.x += vel.x * delta;
        clampToPatrolArea();
        updateBounds();
    }

    protected void chasePlayerX(float speed, float delta) {
        animTime += delta;
        if (playerRef == null) {
            patrol(speed * 0.5f, delta);
            return;
        }

        float dx = playerRef.pos.x - pos.x;

        if (!hasGroundAhead()) {
            movingRight = !movingRight;
            patrol(speed * 0.5f, delta);
            return;
        }

        if (dx > 0f) {
            vel.x = speed;
            movingRight = true;
        } else {
            vel.x = -speed;
            movingRight = false;
        }

        pos.x += vel.x * delta;
        clampToPatrolArea();
        updateBounds();
    }

    protected boolean hasGroundAhead() {
        if (levelSolids == null || levelSolids.size == 0) return true;
        if (this instanceof com.grandslamwarriors.game.entities.enemies.flying.FlyingEnemy) return true;

        float lookAheadDist = movingRight ? bounds.width + 5f : -15f;
        float checkX = pos.x + lookAheadDist;
        float checkY = pos.y - 10f;

        for (Rectangle r : levelSolids) {
            if (r.contains(checkX, checkY)) return true;
        }
        return false;
    }

    public void setLevelSolids(Array<Rectangle> solids) {
        this.levelSolids = solids;
    }

    protected boolean isPlayerWithinX(float range) {
        if (playerRef == null) return false;
        return Math.abs(playerRef.pos.x - pos.x) <= range;
    }

    protected void clampToPatrolArea() {
        if (pos.x < patrolMinX) {
            pos.x = patrolMinX;
            movingRight = true;
            vel.x = 0f;
        }

        if (pos.x > patrolMaxX) {
            pos.x = patrolMaxX;
            movingRight = false;
            vel.x = 0f;
        }
    }

    protected void updateBounds() {
        bounds.setPosition(pos.x, pos.y);
    }

    public void setPlayer(Player player) {
        this.playerRef = player;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getPosition() {
        return pos;
    }

    public boolean isDead() {
        return dead;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(float amount) {
        if (dead) return;
        health -= Math.max(1, (int)amount);
        if (health <= 0) {
            health = 0;
            dead = true;
        }
    }

    public void damage(int amount) {
        if (dead) return;
        health -= amount;
        if (health <= 0) {
            health = 0;
            dead = true;
        }
    }

    public void kill() {
        dead = true;
        health = 0;
    }

    public boolean isSolid() {
        return false;
    }

    public boolean dealsContinuousDamage() {
        return false;
    }

    public void setDifficultyFactor(float factor) {
        this.difficultyFactor = factor;
    }
}
