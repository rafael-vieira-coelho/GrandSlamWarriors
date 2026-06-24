package com.grandslamwarriors.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class RG_Enemy2 extends Enemy {

    private boolean spawned = false;
    private float jumpTimer = 0f;
    private float jumpCooldown = 0f;
    private boolean isJumping = false;
    private float jumpVelocity = 0f;
    private static final float GRAVITY = -600f;
    private static final float JUMP_SPEED = 250f;

    private static final float SPAWN_FPS = 10f;
    private static final float IDLE_FPS = 4f;

    public RG_Enemy2(float x, float y, Texture texture) {
        super(x, y, texture);

        if (texture != null) {
            this.regions = TextureRegion.split(texture, texture.getWidth() / 6, texture.getHeight())[0];
        }

        health = 3;
        setBoundsSize(54f, 80f);
        setDrawSize(54f, 80f);
        setDrawOffset(-40f, -2f);
        setPatrolRangeSymmetric(110f);
        animationSpeed = 4f;

        jumpCooldown = MathUtils.random(2f, 4f);
    }

    @Override
    public void update(float delta) {
        if (dead) return;

        animTime += delta;

        if (!spawned) {
            float spawnDuration = regions.length / SPAWN_FPS;
            if (animTime >= spawnDuration) {
                spawned = true;
                animTime = 0f;
            }
            updateBounds();
            return;
        }

        if (isJumping) {
            jumpVelocity += GRAVITY * delta;
            pos.y += jumpVelocity * delta;
            if (pos.y <= spawnY) {
                pos.y = spawnY;
                isJumping = false;
                jumpVelocity = 0f;
                jumpCooldown = MathUtils.random(1.5f, 4f);
            }
        } else {
            if (jumpCooldown > 0) {
                jumpCooldown -= delta;
            } else {
                if (isPlayerWithinX(150f) || MathUtils.random() < 0.2f) {
                    isJumping = true;
                    jumpVelocity = JUMP_SPEED + MathUtils.random(-30f, 30f);
                    vel.x = (movingRight ? 1 : -1) * (15f + MathUtils.random(5f, 20f));
                } else {
                    jumpCooldown = MathUtils.random(1f, 3f);
                }
            }
        }

        if (!isJumping) {
            if (isPlayerWithinX(210f)) {
                chasePlayerX(34f * difficultyFactor, delta);
            } else {
                patrol(18f * difficultyFactor, delta);
            }
        }

        updateBounds();
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (dead || regions == null || regions.length == 0) return;
        TextureRegion region;
        int frameIndex;
        if (!spawned) {
            frameIndex = Math.min((int)(animTime * SPAWN_FPS), regions.length - 1);
        } else {
            int[] idleFrames = {3, 4, 5};
            int idleIndex = (int)(animTime * IDLE_FPS) % idleFrames.length;
            frameIndex = idleFrames[idleIndex];
        }
        region = regions[frameIndex];
        float drawX = pos.x + drawOffsetX;
        float drawY = pos.y + drawOffsetY;
        if (movingRight) {
            batch.draw(region, drawX, drawY, drawWidth, drawHeight);
        } else {
            batch.draw(region, drawX + drawWidth, drawY, -drawWidth, drawHeight);
        }
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}
