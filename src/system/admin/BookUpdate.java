package system.admin;

import system.components.*;
import system.resource.Resource;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.Color;

import java.sql.*;

public class BookUpdate extends JPanel {
    private static final long serialVersionUID = 1L;
    private final String str_book[] = {"Dar de Baja", "Dar de Alta"};
    private SearchBook searchbooks;
    private JTable table;
    private InputGroup group_update;
    private JLabel show_update;
    private JButton jbutton_update;
    private JButton button_baja_alta;
    private ImageIcon images[];
    private Access access;
    private int id_book;
    private int typebook;

    public BookUpdate(Connection connection) {
        //Inicializar access
        this.access = new Access(connection);

        // Crear el gestor de diseño de BookUpdate
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Crear titulo del panel
        JLabel title = new JLabel("Actualizar Libro");
        title.setFont(FontUse.monospaced_bold_20);

        // Crear el objeto de busqueda de libros e inicializar table
        searchbooks = new SearchBook(access, "Buscar Libro Por", false, false);
        table = searchbooks.get_table();

        // Crear el grupo de inputs de update
        create_group_update();
        group_update.setVisible(false);

        // Crear el boton showuptade
        images = new ImageIcon[2];
        images[0] = Resource.get("admin/left.png");
        images[1] = Resource.get("admin/right.png");
        show_update = new JLabel("Mostrar", images[0], SwingConstants.LEFT);

        // Crear el boton de actualizar
        jbutton_update = new JButton("Actualizar");
        jbutton_update.setVisible(false);
        //Crear el botón para dar de alta un libro o darlo de baja
        button_baja_alta = new JButton();
        button_baja_alta.setVisible(false);
        
        // Diseño de BookUpdate
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(title)
            .addGroup(layout.createSequentialGroup()
                .addComponent(searchbooks)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(group_update, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jbutton_update).addComponent(button_baja_alta))
                    .addComponent(show_update, GroupLayout.Alignment.LEADING))
            )
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(title)
            .addGroup(layout.createParallelGroup()
                .addComponent(searchbooks)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(group_update)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(jbutton_update).addComponent(button_baja_alta))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
                    .addComponent(show_update)
                )
            )
        );

        // Asignar manejador de eventos a los componentes
        set_listener_components();
    }
    
    private void create_group_update() {
        Input[] inputs = {
            new InputText(null, "Titulo", 15, 80),
            new InputText(null, "Autor", 15, 50),
            new InputNumber("Año Publicación", 15, 4),
            new InputText(null, "Editorial", 15, 60),
            new InputText(null, "País", 15, 15),
            new InputText(null, "Ciudad", 15, 30),
            new InputNumber("Código de barras", 15, 13),
            new InputNumber("Ejemplares", 15, 9)
        };
        group_update = new InputGroup(inputs, true, "Datos Nuevos", FontUse.sansserif_bold_16);
    }

    private void set_listener_components() {
        show_update.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (group_update.isVisible()) {
                    group_update.setVisible(false);
                    jbutton_update.setVisible(false);
                    button_baja_alta.setVisible(false);
                    show_update.setIcon(images[0]);
                    show_update.setText("Mostrar");
                } else {
                    if (id_book != 0) {
                        button_baja_alta.setVisible(false);
                        jbutton_update.setVisible(true);
                    }
                    group_update.setVisible(true);
                    show_update.setIcon(images[1]);
                    show_update.setText("Ocultar");
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                show_update.setForeground(ColorUse.blue_main);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                show_update.setForeground(Color.BLACK);
            }
        });

        jbutton_update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str[] = new String[group_update.inputs.length];
                for (int i = 0; i < str.length; i++)
                    str[i] = group_update.inputs[i].field.getText();
                if (1 == access.update_book(str, id_book)) {
                    id_book = 0;
                    for (int i = 0; i < group_update.inputs.length; i++)
                        group_update.inputs[i].field.setText("");
                    group_update.setVisible(false);
                    jbutton_update.setVisible(false);
                    button_baja_alta.setVisible(false);
                    show_update.setIcon(images[0]);
                    show_update.setText("Mostrar");
                    JOptionPane.showMessageDialog(BookUpdate.this, "Actualizado", "Estado",
                        JOptionPane.INFORMATION_MESSAGE);
                } else
                    JOptionPane.showMessageDialog(BookUpdate.this, "No se pudo actualizar", "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        button_baja_alta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (access.isin_lent_thisbook()) {
                    JOptionPane.showMessageDialog(BookUpdate.this,
                        "No se puede dar de baja este libro\nEsta en prestamo",
                        "Estado", JOptionPane.WARNING_MESSAGE);
                } else if (access.baja_alta_book() == 1) {
                    id_book = 0;
                    for (int i = 0; i < group_update.inputs.length; i++)
                        group_update.inputs[i].field.setText("");
                    group_update.setVisible(false);
                    jbutton_update.setVisible(false);
                    button_baja_alta.setVisible(false);
                    show_update.setIcon(images[0]);
                    show_update.setText("Mostrar");
                    JOptionPane.showMessageDialog(BookUpdate.this, "Hecho", "Estado",
                        JOptionPane.INFORMATION_MESSAGE);
                } else
                    JOptionPane.showMessageDialog(BookUpdate.this, "No se Pudo " + str_book[typebook],
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
        
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (table.getSelectedRow() < 0) {
                    id_book = 0;
                    for (int i = 0; i < group_update.inputs.length; i++)
                        group_update.inputs[i].field.setText("");
                    group_update.setVisible(false);
                    jbutton_update.setVisible(false);
                    button_baja_alta.setVisible(false);
                    show_update.setIcon(images[0]);
                    show_update.setText("Mostrar");
                } else if (!event.getValueIsAdjusting()) {
                    int row = table.getSelectedRow();
                    int row_model = table.convertRowIndexToModel(row);
                    id_book = Integer.parseInt(table.getModel().getValueAt(row_model, 0).toString());
                    typebook = Integer.parseInt(table.getModel().getValueAt(row_model, 9).toString());
                    typebook = (typebook == 0 ? 1 : 0);
                    for (int i = 0; i < group_update.inputs.length; i++)
                        group_update.inputs[i].field.setText(table.getValueAt(row, i).toString());
                    group_update.setVisible(true);
                    jbutton_update.setVisible(true);
                    button_baja_alta.setText(str_book[typebook]);
                    button_baja_alta.setVisible(true);
                    show_update.setIcon(images[1]);
                    show_update.setText("Ocultar");
                }
            }
        });
    }

    private class Access extends SearchBook.Access {
        private Connection connection;

        public Access(Connection connection) {
            super(connection, "SELECT * FROM libros WHERE", "SELECT * FROM libros");
            this.connection = connection;
        }

        public int update_book(String[] values, int id_libro) {
            int state = 0;
            try {
                PreparedStatement sql_updatebook = connection.prepareStatement(
                    "UPDATE libros SET titulo=?,autor=?,anio_publ=?,editorial=?,pais=?,"
                    +"ciudad=?,codigo=?,ejemplares=? WHERE id_libro=?");
                sql_updatebook.setString(1, values[0]);                //Titulo
                sql_updatebook.setString(2, values[1]);                //Autor
                sql_updatebook.setInt(3, Integer.parseInt(values[2])); //Año
                sql_updatebook.setString(4, values[3]);                //Editorial
                sql_updatebook.setString(5, values[4]);                //País
                sql_updatebook.setString(6, values[5]);                //Ciudad
                sql_updatebook.setString(7, values[6]);                //ISBN
                sql_updatebook.setInt(8, Integer.parseInt(values[7])); //Ejemplares
                sql_updatebook.setInt(9, id_libro);                   //ID libro a actualizar

                state = sql_updatebook.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return state;
        }

        public int baja_alta_book() {
            int state = 0;
            try {
                PreparedStatement sql_updatebook = connection.prepareStatement(
                    "UPDATE libros SET activo=? WHERE id_libro=?");
                sql_updatebook.setInt(1, typebook);
                sql_updatebook.setInt(2, id_book);
                state = sql_updatebook.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return state;
        }

        public boolean isin_lent_thisbook() {
            boolean state = true;
            try {
                PreparedStatement sql_ask = connection.prepareStatement(
                    "SELECT id_prestamo FROM prestamos WHERE fk_libro=? AND devuelto=0");
                sql_ask.setInt(1, id_book);
                state = sql_ask.executeQuery().first();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return state;
        }
    }
}