package net.lenni0451.gradlecachedeleter;

import net.lenni0451.commons.swing.GBC;
import net.lenni0451.gradlecachedeleter.elements.LoadingPane;
import net.lenni0451.gradlecachedeleter.utils.FileUtils;
import net.lenni0451.gradlecachedeleter.utils.Tuple;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GUI extends JFrame {

    private CompletableFuture<DependencyList> dependenciesFuture;
    private LoadingPane loadingPane;
    private JTextField searchField;
    private JTable dependenciesTable;

    public GUI() {
        super("Gradle Cache Deleter");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.init();
        this.setVisible(true);
        this.refresh();
    }

    private void init() {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        this.setContentPane(contentPane);

        GBC.create(contentPane).grid(0, 0).insets(5, 5, 0, 5).anchor(GBC.LINE_START).add(new JLabel("Search:"));
        GBC.create(contentPane).grid(1, 0).insets(5, 0, 0, 5).weightx(1).fill(GBC.HORIZONTAL).add(() -> {
            this.searchField = new JTextField();
            this.searchField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    GUI.this.refreshTable();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    GUI.this.refreshTable();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    GUI.this.refreshTable();
                }
            });
            return this.searchField;
        });

        GBC.create(contentPane).grid(0, 1).insets(5, 5, 5, 5).width(2).weight(1, 1).fill(GBC.BOTH).add(() -> {
            this.dependenciesTable = new JTable(new DefaultTableModel(new Object[]{"Package", "Name", "Version"}, 0));

            JPopupMenu contextMenu = new JPopupMenu();
            JMenuItem deleteMenuItem = new JMenuItem("Delete");
            JMenuItem refreshMenuItem = new JMenuItem("Refresh");
            contextMenu.add(deleteMenuItem);
            contextMenu.add(refreshMenuItem);
            deleteMenuItem.addActionListener(e -> {
                int[] selectedRows = this.dependenciesTable.getSelectedRows();
                DefaultTableModel model = (DefaultTableModel) this.dependenciesTable.getModel();
                int total = 0;
                int success = 0;
                for (int row : selectedRows) {
                    Tuple<Integer, Integer> result = this.delete((String) model.getValueAt(row, 0), (String) model.getValueAt(row, 1), (String) model.getValueAt(row, 2));
                    total += result.a();
                    success += result.b();
                }
                JOptionPane.showMessageDialog(this, "Deleted " + success + " of " + total + " file(s)", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                this.refresh();
            });
            refreshMenuItem.addActionListener(e -> this.refresh());
            this.dependenciesTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        contextMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(this.dependenciesTable);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
            return scrollPane;
        });
    }

    private void refresh() {
        this.loadingPane = new LoadingPane();
        this.setGlassPane(this.loadingPane);
        this.loadingPane.setVisible(true);

        this.dependenciesFuture = CompletableFuture
                .supplyAsync(DependencyList::new)
                .whenComplete((list, t) -> SwingUtilities.invokeLater(() -> {
                    this.loadingPane.setVisible(false);
                    this.loadingPane = null;
                    this.setGlassPane(new JPanel());

                    this.refreshTable();
                }));
    }

    private void refreshTable() {
        DependencyList list = this.dependenciesFuture.join();
        Map<String, Map<String, Map<String, List<File>>>> dependencies = list.getDependencies();
        String[] search = this.searchField.getText().toLowerCase().split("[\\s:]");

        DefaultTableModel model = (DefaultTableModel) this.dependenciesTable.getModel();
        model.setRowCount(0);

        for (Map.Entry<String, Map<String, Map<String, List<File>>>> pkgEntry : dependencies.entrySet()) {
            String pkg = pkgEntry.getKey();
            for (Map.Entry<String, Map<String, List<File>>> nameEntry : pkgEntry.getValue().entrySet()) {
                String name = nameEntry.getKey();
                for (Map.Entry<String, List<File>> versionEntry : nameEntry.getValue().entrySet()) {
                    String version = versionEntry.getKey();

                    if (this.matchesAll(search, pkg, name, version)) model.addRow(new Object[]{pkg, name, version});
                }
            }
        }
    }

    private boolean matchesAll(final String[] search, final String... parts) {
        for (String s : search) {
            boolean found = false;
            for (String part : parts) {
                if (part.toLowerCase().contains(s)) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    private Tuple<Integer, Integer> delete(final String pkg, final String name, final String version) {
        DependencyList list = this.dependenciesFuture.join();
        Map<String, Map<String, Map<String, List<File>>>> dependencies = list.getDependencies();

        List<File> files = dependencies.get(pkg).get(name).get(version);
        int success = 0;
        for (File file : files) {
            if (FileUtils.delete(file)) success++;
        }
        return new Tuple<>(files.size(), success);
    }

}
