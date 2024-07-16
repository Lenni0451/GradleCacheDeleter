package net.lenni0451.gradlecachedeleter.elements;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.util.Arrays;

public class PopupMenuTableSelectionListener implements PopupMenuListener {

    private final JPopupMenu popupMenu;
    private final JTable table;

    public PopupMenuTableSelectionListener(final JPopupMenu popupMenu, final JTable table) {
        this.popupMenu = popupMenu;
        this.table = table;
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        Point mousePosition = this.popupMenu.getInvoker().getMousePosition();
        if (mousePosition == null) return;

        int[] selectedRows = this.table.getSelectedRows();
        int hoveredRow = this.table.rowAtPoint(mousePosition);
        if (hoveredRow >= 0 && Arrays.stream(selectedRows).noneMatch(i -> i == hoveredRow)) {
            this.table.setRowSelectionInterval(hoveredRow, hoveredRow);
        }
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
    }

}
