package com.grandslamwarriors.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.grandslamwarriors.game.GrandSlamWarriorsGame;
import com.grandslamwarriors.game.data.CharacterConfig;
import com.grandslamwarriors.game.data.CharacterFactory;
import com.grandslamwarriors.game.data.CharacterRepository;
import com.grandslamwarriors.game.data.GrandSlam;
import com.grandslamwarriors.game.data.SettingsManager;
import com.grandslamwarriors.game.entities.GameCharacter;

import java.util.HashMap;
import java.util.Map;

public class CharacterSelectScreen implements Screen {

    private final GrandSlamWarriorsGame game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final SpriteBatch batch;
    private final CharacterRepository repository;
    private final CharacterFactory factory;
    private final Array<CharacterConfig> characters; // lista filtrada
    private final Texture backgroundTexture;
    private final BitmapFont font;
    private final GlyphLayout layout;
    private final Array<Rectangle> buttonAreas;
    private final Map<String, GameCharacter> characterPreviews;
    private Texture whitePixel;
    private com.badlogic.gdx.audio.Sound selectionSound;
    private com.badlogic.gdx.audio.Music themeMusic;

    public CharacterSelectScreen(GrandSlamWarriorsGame game) {
        this.game = game;

        // Música tema
        if (SettingsManager.isSoundEnabled()) {
            try {
                themeMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/intro_music.wav"));
                themeMusic.setLooping(true);
                themeMusic.setVolume(0.5f);
                themeMusic.play();
            } catch (Exception e) {
                themeMusic = null;
            }
        }

        try {
            selectionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/menu_seleciona_personagem.wav"));
        } catch (Exception e) {
            selectionSound = null;
        }

        camera = new OrthographicCamera();
        viewport = new StretchViewport(
            GrandSlamWarriorsGame.V_WIDTH,
            GrandSlamWarriorsGame.V_HEIGHT,
            camera
        );
        batch = game.batch;
        repository = new CharacterRepository();
        factory = new CharacterFactory(game.assetManager, repository);

        // --- FILTRA personagens desbloqueados ---
        characters = new Array<>();
        characterPreviews = new HashMap<>();
        boolean agassiUnlocked = SettingsManager.isAgassiUnlocked();
        for (CharacterConfig c : repository.getAll()) {
            // Se for o Agassi e não estiver desbloqueado, pula
            if (c.id.equals("agassi") && !agassiUnlocked) {
                continue;
            }
            characters.add(c);
            characterPreviews.put(c.id, factory.create(c.id));
        }

        buttonAreas = new Array<>();

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        whitePixel = new Texture(pix);
        pix.dispose();

        Texture bg = null;
        try {
            bg = new Texture("ui/icone.png");
        } catch (Exception e) {
            bg = whitePixel;
        }
        backgroundTexture = bg;

        font = new BitmapFont();
        font.getData().setScale(1.4f);
        layout = new GlyphLayout();

        // Calcula posições dos botões (até 3 ou 4 personagens)
        int cols = characters.size;
        float buttonWidth  = 160f;
        float buttonHeight = 220f;
        float spacing      = 20f;
        float totalWidth   = cols * buttonWidth + (cols - 1) * spacing;
        float startX = (GrandSlamWarriorsGame.V_WIDTH  - totalWidth)  / 2f;
        float startY = (GrandSlamWarriorsGame.V_HEIGHT - buttonHeight) / 2f;

        for (int i = 0; i < characters.size; i++) {
            buttonAreas.add(new Rectangle(
                startX + i * (buttonWidth + spacing),
                startY,
                buttonWidth,
                buttonHeight
            ));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Fundo
        batch.setColor(0.2f, 0.2f, 0.2f, 1);
        batch.draw(backgroundTexture, 0, 0,
            GrandSlamWarriorsGame.V_WIDTH, GrandSlamWarriorsGame.V_HEIGHT);
        batch.setColor(Color.WHITE);

        for (int i = 0; i < characters.size; i++) {
            Rectangle rect = buttonAreas.get(i);
            CharacterConfig cfg = characters.get(i);

            // Card com cor temática
            batch.setColor(getCharacterColor(cfg.id));
            batch.draw(whitePixel, rect.x, rect.y, rect.width, rect.height);

            // Borda
            float b = 2f;
            batch.setColor(Color.BLACK);
            batch.draw(whitePixel, rect.x,                   rect.y,                   rect.width, b);
            batch.draw(whitePixel, rect.x,                   rect.y + rect.height - b, rect.width, b);
            batch.draw(whitePixel, rect.x,                   rect.y,                   b, rect.height);
            batch.draw(whitePixel, rect.x + rect.width - b,  rect.y,                   b, rect.height);
            batch.setColor(Color.WHITE);

            // Nome
            layout.setText(font, cfg.displayName);
            font.draw(batch, cfg.displayName,
                rect.x + (rect.width - layout.width) / 2f,
                rect.y + rect.height - 30f);

            // Preview — usa o frame de seleção (4ª imagem da PNG)
            GameCharacter preview = characterPreviews.get(cfg.id);
            if (preview != null) {
                drawCharacterPreview(preview, rect);
            }

            // Se for o Agassi e estiver bloqueado, desenha um cadeado (nunca ocorre, pois filtramos)
            // Mas mantemos a lógica por segurança
            if (cfg.id.equals("agassi") && !SettingsManager.isAgassiUnlocked()) {
                // Desenha um cadeado (opcional)
                font.draw(batch, "🔒", rect.x + rect.width / 2f - 10, rect.y + rect.height / 2f + 10);
            }
        }

        batch.end();

        // Input
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchPos);
            for (int i = 0; i < buttonAreas.size; i++) {
                if (buttonAreas.get(i).contains(touchPos.x, touchPos.y)) {
                    if (selectionSound != null && SettingsManager.isSoundEnabled()) {
                        selectionSound.play(SettingsManager.getSfxVolume());
                    }
                    GameCharacter chosen = factory.create(characters.get(i).id);
                    // Inicia o jogo na primeira fase (Australian Open)
                    game.setScreen(new GameScreen(game, GrandSlam.AUSTRALIAN_OPEN, chosen));
                    return;
                }
            }
        }
    }

    private Color getCharacterColor(String id) {
        switch (id) {
            case "clay_king":        return new Color(0.8f, 0.3f, 0.1f, 1f); // Laranja — saibro
            case "grass_maestro":    return new Color(0.2f, 0.6f, 0.2f, 1f); // Verde — grama
            case "baseline_machine": return new Color(0.1f, 0.3f, 0.7f, 1f); // Azul — quadra dura
            case "agassi":           return new Color(0.9f, 0.7f, 0.1f, 1f); // Dourado
            default:                 return Color.GRAY;
        }
    }

    private void drawCharacterPreview(GameCharacter c, Rectangle rect) {
        TextureRegion selectFrame = c.getSelectFrame();
        float ratio = (float) selectFrame.getRegionWidth() / selectFrame.getRegionHeight();

        float scale = 0.95f;
        if (c.getConfig().id.equals("grass_maestro")) {
            scale = 1.05f;
        } else if (c.getConfig().id.equals("agassi")) {
            scale = 1.0f;
        }

        float h = rect.height * scale;
        float w = h * ratio;

        float x = rect.x + (rect.width - w) / 2f;
        float y = rect.y + (rect.height - h) / 2f - 5f;

        c.drawSelect(batch, x, y, w, h, false);
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void show()   { Gdx.input.setInputProcessor(null); }
    @Override public void hide()   {
        if (themeMusic != null) {
            themeMusic.stop();
            themeMusic.dispose();
            themeMusic = null;
        }
    }
    @Override public void pause()  {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        if (backgroundTexture != null && backgroundTexture != whitePixel)
            backgroundTexture.dispose();
        if (font != null) font.dispose();
        if (whitePixel != null) whitePixel.dispose();
        if (selectionSound != null) selectionSound.dispose();
        if (themeMusic != null) themeMusic.dispose();
    }
}
