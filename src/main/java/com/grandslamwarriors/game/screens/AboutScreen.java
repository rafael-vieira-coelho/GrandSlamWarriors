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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.grandslamwarriors.game.GrandSlamWarriorsGame;

public class AboutScreen implements Screen {
    private final GrandSlamWarriorsGame game;
    private Stage stage;
    private Skin skin;
    private Texture background;

    public AboutScreen(final GrandSlamWarriorsGame game) {
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        Viewport viewport = new StretchViewport(GrandSlamWarriorsGame.V_WIDTH, GrandSlamWarriorsGame.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);

        skin = new Skin();
        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.2f);
        skin.add("default", font);
        skin.add("default", new Label.LabelStyle(font, Color.WHITE));

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.YELLOW;
        skin.add("default", textButtonStyle, TextButton.TextButtonStyle.class);

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(new Color(0, 0, 0, 0.8f));
        pm.fill();
        background = new Texture(pm);
        pm.dispose();

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label title = new Label("ABOUT GRAND SLAM WARRIORS", skin);
        title.setFontScale(1.8f);
        table.add(title).padBottom(30).row();

        String aboutText = "Grand Slam Warriors is an action platformer where tennis meets combat.\n" +
                "Conquer the four major tournaments: Australia, France, England, and USA.\n\n" +
                "CONTROLS:\n" +
                "- ARROWS / WASD: Move and Crouch\n" +
                "- SPACE / UP: Jump (Serve)\n" +
                "- Z: Melee Attack (Slice)\n" +
                "- X: Shoot Tennis Ball (Smash)\n" +
                "- ESC: Pause Menu\n\n" +
                "GOAL:\n" +
                "Collect all health items to unlock the Trophy and finish the level.\n" +
                "Watch the shot clock! You have 120 seconds per level.\n\n" +
                "Designed by Rafael Vieira Coelho";

        Label content = new Label(aboutText, skin);
        content.setAlignment(com.badlogic.gdx.utils.Align.center);
        table.add(content).padBottom(30).row();

        TextButton backBtn = new TextButton("BACK TO MENU", skin);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new MenuScreen(game));
            }
        });
        table.add(backBtn).width(200).height(50);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(background, 0, 0, GrandSlamWarriorsGame.V_WIDTH, GrandSlamWarriorsGame.V_HEIGHT);
        game.batch.end();
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); background.dispose(); }
}
