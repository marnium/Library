package system.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.Component;

public class MyTable extends JTable {
    private static final long serialVersionUID = 1L;
    public static final Color header_table = ColorUse.blue_main;
    public static final Color focus_row = ColorUse.blue_secondary;
    public static final Color even_row = Color.WHITE;
    public static final Color odd_row = ColorUse.gray_main;

    public MyTable(TableModel tm) {
        super(tm);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoCreateRowSorter(true);
        setFont(FontUse.sansserif_plain_16);
        setShowGrid(false);
        getTableHeader().setBackground(ColorUse.blue_new);
        getTableHeader().setFont(FontUse.sansserif_bold_14);
        setRowHeight(20);
        setDefaultRenderer(Object.class, new CellRender());
    }

    public static class MyTableModel extends DefaultTableModel {
        private static final long serialVersionUID = 1L;

        public MyTableModel(String[] headers) {
            super(headers, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    public static class CellRender extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean is_selected,
                boolean has_focus, int row, int column) {
            super.getTableCellRendererComponent(table, value, is_selected, has_focus, row, column);

            if (is_selected)
                setBackground(focus_row);
            else if (row % 2 == 0)
                setBackground(even_row);
            else
                setBackground(odd_row);

            return this;
        }
    }
}