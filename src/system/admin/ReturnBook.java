package system.admin;

import system.components.*;
import system.resource.Resource;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.Color;

import java.sql.*;

public class ReturnBook extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private InputGroup group;
    private ImageIcon images[];
    private JLabel show_book;
    private JButton button;
    private int id_lend;
    private int id_book;
    private Access access;

    public ReturnBook(Connection connection) {
        access = new Access(connection);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Titulo
        JLabel title = new JLabel("Devolver Libro");
        title.setFont(FontUse.monospaced_bold_20);

        // Crear el objeto de busqueda de prestamos
        SearchLend search = new SearchLend(new SearchLend.Access(connection), 0);
        table = search.get_table();
        search.set_search_listener(new SearchListener(){
            @Override
            public void no_data() {
                JOptionPane.showMessageDialog(ReturnBook.this, "No Hay Libros Por Devolver");
            }
        });

        // Crear las entradas donde se mostraran los datos del libro seleccionado
        Input inputs[] = { 
            new InputNumber("ID del Libro", 15, 9),
            new InputText(null, "Titulo", 15, 80),
            new InputText(null, "Autor", 15, 50) };
        group = new InputGroup(inputs, true, "Devolver este Libro", FontUse.sansserif_bold_16);
        group.setVisible(false);

        // Crear el boton showuptade
        images = new ImageIcon[2];
        images[0] = Resource.get("admin/left.png");
        images[1] = Resource.get("admin/right.png");
        show_book = new JLabel("Mostrar", images[0], SwingConstants.LEFT);

        // Crear el boton devolver libro
        button = new JButton("Devolver Libro");
        button.setVisible(false);

        // Diseño de ReturnBook
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(title)
            .addGroup(layout.createSequentialGroup()
                .addComponent(search)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(group, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(button)
                    .addComponent(show_book, GroupLayout.Alignment.LEADING))
            )
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(title)
            .addGroup(layout.createParallelGroup()
                .addComponent(search)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(group)
                    .addComponent(button)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
                    .addComponent(show_book)
                )
            )
        );

        // Asignar manejador de eventos a los componentes
        set_listener_components();
    }

    private void set_listener_components() {
        show_book.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (group.isVisible()) {
                    group.setVisible(false);
                    button.setVisible(false);
                    show_book.setIcon(images[0]);
                    show_book.setText("Mostrar");
                } else {
                    if (id_lend != 0)
                        button.setVisible(true);
                    group.setVisible(true);
                    show_book.setIcon(images[1]);
                    show_book.setText("Ocultar");
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                show_book.setForeground(ColorUse.blue_main);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                show_book.setForeground(Color.BLACK);
            }
        });

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (access.return_book() != 1) {
                    JOptionPane.showMessageDialog(ReturnBook.this, "Error al devolver el libro",
                        "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    for (int i = 0; i < group.inputs.length; i++)
                        group.inputs[i].field.setText("");
                    JOptionPane.showMessageDialog(ReturnBook.this, "Hecho: Libro Devuleto");
                    group.setVisible(false);
                    button.setVisible(false);
                    show_book.setIcon(images[0]);
                    show_book.setText("Mostrar");
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
        
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (table.getSelectedRow() < 0) {
                    id_lend = 0;
                    id_book = 0;
                    for (int i = 0; i < group.inputs.length; i++)
                        group.inputs[i].field.setText("");
                    group.setVisible(false);
                    button.setVisible(false);
                    show_book.setIcon(images[0]);
                    show_book.setText("Mostrar");
                } else {
                    int row = table.getSelectedRow();
                    id_lend = Integer.parseInt(table.getModel().getValueAt(table.
                        convertColumnIndexToModel(row), 8).toString());
                    id_book = Integer.parseInt(table.getValueAt(row, 5).toString());
                    System.out.println("id_prestamo = "+id_lend);
                    System.out.println("id_libro = "+id_book);
                    for (int i = 0; i < group.inputs.length; i++)
                        group.inputs[i].field.setText(table.getValueAt(row, i + 5).toString());
                    group.setVisible(true);
                    button.setVisible(true);
                    show_book.setIcon(images[1]);
                    show_book.setText("Ocultar");
                }
            }
        });
    }

    private class Access {
        private Connection connection;

        public Access(Connection connection) {
            this.connection = connection;
        }

        public int return_book() {
            int state = 0;
            try {
                PreparedStatement sql_updatelend = connection.prepareStatement(
                    "UPDATE prestamos SET devuelto=1 WHERE id_prestamo=?");
                PreparedStatement sql_returnbook = connection.prepareStatement(
                    "UPDATE libros SET ejemplares=ejemplares+1 WHERE id_libro=?");
                sql_updatelend.setInt(1, id_lend);
                if (sql_updatelend.executeUpdate() == 1) {
                    sql_returnbook.setInt(1, id_book);
                    state = sql_returnbook.executeUpdate();
                    PreparedStatement sql_updatesanction = connection.prepareStatement(
                    "UPDATE sanciones SET cumplido=1 WHERE fk_prestamo=?");
                    sql_updatesanction.setInt(1, id_lend);
                    sql_updatesanction.executeUpdate();
                }
            } catch (SQLException e) {
                state = -1;
                e.printStackTrace();
            }
            return state;
        }
    }
}