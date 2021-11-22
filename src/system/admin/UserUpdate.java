package system.admin;

import system.components.*;
import system.resource.Resource;

import javax.swing.*;
import java.awt.event.*;
import java.math.BigDecimal;
import javax.swing.event.*;
import java.awt.Color;

import java.sql.*;

public class UserUpdate extends JPanel {
    private static final long serialVersionUID = 1L;
    private final String str_user[] = {"Dar de Baja", "Dar de Alta"};
    private SearchUser search;
    private Access access;
    private JTable table;
    private InputGroup group_update;
    private ImageIcon images[];
    private JLabel show_update;
    private JButton jbutton_update;
    private JButton button_baja_alta;
    private int id_user;
    private int id_data;
    private int type_user;
    private int user_altabaja;

    public UserUpdate(Connection connection) {
        access = new Access(connection);

        // Crear el gestor de diseño de BookUpdate
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Crear titulo del panel
        JLabel title = new JLabel("Actualizar usuario");
        title.setFont(FontUse.monospaced_bold_20);

        // Crear el objeto de busqueda de libros e inicializar table
        search = new SearchUser(access, false, false);
        table = search.get_table();

        // Crear la entrada de datos para actualización
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
        //Crear el botón para realizar la baja y alte de los usuarios
        button_baja_alta = new JButton();
        button_baja_alta.setVisible(false);
        
        // Diseño de BookUpdate
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(title)
            .addGroup(layout.createSequentialGroup()
                .addComponent(search)
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
                .addComponent(search)
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
            new InputText(null, "Usuario", 20, 20),
            new InputText(null, "Nombre", 20, 50),
            new InputText(null, "Apellido Paterno", 20, 50),
            new InputText(null, "Apellido Materno", 20, 50),
            new InputNumber("Núm. Tel.", 20, 10),
            new InputText(null, "Dirección", 20, 60),
            new InputText(null, "E-Mail", 20, 50),
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
                    if (id_user != 0) {
                        button_baja_alta.setVisible(true);
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
                String str[] = new String[group_update.inputs.length - 1];
                for (int i = 0; i < str.length; i++) {
                    str[i] = group_update.inputs[i+1].field.getText();
                }
                if (1 == access.update_data(str) &&
                    1 == access.update_user(group_update.inputs[0].field.getText())) {
                    id_user = 0;
                    id_data = 0;
                    for (int i = 0; i < group_update.inputs.length; i++)
                        group_update.inputs[i].field.setText("");
                    group_update.setVisible(false);
                    jbutton_update.setVisible(false);
                    button_baja_alta.setVisible(false);
                    show_update.setIcon(images[0]);
                    show_update.setText("Mostrar");
                    JOptionPane.showMessageDialog(UserUpdate.this, "Actualizado", "Estado",
                        JOptionPane.INFORMATION_MESSAGE);
                } else
                    JOptionPane.showMessageDialog(UserUpdate.this, "No se pudo actualizar", "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        button_baja_alta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (access.has_book_toreturn()) {
                    JOptionPane.showMessageDialog(UserUpdate.this,
                        "No se puede dar de baja a este Usuario\nTiene Libros por devolver", 
                        "Estado", JOptionPane.WARNING_MESSAGE);
                } else if (1 == access.alta_baja_user()) {
                    id_user = 0;
                    id_data = 0;
                    for (int i = 0; i < group_update.inputs.length; i++)
                        group_update.inputs[i].field.setText("");
                    group_update.setVisible(false);
                    jbutton_update.setVisible(false);
                    button_baja_alta.setVisible(false);
                    show_update.setIcon(images[0]);
                    show_update.setText("Mostrar");
                    JOptionPane.showMessageDialog(UserUpdate.this, "Hecho", "Estado",
                        JOptionPane.INFORMATION_MESSAGE);
                } else
                    JOptionPane.showMessageDialog(UserUpdate.this, "No se Pudo " + str_user[user_altabaja], "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (table.getSelectedRow() < 0) {
                    id_user = 0;
                    for (int i = 0; i < group_update.inputs.length; i++)
                        group_update.inputs[i].field.setText("");
                    group_update.setVisible(false);
                    jbutton_update.setVisible(false);
                    button_baja_alta.setVisible(false);
                    show_update.setIcon(images[0]);
                    show_update.setText("Mostrar");
                } else if (!event.getValueIsAdjusting()) {
                    int row = table.getSelectedRow();
                    int length = group_update.inputs.length;
                    int offset;
                    javax.swing.table.TableModel model = table.getModel();
                    int row_model = table.convertRowIndexToModel(row);
                    user_altabaja = Integer.parseInt(model.getValueAt(row_model, 9).toString());
                    user_altabaja = (user_altabaja == 0 ? 1 : 0);
                    if (table.getColumnCount() == length) {
                        offset = 0;
                        type_user = 0;
                        id_user = Integer.parseInt(model.getValueAt(row_model, length).toString());
                        id_data = Integer.parseInt(model.getValueAt(row_model, length + 1).toString());
                    } else {
                        offset = 1;
                        type_user = 1;
                        id_user = Integer.parseInt(model.getValueAt(row_model, length + 1).toString());
                        id_data = Integer.parseInt(model.getValueAt(row_model, 0).toString());
                    }
                    System.out.println("id_usuario: " + id_user);
                    System.out.println("id_data: " + id_data);
                    System.out.println("type_user: "+user_altabaja);
                    for (int i = 0; i < length; i++)
                        group_update.inputs[i].field.setText(table.getValueAt(row, i + offset).toString());
                    group_update.setVisible(true);
                    jbutton_update.setVisible(true);
                    button_baja_alta.setText(str_user[user_altabaja]);
                    button_baja_alta.setVisible(true);
                    show_update.setIcon(images[1]);
                    show_update.setText("Ocultar");
                }
            }
        });
    }

    private class Access extends SearchUser.Access {
        private PreparedStatement[] sql_updatedatabyid;
        private Connection connection;

        public Access(Connection connection) {
            super(connection);
            this.connection = connection;
            try {
                String list_assign = "nombre=?,apellido_pat=?,apellido_mat=?,telefono=?,direccion=?,correo=?";
                sql_updatedatabyid = new PreparedStatement[2];
                sql_updatedatabyid[0] = connection.prepareStatement("UPDATE personal SET " +
                    list_assign + " WHERE id_personal=?");
                sql_updatedatabyid[1] = connection.prepareStatement("UPDATE alumnos SET " + list_assign +
                    " WHERE num_control=?");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public int update_data(String[] data) {
            int state = 0;
            try {
                sql_updatedatabyid[type_user].setString(1, data[0]);               //Nombre
                sql_updatedatabyid[type_user].setString(2, data[1]);               //Apellido Paterno
                sql_updatedatabyid[type_user].setString(3, data[2]);               //Apellido Materno
                sql_updatedatabyid[type_user].setBigDecimal(4, new BigDecimal(data[3])); //Telefono
                sql_updatedatabyid[type_user].setString(5, data[0]);                //Dirección
                sql_updatedatabyid[type_user].setString(6, data[0]);                //Correo
                sql_updatedatabyid[type_user].setInt(7, id_data);                   //Id o núm. de control
                state = sql_updatedatabyid[type_user].executeUpdate();
                System.out.println(state);
            } catch (SQLException e) {
                state = -1;
                e.printStackTrace();
            }
            return state;
        }

        public int update_user(String name_user) {
            int state = 0;
            try {
                PreparedStatement sql_updateuser = connection.prepareStatement(
                    "UPDATE usuarios SET usuario=? WHERE id_usuario=?");
                sql_updateuser.setString(1, name_user);
                sql_updateuser.setInt(2, id_user);
                state = sql_updateuser.executeUpdate();
                System.out.println(state);
            } catch (SQLException e) {
                state = -1;
                e.printStackTrace();
            }
            return state;
        }

        public int alta_baja_user() {
            int state = 0;
            try {
                PreparedStatement sql_updateuser = connection.prepareStatement(
                    "UPDATE usuarios SET activo=? WHERE id_usuario=?");
                sql_updateuser.setInt(1, user_altabaja);
                sql_updateuser.setInt(2, id_user);
                state = sql_updateuser.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return state;
        }

        public boolean has_book_toreturn() {
            boolean state = true;
            try {
                PreparedStatement sql_ask = connection.prepareStatement(
                    "SELECT id_prestamo FROM prestamos WHERE fk_usuario=? AND devuelto=0");
                sql_ask.setInt(1, id_user);
                state = sql_ask.executeQuery().first();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return state;
        }
    }
}