package org.mini2dx.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import org.mini2Dx.core.Mdx;
import org.mini2Dx.core.assets.FallbackFileHandleResolver;
import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.screen.BasicGameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.transition.NullTransition;
import org.mini2Dx.core.serialization.SerializationException;
import org.mini2Dx.ui.UiContainer;
import org.mini2Dx.ui.UiThemeLoader;
import org.mini2Dx.ui.element.*;
import org.mini2Dx.ui.event.ActionEvent;
import org.mini2Dx.ui.listener.ActionListener;
import org.mini2Dx.ui.style.UiTheme;

import java.util.List;
import java.util.Objects;

public class MainMenu extends BasicGameScreen {
    public final static int ID = 1;
    public static final String UI_MAINMENU_LAYOUT_XML = "mainmenu_ui.xml";
    public static final String UI_LEADERBOARD_LAYOUT_XML = "leaderboard_ui.xml";

    private AssetManager assetManager;
    private UiContainer uiContainer;
    private int screenToLoad = 0;

    @Override
    public void initialise(GameContainer gc) {
        FileHandleResolver fileHandleResolver = new FallbackFileHandleResolver(new ClasspathFileHandleResolver(), new InternalFileHandleResolver());
        assetManager = new AssetManager(fileHandleResolver);
        assetManager.setLoader(UiTheme.class, new UiThemeLoader(fileHandleResolver));
        assetManager.load(UiTheme.DEFAULT_THEME_FILENAME, UiTheme.class);

        uiContainer = new UiContainer(gc, assetManager);
        Gdx.input.setInputProcessor(uiContainer);
        Container mainMenuContainer = null;
        try {
            mainMenuContainer = Mdx.xml.fromXml(Gdx.files.internal(UI_MAINMENU_LAYOUT_XML).reader(), Container.class);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(mainMenuContainer);

        TextButton newGameButton = (TextButton) mainMenuContainer.getElementById("newGameButton");
        TextButton leaderboardButton = (TextButton) mainMenuContainer.getElementById("leaderboardButton");
        TextButton quitButton = (TextButton) mainMenuContainer.getElementById("quitButton");
        mainMenuContainer.shrinkToContents(true);

        mainMenuContainer.setXY((BreakoutGame.gameWidth - mainMenuContainer.getWidth()) / 2, (BreakoutGame.gameHeight - mainMenuContainer.getHeight()) / 2);
        uiContainer.add(mainMenuContainer);

        Container temp_leaderboardContainer = null;
        try {
            temp_leaderboardContainer = Mdx.xml.fromXml(Gdx.files.internal(UI_LEADERBOARD_LAYOUT_XML).reader(), Container.class);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        final Container leaderboardContainer = Objects.requireNonNull(temp_leaderboardContainer);
        leaderboardContainer.shrinkToContents(true);
        leaderboardContainer.setXY((BreakoutGame.gameWidth - leaderboardContainer.getWidth()) / 2, (BreakoutGame.gameHeight - leaderboardContainer.getHeight()) / 2);

        TextButton mainMenuButton = (TextButton) leaderboardContainer.getElementById("mainMenuButton");
        Container scoreContainer = (Container) leaderboardContainer.getElementById("scoreContainer");

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void onActionBegin(ActionEvent event) {

            }

            @Override
            public void onActionEnd(ActionEvent event) {
                screenToLoad = BreakoutGame.ID;
            }
        });

        leaderboardButton.addActionListener(new ActionListener() {
            @Override
            public void onActionBegin(ActionEvent event) {

            }

            @Override
            public void onActionEnd(ActionEvent event) {
                scoreContainer.removeAll();
                List<Integer> scores = LeaderboardHandler.getInstance().getScores();
                for (Integer currentScore : scores) {
                    Label currentScoreLabel = new Label();
                    currentScoreLabel.setText(currentScore.toString());
                    currentScoreLabel.setVisibility(Visibility.VISIBLE);
                    Div currentScoreDiv = new FlexRow();
                    currentScoreDiv.setVisibility(Visibility.VISIBLE);
                    currentScoreDiv.add(currentScoreLabel);
                    scoreContainer.add(currentScoreDiv);
                }
                uiContainer.get(0).setVisibility(Visibility.NO_RENDER);
                uiContainer.add(leaderboardContainer);
                uiContainer.shrinkToContents(true);
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void onActionBegin(ActionEvent event) {

            }

            @Override
            public void onActionEnd(ActionEvent event) {
                Gdx.app.exit();
            }
        });

        mainMenuButton.addActionListener(new ActionListener() {
            @Override
            public void onActionBegin(ActionEvent event) {

            }

            @Override
            public void onActionEnd(ActionEvent event) {
                uiContainer.remove(leaderboardContainer);
                uiContainer.get(0).setVisibility(Visibility.VISIBLE);
            }
        });


    }

    @Override
    public void update(GameContainer gc, ScreenManager screenManager, float delta) {
        if (!assetManager.update()) {
            return;
        }
        if (!UiContainer.isThemeApplied()) {
            UiContainer.setTheme(assetManager.get(UiTheme.DEFAULT_THEME_FILENAME, UiTheme.class));
        }
        uiContainer.update(delta);
        if (screenToLoad != 0) {
            screenManager.enterGameScreen(screenToLoad, new NullTransition(), new NullTransition());
            screenToLoad = 0;
        }
    }

    @Override
    public void interpolate(GameContainer gc, float alpha) {
        uiContainer.interpolate(alpha);
    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        uiContainer.render(g);
    }

    @Override
    public int getId() {
        return ID;
    }
}