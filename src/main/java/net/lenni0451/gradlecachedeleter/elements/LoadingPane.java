package net.lenni0451.gradlecachedeleter.elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoadingPane extends JPanel {

    private static final MouseAdapter VOID_MOUSE_ADAPTER = new MouseAdapter() {
    };
    private static final KeyAdapter VOID_KEY_ADAPTER = new KeyAdapter() {
    };
    private static final FocusAdapter STEALING_FOCUS_ADAPTER = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            e.getComponent().requestFocus();
        }
    };


    private final Timer animationTimer;
    private String text = "Loading";

    public LoadingPane() {
        this.animationTimer = new Timer(1000, this::tick);
        this.setOpaque(false);
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

        this.addMouseListener(VOID_MOUSE_ADAPTER);
        this.addMouseMotionListener(VOID_MOUSE_ADAPTER);
        this.addMouseWheelListener(VOID_MOUSE_ADAPTER);
        this.addKeyListener(VOID_KEY_ADAPTER);
        this.addFocusListener(STEALING_FOCUS_ADAPTER);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        this.animationTimer.stop();

        this.removeMouseListener(VOID_MOUSE_ADAPTER);
        this.removeMouseMotionListener(VOID_MOUSE_ADAPTER);
        this.removeMouseWheelListener(VOID_MOUSE_ADAPTER);
        this.removeKeyListener(VOID_KEY_ADAPTER);
        this.removeFocusListener(STEALING_FOCUS_ADAPTER);
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 0, 0, 127));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        g.setColor(Color.BLACK);
        g.setFont(g.getFont().deriveFont(20f));
        int width = g.getFontMetrics().stringWidth(this.text);
        int height = g.getFontMetrics().getHeight();
        g.drawString(this.text, (this.getWidth() - width) / 2, (this.getHeight() - height) / 2);
    }

}
