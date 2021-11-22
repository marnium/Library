package system.admin;

import system.components.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.sql.*;

public class Sanction extends JPanel {
    private static final long serialVersionUID = 1L;
    private final String[] columns = {"Inicio Sanción", "Fin Sanción", "Núm. Control",
        "Nombre", "Apellido Paterno", "ID Libro", "Titulo", "Autor"};
    private final JRadioButton radios[];
    private JTable table;
    private Access access;
    private int radio_selected = -1;

    public Sanction (Connection connection) {
        access = new Access(connection);

        //Insertar nuevas sanciones, si corresponde, de acuerdo a la fecha actual
        access.insert_sancion();

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        //Titulo
        JLabel title = new JLabel("Generar PDF");
        title.setFont(FontUse.monospaced_bold_20);

        JLabel label_select = new JLabel("Seleccionar");
        radios = new JRadioButton[3];
        radios[0] = new JRadioButton("Todos", true);
        radios[1] = new JRadioButton("No Cumplidos");
        radios[2] = new JRadioButton("Cumplidos");
        ButtonGroup group_button = new ButtonGroup();
        group_button.add(radios[0]);
        group_button.add(radios[1]);
        group_button.add(radios[2]);
        ChangeRadio listener = new ChangeRadio();
        radios[0].addItemListener(listener);
        radios[1].addItemListener(listener);
        radios[2].addItemListener(listener);
        JButton button_select = new JButton("Seleccionar");
        JButton button_print = new JButton("Generar PDF");

        table = new MyTable(new MyTable.MyTableModel(columns));
        JScrollPane scroll = new JScrollPane(table);

        //Diseñar Sanction
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(title)
            .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                .addComponent(label_select)
                .addComponent(radios[0]).addComponent(radios[1]).addComponent(radios[2])
                .addComponent(button_select))
            .addComponent(scroll)
            .addComponent(button_print));
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(title)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(label_select)
                .addComponent(radios[0]).addComponent(radios[1]).addComponent(radios[2])
                .addComponent(button_select))
            .addComponent(scroll)
            .addComponent(button_print));
        
        button_select.addActionListener((ActionEvent event) -> {
            int index = 0;
            if (radio_selected >= 0)
                index = 2;
            print_table(access.selects(index, radio_selected),
                    access.selects(index + 1, radio_selected));
        });
        button_print.addActionListener((ActionEvent event) -> {
            int count_row = table.getRowCount();
            if (count_row > 0) {
                JFileChooser chooser_file = new JFileChooser();
                int option = chooser_file.showSaveDialog(Sanction.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    java.io.File path_file = chooser_file.getSelectedFile();
                    try {
                        Document doc = new Document(PageSize.LETTER, 57, 57, 57, 57);
                        PdfWriter.getInstance(doc, new java.io.FileOutputStream(path_file + ".pdf"));
                        doc.open();
                        Paragraph prg_titulo = new Paragraph("Biblioteca del Instituto Tecnologico de Pochutla\n",
                                new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
                        Paragraph prg_subtitle = new Paragraph("Sanciones\n\n",
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
                        for (int i = 0; i < count_row; ++i) {
                            for (int j = 0; j < count_column; j++) {
                                table_imprimir.addCell(new Paragraph(table.getValueAt(i, j).toString(), font1));
                            }
                        }
                        doc.add(table_imprimir);
                        doc.close();
                        JOptionPane.showMessageDialog(Sanction.this, "Datos Guardados Correctamente");
                    }catch (FileNotFoundException | DocumentException e) {
                        JOptionPane.showMessageDialog(Sanction.this, "Error al guardar los datos", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(Sanction.this, "No hay datos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void print_table(ResultSet resultp, ResultSet results) {
        MyTable.MyTableModel model_table = new MyTable.MyTableModel(columns);
        table.setModel(model_table);
        String[] str_row = new String[columns.length];
        try {
            while (resultp.next()) {
                str_row[2] = "";
                for (int j = 0, k = 1; j < columns.length; j++) {
                    if (j == 2)
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
        if (table.getRowCount() == 0 )
            JOptionPane.showMessageDialog(Sanction.this, "No hay sanciones");
    }

    private class ChangeRadio implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Object o = event.getItemSelectable();
                if (o == radios[0])
                    radio_selected = -1;
                else if (o == radios[1])
                    radio_selected = 0;
                else
                    radio_selected = 1;
            }
        }
    }

    public static class Access {
        private PreparedStatement[] sql_selects;
        private Connection connection;

        public Access(Connection connection) {
            this.connection = connection;
            try {
                String sql_student = "SELECT inicio_sancion,fin_sancion,num_control,nombre,apellido_pat," +
                    "id_libro,titulo,autor FROM sanciones INNER JOIN prestamos INNER JOIN (libros,usuarios)"+
                    " INNER JOIN alumnos ON (sanciones.fk_prestamo=prestamos.id_prestamo AND " +
                    "prestamos.fk_libro=libros.id_libro AND prestamos.fk_usuario=usuarios.id_usuario AND " +
                    "usuarios.fk_alumno=alumnos.num_control)";
                String sql_personal = "SELECT inicio_sancion,fin_sancion,nombre,apellido_pat,id_libro,titulo," +
                    "autor,id_prestamo FROM sanciones INNER JOIN prestamos INNER JOIN (libros,usuarios) " +
                    "INNER JOIN  personal ON (sanciones.fk_prestamo=prestamos.id_prestamo AND " +
                    "prestamos.fk_libro=libros.id_libro AND prestamos.fk_usuario=usuarios.id_usuario AND " +
                    "usuarios.fk_personal=personal.id_personal)";
                sql_selects = new PreparedStatement[] {
                    connection.prepareStatement(sql_personal),
                    connection.prepareStatement(sql_student),
                    connection.prepareStatement(sql_personal + " WHERE sanciones.cumplido=?"),
                    connection.prepareStatement(sql_student + " WHERE sanciones.cumplido=?")
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

        public void insert_sancion() {
            //Insertar nuevas sanciones
            try {
                PreparedStatement sql_selectlend = connection.prepareStatement(
                    "SELECT id_prestamo FROM prestamos WHERE devuelto=0 AND fecha_final<NOW()");
                PreparedStatement sql_selectsantion = connection.prepareStatement(
                    "SELECT id_sancion FROM sanciones WHERE fk_prestamo=?");
                PreparedStatement sql_insertsanction = connection.prepareStatement(
                    "INSERT INTO sanciones VALUES(0,?,DEFAULT,DEFAULT,0)");
                ResultSet r = sql_selectlend.executeQuery();
                while (r.next()) {
                    sql_selectsantion.setInt(1, r.getInt(1));
                    if (!sql_selectsantion.executeQuery().next()) {
                        sql_insertsanction.setInt(1, r.getInt(1));
                        sql_insertsanction.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}