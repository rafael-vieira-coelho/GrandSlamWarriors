package com.grandslamwarriors.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class US_Enemy1 extends Enemy {

    private float bobTime = 0f;
    private float dirLockTimer = 0f;
    private float jumpTimer = 0f;
    private float jumpCooldown = 0f;
    private boolean isJumping = false;
    private float jumpVelocity = 0f;
    private static final float GRAVITY = -600f;
    private static final float JUMP_SPEED = 0f;

    public US_Enemy1(float x, float y, Texture texture) {
        super(x, y, texture);
        if (texture != null) {
            this.regions = TextureRegion.split(texture, texture.getWidth() / 6, texture.getHeight())[0];
        }

        health = 4;
        setBoundsSize(38f, 38f);
        setDrawSize(100f, 160f);
        setDrawOffset(-32f, -8f);
        setPatrolRangeSymmetric(120f);
        animationSpeed = 6f;

        jumpCooldown = MathUtils.random(1f, 3f);
    }

    @Override
    public void update(float delta) {
        if (dead) return;

        animTime += delta;
        bobTime += delta;
        dirLockTimer -= delta;

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
                if (isPlayerWithinX(100f) || MathUtils.random() < 0.15f) {
                    isJumping = true;
                    jumpVelocity = JUMP_SPEED + MathUtils.random(-30f, 30f);
                    vel.x = (movingRight ? 1 : -1) * (10f + MathUtils.random(5f, 15f));
                } else {
                    jumpCooldown = MathUtils.random(0.5f, 2f);
                }
            }
        }

        if (!isJumping) {
            float baseSpeed = 26f * difficultyFactor;
            float extra = 8f * MathUtils.sin(bobTime * 2f) * difficultyFactor;
            float speed = baseSpeed + extra;
            float oldVelX = vel.x;
            patrol(speed, delta);
            if (Math.signum(vel.x) != Math.signum(oldVelX) && dirLockTimer <= 0f) {
                dirLockTimer = 0.2f;
            } else if (dirLockTimer > 0f) {
                vel.x = oldVelX;
            }
            movingRight = vel.x >= 0f;
        }

        float bobOffset = MathUtils.sin(bobTime * 6f) * 3f;
        pos.y += bobOffset;
        updateBounds();
        pos.y -= bobOffset;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (dead || regions == null || regions.length == 0) return;
        int usableFrames = Math.min(4, regions.length);
        int frame = (int)(animTime * 1.5f) % usableFrames;
        TextureRegion region = regions[frame];
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
        return false;
    }
}
