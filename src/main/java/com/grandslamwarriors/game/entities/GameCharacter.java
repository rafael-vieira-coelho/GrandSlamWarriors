package com.grandslamwarriors.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.grandslamwarriors.game.data.CharacterConfig;

public final class GameCharacter {

    private final CharacterConfig config;

    private final TextureRegion[] animFrames;  // frames 0, 1, 2 — animação
    private final TextureRegion selectFrame;   // frame 3 — tela de seleção

    private static final float FRAME_DURATION = 0.20f; // Aumentado de 0.12f para 0.20f (mais lento)

    public GameCharacter(
        CharacterConfig config,
        TextureRegion frame0,
        TextureRegion frame1,
        TextureRegion frame2,
        TextureRegion selectFrame
    ) {
        this.config = config;
        this.animFrames = new TextureRegion[]{ frame0, frame1, frame2 };
        this.selectFrame = selectFrame;
    }

    public TextureRegion getAnimFrame(float animTime) {
        int index = (int)(animTime / FRAME_DURATION) % animFrames.length;
        return animFrames[index];
    }

    public TextureRegion getSelectFrame() {
        return selectFrame;
    }

    /** Chamado durante o gameplay — usa animTime para animar. */
    public void draw(SpriteBatch batch, float x, float y,
                     float width, float height, boolean flipX, float animTime) {
        TextureRegion frame = getAnimFrame(animTime);
        drawRegion(batch, frame, x, y, width, height, flipX);
    }

    /** Chamado na tela de seleção — usa o frame estático (4ª imagem). */
    public void drawSelect(SpriteBatch batch, float x, float y,
                           float width, float height, boolean flipX) {
        drawRegion(batch, selectFrame, x, y, width, height, flipX);
    }

    private void drawRegion(SpriteBatch batch, TextureRegion region,
                            float x, float y, float width, float height, boolean flipX) {
        if (region == null) return;
        if (flipX) {
            batch.draw(region, x + width, y, -width, height);
        } else {
            batch.draw(region, x, y, width, height);
        }
    }

    public CharacterConfig getConfig()        { return config; }
    public float getMoveSpeedMultiplier()     { return 1f + config.speedBonus; }
    public float getJumpMultiplier()          { return 1f + config.jumpBonus; }
    public float getStabilityMultiplier()     { return 1f + config.stabilityBonus; }
    public float getAttackCooldown()          { return config.attackCooldown; }
    public float getAttackDamage()            { return config.attackDamage; }
}
