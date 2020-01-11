package co.circlewave.cirzle.gui.utils;

import co.circlewave.cirzle.services.Forismatic;
import co.circlewave.cirzle.services.ServicesManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class Quote extends Table {

    private final ServicesManager servicesManager;
    private final int maxWordNumber;
    private final Label label;

    private int wordPerLine;
    private float showDuration;
    private boolean state;

    public Quote(final ServicesManager servicesManager, final BitmapFont font, final int wordPerLine, final float showDuration) {
        this.servicesManager = servicesManager;
        this.maxWordNumber = 15;
        this.wordPerLine = wordPerLine;
        this.showDuration = showDuration;
        this.state = true;
        this.label = new Label("", new Label.LabelStyle(font, Color.WHITE));
        this.label.setAlignment(Align.center);
        add(label);
        addAction(Actions.alpha(0));
        run();
        setPosition(getX(), getY(), Align.center);
    }

    public void run() {
        clearActions();
        addAction(Actions.forever(Actions.sequence(Actions.run(() -> {
            if (state) {
                setVisible(false);
                new Thread(() -> {
                    String text = "";
                    if (servicesManager.isInternetAvailable()) {
                        text = new Forismatic().getQuote().getQuoteText();
                        while (text != null && text.split(" ").length >= maxWordNumber) {
                            text = new Forismatic().getQuote().getQuoteText();
                        }
                    }
                    assert text != null;
                    label.setText(textSplitter(text, wordPerLine));
                    setVisible(true);
                }).start();
            }
        }), Actions.alpha(1, 0.1f), Actions.delay(showDuration), Actions.alpha(0, 0.1f))));
    }

    public void clearActions() {
        getActions().clear();
    }


    public void pause() {
        state = false;
    }

    public void hide() {
        pause();
        addAction(Actions.fadeOut(0.1f));
    }

    public void show() {
        resume();
        addAction(Actions.fadeIn(0.1f));
    }

    public void resume() {
        state = true;
    }

    private String textSplitter(final String text, final int wordPerLine) {
        if (text != null) {
            final String[] splitter = text.split(" ");
            StringBuilder temp = new StringBuilder();
            if (splitter.length / wordPerLine < 1) {
                temp.append(text);
            } else {
                int index = 0;
                while (index < splitter.length) {
                    if (index % wordPerLine == 0 && index != 0) {
                        temp.append("\n");
                    }
                    temp.append(splitter[index]);
                    temp.append(" ");
                    index++;
                }
            }
            temp.append("\n");
            return temp.toString();
        } else {
            return "";
        }
    }

    public String getText() {
        return label.getText().toString();
    }

    public void setText(final String text) {
        toFront();
        label.setText(text);
    }
}
