package system.student;

import system.components.*;
import javax.swing.*;
import java.sql.*;

public class ShowData extends JPanel {
    private static final long serialVersionUID = 1L;

    public ShowData(int typeuser, int iduser, Connection connection) {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        //Datos 
        int count_fields = (typeuser == 1 ? 8 : 7);
        Input inputs[] = new Input[count_fields];
        inputs[0] = new InputText(null, "Usuario", 20, 20);
        int i = 1;
        if (typeuser == 1) {
            inputs[i++] = new InputNumber("Núm. de Control", 20, 9);
            typeuser = 0;
        } else {
            typeuser = 1;
        }   
        inputs[i++] = new InputText(null, "Nombre", 20, 50);
        inputs[i++] = new InputText(null, "Apellido Paterno", 20, 50);
        inputs[i++] = new InputText(null, "Apellido Materno", 20, 50);
        inputs[i++] = new InputNumber("Teléfono", 20, 10);
        inputs[i++] = new InputText(null, "Dirección", 20, 60);
        inputs[i++] = new InputText(null, "Email", 20, 50);
        InputGroup group = new InputGroup(inputs, true, "Datos Personales", FontUse.monospaced_bold_20);

        //Obtener datos
        Access access = new Access(connection);
        ResultSet result = access.select_data(typeuser, iduser);
        if (result != null) {
            try {
                if (result.first()) {
                    for (int j = 0; j < inputs.length; j++) {
                        inputs[j].field.setText(result.getString(j + 1));
                        inputs[j].field.setEditable(false);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else
            System.out.println("result en ShowData es null");

        //Diseñar ShowData
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
            .addComponent(group, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(group));
    }

    private class Access {
        private PreparedStatement[] sql_selectdatabyid;

        public Access(Connection connection) {
            String name_columns = "nombre,apellido_pat,apellido_mat,telefono,direccion,correo";
            sql_selectdatabyid = new PreparedStatement[2];
            try {
                sql_selectdatabyid[0] = connection.prepareStatement("SELECT usuario,num_control," + name_columns +
                    " FROM alumnos INNER JOIN usuarios ON alumnos.num_control=usuarios.fk_alumno WHERE "
                    + "usuarios.id_usuario=?");
                sql_selectdatabyid[1] = connection.prepareStatement("SELECT usuario," + name_columns +
                    " FROM personal INNER JOIN usuarios ON personal.id_personal=usuarios.fk_personal"
                    + " WHERE usuarios.id_usuario=?");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public ResultSet select_data(int type_user, int id_user) {
            ResultSet result = null;
            try {
                sql_selectdatabyid[type_user].setInt(1, id_user);
                result = sql_selectdatabyid[type_user].executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}