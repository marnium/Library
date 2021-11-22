package system.student;

import system.components.*;
import system.connector.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Toolkit;
import java.awt.BorderLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SearchBookWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private final int id_login;
    private Connection connection = null;
    private GroupLayout layout;
    private JPanel[] paneles;
    private JPanel panel_active;
    private int index_panel_active;

    public SearchBookWindow(int typeuser, int iduser, int id_login) {
        super("Buscar Libros");

        this.id_login = id_login;
        // Crear menu
        Menu menu = create_menu();
        menu.add_menu_listener((MenuEvent event) -> {
            if (event.get_type_item() == MenuEvent.MENU) {
                int index = event.get_index();
                if (index < 3) {
                    layout.replace(panel_active, paneles[index]);
                    panel_active = paneles[index];
                    index_panel_active = index;
                } else if (index == 3) {
                    logout();
                    system.login.Login login = new system.login.Login();
                    login.setVisible(true);
                    login.createAccess();
                    dispose();
                } else {
                    logout();
                    System.exit(0);
                }
            }
        });
        add(menu, BorderLayout.NORTH);

        // Establecer conexión con la base de datos
        try {
            connection = Connector.get_connection();
        } catch (SQLException e) {
            System.out.println("Conexión fallida en SearchBookWindow");
        }

        //Paneles
        SearchBook search = new SearchBook(new SearchBook.Access(connection), "Buscar Libro Por", false, false);
        search.active_space_border();
        paneles = new JPanel[3];
        paneles[0] = search;
        paneles[1] = new ShowData(typeuser, iduser, connection);
        paneles[2] = new ChangePassword(iduser, connection);
        index_panel_active = 0;
        panel_active = paneles[index_panel_active];

        // Crear el panel principal -> Mostrará el panel que corresponde al item de menu activo
        JPanel mainpanel = new JPanel();
        layout = new GroupLayout(mainpanel);
        mainpanel.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(panel_active));
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(panel_active));
        add(mainpanel, BorderLayout.CENTER);

        // Configurar ventana
        setSize(Toolkit.getDefaultToolkit().getScreenSize().width - 50,
            Toolkit.getDefaultToolkit().getScreenSize().height - 100);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(600, 500));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                logout();
            }
        });
    }

    private Menu create_menu() {
        String menu_items[] = {
            "Buscar Libro", "Datos del Usuario",
            "Cambiar Contraseña", "Cerrar Sesión",
            "Salir"};
        Menu.NameItems name_items = new Menu.NameItems(menu_items, null);
        Menu.Images images = new Menu.Images(new String[]{
            "student/ico_searchbook.png",
            "ico_data.png",
            "ico_pass.png",
            "ico_logout.png",
            "exit.png"
        }, null);
        return new Menu(images, name_items);
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