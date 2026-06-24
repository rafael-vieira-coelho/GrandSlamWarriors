package com.grandslamwarriors.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class AUS_Enemy1 extends Enemy {

    private float jumpTimer = 0f;
    private float jumpCooldown = 0f;
    private boolean isJumping = false;
    private float jumpVelocity = 0f;
    private static final float GRAVITY = -600f;
    private static final float JUMP_SPEED = 300f;

    public AUS_Enemy1(float x, float y, Texture texture) {
        super(x, y, texture);

        if (texture != null && texture.getWidth() > texture.getHeight()) {
            int frameWidth = texture.getWidth() / 6;
            int frameHeight = texture.getHeight();
            TextureRegion[][] tmp = TextureRegion.split(texture, frameWidth, frameHeight);
            this.regions = tmp[0];

            // Corrige o "bleeding" (pedaços do próximo frame aparecendo)
            // Reduz a largura de cada região em 1 pixel para evitar pegar a borda vizinha
            for (TextureRegion region : this.regions) {
                region.setRegionWidth(region.getRegionWidth() - 1);
            }
        }

        health = 1;
        setBoundsSize(44f, 34f);
        setDrawSize(72f, 48f);
        setDrawOffset(-14f, -1f);
        setPatrolRangeSymmetric(64f);
        animationSpeed = 6f;

        jumpCooldown = MathUtils.random(1f, 3f);
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
                jumpCooldown = 0.3f / difficultyFactor;//MathUtils.random(0.5f, 2.5f);
            }
        } else {
            if (jumpCooldown > 0) {
                jumpCooldown -= delta;
            } else {
                if (isPlayerWithinX(120f) || MathUtils.random() < 0.3f) {
                    isJumping = true;
                    jumpVelocity = JUMP_SPEED + difficultyFactor;//MathUtils.random(-50f, 50f);
                    vel.x = (movingRight ? 1 : -1) * (20f + MathUtils.random(10f, 30f));
                } else {
                    jumpCooldown = MathUtils.random(0.5f, 1.5f);
                }
            }
        }

        if (!isJumping) {
            if (isPlayerWithinX(96f)) {
                chasePlayerX(32f * difficultyFactor, delta);
            } else {
                patrol(18f * difficultyFactor, delta);
            }
        }

        updateBounds();
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}
