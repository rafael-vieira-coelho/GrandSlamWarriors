package com.grandslamwarriors.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.grandslamwarriors.game.GrandSlamWarriorsGame;
import com.grandslamwarriors.game.data.SettingsManager;

public class MenuScreen implements Screen {
    private final GrandSlamWarriorsGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;
    private com.badlogic.gdx.audio.Music music;

    public MenuScreen(GrandSlamWarriorsGame game) {
        this.game = game;

        // Música inicial
        if (SettingsManager.isSoundEnabled()) {
            try {
                music = Gdx.audio.newMusic(Gdx.files.internal("sounds/intro_music.wav"));
                music.setLooping(true);
                music.play();
            } catch (Exception e) {
                music = null;
            }
        }

        camera = new OrthographicCamera();
        viewport = new StretchViewport(GrandSlamWarriorsGame.V_WIDTH, GrandSlamWarriorsGame.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();

        BitmapFont font = new BitmapFont();
        font.getData().setScale(2.5f);

        // --- CARREGA BACKGROUND COM FALLBACK ---
        try {
            if (Gdx.files.internal("backgrounds/main.png").exists()) {
                backgroundTexture = new Texture("backgrounds/main.png");
            } else {
                Gdx.app.error("MenuScreen", "Arquivo backgrounds/main.png não encontrado! Usando fallback.");
                // Cria fallback: gradiente cinza-escuro
                Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
                pm.setColor(new Color(0.15f, 0.15f, 0.25f, 1f));
                pm.fill();
                backgroundTexture = new Texture(pm);
                pm.dispose();
            }
        } catch (Exception e) {
            Gdx.app.error("MenuScreen", "Erro ao carregar background", e);
            // Fallback de emergência
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(Color.DARK_GRAY);
            pm.fill();
            backgroundTexture = new Texture(pm);
            pm.dispose();
        }

        // =========================
        // STYLE
        // =========================
        TextButton.TextButtonStyle textButtonStyle =
            new TextButton.TextButtonStyle();

        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.YELLOW;
        textButtonStyle.overFontColor = Color.WHITE;


        textButtonStyle.pressedOffsetX = 2f;
        textButtonStyle.pressedOffsetY = -2f;

        skin.add(
            "default",
            textButtonStyle,
            TextButton.TextButtonStyle.class
        );

        // =========================
        // BUTTON ABOUT
        // =========================
        TextButton.TextButtonStyle aboutStyle = new TextButton.TextButtonStyle(textButtonStyle);
        aboutStyle.fontColor = Color.CYAN;
        TextButton aboutBtn = new TextButton("ABOUT", aboutStyle);
        aboutBtn.setSize(120, 50);
        aboutBtn.setPosition(20, 20);
        aboutBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new AboutScreen(game));
            }
        });
        stage.addActor(aboutBtn);

        // =========================
        // BUTTON UP
        // =========================
        Pixmap pixUp = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        pixUp.setColor(new Color(0.1f, 0.1f, 0.1f, 0.85f));
        pixUp.fill();
        pixUp.setColor(Color.YELLOW);
        pixUp.drawRectangle(0, 0, 12, 12);
        pixUp.drawRectangle(1, 1, 10, 10);

        TextureRegionDrawable upDrawable =
            new TextureRegionDrawable(
                new TextureRegion(new Texture(pixUp))
            );

        pixUp.dispose();

        // =========================
        // BUTTON OVER
        // =========================
        Pixmap pixOver = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        pixOver.setColor(new Color(0.25f, 0.25f, 0.25f, 0.9f));
        pixOver.fill();
        pixOver.setColor(Color.WHITE);
        pixOver.drawRectangle(0, 0, 12, 12);
        pixOver.drawRectangle(1, 1, 10, 10);

        TextureRegionDrawable overDrawable =
            new TextureRegionDrawable(
                new TextureRegion(new Texture(pixOver))
            );

        pixOver.dispose();

        // =========================
        // BUTTON DOWN
        // =========================
        Pixmap pixDown = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        pixDown.setColor(new Color(0.1f, 0.45f, 0.1f, 1f));
        pixDown.fill();
        pixDown.setColor(Color.WHITE);
        pixDown.drawRectangle(0, 0, 12, 12);

        TextureRegionDrawable downDrawable =
            new TextureRegionDrawable(
                new TextureRegion(new Texture(pixDown))
            );

        pixDown.dispose();



        // --- BOTÃO RANKING (SEM EMOJI) ---
        TextButton rankingBtn = new TextButton("RANKING", skin, "default");
        rankingBtn.setSize(220, 65);
        rankingBtn.setPosition(
            GrandSlamWarriorsGame.V_WIDTH / 2f - rankingBtn.getWidth() / 2f,
            95
        );
        rankingBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new LeaderboardScreen(game));
            }
        });
        stage.addActor(rankingBtn);

        // --- BOTÕES DE DIFICULDADE ---
        final TextButton easyBtn = new TextButton("EASY", skin, "default");
        easyBtn.setSize(240, 85);
        easyBtn.setPosition(
            GrandSlamWarriorsGame.V_WIDTH / 2f - 260,
            180
        );

        final TextButton hardBtn = new TextButton("HARD", skin, "default");
        hardBtn.setSize(240, 85);
        hardBtn.setPosition(
            GrandSlamWarriorsGame.V_WIDTH / 2f + 20,
            180
        );

        easyBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                SettingsManager.setDifficulty(0);
                easyBtn.setColor(Color.GREEN);
                hardBtn.setColor(Color.WHITE);
                game.setScreen(new CharacterSelectScreen(game));
            }
        });
        stage.addActor(easyBtn);

        hardBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                SettingsManager.setDifficulty(1);
                hardBtn.setColor(Color.RED);
                easyBtn.setColor(Color.WHITE);
                game.setScreen(new CharacterSelectScreen(game));
            }
        });
        stage.addActor(hardBtn);

        // Destacar a dificuldade atual
        if (SettingsManager.getDifficulty() == 0) {
            easyBtn.setColor(Color.GREEN);
            hardBtn.setColor(Color.WHITE);
        } else {
            hardBtn.setColor(Color.RED);
            easyBtn.setColor(Color.WHITE);
        }

        // --- CHECKBOX DE SOM ---
        BitmapFont smallFont = new BitmapFont();
        smallFont.getData().setScale(1.5f);
        CheckBox.CheckBoxStyle cbStyle = new CheckBox.CheckBoxStyle();
        cbStyle.font = smallFont;
        cbStyle.fontColor = Color.YELLOW;

        Pixmap cbPix = new Pixmap(24, 24, Pixmap.Format.RGBA8888);
        cbPix.setColor(Color.WHITE);
        cbPix.drawRectangle(0, 0, 24, 24);
        Texture cbTex = new Texture(cbPix);
        cbStyle.checkboxOff =
            new TextureRegionDrawable(
                new TextureRegion(cbTex)
            );

        cbPix.setColor(Color.YELLOW);
        cbPix.fillRectangle(4, 4, 16, 16);
        Texture cbTexOn = new Texture(cbPix);
        cbStyle.checkboxOn =
            new TextureRegionDrawable(
                new TextureRegion(cbTexOn)
            );
        cbPix.dispose();

        final CheckBox soundCb = new CheckBox(" SOUND", cbStyle);
        soundCb.setChecked(SettingsManager.isSoundEnabled());
        soundCb.pack();
        soundCb.setPosition(
            GrandSlamWarriorsGame.V_WIDTH / 2f - soundCb.getWidth() / 2f,
            25
        );
        soundCb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                boolean enabled = soundCb.isChecked();
                SettingsManager.setSoundEnabled(enabled);
                if (enabled) {
                    if (music == null) {
                        try {
                            music = Gdx.audio.newMusic(Gdx.files.internal("sounds/intro_music.wav"));
                            music.setLooping(true);
                            music.play();
                        } catch (Exception ignored) {}
                    } else {
                        music.play();
                    }
                } else {
                    if (music != null) {
                        music.pause();
                    }
                }
            }
        });
        stage.addActor(soundCb);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        if (backgroundTexture != null) {
            game.batch.draw(backgroundTexture, 0, 0, GrandSlamWarriorsGame.V_WIDTH, GrandSlamWarriorsGame.V_HEIGHT);
        }
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override
    public void hide() {
        if (music != null) {
            music.stop();
            music.dispose();
            music = null;
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        // As texturas internas são gerenciadas pelo Skin (dispose automático)
        if (music != null) music.dispose();
    }
}
