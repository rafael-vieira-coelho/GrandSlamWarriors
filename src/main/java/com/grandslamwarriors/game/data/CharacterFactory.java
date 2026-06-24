package com.grandslamwarriors.game.data;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.grandslamwarriors.game.entities.GameCharacter;

public class CharacterFactory {

    private final AssetManager assetManager;
    private final CharacterRepository repository;

    public CharacterFactory(AssetManager assetManager, CharacterRepository repository) {
        this.assetManager = assetManager;
        this.repository = repository;
    }

    public GameCharacter create(String id) {
        CharacterConfig cfg = repository.getById(id);
        if (cfg == null) return null;

        // Carrega como Pixmap para processar transparência via código
        com.badlogic.gdx.graphics.Pixmap raw = new com.badlogic.gdx.graphics.Pixmap(com.badlogic.gdx.Gdx.files.internal(cfg.texturePath));
        com.badlogic.gdx.graphics.Pixmap processed = makeTransparent(raw);

        Texture sheet = new Texture(processed);
        sheet.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        int frameW = sheet.getWidth() / 4;
        int frameH = sheet.getHeight();

        // Padding lateral ainda mais agressivo para limpar 100% dos resíduos vizinhos
        int padSide = 65;
        int padTop = 10;
        TextureRegion frame0 = new TextureRegion(sheet, padSide, padTop, frameW - padSide * 2, frameH - padTop);
        TextureRegion frame1 = new TextureRegion(sheet, frameW + padSide, padTop, frameW - padSide * 2, frameH - padTop);
        TextureRegion frame2 = new TextureRegion(sheet, frameW * 2 + padSide, padTop, frameW - padSide * 2, frameH - padTop);
        TextureRegion select = new TextureRegion(sheet, frameW * 3 + padSide, padTop, frameW - padSide * 2, frameH - padTop);


        raw.dispose();
        processed.dispose();

        return new GameCharacter(cfg, frame0, frame1, frame2, select);
    }

    private com.badlogic.gdx.graphics.Pixmap makeTransparent(com.badlogic.gdx.graphics.Pixmap source) {
        com.badlogic.gdx.graphics.Pixmap result = new com.badlogic.gdx.graphics.Pixmap(source.getWidth(), source.getHeight(), com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int color = source.getPixel(x, y);
                int r = (color >>> 24) & 0xff;
                int g = (color >>> 16) & 0xff;
                int b = (color >>> 8) & 0xff;

                // Filtro para branco e cinza claro (checkerboard fake)
                if (r > 200 && g > 200 && b > 200) {
                    result.drawPixel(x, y, 0x00000000);
                } else {
                    result.drawPixel(x, y, color);
                }
            }
        }
        return result;
    }
}
