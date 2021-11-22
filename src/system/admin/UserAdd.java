package system.admin;

import system.components.*;
import system.resource.Resource;

import javax.swing.*;
import java.awt.event.*;
import java.math.BigDecimal;

import java.sql.*;

public class UserAdd extends JPanel {
    private static final long serialVersionUID = 1L;
    private Access access;
    private JRadioButton[] type_users;
    private InputGroup inputs_data;
    private InputGroup inputs_account;
    private JCheckBox checkadmin;

    public UserAdd(Connection connection) {
        //Inicializar access
        this.access = new Access(connection);

        // Administrador de diseño
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Titulo del panel
        JLabel title = new JLabel("Nuevo usuario");
        title.setFont(FontUse.monospaced_bold_20);

        // Imagen representativo del panel
        JLabel image_rigth = new JLabel(Resource.get("admin/adduser.png"));

        // Crear el grupo de entrada de datos personales y de la cuenta
        create_inputs_data();
        create_inputs_account();
        // Crear el checkbox admin
        checkadmin = new JCheckBox("Convertir este Usuario en Administrador");
        checkadmin.setFont(FontUse.sansserif_bold_16);

        // Crear el button de agregar usuario
        JButton button_adduser = new JButton("Agregar Usuario");
        button_adduser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validate_and_inseruser();
            }
        });
        JButton button_clean = new JButton("Limpiar");
        button_clean.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < inputs_data.inputs.length; i++)
                    inputs_data.inputs[i].field.setText("");
                inputs_account.inputs[0].field.setText("");
                inputs_account.inputs[1].field.setText("");
                checkadmin.setSelected(false);
            }
        });

        //Crear los radiobutton para manejar los tipos de usuarios
        String[] str_type_users = {"Alumno", "Personal Institución"};
        ChangeTypeUser listener_radio = new ChangeTypeUser();
        JLabel label_typeusers = new JLabel("Tipo de Usuario");
        label_typeusers.setFont(FontUse.sansserif_bold_16);
        GroupLayout.SequentialGroup hgroup_radios = layout.createSequentialGroup();
        GroupLayout.ParallelGroup vgroup_radios = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        hgroup_radios.addComponent(label_typeusers);
        vgroup_radios.addComponent(label_typeusers);
        type_users = new JRadioButton[str_type_users.length];
        ButtonGroup group_options = new ButtonGroup();
        for (int i = 0; i < type_users.length; i++) {
            type_users[i] = new JRadioButton(str_type_users[i]);
            group_options.add(type_users[i]);
            type_users[i].addItemListener(listener_radio);
            type_users[i].setFont(FontUse.sansserif_bold_16);
            hgroup_radios.addComponent(type_users[i]);
            vgroup_radios.addComponent(type_users[i]);
        }
        type_users[0].setSelected(true);

        //Diseñar UserAdd
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(title)
            .addGroup(GroupLayout.Alignment.LEADING, hgroup_radios)
            .addGroup(layout.createSequentialGroup()
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER, false)
                    .addComponent(inputs_data, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkadmin)
                    .addComponent(inputs_account, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(button_adduser).addComponent(button_clean))
                ).addComponent(image_rigth, 0, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(title, 0, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGroup(vgroup_radios)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(inputs_data).addComponent(checkadmin).addComponent(inputs_account)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(button_adduser).addComponent(button_clean))
                ).addComponent(image_rigth)
            ));
    }

    public void validate_and_inseruser() {
        boolean is_empty = false;
        int offset = 1;
        String data[] = new String[inputs_data.inputs.length];
        String account[] = {inputs_account.inputs[0].field.getText(), inputs_account.inputs[1].field.getText()};
        if (type_users[0].isSelected()) {
            offset = 0;
        } else
            data[0] = "";
        for (int i = offset; i < inputs_data.inputs.length; i++) {
            if ((data[i] = inputs_data.inputs[i].field.getText()).isEmpty()) {
                is_empty = true;
                break;
            }
        }
        is_empty = (account[0].isEmpty() ? true : account[1].isEmpty() ? true : false);
        if (is_empty) {
            JOptionPane.showMessageDialog(UserAdd.this, "Rellene todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (access.exists_user(account[0]) != 0) {
            JOptionPane.showMessageDialog(UserAdd.this, "Ya Existe el Usuario\n" + account[0],
                "Error", JOptionPane.ERROR_MESSAGE);
        } else if (offset == 0 && access.exists_student(Integer.parseInt(data[0])) != 0) {
            JOptionPane.showMessageDialog(UserAdd.this, "Ya Existe un Usuario con el Núm. de Control\n" + data[0],
                "Error", JOptionPane.ERROR_MESSAGE);
        } else if (access.inser_new_user(offset, data, account, (offset == 0 ? 1 : checkadmin.isSelected() ? 3 : 2)) != 1) {
            JOptionPane.showMessageDialog(UserAdd.this, "NO se pudo agregar el Usuario", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(UserAdd.this, "Usuario Agregado");
        }
    }

    private void create_inputs_data() {
        Input[] inputs = {
            new InputNumber("Núm. Control", 20, 9),
            new InputText(null, "Nombre", 20, 50),
            new InputText(null, "Apellido Paterno", 20, 50),
            new InputText(null, "Apellido Materno", 20, 50),
            new InputNumber("Núm. Tel.", 20, 10),
            new InputText(null, "Dirección", 20, 60),
            new InputText(null, "E-Mail", 20, 50),
        };
        inputs_data = new InputGroup(inputs, true, "Datos Personales", FontUse.sansserif_bold_16);
    }

    private void create_inputs_account() {
        Input[] inputs = {
            new InputText(null, "Usuario", 20, 20),
            new InputPassword(null, "Contraseña", 20, 15)
        };
        inputs_account = new InputGroup(inputs, true, "Datos de la Cuenta", FontUse.sansserif_bold_16);
    }

    private class ChangeTypeUser implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (type_users[0] == e.getItemSelectable()) {
                    inputs_data.inputs[0].setVisible(true);
                    checkadmin.setVisible(false);
                } else {
                    inputs_data.inputs[0].setVisible(false);
                    checkadmin.setVisible(true);
                }
            }
        }
    }

    private class Access {
        private Connection connection;
        private PreparedStatement[] sql_inserdata;
        private PreparedStatement[] sql_inseraccount;

        public Access(Connection connection) {
            this.connection = connection;
            sql_inserdata = new PreparedStatement[2];
            sql_inseraccount = new PreparedStatement[2];
            try {
                sql_inserdata[0] = connection.prepareStatement("INSERT INTO alumnos VALUES(?,?,?,?,?,?,?)");
                sql_inserdata[1] = connection.prepareStatement("INSERT INTO personal VALUES(?,?,?,?,?,?,?)");
                sql_inseraccount[0] = connection.prepareStatement("INSERT INTO usuarios VALUES(0,?,UNHEX(SHA1(?)),?,?,null,1)"); //Alumno
                sql_inseraccount[1] = connection.prepareStatement("INSERT INTO usuarios VALUES(0,?,UNHEX(SHA1(?)),?,null,?,1)"); //Personal
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public int inser_new_user(int offset, String data[], String account[], int type_user) {
            int state = 0;
            try {
                sql_inserdata[offset].setInt(1, (offset == 1 ? 0 : Integer.parseInt(data[0])));  //Numero control o id_personal
                sql_inserdata[offset].setString(2, data[1]);                     //Nombre
                sql_inserdata[offset].setString(3, data[2]);                     //Apellido paterno
                sql_inserdata[offset].setString(4, data[3]);                     //Apellido materno
                sql_inserdata[offset].setBigDecimal(5, new BigDecimal(data[4])); //Telefono
                sql_inserdata[offset].setString(6, data[5]);                     //Dirección
                sql_inserdata[offset].setString(7, data[6]);                     //Correo
                sql_inserdata[offset].executeUpdate();

                int fk_data = 0;
                if (offset == 0) {
                    fk_data = Integer.parseInt(data[0]);
                } else {
                    PreparedStatement sql_selectiddata = connection.prepareStatement("SELECT LAST_INSERT_ID()");
                    ResultSet r = sql_selectiddata.executeQuery();
                    if (r.first()) {
                        System.out.println(r.getInt(1));
                        fk_data = r.getInt(1);
                    }
                }

                sql_inseraccount[offset].setString(1, account[0]);
                sql_inseraccount[offset].setString(2, account[1]);
                sql_inseraccount[offset].setInt(3, type_user);
                sql_inseraccount[offset].setInt(4, fk_data);
                state = sql_inseraccount[offset].executeUpdate();
            } catch (SQLException e) {
                state = -1;
                e.printStackTrace();
            }
            return state;
        }

        public int exists_user(String user) {
            int state = 0;
            try {
                PreparedStatement sql_validateuser = connection.prepareStatement(
                    "SELECT id_usuario FROM usuarios WHERE usuario LIKE ?");
                sql_validateuser.setString(1, user);
                if (sql_validateuser.executeQuery().first())
                    state = 1;
            } catch (SQLException e) {
                state = -1;
                e.printStackTrace();
            }
            return state;
        }

        public int exists_student(int num_control) {
            int state = 0;
            try {
                PreparedStatement sql_validatenumcontrol = connection.prepareStatement(
                    "SELECT telefono FROM alumnos WHERE num_control=?");
                sql_validatenumcontrol.setInt(1, num_control);
                if (sql_validatenumcontrol.executeQuery().first())
                    state = 1;
            } catch (SQLException e) {
                state = -1;
                e.printStackTrace();
            }
            return state;
        }
    }
}