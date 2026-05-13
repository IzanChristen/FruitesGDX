package com.fruitesdeltemps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;

public class SplashScreen implements Screen {
    private MainGame game;
    private Texture splashImage;
    private SpriteBatch batch;
    private float alpha = 0;  // Para efecto fade in/out

    public SplashScreen(MainGame game) {
        this.game = game;
        splashImage = new Texture("images/splash.png");
        batch = new SpriteBatch();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new MenuScreen(game));
            }
        }, 2.5f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Efecto fade in/out (opcional)
        alpha += delta * 0.5f;
        if (alpha > 1) alpha = 1;

        batch.begin();
        batch.setColor(1, 1, 1, alpha);
        batch.draw(splashImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void dispose() {
        splashImage.dispose();
        batch.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
