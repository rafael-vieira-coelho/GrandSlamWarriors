package com.grandslamwarriors.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.grandslamwarriors.game.GrandSlamWarriorsGame;
import com.grandslamwarriors.game.data.LeaderboardManager;

public class LeaderboardScreen implements Screen {

    private final GrandSlamWarriorsGame game;
    private Stage stage;
    private Skin skin;
    private Texture background;
    private Texture separatorTexture;
    private LeaderboardManager leaderboard;

    public LeaderboardScreen(GrandSlamWarriorsGame game) {
        this.game = game;
        this.leaderboard = new LeaderboardManager();

        // Carrega fundo com fallback
        try {
            background = new Texture("ui/icone.png");
        } catch (Exception e) {
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(Color.DARK_GRAY);
            pm.fill();
            background = new Texture(pm);
            pm.dispose();
        }

        // Cria textura para separador
        Pixmap pmSep = new Pixmap(1, 2, Pixmap.Format.RGBA8888);
        pmSep.setColor(Color.GRAY);
        pmSep.fill();
        separatorTexture = new Texture(pmSep);
        pmSep.dispose();
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(
            GrandSlamWarriorsGame.V_WIDTH,
            GrandSlamWarriorsGame.V_HEIGHT,
            new OrthographicCamera()
        ));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();
        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.2f);
        skin.add("default", font);

        TextButton.TextButtonStyle defaultBtnStyle = new TextButton.TextButtonStyle();
        defaultBtnStyle.font = font;
        defaultBtnStyle.fontColor = Color.WHITE;
        skin.add("default", defaultBtnStyle, TextButton.TextButtonStyle.class);

        // --- ESTILOS COM CORES PERSONALIZADAS ---
        // Título: dourado
        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.GOLD);
        // Cabeçalho: preto (para contraste com fundo claro)
        Label.LabelStyle headerStyle = new Label.LabelStyle(font, Color.BLACK);
        // Entradas do ranking: preto
        Label.LabelStyle entryStyle = new Label.LabelStyle(font, Color.BLACK);

        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(30);

        // Título
        Label title = new Label("RANKING", titleStyle);
        title.setFontScale(2f);
        root.add(title).colspan(4).padBottom(20).row();

        // Cabeçalho
        Label posHeader = new Label("#", headerStyle);
        Label dateHeader = new Label("Date/Time", headerStyle);
        Label slamHeader = new Label("Slam", headerStyle);
        Label scoreHeader = new Label("Score", headerStyle);

        float cellWidth = GrandSlamWarriorsGame.V_WIDTH / 4f;
        root.add(posHeader).width(cellWidth).pad(5);
        root.add(dateHeader).width(cellWidth).pad(5);
        root.add(slamHeader).width(cellWidth).pad(5);
        root.add(scoreHeader).width(cellWidth).pad(5).row();

        // Separador
        Image separator = new Image(separatorTexture);
        separator.setWidth(GrandSlamWarriorsGame.V_WIDTH - 40);
        separator.setHeight(2);
        root.add(separator).colspan(4).padBottom(10).row();

        // Entradas do ranking (top 5)
        Array<LeaderboardManager.LeaderboardEntry> entries = leaderboard.getEntries();
        if (entries.size == 0) {
            Label empty = new Label("No scores registered yet!", entryStyle);
            empty.setFontScale(1.5f);
            root.add(empty).colspan(4).pad(20).row();
        } else {
            for (int i = 0; i < entries.size; i++) {
                LeaderboardManager.LeaderboardEntry entry = entries.get(i);
                Label posLabel = new Label(String.valueOf(i + 1), entryStyle);
                Label dateLabel = new Label(entry.getFormattedDate(), entryStyle);
                Label slamLabel = new Label(entry.slamName, entryStyle);
                Label scoreLabel = new Label(String.valueOf(entry.score), entryStyle);

                // Primeiro lugar em destaque (dourado)
                if (i == 0) {
                    posLabel.setColor(Color.GOLD);
                    scoreLabel.setColor(Color.GOLD);
                }

                root.add(posLabel).width(cellWidth).pad(3);
                root.add(dateLabel).width(cellWidth).pad(3);
                root.add(slamLabel).width(cellWidth).pad(3);
                root.add(scoreLabel).width(cellWidth).pad(3).row();
            }
        }

        // Botão Voltar
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.WHITE;
        // Fundo do botão (para contraste)
        Pixmap btnPix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        btnPix.setColor(new Color(0.2f, 0.2f, 0.2f, 0.8f));
        btnPix.fill();
        Texture btnTex = new Texture(btnPix);
        btnPix.dispose();
        btnStyle.up = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(btnTex);

        TextButton backBtn = new TextButton("← BACK", btnStyle);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                try {
                    game.setScreen(new MenuScreen(game));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        root.add(backBtn).colspan(4).padTop(30).width(300).height(60);

        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        if (background != null) {
            game.batch.draw(background, 0, 0,
                GrandSlamWarriorsGame.V_WIDTH, GrandSlamWarriorsGame.V_HEIGHT);
        } else {
            Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1f); // fundo claro
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (background != null) background.dispose();
        if (separatorTexture != null) separatorTexture.dispose();
    }
}
