package system.components;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.event.*;

import java.sql.*;

public class SearchBook extends JPanel {
    private static final long serialVersionUID = 1L;
    private final String[] name_columns = {"ID", "Titulo", "Autor", "Año Publicación", "Editorial",
        "País", "Ciudad", "Código", "Ejemplares", "active"};
    private JRadioButton[] radio_options;
    private JComboBox<String> type_books;
    private JTable table;
    private Input[] inputs;
    private JButton button;
    private boolean can_search = true;
    private int index_option_active = 1;
    private Access access;
    private GroupLayout layout;

    public SearchBook(Access access_b, String str_label, boolean active_all, boolean create_typebooks) {
        this.access = access_b;

        layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);

        JLabel label = new JLabel(str_label);
        label.setFont(FontUse.monospaced_bold_20);
        button = new JButton("Seleccionar");

        GroupLayout.SequentialGroup hgroupopts = layout.createSequentialGroup();
        GroupLayout.ParallelGroup vgroupopts = layout.createParallelGroup();
        GroupLayout.ParallelGroup hgroupinp = layout.createParallelGroup();
        GroupLayout.SequentialGroup vgroupinp = layout.createSequentialGroup();

        hgroupinp.addGroup(hgroupopts);
        vgroupopts.addComponent(label);

        String[] name_options = {"Todos", "Titulo", "Autor", "Editorial"};
        ButtonGroup group_button = new ButtonGroup();
        radio_options = new JRadioButton[name_options.length];
        for (int i = 0; i < radio_options.length; i++) {
            radio_options[i] = new JRadioButton(name_options[i]);
            group_button.add(radio_options[i]);
            hgroupopts.addComponent(radio_options[i]);
            vgroupopts.addComponent(radio_options[i]);
        }
        radio_options[0].setSelected(true);
        hgroupopts.addComponent(button);
        vgroupopts.addComponent(button);
        
        inputs = new Input[name_options.length - 1];
        inputs[0] = new InputText(null, name_options[1], 20, 80);
        inputs[1] = new InputText(null, name_options[2], 20, 50);
        inputs[2] = new InputText(null, name_options[3], 20, 60);
        DocumentListener listenerinp = new ChangeSearch();
        for (Input input : inputs) {
            input.field.getDocument().addDocumentListener(listenerinp);
            input.setVisible(false);
            hgroupinp.addComponent(input);
            vgroupinp.addComponent(input);
        }

        table = new MyTable(new MyTable.MyTableModel(name_columns));
        table.removeColumn(table.getColumnModel().getColumn(9));
        table.removeColumn(table.getColumnModel().getColumn(0));
        JScrollPane scroll = new JScrollPane(table);

        GroupLayout.ParallelGroup hgroup = layout.createParallelGroup();
        GroupLayout.SequentialGroup vgroup = layout.createSequentialGroup();
        if (create_typebooks) {
            type_books = new JComboBox<>(new String[]
            {"Libros dados de baja", "Libros activos"});
            type_books.setSelectedIndex(1);
            type_books.addItemListener((ItemEvent event) -> {
                if (event.getStateChange() == ItemEvent.SELECTED)
                    access.type_book = type_books.getSelectedIndex();
            });
            hgroup.addComponent(type_books);
            vgroup.addComponent(type_books, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
        }

        //Diseño de SearchBook
        layout.setHorizontalGroup(hgroup
            .addGroup(layout.createSequentialGroup()
                .addComponent(label)
                .addGroup(hgroupinp))
            .addComponent(scroll)
        );
        layout.setVerticalGroup(vgroup
            .addGroup(vgroupopts)
            .addGroup(vgroupinp)
            .addComponent(scroll)
        );

        //Manejadores de eventos
        radio_options[0].addItemListener((ItemEvent event) -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                can_search = false;
                for (int i = 0; i < inputs.length; i++) {
                    inputs[i].setVisible(false);
                    inputs[i].field.setText("");
                }
                button.setVisible(true);
            } else {
                button.setVisible(false);
                can_search = true;
            }
        });
        for (int i = 1; i < radio_options.length; i++)
            radio_options[i].addItemListener(new ChangeOptions(i));
        button.addActionListener((ActionEvent event) -> {
            print_table(access.select_alldata());
        });
        
        //Analizar el valor de active_all
        radio_options[0].setVisible(active_all);
        radio_options[1].setSelected(!active_all);
    }

    public JTable get_table() {
        return table;
    }

    public void active_space_border() {
        layout.setAutoCreateContainerGaps(true);
    }

    private void print_table(ResultSet result) {
        MyTable.MyTableModel model = new MyTable.MyTableModel(name_columns);
        table.setModel(model);
        table.removeColumn(table.getColumnModel().getColumn(9));
        table.removeColumn(table.getColumnModel().getColumn(0));
        if (result != null) {
            String row_data[] = new String[name_columns.length];
            try {
                while (result.next()) {
                    for (int i = 0; i < row_data.length; i++)
                        row_data[i] = result.getString(i + 1);
                    model.addRow(row_data);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class ChangeOptions implements ItemListener {
        private final int index;

        public ChangeOptions(int index) {
            this.index = index - 1;
        }

        @Override
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED && index != index_option_active) {
                can_search = false;
                inputs[index_option_active].setVisible(false);
                inputs[index_option_active].field.setText("");
                inputs[index].setVisible(true);
                index_option_active = index;
                access.option = index;
                can_search = true;
            }
        }
    }

    private class ChangeSearch implements DocumentListener {
        @Override
        public void changedUpdate(DocumentEvent event) {}

        @Override
        public void insertUpdate(DocumentEvent event) {
            if (can_search)
                print_table(access.select_databookbyoption(inputs[index_option_active].field.getText()));
        }

        @Override
        public void removeUpdate(DocumentEvent event) {
            if (can_search)
                print_table(access.select_databookbyoption(inputs[index_option_active].field.getText()));
        }
    }

    public static class Access {
        protected PreparedStatement sql_selectdatabyoptions[];
        protected PreparedStatement sql_selectall;
        protected int option = -1;
        protected int type_book = 1;

        public Access(Connection connection) {
            this(connection, "SELECT * FROM libros WHERE activo=1 AND", "SELECT * FROM libros WHERE activo=1");
        }

        public Access(Connection connection, String sql_data, String sql_all) {
            sql_selectdatabyoptions = new PreparedStatement[3];
            try {
                sql_selectdatabyoptions[0] = connection.prepareStatement(sql_data + " titulo LIKE ?");
                sql_selectdatabyoptions[1] = connection.prepareStatement(sql_data + " autor LIKE ?");
                sql_selectdatabyoptions[2] = connection.prepareStatement(sql_data + " editorial LIKE ?");
                sql_selectall = connection.prepareStatement(sql_all);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public ResultSet select_databookbyoption(String value) {
            ResultSet result = null;
            if (!value.isEmpty()) {
                try {
                    sql_selectdatabyoptions[option].setString(1, value + '%');
                    result = sql_selectdatabyoptions[option].executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        public ResultSet select_alldata() {
            ResultSet result = null;
            try {
                result = sql_selectall.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}