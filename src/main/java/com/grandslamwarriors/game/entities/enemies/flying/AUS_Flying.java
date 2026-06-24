package com.grandslamwarriors.game.entities.enemies.flying;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AUS_Flying extends FlyingEnemy {

    public AUS_Flying(float x, float y, Texture texture, Texture fireTexture) {
        super(x, y, texture, fireTexture);
        health = 1;
        setBoundsSize(60f, 60f);
        setDrawSize(90f, 90f);
        setDrawOffset(-20f, -20f);
        setPatrolRangeSymmetric(200f);
        animationSpeed = 5f;

        // Sobrescreve a textura para 6 quadros horizontais
        fillTexture();
    }

    @Override
    protected void fillTexture() {
        if (texture == null) return;

        // Divide a textura em 6 colunas e 1 linha
        TextureRegion[][] tmp = TextureRegion.split(
            texture,
            texture.getWidth() / 6,
            texture.getHeight()
        );
        regions = tmp[0]; // pega a primeira linha com 6 frames
    }
}
