package net.lenni0451.gradlecachedeleter.elements;

import net.lenni0451.commons.swing.components.OverlayPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoadingPane extends OverlayPanel {

    private final Timer animationTimer;
    private String text = "Loading";

    public LoadingPane() {
        super(new Color(0, 0, 0, 127));
        this.animationTimer = new Timer(1000, this::tick);
    }

    private void tick(final ActionEvent event) {
        this.text += ".";
        if (this.text.length() > 10) this.text = "Loading";
        this.repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        this.requestFocus();
        this.animationTimer.start();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        this.animationTimer.stop();
    }

    @Override
    protected void paintOverlay(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(g.getFont().deriveFont(20f));
        int width = g.getFontMetrics().stringWidth(this.text);
        int height = g.getFontMetrics().getHeight();
        g.drawString(this.text, (this.getWidth() - width) / 2, (this.getHeight() - height) / 2);
    }

}
