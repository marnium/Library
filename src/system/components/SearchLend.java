package system.components;

import javax.swing.*;
import java.awt.event.*;
import java.util.Calendar;

import java.sql.*;

public class SearchLend extends JPanel {
    private static final long serialVersionUID = 1L;
    private String[] columnsstd = {"Fecha Prestamo", "Fecha Devolución", "Núm. Control",
        "Nombre", "Apellido Paterno", "ID Libro", "Titulo", "Autor", "id_prestamo"};
    private JRadioButton radio_date;
    private InputDate date_init;
    private InputDate date_end;
    private JTable table;
    private Access access;
    private SearchListener search_listener = null;
    private int offset = 0;
    private int type_select = 0;

    public SearchLend(Access access_l, int only_noreturn_or_return) {
        access = access_l;

        if (only_noreturn_or_return < 2) {
            offset = 2;
            if (only_noreturn_or_return == 1)
                type_select = 1;
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);

        JLabel label_select = new JLabel("Seleccionar");
        JRadioButton radio_all = new JRadioButton("Todos", true);
        radio_date = new JRadioButton("Entre");
        ButtonGroup group_button = new ButtonGroup();
        group_button.add(radio_all);
        group_button.add(radio_date);
        JLabel label_and = new JLabel("Y");
        date_init = new InputDate("Fecha Inicial");
        date_end = new InputDate("Fecha Final");
        JButton button_select = new JButton("Seleccionar");

        table = new MyTable(new MyTable.MyTableModel(columnsstd));
        JScrollPane scroll = new JScrollPane(table);
        table.removeColumn(table.getColumnModel().getColumn(8));

        //Diseñar SearchLend
        layout.setHorizontalGroup(layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addComponent(label_select).addComponent(radio_all).addComponent(radio_date)
                .addComponent(date_init).addComponent(label_and).addComponent(date_end).addComponent(button_select))
            .addComponent(scroll));
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(label_select).addComponent(radio_all).addComponent(radio_date)
                .addComponent(date_init, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(label_and)
                .addComponent(date_end, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(button_select))
            .addComponent(scroll));
        
        button_select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (radio_date.isSelected()) {
                    if (date_init.getCalendar() == null || date_end.getCalendar() == null) {
                        JOptionPane.showMessageDialog(SearchLend.this, "Rellene todos los campos");
                    } else if (date_init.getCalendar().compareTo(date_end.getCalendar()) > 0) {
                        JOptionPane.showMessageDialog(SearchLend.this, "Fecha Inicial no puede ser mayor a Fecha Final");
                    } else {
                        Calendar ca = date_init.getCalendar();
                        ca.set(Calendar.HOUR_OF_DAY, 0);
                        ca.set(Calendar.MINUTE, 0);
                        ca.set(Calendar.SECOND, 0);
                        ca.set(Calendar.MILLISECOND, 0);
                        Calendar cb = date_end.getCalendar();
                        cb.set(Calendar.HOUR_OF_DAY, 18);
                        cb.set(Calendar.MINUTE, 59);
                        cb.set(Calendar.SECOND, 59);
                        cb.set(Calendar.MILLISECOND, 0);
                        Timestamp a = Timestamp.from(ca.toInstant());
                        Timestamp b = Timestamp.from(cb.toInstant());
                        print_table(access.selectsbydate(0 + offset, a, b, type_select),
                            access.selectsbydate(1 + offset, a, b, type_select));
                    }
                } else
                    print_table(access.selects(0 + offset, type_select),
                        access.selects(1 + offset, type_select));
            }
        });
    }

    public JTable get_table() {
        return table;
    }

    public void set_type_select(int typeselect) {
        this.type_select = typeselect;
    }

    public void clean_table() {
        table.setModel(new MyTable.MyTableModel(columnsstd));
        table.removeColumn(table.getColumnModel().getColumn(8));
    }

    public void set_search_listener(SearchListener listener) {
        search_listener = listener;
    }

    private void print_table(ResultSet resultp, ResultSet results) {
        MyTable.MyTableModel model_table = new MyTable.MyTableModel(columnsstd);
        table.setModel(model_table);
        table.removeColumn(table.getColumnModel().getColumn(8));
        String[] str_row = new String[columnsstd.length];
        try {
            while (resultp.next()) {
                str_row[2] = "";
                for (int j = 0, k = 1; j < columnsstd.length; j++) {
                    if (j == 2)
                        continue;
                    str_row[j] = resultp.getString(k++);
                }
                model_table.addRow(str_row);
            }
            while (results.next()) {
                for (int j = 0; j < columnsstd.length; j++)
                    str_row[j] = results.getString(j + 1);
                model_table.addRow(str_row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (table.getRowCount() == 0 && search_listener != null) {
            search_listener.no_data();
        }
    }

    public static class Access {
        private PreparedStatement[] sql_selects;
        private PreparedStatement[] sql_selectsbydate;

        public Access(Connection connection) {
            try {
                String sql_student = "SELECT fecha_inicio,fecha_final,num_control,nombre,apellido_pat," +
                    "id_libro,titulo,autor,id_prestamo FROM prestamos INNER JOIN (libros,usuarios) INNER JOIN  alumnos ON (" +
                    "prestamos.fk_libro=libros.id_libro AND prestamos.fk_usuario=usuarios.id_usuario AND " +
                    "usuarios.fk_alumno=alumnos.num_control)";
                String sql_personal = "SELECT fecha_inicio,fecha_final,nombre,apellido_pat,id_libro,titulo," +
                    "autor,id_prestamo FROM prestamos INNER JOIN (libros,usuarios) INNER JOIN  personal ON (" +
                    "prestamos.fk_libro=libros.id_libro AND prestamos.fk_usuario=usuarios.id_usuario AND " +
                    "usuarios.fk_personal=personal.id_personal)";
                sql_selects = new PreparedStatement[] {
                    connection.prepareStatement(sql_personal),
                    connection.prepareStatement(sql_student),
                    connection.prepareStatement(sql_personal + " WHERE prestamos.devuelto=?"),
                    connection.prepareStatement(sql_student + " WHERE prestamos.devuelto=?")
                };
                sql_selectsbydate = new PreparedStatement[] {
                    connection.prepareStatement(sql_personal +
                    " WHERE ?<=prestamos.fecha_inicio AND prestamos.fecha_inicio<=?"),
                    connection.prepareStatement(sql_student +
                    " WHERE ?<=prestamos.fecha_inicio AND prestamos.fecha_inicio<=?"),
                    connection.prepareStatement(sql_personal +
                    " WHERE prestamos.devuelto=? AND ?<=prestamos.fecha_inicio AND prestamos.fecha_inicio<=?"),
                    connection.prepareStatement(sql_student +
                    " WHERE prestamos.devuelto=? AND ?<=prestamos.fecha_inicio AND prestamos.fecha_inicio<=?")
                };
            } catch (SQLException e) {}
        }

        public ResultSet selects(int index, int typeselect) {
            ResultSet result = null;
            try {
                if (index > 1)
                    sql_selects[index].setInt(1, typeselect);
                result = sql_selects[index].executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }

        public ResultSet selectsbydate(int index, Timestamp a, Timestamp b, int tyselect) {
            ResultSet result = null;
            try {
                if (index > 1) {
                    sql_selectsbydate[index].setInt(1, tyselect);
                    sql_selectsbydate[index].setTimestamp(2, a);
                    sql_selectsbydate[index].setTimestamp(3, b);  
                } else {
                    sql_selectsbydate[index].setTimestamp(1, a);
                    sql_selectsbydate[index].setTimestamp(2, b);  
                }
                result = sql_selectsbydate[index].executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}