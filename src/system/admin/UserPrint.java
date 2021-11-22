package system.admin;

import system.components.*;
import system.resource.Resource;

import javax.swing.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.sql.*;

public class UserPrint extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable table;

    public UserPrint(Connection connection) {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel title = new JLabel("Imprimir Usuarios registrados");
        title.setFont(FontUse.monospaced_bold_20);

        SearchUser search = new SearchUser(new SearchUser.Access(connection, " WHERE activo=?", " WHERE activo=? AND ") {
            public ResultSet selects(int index) {
                ResultSet result = null;
                try {
                    sql_selects[index].setInt(1, typeuser);
                    result = sql_selects[index].executeQuery();
                } catch (SQLException e) {}
                return result;
            }
    
            public ResultSet select_studentbyopts(int index, String value) {
                ResultSet result = null;
                if (!value.isEmpty()) {
                    try {
                        sql_selectpersonalbyopts[index].setInt(1, typeuser);
                        sql_selectstudentbyopts[index].setString(1, value + '%');
                        result = sql_selectstudentbyopts[index].executeQuery();
                    } catch (SQLException e) {}
                }
                return result;
            }
    
            public ResultSet select_personalbyopts(int index, String value) {
                ResultSet result = null;
                if (!value.isEmpty()) {
                    try {
                        sql_selectpersonalbyopts[index].setString(1, value + '%');
                        result = sql_selectpersonalbyopts[index].executeQuery();
                    } catch (SQLException e) {}
                }
                return result;
            }
        }
        , true, true);
        table = search.get_table();

        JButton button = new JButton("Generar PDF", Resource.get("admin/pdf.png"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int count_row = table.getRowCount();
                if (count_row > 0) {
                    JFileChooser chooser_file = new JFileChooser();
                    int option = chooser_file.showSaveDialog(UserPrint.this);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        java.io.File path_file = chooser_file.getSelectedFile();
                        try {
                            Document doc = new Document(PageSize.LETTER, 57, 57, 57, 57);
                            PdfWriter.getInstance(doc, new java.io.FileOutputStream(path_file + ".pdf"));
                            doc.open();
                            Paragraph prg_titulo = new Paragraph("Biblioteca del Instituto Tecnologico de Pochutla\n",
                                new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
                            Paragraph prg_subtitle = new Paragraph("Usuarios Registrados\n\n",
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
                            Font font = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
                            for (int i = 0; i < count_column; ++i) {
                                table_imprimir.addCell(new Paragraph(table.getColumnName(i), font));
                            }
                            font = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
                            for (int i = 0; i < count_row; ++i) {
                                for (int j = 0; j < count_column; j++)
                                    table_imprimir.addCell(new Paragraph(table.getValueAt(i, j).toString(), font));   
                            }
                            doc.add(table_imprimir);
                            doc.close();
                            JOptionPane.showMessageDialog(UserPrint.this, "Datos Guardados Correctamente");
                        } catch (FileNotFoundException | DocumentException e) {
                            JOptionPane.showMessageDialog(UserPrint.this, "Error al guardar los datos", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else
                    JOptionPane.showMessageDialog(UserPrint.this, "No hay datos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(title).addComponent(search).addComponent(button));
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(title).addComponent(search).addComponent(button));
    }
}