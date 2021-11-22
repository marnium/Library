package system.admin;

import system.components.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.Calendar;
import java.io.FileNotFoundException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.sql.*;

public class Signin extends JPanel {
    private static final long serialVersionUID = 1L;
    private final String[] columns = {"Usuario", "Núm. Control", "Nombre", "Apellido Paterno",
        "Apellido Materno", "Fecha Inicio", "Fecha Final"};
    private JRadioButton radio_date;
    private InputDate date_init;
    private InputDate date_end;
    private JTable table;
    private Access access;

    public Signin(Connection connection) {
        access = new Access(connection);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        //Titulo
        JLabel title = new JLabel("Generar PDF");
        title.setFont(FontUse.monospaced_bold_20);

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
        JButton button_print = new JButton("Generar PDF");

        table = new MyTable(new MyTable.MyTableModel(columns));
        JScrollPane scroll = new JScrollPane(table);

        //Diseñar SearchLend
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(title)
            .addGroup(layout.createSequentialGroup()
                .addComponent(label_select).addComponent(radio_all).addComponent(radio_date)
                .addComponent(date_init).addComponent(label_and).addComponent(date_end).addComponent(button_select))
            .addComponent(scroll)
            .addComponent(button_print));
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(title)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(label_select).addComponent(radio_all).addComponent(radio_date)
                .addComponent(date_init, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(label_and)
                .addComponent(date_end, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(button_select))
            .addComponent(scroll)
            .addComponent(button_print));
        
        button_select.addActionListener((ActionEvent event) -> {
            if (radio_date.isSelected()) {
                if (date_init.getCalendar() == null || date_end.getCalendar() == null) {
                    JOptionPane.showMessageDialog(Signin.this, "Rellene todos los campos");
                } else if (date_init.getCalendar().compareTo(date_end.getCalendar()) > 0) {
                    JOptionPane.showMessageDialog(Signin.this, "Fecha Inicial no puede ser mayor a Fecha Final");
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
                    print_table(access.selectsbydate(0, a, b),
                            access.selectsbydate(1, a, b));
                }
            } else
                print_table(access.selects(0),
                        access.selects(1));
        });
        button_print.addActionListener((ActionEvent event) -> {
            int count_row = table.getRowCount();
            if (count_row > 0) {
                JFileChooser chooser_file = new JFileChooser();
                int option = chooser_file.showSaveDialog(Signin.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    java.io.File path_file = chooser_file.getSelectedFile();
                    try {
                        Document doc = new Document(PageSize.LETTER, 57, 57, 57, 57);
                        PdfWriter.getInstance(doc, new java.io.FileOutputStream(path_file + ".pdf"));
                        doc.open();
                        Paragraph prg_titulo = new Paragraph("Biblioteca del Instituto Tecnologico de Pochutla\n",
                                new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
                        Paragraph prg_subtitle = new Paragraph("Sesiones\n\n",
                                new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
                        prg_titulo.setAlignment(Element.ALIGN_CENTER);
                        prg_subtitle.setAlignment(Element.ALIGN_CENTER);
                        doc.add(prg_titulo);
                        doc.add(prg_subtitle);
                        // id    titulo  autor  año   editor  país  ciudad  isbn   ejemp
                        //float widths[] = { 41.6f, 67.2f, 62.2f, 41.6f, 62.2f, 57.2f, 62.2f, 62.2f, 41.6f};
                        int count_column = table.getColumnCount();
                        PdfPTable table_imprimir = new PdfPTable(count_column);
                        //table_imprimir.setTotalWidth(widths);
                        //table_imprimir.setLockedWidth(true);
                        Font font1 = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
                        for (int i = 0; i < count_column; ++i) {
                            table_imprimir.addCell(new Paragraph(table.getColumnName(i), font1));
                        }
                        font1 = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
                        for (int i = 0; i < count_row; i++) {
                            for (int j = 0; j < count_column; j++) {
                                table_imprimir.addCell(new Paragraph(table.getValueAt(i, j).toString(), font1));
                            }
                        }
                        doc.add(table_imprimir);
                        doc.close();
                        JOptionPane.showMessageDialog(Signin.this, "Datos Guardados Correctamente");
                    }catch (FileNotFoundException | DocumentException e) {
                        JOptionPane.showMessageDialog(Signin.this, "Error al guardar los datos", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(Signin.this, "No hay datos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void print_table(ResultSet resultp, ResultSet results) {
        MyTable.MyTableModel model_table = new MyTable.MyTableModel(columns);
        table.setModel(model_table);
        String[] str_row = new String[columns.length];
        try {
            while (resultp.next()) {
                str_row[1] = "";
                for (int j = 0, k = 1; j < columns.length; j++) {
                    if (j == 1)
                        continue;
                    str_row[j] = resultp.getString(k++);
                }
                model_table.addRow(str_row);
            }
            while (results.next()) {
                for (int j = 0; j < columns.length; j++)
                    str_row[j] = results.getString(j + 1);
                model_table.addRow(str_row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class Access {
        private PreparedStatement[] sql_selects;
        private PreparedStatement[] sql_selectsbydate;

        public Access(Connection connection) {
            try {
                String sql_student = "SELECT usuario,num_control,nombre,apellido_pat," +
                    "apellido_mat,fecha_inicio,fecha_final FROM sesiones INNER JOIN" +
                    " usuarios INNER JOIN  alumnos ON (sesiones.fk_usuario=usuarios.id_usuario" +
                    " AND usuarios.fk_alumno=alumnos.num_control)";
                String sql_personal = "SELECT usuario,nombre,apellido_pat,apellido_mat," +
                    "fecha_inicio,fecha_final FROM sesiones INNER JOIN usuarios INNER JOIN" +
                    " personal ON (sesiones.fk_usuario=usuarios.id_usuario AND " +
                    "usuarios.fk_personal=personal.id_personal)";
                sql_selects = new PreparedStatement[] {
                    connection.prepareStatement(sql_personal),
                    connection.prepareStatement(sql_student),
                };
                sql_selectsbydate = new PreparedStatement[] {
                    connection.prepareStatement(sql_personal +
                    " WHERE ?<=sesiones.fecha_inicio AND sesiones.fecha_inicio<=?"),
                    connection.prepareStatement(sql_student +
                    " WHERE ?<=sesiones.fecha_inicio AND sesiones.fecha_inicio<=?")
                };
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

        public ResultSet selectsbydate(int index, Timestamp a, Timestamp b) {
            ResultSet result = null;
            try {
                sql_selectsbydate[index].setTimestamp(1, a);
                sql_selectsbydate[index].setTimestamp(2, b);  
                result = sql_selectsbydate[index].executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}