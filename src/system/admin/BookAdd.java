package system.admin;

import system.components.*;
import system.resource.Resource;

import javax.swing.*;
import java.awt.event.*;

import java.sql.*;

public class BookAdd extends JPanel {
    private static final long serialVersionUID = 1L;
    private InputGroup group_inputs;
    private Access access;

    public BookAdd(Connection connection) {
        // Inicializar el access
        this.access = new Access(connection);

        // Crear el gestor de diseño de BookAdd
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        // Este panel contendra todos los componentes
        JPanel panel_components = new JPanel();
        // Se centra el panel de componentes
        add(Box.createHorizontalGlue());
        add(panel_components);
        add(Box.createHorizontalGlue());
        
        // Crear el gestor de diseño del panel de componentes
        GroupLayout layout = new GroupLayout(panel_components);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        panel_components.setLayout(layout);
        panel_components.setAlignmentY(TOP_ALIGNMENT);

        // Crear titulo del panel de componentes
        JLabel jlb_title = new JLabel("Agregar nuevo Libro");
        jlb_title.setFont(FontUse.monospaced_bold_20);

        //Crear el grupo de Inputs, el botón de insertar datos y el botón de limpiar campos
        create_group_inputs();
        JButton button_insert = new JButton("Agregar Libro");
        button_insert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean is_empty = false;
                String values[] = new String[group_inputs.inputs.length];
                for (int i = 0; i < group_inputs.inputs.length; i++) {
                    values[i] = group_inputs.inputs[i].field.getText();
                    if (values[i].isEmpty()) {
                        is_empty = true;
                        break;
                    }
                }
                if (is_empty) {
                    JOptionPane.showMessageDialog(BookAdd.this, "Rellene todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (1 == access.insert_book(values)) {
                    JOptionPane.showMessageDialog(BookAdd.this, "Agregado", "Estado", JOptionPane.INFORMATION_MESSAGE);
                } else
                    JOptionPane.showMessageDialog(BookAdd.this, "No agregado", "Estado", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton button_clean = new JButton("Limpiar");
        button_clean.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < group_inputs.inputs.length; i++)
                    group_inputs.inputs[i].field.setText("");
            }
        });

        // Cargar la imagen, que se colocara a la derecha del grupo de inputs
        JLabel jlb_imagen = new JLabel(Resource.get("admin/bookadd.png"));

        // Diseño del panel de componentes
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(jlb_title)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(group_inputs, GroupLayout.PREFERRED_SIZE,
                    GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(button_insert).addComponent(button_clean))
                ).addComponent(jlb_imagen, 0, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            )
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(jlb_title, 0, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(group_inputs)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(button_insert).addComponent(button_clean))
                ).addComponent(jlb_imagen)
            )
        );
    }

    private void create_group_inputs() {
        Input[] inputs = {
            new InputText(null, "Titulo", 15, 80),
            new InputText(null, "Autor", 15, 50),
            new InputNumber("Año Publicación", 15, 4),
            new InputText(null, "Editorial", 15, 60),
            new InputText(null, "País", 15, 15),
            new InputText(null, "Ciudad", 15, 30),
            new InputNumber("Código de Barras", 15, 13),
            new InputNumber("Ejemplares", 15, 9)
        };
        group_inputs = new InputGroup(inputs, true, "Datos del Libro", FontUse.sansserif_bold_16);
    }

    private class Access {
        PreparedStatement sql_insertbook;
        
        public Access(Connection connection) {
            try {
                sql_insertbook = connection.prepareStatement("INSERT INTO libros VALUES (0,?,?,?,?,?,?,?,?,1)");
            } catch (SQLException e) {}
        }
        
        public int insert_book(String[] values) {
            int state = 0;
            try {
                sql_insertbook.setString(1, values[0]);                //Titulo
                sql_insertbook.setString(2, values[1]);                //Autor
                sql_insertbook.setInt(3, Integer.parseInt(values[2])); //Año
                sql_insertbook.setString(4, values[3]);                //Editorial
                sql_insertbook.setString(5, values[4]);                //País
                sql_insertbook.setString(6, values[5]);                //Ciudad
                sql_insertbook.setString(7, values[6]);                //ISBN
                sql_insertbook.setInt(8, Integer.parseInt(values[7])); //Ejemplares

                state = sql_insertbook.executeUpdate();
            } catch (SQLException e) {}

            return state;
        }
    }
}