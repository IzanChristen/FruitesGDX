package com.fruitesdeltemps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class MenuScreen implements Screen {
    private MainGame game;
    private Texture background;
    private Texture logo;
    private Rectangle playButton;

    public MenuScreen(MainGame game) {
        this.game = game;
        background = new Texture("images/background.jpg");
        logo = new Texture("images/logo.png");

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Botón JUGAR centrado
        float buttonWidth = 400;
        float buttonHeight = 120;
        float buttonX = screenWidth / 2 - buttonWidth / 2;
        float buttonY = screenHeight / 2 - buttonHeight / 2;
        playButton = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        game.batch.begin();
        game.batch.draw(background, 0, 0, screenWidth, screenHeight);

        // Logo grande centrado
        float logoWidth = 600;
        float logoHeight = 300;
        float logoX = screenWidth / 2 - logoWidth / 2;
        float logoY = screenHeight - 350;
        game.batch.draw(logo, logoX, logoY, logoWidth, logoHeight);

        // Botón JUGAR con fondo
        game.batch.setColor(0.1f, 0.6f, 0.1f, 0.9f);
        game.batch.draw(background, playButton.x, playButton.y, playButton.width, playButton.height);
        game.batch.setColor(1, 1, 1, 1);

        // Texto del botón
        game.titleFont.draw(game.batch, "JUGAR", playButton.x + 130, playButton.y + 80);

        game.batch.end();

        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = screenHeight - Gdx.input.getY();
            if (playButton.contains(touchX, touchY)) {
                game.setScreen(new GameScreen(game));
            }
        }
    }

    @Override
    public void dispose() {
        background.dispose();
        logo.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
