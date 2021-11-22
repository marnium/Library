package system.admin;

import system.components.*;
import system.resource.Resource;

import javax.swing.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.sql.*;

public class BookPrint extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable table;

    public BookPrint(Connection connection) {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel title = new JLabel("Imprimir Libros registrados");
        title.setFont(FontUse.monospaced_bold_20);

        SearchBook search_book = new SearchBook(new SearchBook.Access(connection, 
            "SELECT * FROM libros WHERE activo=? AND", "SELECT * FROM libros WHERE activo=?") {
            @Override
            public ResultSet select_databookbyoption(String value) {
                ResultSet result = null;
                if (!value.isEmpty()) {
                    try {
                        sql_selectdatabyoptions[option].setInt(1, type_book);
                        sql_selectdatabyoptions[option].setString(2, value + '%');
                        result = sql_selectdatabyoptions[option].executeQuery();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }
    
            @Override
            public ResultSet select_alldata() {
                ResultSet result = null;
                try {
                    sql_selectall.setInt(1, type_book);
                    result = sql_selectall.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return result;
            }
        }, "Seleccionar", true, true);
        search_book.active_space_border();
        table = search_book.get_table();
        JButton button = new JButton("Generar PDF", Resource.get("admin/pdf.png"));
        button.addActionListener((ActionEvent event) -> {
            int count_row = table.getRowCount();
            if (count_row > 0) {
                JFileChooser chooser_file = new JFileChooser();
                int option = chooser_file.showSaveDialog(BookPrint.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    java.io.File path_file = chooser_file.getSelectedFile();
                    try {
                        Document doc = new Document(PageSize.LETTER, 57, 57, 57, 57);
                        PdfWriter.getInstance(doc, new java.io.FileOutputStream(path_file + ".pdf"));
                        doc.open();
                        Paragraph prg_titulo = new Paragraph("Biblioteca del Instituto Tecnologico de Pochutla\n",
                                new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
                        prg_titulo.setAlignment(Element.ALIGN_CENTER);
                        Paragraph prg_subtitle = new Paragraph("Libros Registrados\n\n",
                                new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
                        prg_subtitle.setAlignment(Element.ALIGN_CENTER);
                        doc.add(prg_titulo);
                        doc.add(prg_subtitle);
                        // titulo  autor  año   editor  país  ciudad  isbn   ejemp
                        float widths[] = { 81.8f, 76.2f, 41.6f, 75.2f, 57.2f, 62.2f, 62.2f, 41.6f};
                        int count_column = table.getColumnCount();
                        PdfPTable table_imprimir = new PdfPTable(count_column);
                        table_imprimir.setTotalWidth(widths);
                        table_imprimir.setLockedWidth(true);
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
                        JOptionPane.showMessageDialog(BookPrint.this, "Datos Guardados Correctamente");
                    }catch (FileNotFoundException | DocumentException e) {
                        JOptionPane.showMessageDialog(BookPrint.this, "Error al guardar los datos", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(BookPrint.this, "No hay datos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(title).addComponent(search_book).addComponent(button));
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(title).addComponent(search_book).addComponent(button));
    }
}