package system.login;

import system.resource.Resource;
import system.components.*;
import system.connector.*;
import system.admin.Admin;
import system.student.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.io.IOException;

import java.sql.*;

public class Login extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;
    private Image background;
    private Input[] inputs;
    private JButton jbt_exit;
    private JButton button_in;
    private JButton button_conf;
    private final JPanel canvas;
    private Access access;

    public Login() {
        super("Login");

        // Crear y agregar el canvas al jframe
        add(canvas = create_canvas());
        // Agregar componentes al canvas
        set_components();
        // Agregar acciones a los componentes
        set_acctions_to_components();

        // Configurar ventana
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 450);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public int createAccess() {
        int state = 0;
        try {
            access = new Access();
            state = 1;
        } catch (SQLException e) {
            state = -1;
            JOptionPane.showMessageDialog(Login.this, "Error, No se Pudo Conectar a la Base de Datos",
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return state;
    }

    private JPanel create_canvas () {
        background = Resource.get("login/background.png").getImage();
        return new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paint(java.awt.Graphics g) {
                g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(),this);
                this.setOpaque(false);
                super.paint(g);
            }
        };
    }

    private void set_components() {
        // Colocar logo
        JLabel jlb_logo = new JLabel(Resource.get("login/logo.png"));
        // Entrada Usuario y contrseña
        inputs = new Input[2];
        inputs[0] = new InputText(Resource.get("login/user.png"), null, 20, 20);
        inputs[1] = new InputPassword(Resource.get("login/password.png"), null, 20, 15);
        inputs[0].setBackground(Color.WHITE);
        inputs[1].setBackground(Color.WHITE);
        Border border = new CompoundBorder(BorderFactory.createLineBorder(ColorUse.blue_main),
            new EmptyBorder(4, 4, 4, 4));
        inputs[0].setBorder(border);
        inputs[1].setBorder(border);
        inputs[0].field.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        inputs[1].field.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        InputGroup group = new InputGroup(inputs, true, null, null);
        group.setOpaque(false);

        // Botón entrar
        button_in = new JButton("Entrar");
        // Boton salir
        jbt_exit = new JButton("Salir", Resource.get("login/exit.png"));
        // Boton configurar
        button_conf = new JButton("Configurar");

        // Agregar componentes al canvas
        JLabel hide = new JLabel();
        GroupLayout layout = new GroupLayout(canvas);
        canvas.setLayout(layout);
        layout.setAutoCreateContainerGaps(true);
        //layout.setAutoCreateGaps(true);
        layout.setHorizontalGroup(layout.createParallelGroup()
            .addComponent(button_conf, GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(jlb_logo)
                    .addComponent(group, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_in))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
                .addComponent(hide))
            .addComponent(jbt_exit, GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(button_conf).addComponent(jlb_logo)
            .addComponent(group).addComponent(button_in)
            .addComponent(hide)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
            .addComponent(jbt_exit)
        );
    }

    private void set_acctions_to_components() {
        jbt_exit.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        ActionListener listener = (ActionEvent event) -> {
            validate_input_and_selectid();  
        };
        ((JPasswordField) inputs[1].field).addActionListener(listener);
        button_in.addActionListener(listener);
        button_conf.addActionListener((ActionEvent event) -> {
            new ConfigureServer(Login.this).setVisible(true);
        });
    }

    private void validate_input_and_selectid() {
        String user = inputs[0].field.getText();
        String password = String.valueOf(((JPasswordField) inputs[1].field).getPassword());

        if (user.isEmpty())
            JOptionPane.showMessageDialog(Login.this, "Ingrese su usuario", "Error", JOptionPane.ERROR_MESSAGE);
        else if (password.isEmpty())
            JOptionPane.showMessageDialog(Login.this, "Ingrese su Contraseña", "Error", JOptionPane.ERROR_MESSAGE);
        else {
            ResultSet result = access.select_idandtype_user(user, password);
            if (result != null) {
                try {
                    if (result.first()) {
                        int id_user = result.getInt(1);
                        int type_user = result.getInt(2);
                        int id_login = access.insert_signin(id_user);
                        if (type_user == 3) {//Administrador -> Control total de la base de datos
                            new Admin(type_user, id_user, id_login).setVisible(true);
                            dispose();
                        } else {//Alumno o Personal -> Solo busqueda de Libros
                            new SearchBookWindow(type_user, id_user, id_login).setVisible(true);
                            dispose();
                        }
                    } else
                        JOptionPane.showMessageDialog(Login.this, "Usuario y/o Contraseña incorrecta");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Access {
        private PreparedStatement sql_insertsignin;
        private PreparedStatement sql_selectidandtype;
        private PreparedStatement sql_selectidlogin;

        public Access() throws SQLException {
            try {
                Connector.set_data(FileData.read());
                Connection connection = Connector.get_connection();
                sql_selectidandtype = connection.prepareStatement("SELECT id_usuario,tipo_usuario+0 FROM usuarios"
                        + " WHERE usuario LIKE ? AND contrasena=UNHEX(SHA1(?))");
                sql_insertsignin = connection.prepareStatement("INSERT INTO sesiones VALUES(0,?,DEFAULT,DEFAULT)");
                sql_selectidlogin = connection.prepareStatement("SELECT LAST_INSERT_ID()");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(Login.this,
                    "Error al leer el archivo de configuración",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public ResultSet select_idandtype_user(String user, String pass) {
            ResultSet result = null;
            try {
                sql_selectidandtype.setString(1, user);
                sql_selectidandtype.setString(2, pass);
                result = sql_selectidandtype.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }

        public int insert_signin(int id_user) {
            int id_login = 0;
            try {
                sql_insertsignin.setInt(1, id_user);
                sql_insertsignin.executeUpdate();
                ResultSet r = sql_selectidlogin.executeQuery();
                if (r.first())
                    id_login = r.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return id_login;
        }
    }

    private class ConfigureServer extends JDialog {
        private static final long serialVersionUID = 1L;
        private Input inputs[];
    
        public ConfigureServer(Frame owner) {
            super(owner, "Configurar Conexión");
    
            JPanel panel = new JPanel();
            GroupLayout layout = new GroupLayout(panel);
            panel.setLayout(layout);
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);
            add(panel);
    
            inputs = new Input[] {
                new InputText(null, "Servidor", 20, 50),
                new InputText(null, "Usuario", 20, 50),
                new InputPassword(null, "Contraseña", 20, 50)
            };
            inputs[0].field.setText("localhost");
            inputs[1].field.setText("root");
            InputGroup group = new InputGroup(inputs, true, "Datos de Conexión", FontUse.sansserif_bold_16);
            JButton button_ok = new JButton("Probar Conexión");
            button_ok.addActionListener((java.awt.event.ActionEvent event) -> {
                if (inputs[0].field.getText().isEmpty() || inputs[1].field.getText().isEmpty()
                        || inputs[2].field.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(ConfigureServer.this, "Rellena Todos los campos",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    DataConnector data = new DataConnector(inputs[0].field.getText(),
                            inputs[1].field.getText(), inputs[2].field.getText());
                    try {
                        FileData.write(data);
                        Connector.close_connection();
                        if (createAccess() == 1)
                            JOptionPane.showMessageDialog(ConfigureServer.this, "Conectado");
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(ConfigureServer.this, 
                                "Error al guardar los datos");
                    }
                }
            });
    
            layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
                    .addComponent(group, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
                ).addComponent(button_ok)
            );
            layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(group)
                .addComponent(button_ok)
            );
    
            // Configurar ventana
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setSize(550, 250);
            setLocationRelativeTo(null);
        }
    }
}