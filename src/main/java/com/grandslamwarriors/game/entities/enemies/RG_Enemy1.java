package com.grandslamwarriors.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;

public class RG_Enemy1 extends Enemy {

    public RG_Enemy1(float x, float y, Texture texture) {
        super(x, y, texture);
        if (texture != null) {
            // Agora com 6 imagens horizontais
            this.regions = com.badlogic.gdx.graphics.g2d.TextureRegion.split(texture, texture.getWidth() / 6, texture.getHeight())[0];
        }

        health = 1;
        // Mais comprido e mais baixo
        setBoundsSize(70f, 35f);
        setDrawSize(80f, 60f);
        setDrawOffset(-20f, -2f);
        setPatrolRangeSymmetric(100f);
    }

    @Override
    public void update(float delta) {
        if (dead) return;

        if (isPlayerWithinX(170f)) {
            chasePlayerX(50f, delta);
        } else {
            patrol(26f, delta);
        }

        updateBounds();
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}
