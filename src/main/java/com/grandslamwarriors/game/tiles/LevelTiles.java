package com.grandslamwarriors.game.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LevelTiles {
    public static final int TILE_SIZE = 128;

    public TextureRegion background;
    public TextureRegion[] tileVariants;
    public TextureRegion specialTile;

    private Texture bgTexture;
    private Texture tilesTexture;

    public LevelTiles(String backgroundPath, String tilesPath) {
        bgTexture = new Texture(Gdx.files.internal(backgroundPath));
        tilesTexture = new Texture(Gdx.files.internal(tilesPath));

        background = new TextureRegion(bgTexture);

        TextureRegion[][] split = TextureRegion.split(tilesTexture, TILE_SIZE, TILE_SIZE);
        int cols = split[0].length;

        tileVariants = new TextureRegion[cols];
        for (int i = 0; i < cols; i++) {
            tileVariants[i] = split[0][i];
        }

        if (split.length > 1 && split[1].length > 0) {
            specialTile = split[1][0];
        } else {
            specialTile = tileVariants[0];
        }
    }

    public void dispose() {
        bgTexture.dispose();
        tilesTexture.dispose();
    }
}
