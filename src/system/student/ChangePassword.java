package system.student;

import system.components.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class ChangePassword extends JPanel {
    private static final long serialVersionUID = 1L;
    private InputGroup group;
    private final int iduser;
    private Access access;

    public ChangePassword(int id_user, Connection connection) {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        iduser = id_user;
        this.access = new Access(connection);

        //Entrada de contraseña
        Input inputs[] = {
            new InputPassword(null, "Contraseña Actual", 20, 15),
            new InputPassword(null, "Contraseña Nueva", 20, 15)
        };
        group = new InputGroup(inputs, true, "Cambiar Contraseña", FontUse.monospaced_bold_20);

        //Botón actualizar contraseña
        JButton button = new JButton("Cambiar Contraseña");
        button.addActionListener((ActionEvent event) -> {
            if (group.inputs[0].field.getText().isEmpty() || group.inputs[1].field.getText().isEmpty()) {
                JOptionPane.showMessageDialog(ChangePassword.this, "Rellena todos los campos", "Campos Vacíos",
                        JOptionPane.ERROR_MESSAGE);
            } else if (!access.is_password_correct(iduser, group.inputs[0].field.getText())) {
                JOptionPane.showMessageDialog(ChangePassword.this, "Contraseña actual incorrecta", "Estado",
                        JOptionPane.ERROR_MESSAGE);
            } else if (access.update_passwordbyid(iduser, group.inputs[1].field.getText()) != 1) {
                JOptionPane.showMessageDialog(ChangePassword.this, "No se pudo modificar la contraseña", "Estado",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(ChangePassword.this, "Contraseña Modificada");
            }
        });
        JButton button_clean = new JButton("Limpiar");
        button_clean.addActionListener((ActionEvent event) -> {
            group.inputs[0].field.setText("");
            group.inputs[1].field.setText("");
        });

        //Diseño de ChangePassword
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(group, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createSequentialGroup().addComponent(button).addComponent(button_clean)))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(group)
            .addGroup(layout.createParallelGroup().addComponent(button).addComponent(button_clean))
        );
    }

    private class Access {
        private PreparedStatement sql_updatepasswordbyid;
        private PreparedStatement sql_selectpasswordbyid;

        public Access(Connection connection) {
            try {
                sql_updatepasswordbyid = connection.prepareStatement("UPDATE usuarios SET contrasena=UNHEX(SHA1(?))"
                    + " WHERE id_usuario=?");
                sql_selectpasswordbyid = connection.prepareStatement("SELECT contrasena=UNHEX(SHA1(?)) FROM usuarios WHERE id_usuario=?");
            } catch (SQLException e) {}
        }

        public int update_passwordbyid(int id_user, String pass) {
            int state = 0;
            try {
                sql_updatepasswordbyid.setString(1, pass);
                sql_updatepasswordbyid.setInt(2, id_user);
                state = sql_updatepasswordbyid.executeUpdate();
            } catch (SQLException e) {
                state = -1;
                e.printStackTrace();
            }
            return state;
        }

        public boolean is_password_correct(int id_user, String pass) {
            boolean state = false;
            try {
                sql_selectpasswordbyid.setString(1, pass);
                sql_selectpasswordbyid.setInt(2, id_user);
                ResultSet result = sql_selectpasswordbyid.executeQuery();
                if (result.first())
                    state = result.getBoolean(1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return state;
        }
    }
}