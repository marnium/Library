package system.admin;

import system.components.*;
import system.connector.*;
import system.resource.Resource;

import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Toolkit;

import java.sql.*;

public class Admin extends javax.swing.JFrame{
    private static final long serialVersionUID = 1L;
    private final int id_login;
    private Connection connection = null;
    private GroupLayout layout;
    private Menu menu;
    private JPanel[] paneles;
    private JPanel panel_active;
    private JPanel[] presentation;
    private int index_item_active;
    private int[] offset;

    public Admin(int type_user, int id_user, int id_login) {
        super("Administrador");

        this.id_login = id_login;

        // Crear y establecer menu
        create_menu();
        add(menu, BorderLayout.NORTH);
        menu.add_menu_listener(new MenuListener(){
            @Override
            public void changed_item_enable(MenuEvent event) {
                int index = event.get_index();
                if (event.get_type_item() == MenuEvent.SUBMENU) {
                    index += offset[index_item_active];
                    if (index < 14) {
                        layout.replace(panel_active, paneles[index]);
                        panel_active = paneles[index];
                    } else if (index == 14) {
                        logout();
                        system.login.Login login = new system.login.Login();
                        login.setVisible(true);
                        login.createAccess();
                        dispose();
                    } else {
                        logout();
                        System.exit(0);
                    }
                } else {
                    layout.replace(panel_active, presentation[index]);
                    panel_active = presentation[index];
                    index_item_active = index;
                }
            }
        });

        // Establecer conexión con la base de datos
        try {
            connection = Connector.get_connection();
        } catch (SQLException e) {
            System.out.println("Conexión fallida en Admin");
        }

        //Paneles de presentación
        presentation = new JPanel[] {
            new Presentation("book.png", "Libros"),
            new Presentation("user.png", "Usuarios"),
            new Presentation("lend.png", "Prestamos"),
            new Presentation("returnbook.png", "Devoluciones"),
            new Presentation("sanction.png", "Sanciones"),
            new Presentation("login.png", "Sesiones"),
            new Presentation("admin.png", "Administrador")
        };
        index_item_active = 0;
        panel_active = presentation[index_item_active];
        offset = new int[7];
        offset[0] = 0;
        offset[1] = 3;
        offset[2] = 6;
        offset[3] = 8;
        offset[4] = 10;
        offset[5] = 11;
        offset[6] = 12;

        //Paneles de componentes
        paneles = new javax.swing.JPanel[14];
        paneles[0] = new BookAdd(connection);
        paneles[1] = new BookUpdate(connection);
        paneles[2] = new BookPrint(connection);
        paneles[3] = new UserAdd(connection);
        paneles[4] = new UserUpdate(connection);
        paneles[5] = new UserPrint(connection);
        paneles[6] = new LendAdd(this, connection);
        paneles[7] = new LendPrint(connection);
        paneles[8] = new ReturnBook(connection);
        paneles[9] = new ReturnBookPrint(connection);
        paneles[10] = new Sanction(connection);
        paneles[11] = new Signin(connection);
        paneles[12] = new system.student.ShowData(type_user, id_user, connection);
        paneles[13] = new system.student.ChangePassword(id_user, connection);


        // Crear y establecer el panel principal de componentes
        JPanel panel_principal = new JPanel();
        layout = new GroupLayout(panel_principal);
        panel_principal.setLayout(layout);
        add(panel_principal, BorderLayout.CENTER);

        // Diseñar el espacio de visible de los componentes
        layout.setHorizontalGroup(layout.createParallelGroup()
            .addComponent(panel_active));
        layout.setVerticalGroup(layout.createParallelGroup()
            .addComponent(panel_active));

        // Configurar ventana
        setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width,
            Toolkit.getDefaultToolkit().getScreenSize().height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                logout();
            }
        });
    }

    private void create_menu() {
        String menu_items[] = {
            "Libros", "Usuarios", 
            "Prestamos", "Devoluciones", 
            "Sanciones", "Sesiones", 
            "Administrador"};
        String submenu_items[][] = {
        {"Agregar", "Editar", "Generar PDF"},
        {"Crear", "Editar", "Generar PDF"}, {"Realizar", "Generar PDF"},
        {"Devolver Libro", "Generar PDF"},
        {"Generar PDF"},
        {"Generar PDF"},
        {"Datos del Admin", "Cambiar Contraseña", "Cerrar Sesión", "Salir"}};
        String[] path_menu = {
            "admin/ico_book.png",
            "admin/ico_user.png",
            "admin/ico_lend.png",
            "admin/ico_returnbook.png",
            "admin/ico_sanction.png",
            "admin/ico_login.png",
            "admin/ico_admin.png"};
        String[][] path_submenu = {
            {"admin/ico_addbook.png", "admin/ico_editbook.png", "admin/ico_pdf.png"},
            {"admin/ico_adduser.png", "admin/ico_edituser.png", "admin/ico_pdf.png"}, 
            {"admin/ico_addlend.png", "admin/ico_pdf.png"},
            {"admin/ico_returnbook.png", "admin/ico_pdf.png"},
            {"admin/ico_pdf.png"},
            {"admin/ico_pdf.png"},
            {"ico_data.png",  "ico_pass.png", "ico_logout.png", "exit.png"}
        };
        Menu.NameItems name_items = new Menu.NameItems(menu_items, submenu_items);
        Menu.Images images = new Menu.Images(path_menu, path_submenu);
        menu = new Menu(images, name_items);
    }

    private class Presentation extends JPanel {
        private static final long serialVersionUID = 1L;

        public Presentation(String path, String title) {
            GroupLayout layoutp = new GroupLayout(this);
            setLayout(layoutp);
            layoutp.setAutoCreateGaps(true);
            layoutp.setAutoCreateContainerGaps(true);
            
            JLabel ltitle = new JLabel(title);
            ltitle.setFont(FontUse.monospaced_bold_20);
            JLabel image = new JLabel(Resource.get("admin/" + path));

            layoutp.setHorizontalGroup(layoutp.createSequentialGroup()
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
                .addGroup(layoutp.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(ltitle).addComponent(image))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
            );
            layoutp.setVerticalGroup(layoutp.createSequentialGroup()
                .addComponent(ltitle).addComponent(image));
        }
    }

    private void logout() {
        try {
            PreparedStatement sql_logout = connection.prepareStatement(
                "UPDATE sesiones SET fecha_final=DEFAULT WHERE id_sesion=?");
            sql_logout.setInt(1, id_login);
            sql_logout.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
