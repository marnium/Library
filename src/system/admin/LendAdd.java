package system.admin;

import system.components.*;
import system.resource.Resource;

import javax.swing.*;
import java.awt.event.*;
import java.math.BigDecimal;

import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Color;
import java.awt.Frame;
import java.util.Calendar;

import java.sql.*;

public class LendAdd extends JPanel {
    private static final long serialVersionUID = 1L;
    private final String[] name_columns_book = {"ID", "Titulo", "Autor", "Año Publ.", "Editorial", "País",
        "Ciudad", "ISBN", "Ejemp.", "activo"};
    private String[] name_columns_user = { "Núm. Control", "Nombre", "Apellido Paterno", "Apellido Materno",
        "Núm. Tel.", "Dirección", "E-Mail" };
    private Frame owner;
    private Input input_idlibro;
    private Input input_user;
    private InputDate timelend;
    private Input input_observations;
    private JLabel action_searchbook;
    private JLabel action_showbook;
    private JLabel action_showuser;
    private ImageIcon images[] = new ImageIcon[2];
    private Access access;
    private JScrollPane scroll_book;
    private JTable table_book;
    private JScrollPane scroll_user;
    private JTable table_user;
    private String user_current = "";

    public LendAdd(Frame owner, Connection connection) {
        // administra la busqueda de libros
        access = new Access(connection);

        // Gestor de diseño
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        this.owner = owner;

        // Titulo
        JLabel title = new JLabel("Prestar Libro");
        title.setFont(FontUse.monospaced_bold_20);

        // Cargaar la images para los eventos de mostrar y ocultar información
        images[0] = Resource.get("admin/down.png");
        images[1] = Resource.get("admin/up.png");

        // Crear los paneles book, usuario y lend
        JPanel panel_book = create_panel_databook();
        JPanel panel_user = create_panel_datauser();
        JPanel panel_lend = create_panel_datalend();

        // Botón prestar libro y Botón limpiar campos
        JButton button = new JButton("Prestar Libro");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (input_idlibro.field.getText().isEmpty() || input_user.field.getText().isEmpty()
                        || timelend.getCalendar() == null || input_observations.field.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(LendAdd.this, "Rellena Todos Los Campos", "Error",
                        JOptionPane.ERROR_MESSAGE);
                } else {
                    ResultSet result_book = access.select_book(input_idlibro.field.getText());
                    try {
                        if (!result_book.next()) {
                            JOptionPane.showMessageDialog(LendAdd.this, "Libro No Registrado", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        } else if (result_book.getInt(10) == 0) {
                            JOptionPane.showMessageDialog(LendAdd.this, "Este Libro fue dado de baja",
                                "Estado", JOptionPane.WARNING_MESSAGE);
                        } else if (result_book.getInt(9) < 1) {
                            JOptionPane.showMessageDialog(LendAdd.this, "No Hay Ejemplares Diponibles", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        } else {
                            ResultSet result = access.select_user(input_user.field.getText());
                            if (!result.first()) {
                                JOptionPane.showMessageDialog(LendAdd.this, "Usuario No Registrado", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            } else if (result.getInt(5) == 0) {
                                JOptionPane.showMessageDialog(LendAdd.this, "Este Usuario fue dado de baja",
                                    "Estado", JOptionPane.WARNING_MESSAGE);
                            } else if (timelend.getCalendar().compareTo(Calendar.getInstance()) > 0) {
                                Calendar ca = timelend.getCalendar();
                                ca.set(Calendar.HOUR_OF_DAY, 18);
                                ca.set(Calendar.MINUTE, 59);
                                ca.set(Calendar.SECOND, 59);
                                ca.set(Calendar.MILLISECOND, 0);
                                String values[] = { result_book.getString(1), result.getString(1),
                                        input_observations.field.getText() };
                                if (1 == access.insert_lend(values, Timestamp.from(ca.toInstant())))
                                    JOptionPane.showMessageDialog(LendAdd.this, "Hecho: Prestamo Registrado", "Estado",
                                            JOptionPane.INFORMATION_MESSAGE);
                                else
                                    JOptionPane.showMessageDialog(LendAdd.this, "Error al Registrar el Prestamo", "Estado",
                                            JOptionPane.ERROR_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(LendAdd.this, "El Tiempo Mínimo de Prestamo es de 1 Día",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        JButton button_clean = new JButton("Limpiar");
        button_clean.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                user_current = "";
                input_idlibro.field.setText("");
                input_user.field.setText("");
                timelend.cleanField();
                input_observations.field.setText("");
            }
        });

        // Diseño del panel principal
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(title).addComponent(panel_book).addComponent(panel_user)
            .addComponent(panel_lend)
            .addGroup(layout.createSequentialGroup()
                .addComponent(button).addComponent(button_clean))
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(title).addComponent(panel_book).addComponent(panel_user)
            .addComponent(panel_lend)
            .addGroup(layout.createParallelGroup()
                .addComponent(button).addComponent(button_clean))
        );
    }

    private JPanel create_panel_databook() {
        // Gestor de diseño GroupLayout
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ColorUse.blue_main),
            "Datos del Libro", TitledBorder.LEFT, TitledBorder.TOP, FontUse.sansserif_bold_16));

        // Input id libro y la acción mostrar información del libro
        input_idlibro = new InputText(null, "Libro (Titulo o Código de Barras)", 20, 80);
        ((FieldText)input_idlibro.field).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {show_databook();}
        });
        action_showbook = new JLabel("Mostrar Información del Libro", images[0], SwingConstants.LEFT);
        action_showbook.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {show_databook();}
            @Override
            public void mouseEntered(MouseEvent e) {
                action_showbook.setForeground(ColorUse.blue_main);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                action_showbook.setForeground(Color.BLACK);
            }
        });
        table_book = new MyTable(new MyTable.MyTableModel(name_columns_book));
        table_book.removeColumn(table_book.getColumnModel().getColumn(9));
        table_book.removeColumn(table_book.getColumnModel().getColumn(0));
        scroll_book = new JScrollPane(table_book);
        scroll_book.setBorder(BorderFactory.createLineBorder(ColorUse.blue_main));
        scroll_book.setVisible(false);

        // Acción buscar el id del libro
        action_searchbook = new JLabel("Desconoce el titulo", Resource.get("admin/ignorebook.png"),
            SwingConstants.RIGHT);
        action_searchbook.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {new SearchBookWindow(owner);}

            @Override
            public void mouseEntered(MouseEvent e) {
                action_searchbook.setForeground(ColorUse.blue_main);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                action_searchbook.setForeground(Color.BLACK);
            }
        });

        // Diseñar panel información del libro
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                .addComponent(input_idlibro, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE).addComponent(action_showbook))
            .addComponent(scroll_book).addComponent(action_searchbook));
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup()
                .addComponent(input_idlibro).addComponent(action_showbook))
            .addComponent(scroll_book, 60, 60, 60)
            .addComponent(action_searchbook));

        return panel;
    }

    private JPanel create_panel_datauser() {
        // Gestor de diseño GroupLayout
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ColorUse.blue_main),
            "Datos del Usuario", TitledBorder.LEFT, TitledBorder.TOP, FontUse.sansserif_bold_16));

        // Input name user y la acción mostrar información del usuario
        input_user = new InputText(null, "Usuario", 20, 20);
        action_showuser = new JLabel("Mostrar Información del Usuario", images[0], SwingConstants.LEFT);
        action_showuser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                String str = input_user.field.getText();
                if (scroll_user.isVisible()) {
                    action_showuser.setIcon(images[0]);
                    action_showuser.setText("Mostrar Información del Usuario");
                    scroll_user.setVisible(false);
                } else if (str.isEmpty())
                    JOptionPane.showMessageDialog(LendAdd.this, "Especifique el Usuario", "Mensaje",
                        JOptionPane.ERROR_MESSAGE);
                else {
                    if (!user_current.equals(str)) {
                        user_current = str;
                        ResultSet result = access.select_user(user_current);
                        try {
                            if (result.first()) {
                                ResultSet rs;
                                String[] str_row = new String[name_columns_user.length];
                                int j = 0;
                                if (1 == result.getInt(2)) { //Alumno
                                    rs = access.select_student(result.getInt(3));
                                } else { //Otro
                                    rs = access.select_other(result.getInt(4));
                                    str_row[j++] = "";
                                }
                                rs.next();
                                MyTable.MyTableModel model = new MyTable.MyTableModel(name_columns_user);
                                table_user.setModel(model);
                                for (; j < str_row.length; j++)
                                    str_row[j] = rs.getString(j + 1);
                                model.addRow(str_row);
                            } else {
                                JOptionPane.showMessageDialog(LendAdd.this, "Usuario No Regisgrado", 
                                "Mensaje", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    action_showuser.setIcon(images[1]);
                    action_showuser.setText("Ocultar Información del Usuario");
                    scroll_user.setVisible(true);
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                action_showuser.setForeground(ColorUse.blue_main);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                action_showuser.setForeground(Color.BLACK);
            }
        });
        table_user = new MyTable(new MyTable.MyTableModel(name_columns_user));
        scroll_user = new JScrollPane(table_user);
        scroll_user.setBorder(BorderFactory.createLineBorder(ColorUse.blue_main));
        scroll_user.setVisible(false);

        // Diseño del panel información del usuario
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                .addComponent(input_user, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE).addComponent(action_showuser))
            .addComponent(scroll_user));
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup()
                .addComponent(input_user).addComponent(action_showuser))
            .addComponent(scroll_user, 60, 60, 60));
        
        return panel;
    }

    private JPanel create_panel_datalend() {
        // Gestor de diseño GroupLayout
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ColorUse.blue_main),
            "Datos del Prestamo", TitledBorder.LEFT, TitledBorder.TOP, FontUse.sansserif_bold_16));

        // Inputs
        timelend = new InputDate("Fecha de Devolución");
        input_observations = new InputTextArea("Estado del libro", 5, 15, 200);

        // Diseño del panel información del prestamo
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(timelend).addComponent(input_observations));
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(timelend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(input_observations));

        return panel;
    }

    private void show_databook() {
        String str = input_idlibro.field.getText();
        if (scroll_book.isVisible()) {
            scroll_book.setVisible(false);
            action_searchbook.setVisible(true);
            action_showbook.setIcon(images[0]);
            action_showbook.setText("Mostrar Información del Libro");
        } else if (str.isEmpty()) {
            JOptionPane.showMessageDialog(LendAdd.this, "Especifique el Libro", "Mensaje", JOptionPane.ERROR_MESSAGE);
        } else {
            MyTable.MyTableModel model = new MyTable.MyTableModel(name_columns_book);
            table_book.setModel(model);
            table_book.removeColumn(table_book.getColumnModel().getColumn(9));
            table_book.removeColumn(table_book.getColumnModel().getColumn(0));
            ResultSet result = access.select_book(input_idlibro.field.getText());
            try {
                if (result.first()) {
                    String[] str_row = new String[name_columns_book.length];
                    for (int j = 0; j < name_columns_book.length; j++)
                        str_row[j] = result.getString(j + 1);
                    model.addRow(str_row);
                } else {
                    JOptionPane.showMessageDialog(LendAdd.this, "Libro No Registrado", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            action_searchbook.setVisible(false);
            scroll_book.setVisible(true);
            action_showbook.setIcon(images[1]);
            action_showbook.setText("Ocultar Información del Libro");
        }
    }

    private class SearchBookWindow extends JDialog {
        private static final long serialVersionUID = 1L;
        private JTable table;
        private String title_selected;
        private JLabel label;
        private JButton button;
    
        public SearchBookWindow(Frame owner) {
            super(owner, "Seleccionar Libro");
    
            // Panel de componentes
            JPanel panel_components = new JPanel();
            GroupLayout layout = new GroupLayout(panel_components);
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);
            panel_components.setLayout(layout);
            add(panel_components);
    
            // Crera el objeto search e inicializar table
            SearchBook search = new SearchBook(access, "Buscar Libro Por", false, false);
            table = search.get_table();
            // Crear el label
            label = new JLabel("Libro Seleccionado:");
            label.setFont(FontUse.sansserif_bold_16);
            // Botón aceptar ID
            button = new JButton("Aceptar");
            button.setVisible(false);

            // Diseñar la interfaz de SearchBook
            layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(search)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(label).addComponent(button))
            );
            layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(search)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(label).addComponent(button))
            );

            //Asignar manejador de evento a los componentes
            set_listener_components();
    
            // Configurar ventana
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setSize(800, 400);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        private void set_listener_components() {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    input_idlibro.field.setText(title_selected);
                    dispose();
                }
            });
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
        
                @Override
                public void valueChanged(ListSelectionEvent arg0) {
                    if (table.getSelectedRow() < 0) {
                        label.setText("Libro Seleccionado:");
                        button.setVisible(false);
                    } else {
                        title_selected = table.getValueAt(table.getSelectedRow(), 0).toString();
                        label.setText("Libro Seleccionado: " + title_selected);
                        button.setVisible(true);
                    }
                }
            });
        }
    }

    private class Access extends SearchBook.Access {
        private PreparedStatement sql_selectbookbycode;
        private PreparedStatement sql_selectbookbytitle;
        private PreparedStatement sql_selectuser;
        private PreparedStatement sql_selectstudent;
        private PreparedStatement sql_selectother;
        private PreparedStatement sql_insertlend;
        private PreparedStatement sql_lendbook;

        public Access(Connection connection) {
            super(connection);
            try {
                sql_selectbookbycode = connection.prepareStatement("SELECT * FROM libros WHERE codigo=?");
                sql_selectbookbytitle = connection.prepareStatement("SELECT * FROM libros WHERE titulo LIKE ?");
                sql_selectuser = connection.prepareStatement(
                    "SELECT id_usuario,tipo_usuario+0,fk_alumno,fk_personal,activo FROM usuarios WHERE usuario=?");
                sql_selectstudent = connection.prepareStatement("SELECT * FROM alumnos WHERE num_control=?");
                sql_selectother = connection.prepareStatement("SELECT * FROM personal WHERE id_personal=?");
                sql_insertlend = connection.prepareStatement("INSERT INTO prestamos VALUES(0,?,?,DEFAULT,?,?,0)");
                sql_lendbook = connection.prepareStatement(
                    "UPDATE libros SET ejemplares=ejemplares-1 WHERE id_libro=?");
            } catch (SQLException e) {}
        }

        public ResultSet select_book(String str) {
            ResultSet result = null;
            try {
                BigDecimal code = new BigDecimal(str);
                sql_selectbookbycode.setBigDecimal(1, code);
                result = sql_selectbookbycode.executeQuery();
            } catch (SQLException | NumberFormatException e) {}
            if (result == null) {
                try {
                    sql_selectbookbytitle.setString(1, str);
                    result = sql_selectbookbytitle.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        public ResultSet select_user(String user) {
            ResultSet result = null;
            try {
                sql_selectuser.setString(1, user);
                result = sql_selectuser.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return result;
        }

        public ResultSet select_student(int num_control) {
            ResultSet result = null;
            try {
                sql_selectstudent.setInt(1, num_control);
                result = sql_selectstudent.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return result;
        }

        public ResultSet select_other(int id) {
            ResultSet result = null;
            try {
                sql_selectother.setInt(1, id);
                result = sql_selectother.executeQuery();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        public int insert_lend(String values[], Timestamp time) {
            int state = 0;
            try {
                sql_insertlend.setInt(1, Integer.parseInt(values[0])); //ID del libro
                sql_insertlend.setInt(2, Integer.parseInt(values[1])); //ID del usuario
                sql_insertlend.setTimestamp(3, time);                  //Fecha de devolución
                sql_insertlend.setString(4, values[2]);                //Estado del libro
                if (sql_insertlend.executeUpdate() == 1) {
                    sql_lendbook.setInt(1, Integer.parseInt(values[0]));  //ID del Libro
                    state = sql_lendbook.executeUpdate();
                }

            } catch (SQLException e) {
                state = -1;
                e.printStackTrace();
            }
            return state;
        }
    }
}
