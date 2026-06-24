package com.grandslamwarriors.game.renderers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.grandslamwarriors.game.tiles.TileType;

public class LevelRenderer {
    private final TextureRegion background;
    private final TextureRegion[] tileVariants;
    private final TextureRegion fireTile;
    private final TileType[][] map;
    private final int tileSize;
    private final Array<Rectangle> solidRects = new Array<>();
    private final Array<Rectangle> hazardRects = new Array<>();
    private final Array<Rectangle> slowRects = new Array<>();
    private final Array<Rectangle> slipperyRects = new Array<>();
    private final Array<Rectangle> spawnRects = new Array<>();

    private Texture backgroundTexture;
    private Texture tilesTexture;

    public LevelRenderer(TextureRegion background, TextureRegion[] tileVariants, TextureRegion fireTile,
                         TileType[][] map, int tileSize) {
        this.background = background;
        this.tileVariants = tileVariants;
        this.fireTile = fireTile;
        this.map = map;
        this.tileSize = tileSize;
        buildCollisionZones();
        this.backgroundTexture = null;
        this.tilesTexture = null;
    }

    public LevelRenderer(String backgroundPath, String tilesPath, TileType[][] map, int tileSize) {
        this.backgroundTexture = new Texture(backgroundPath);
        this.tilesTexture = new Texture(tilesPath);
        this.background = new TextureRegion(backgroundTexture);
        TextureRegion[][] split = TextureRegion.split(tilesTexture, tileSize, tileSize);
        this.tileVariants = (split.length > 0) ? split[0] : new TextureRegion[0];
        this.fireTile = (split.length > 1 && split[1].length > 0) ? split[1][0] : (tileVariants.length > 0 ? tileVariants[0] : null);
        this.map = map;
        this.tileSize = tileSize;
        buildCollisionZones();
    }

    private void buildCollisionZones() {
        int rows = map.length;
        int cols = map[0].length;
        solidRects.clear();
        hazardRects.clear();
        slowRects.clear();
        slipperyRects.clear();
        spawnRects.clear();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                TileType type = map[y][x];
                if (type == null || type == TileType.EMPTY) continue;

                float wx = x * tileSize;
                float wy = (rows - 1 - y) * tileSize;

                Rectangle rect = new Rectangle(wx, wy, tileSize, tileSize);
                if (type.solid) solidRects.add(rect);
                if (type.damageOverTime) hazardRects.add(rect);
                if (type.movementModifier < 0.99f) slowRects.add(rect);
                if (type.movementModifier > 1.01f) slipperyRects.add(rect);
                if (type.name().startsWith("SPAWN_")) spawnRects.add(rect);
            }
        }
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        float viewportWidth = camera.viewportWidth;
        float viewportHeight = camera.viewportHeight;
        float bgX = camera.position.x - viewportWidth / 2f;
        float bgY = camera.position.y - viewportHeight / 2f;

        if (background != null) {
            batch.draw(background, bgX, bgY, viewportWidth, viewportHeight);
        }

        int rows = map.length;
        int cols = map[0].length;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                TileType type = map[y][x];
                if (type == null || type == TileType.EMPTY) continue;

                TextureRegion region = getTileTexture(type);
                if (region != null) {
                    float drawX = x * tileSize;
                    float drawY = (rows - 1 - y) * tileSize;
                    batch.draw(region, drawX, drawY);
                }
            }
        }
    }

    public TextureRegion getPlatformTexture() {
        return getTileTexture(TileType.PLATFORM);
    }

    private TextureRegion getTileTexture(TileType type) {
        switch (type) {
            case GROUND:   return tileVariants.length > 0 ? tileVariants[0] : null;
            case PLATFORM: return tileVariants.length > 1 ? tileVariants[1] : (tileVariants.length > 0 ? tileVariants[0] : null);
            case WALL:     return tileVariants.length > 2 ? tileVariants[2] : (tileVariants.length > 0 ? tileVariants[0] : null);
            case FIRE:
            case SPIKES:
            case GAS:      return fireTile;
            case SLOW_CLAY:
            case SLIPPERY_GRASS:
                return tileVariants.length > 0 ? tileVariants[0] : null;
            default:       return null;
        }
    }

    public Array<Rectangle> getSolidRects() { return solidRects; }
    public Array<Rectangle> getHazardRects() { return hazardRects; }
    public Array<Rectangle> getSlowRects() { return slowRects; }
    public Array<Rectangle> getSlipperyRects() { return slipperyRects; }
    public Array<Rectangle> getSpawnRects() { return spawnRects; }

    public void dispose() {
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (tilesTexture != null) tilesTexture.dispose();
    }
}
