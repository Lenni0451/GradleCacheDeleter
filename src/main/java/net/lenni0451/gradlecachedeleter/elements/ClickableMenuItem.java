package net.lenni0451.gradlecachedeleter.elements;

import javax.swing.*;

public class ClickableMenuItem extends JMenuItem {

    public ClickableMenuItem(final String text, final Runnable onClick) {
        super(text);
        this.addActionListener(e -> onClick.run());
    }

}
