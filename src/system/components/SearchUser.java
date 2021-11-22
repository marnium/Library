package system.components;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.event.*;

import java.sql.*;

public class SearchUser extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String[] columnsstd = {"Núm. Control", "Usuario", "Nombre", "Apellido Paterno", "Apellido Materno",
        "Telefono", "Dirección", "Correo", "id_usuario", "activo"};
    private static final String[] columnsper = {"Usuario", "Nombre", "Apellido Paterno", "Apellido Materno",
        "Telefono","Dirección", "Correo", "id_usuario", "id_personal", "activo"};
    private JButton buttonall;
    private JRadioButton[] radiosselect;
    private JRadioButton[] radiosopt;
    private JCheckBox checkopt;
    private JComboBox<String> type_user;
    private Input[] inputs;
    private JTable table;
    private int option_active = 0;
    private int type_selection = 0;
    private boolean can_search = false;
    private Access access;

    public SearchUser(Access access_s, boolean active_all, boolean create_typeuser) {
        access = access_s;

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);

        JLabel labelselect = new JLabel("Seleccionar");
        ButtonGroup groupbutton = new ButtonGroup();
        radiosselect = new JRadioButton[4];
        radiosselect[0] = new JRadioButton("Todos", true);
        radiosselect[1] = new JRadioButton("Administrador");
        radiosselect[2] = new JRadioButton("Personal Institución");
        radiosselect[3] = new JRadioButton("Alumno");
        groupbutton.add(radiosselect[0]);
        groupbutton.add(radiosselect[1]);
        groupbutton.add(radiosselect[2]);
        groupbutton.add(radiosselect[3]);
        buttonall = new JButton("Seleccionar");
        GroupLayout.SequentialGroup hgroupall = layout.createSequentialGroup();
        GroupLayout.ParallelGroup vgroupall = layout.createParallelGroup();
        hgroupall.addComponent(radiosselect[0]).addComponent(radiosselect[1])
            .addComponent(radiosselect[2]).addComponent(radiosselect[3]).addComponent(buttonall);
        vgroupall.addComponent(labelselect).addComponent(radiosselect[0]).addComponent(radiosselect[1])
            .addComponent(radiosselect[2]).addComponent(radiosselect[3]).addComponent(buttonall);

        checkopt = new JCheckBox("Por");
        checkopt.setVisible(false);
        GroupLayout.SequentialGroup hgroupradios = layout.createSequentialGroup();
        GroupLayout.ParallelGroup vgroupradios = layout.createParallelGroup();
        vgroupradios.addComponent(checkopt);
        GroupLayout.ParallelGroup hgroupinput = layout.createParallelGroup();
        GroupLayout.SequentialGroup vgroupinput = layout.createSequentialGroup();
        GroupLayout.ParallelGroup hgroup = layout.createParallelGroup();
        if (create_typeuser) {
            type_user = new JComboBox<>(new String[]{"Usuarios dados de baja", "Usuarios activos"});
            type_user.setSelectedIndex(1);
            type_user.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent event) {
                    if (event.getStateChange() == ItemEvent.SELECTED)
                        access.typeuser = type_user.getSelectedIndex();
                }
            });
            hgroup.addComponent(type_user);
            vgroupinput.addComponent(type_user, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
        }
        hgroupinput.addGroup(hgroupall).addGroup(hgroupradios);
        vgroupinput.addGroup(vgroupall).addGroup(vgroupradios);
        String[] str = {"Usuario", "Nombre", "Núm. Control"};
        inputs = new Input[] {
            new InputText(null, str[0], 20, 20),
            new InputText(null, str[1], 20, 50),
            new InputText(null, str[2], 20, 9)
        };
        radiosopt = new JRadioButton[str.length];
        ButtonGroup groupradios = new ButtonGroup();
        for (int i = 0; i < inputs.length; i++) {
            inputs[i].setVisible(false);
            hgroupinput.addComponent(inputs[i]);
            vgroupinput.addComponent(inputs[i]);
            radiosopt[i] = new JRadioButton(str[i]);
            groupradios.add(radiosopt[i]);
            radiosopt[i].setVisible(false);
            hgroupradios.addComponent(radiosopt[i]);
            vgroupradios.addComponent(radiosopt[i]);
        }
        radiosopt[option_active].setSelected(true);
        
        table = new MyTable(new MyTable.MyTableModel(columnsper));
        table.removeColumn(table.getColumnModel().getColumn(9));
        table.removeColumn(table.getColumnModel().getColumn(8));
        table.removeColumn(table.getColumnModel().getColumn(7));
        JScrollPane scroll = new JScrollPane(table);

        //Diseño de SearchUser
        layout.setHorizontalGroup(hgroup
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup().addComponent(labelselect).addComponent(checkopt))
                .addGroup(hgroupinput))
            .addComponent(scroll));
        layout.setVerticalGroup(vgroupinput.addComponent(scroll));
        
        //Manejadores de eventos
        buttonall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                switch (type_selection) {
                    case 0:
                        printall(); //Todos
                        break;
                    case 1:
                        print_personal(access.selects(0)); //Admins
                        break;
                    case 2:
                        print_personal(access.selects(1)); //Personal
                        break;
                    case 3:
                        print_student(access.selects(2)); //Alumnos
                }
                
            }
        });
        checkopt.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    can_search = true;
                    buttonall.setVisible(false);
                    inputs[option_active].setVisible(true);
                } else {
                    can_search = false;
                    buttonall.setVisible(true);
                    inputs[option_active].setVisible(false);
                    inputs[option_active].field.setText("");
                }
            }
        });
        ItemListener listener_all_and_admin = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    type_selection = (event.getItemSelectable() == radiosselect[0] ? 0 : 1);
                    if (checkopt.isVisible()) {
                        checkopt.setVisible(false);
                        checkopt.setSelected(false);
                        for (int j = 0; j < radiosopt.length; j++)
                            radiosopt[j].setVisible(false);
                    }
                }
            }
        };
        radiosselect[0].addItemListener(listener_all_and_admin);
        radiosselect[1].addItemListener(listener_all_and_admin);
        if (!active_all) {
            radiosselect[1].setSelected(true);
            radiosselect[0].setVisible(false);
        }
        ItemListener listener_personal_and_student = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    if (radiosselect[2] == event.getItemSelectable()) {
                        type_selection = 2;
                        radiosopt[2].setVisible(false);
                        if (radiosopt[2].isSelected())
                            radiosopt[0].setSelected(true);
                    } else {
                        type_selection = 3;
                        radiosopt[2].setVisible(true);
                    }
                    if (!checkopt.isVisible()) {
                        checkopt.setVisible(true);
                        radiosopt[0].setVisible(true);
                        radiosopt[1].setVisible(true);
                    }
                }
            }
        };
        radiosselect[2].addItemListener(listener_personal_and_student);
        radiosselect[3].addItemListener(listener_personal_and_student);
        ChangeOption listener_options = new ChangeOption();
        ChangeSearch listener_inputs = new ChangeSearch();
        for (int i = 0; i < radiosopt.length; i++) {
            radiosopt[i].addItemListener(listener_options);
            inputs[i].field.getDocument().addDocumentListener(listener_inputs);
        }
    }

    public JTable get_table() {
        return table;
    }

    private void printall() {
        ResultSet result = access.selects(1);
        MyTable.MyTableModel model_table = new MyTable.MyTableModel(columnsstd);
        table.setModel(model_table);
        table.removeColumn(table.getColumnModel().getColumn(9));
        table.removeColumn(table.getColumnModel().getColumn(8));
        String[] str_row = new String[columnsstd.length];
        try {
            while (result.next()) {
                str_row[0] = "";
                for (int j = 1; j < columnsstd.length; j++)
                    str_row[j] = result.getString(j);
                model_table.addRow(str_row);
            }
            result = access.selects(2);
            while (result.next()) {
                for (int j = 0; j < columnsstd.length; j++)
                    str_row[j] = result.getString(j + 1);
                model_table.addRow(str_row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void print_personal(ResultSet result) {
        MyTable.MyTableModel model_table = new MyTable.MyTableModel(columnsper);
        table.setModel(model_table);
        table.removeColumn(table.getColumnModel().getColumn(9));
        table.removeColumn(table.getColumnModel().getColumn(8));
        table.removeColumn(table.getColumnModel().getColumn(7));
        if (result != null) {
            try {
                String[] str_row = new String[columnsper.length];
                while (result.next()) {
                    for (int j = 0; j < columnsper.length; j++)
                        str_row[j] = result.getString(j + 1);
                    model_table.addRow(str_row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void print_student(ResultSet result) {
        MyTable.MyTableModel model_table = new MyTable.MyTableModel(columnsstd);
        table.setModel(model_table);
        table.removeColumn(table.getColumnModel().getColumn(9));
        table.removeColumn(table.getColumnModel().getColumn(8));
        if (result != null) {
            String[] str_row = new String[columnsstd.length];
            try {
                while (result.next()) {
                    for (int j = 0; j < columnsstd.length; j++)
                        str_row[j] = result.getString(j + 1);
                    model_table.addRow(str_row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class ChangeOption implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                int i = 0;
                for (; i < radiosopt.length; i++)
                    if (event.getItemSelectable() == radiosopt[i]) break;
                if (can_search) {
                    inputs[option_active].setVisible(false);
                    inputs[i].setVisible(true);
                }
                option_active = i;
            }
        }
    }

    private class ChangeSearch implements DocumentListener {
        @Override
        public void changedUpdate(DocumentEvent e) {}
        
        @Override
        public void insertUpdate(DocumentEvent e) {
            if (can_search) {
                if (type_selection == 2) {
                    print_personal(access.select_personalbyopts(option_active,
                        inputs[option_active].field.getText()));
                } else
                    print_student(access.select_studentbyopts(option_active,
                        inputs[option_active].field.getText()));
            }
        }
        @Override
        public void removeUpdate(DocumentEvent e) {
            if (can_search) {
                if (type_selection == 2)
                    print_personal(access.select_personalbyopts(option_active,
                        inputs[option_active].field.getText()));
                else
                    print_student(access.select_studentbyopts(option_active,
                        inputs[option_active].field.getText()));
            }
        }
    }

    public static class Access {
        protected int typeuser = 1;
        protected PreparedStatement[] sql_selects;
        protected PreparedStatement[] sql_selectstudentbyopts;
        protected PreparedStatement[] sql_selectpersonalbyopts;

        public Access(Connection connection) {
            this(connection, "", " WHERE ");
        }

        public Access(Connection connection, String sql_where, String sql_where_and) {
            try {
                String columns = "usuario,nombre,apellido_pat,apellido_mat,telefono,direccion,correo,id_usuario";
                String joinstudent = "SELECT num_control," + columns + ",activo FROM alumnos INNER JOIN usuarios ON("
                    + "alumnos.num_control=usuarios.fk_alumno)";
                String joinpersonal = "SELECT " + columns + ",id_personal,activo FROM personal INNER JOIN  usuarios ON("
                    + " personal.id_personal=usuarios.fk_personal)";
                sql_selects = new PreparedStatement[3];
                sql_selects[0] = connection.prepareStatement(joinpersonal + sql_where_and + "usuarios.tipo_usuario=3");
                sql_selects[1] = connection.prepareStatement(joinpersonal + sql_where);
                sql_selects[2] = connection.prepareStatement(joinstudent + sql_where);
                sql_selectstudentbyopts = new PreparedStatement[3];
                sql_selectstudentbyopts[0] = connection.prepareStatement(joinstudent + sql_where_and +"usuario LIKE ?");
                sql_selectstudentbyopts[1] = connection.prepareStatement(joinstudent + sql_where_and +"alumnos.nombre LIKE ?");
                sql_selectstudentbyopts[2] = connection.prepareStatement(joinstudent + sql_where_and +"alumnos.num_control LIKE ?");
                sql_selectpersonalbyopts = new PreparedStatement[2];
                sql_selectpersonalbyopts[0] = connection.prepareStatement(joinpersonal + sql_where_and +"usuario LIKE ?");
                sql_selectpersonalbyopts[1] = connection.prepareStatement(joinpersonal + sql_where_and +"personal.nombre LIKE ?");
            } catch (SQLException e) {}
        }

        public ResultSet selects(int index) {
            ResultSet result = null;
            try {
                result = sql_selects[index].executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }

        public ResultSet select_studentbyopts(int index, String value) {
            ResultSet result = null;
            if (!value.isEmpty()) {
                try {
                    sql_selectstudentbyopts[index].setString(1, value + '%');
                    result = sql_selectstudentbyopts[index].executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        public ResultSet select_personalbyopts(int index, String value) {
            ResultSet result = null;
            if (!value.isEmpty()) {
                try {
                    sql_selectpersonalbyopts[index].setString(1, value + '%');
                    result = sql_selectpersonalbyopts[index].executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    }
}