package com.grandslamwarriors.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class WIM_Enemy1 extends Enemy {

    private float jumpTimer = 0f;
    private float jumpCooldown = 0f;
    private boolean isJumping = false;
    private float jumpVelocity = 0f;
    private static final float GRAVITY = -600f;
    private static final float JUMP_SPEED = 280f;

    public WIM_Enemy1(float x, float y, Texture texture) {
        super(x, y, texture);
        if (texture != null) {
            this.regions = TextureRegion.split(texture, texture.getWidth() / 6, texture.getHeight())[0];
        }

        health = 3;
        setBoundsSize(60f, 50f);
        setDrawSize(60f, 50f);
        setDrawOffset(-30f, -2f);
        setPatrolRangeSymmetric(105f);
        animationSpeed = 4f;

        jumpCooldown = MathUtils.random(1.5f, 3.5f);
    }

    @Override
    public void update(float delta) {
        if (dead) return;

        animTime += delta;

        if (isJumping) {
            jumpVelocity += GRAVITY * delta;
            pos.y += jumpVelocity * delta;
            if (pos.y <= spawnY) {
                pos.y = spawnY;
                isJumping = false;
                jumpVelocity = 0f;
                jumpCooldown = MathUtils.random(1f, 3f);
            }
        } else {
            if (jumpCooldown > 0) {
                jumpCooldown -= delta;
            } else {
                if (isPlayerWithinX(160f) || MathUtils.random() < 0.2f) {
                    isJumping = true;
                    jumpVelocity = JUMP_SPEED + MathUtils.random(-40f, 40f);
                    vel.x = (movingRight ? 1 : -1) * (15f + MathUtils.random(5f, 20f));
                } else {
                    jumpCooldown = MathUtils.random(0.8f, 2.5f);
                }
            }
        }

        if (!isJumping) {
            if (isPlayerWithinX(200f)) {
                chasePlayerX(40f, delta);
            } else {
                patrol(20f, delta);
            }
        }

        updateBounds();
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}
