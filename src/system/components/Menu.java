package system.components;

import system.resource.Resource;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Menu extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color menu_disable = ColorUse.blue_new;
    private static final Color menu_enable = ColorUse.green_new;
    private static final Color menu_focus = ColorUse.blue_secondary;
    private static final Color submenu_disable = ColorUse.green_new;
    private static final Color submenu_enable = ColorUse.gray_secondary;
    private ItemMenu[] items_menu;
    private ItemMenu item_menu_enable;
    private ItemSubmenu item_submenu_enable;
    private MenuListener listener = null;
    private MenuEvent event = new MenuEvent();

    public Menu(Images images, NameItems name_items) {
        super(new BorderLayout());

        // Agregar los items del menu
        create_items(name_items, images);
        // Agregar acciones al menu
        set_acctions_to_menu();

        // Activar el primer item
        item_menu_enable = items_menu[0];
        if (!item_menu_enable.items_submenu.isEmpty())
            item_submenu_enable = item_menu_enable.items_submenu.get(0);
        item_menu_enable.enable();

        // Diseño del Menu
        JPanel panel_menu = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel_menu.setBackground(menu_disable);
        JPanel panel_submenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel_submenu.setBackground(submenu_disable);
        for (ItemMenu im : items_menu) {
            panel_menu.add(im);
            for (ItemSubmenu is : im.items_submenu)
                panel_submenu.add(is);
        }
        add(panel_menu, BorderLayout.NORTH);
        add(panel_submenu, BorderLayout.CENTER);
    }

    public void add_menu_listener(MenuListener ml) {
        listener = ml;
    }

    private void create_items(NameItems name_items, Images images) {
        items_menu = new ItemMenu[name_items.menu.length];

        for (int i = 0; i < items_menu.length; ++i) {
            items_menu[i] = new ItemMenu(name_items.menu[i], i);
            if (name_items.submenu != null && i < name_items.submenu.length)
                items_menu[i].addItems(name_items.submenu[i]);
        }
        if (images != null) {
            if (images.menu != null) {
                for (int i = 0; i < items_menu.length && i < images.menu.length; i++)
                    items_menu[i].set_image(images.menu[i]);
            }
            for (int i = 0; i < items_menu.length && i < images.submenu.size(); i++) {
                if (images.submenu.get(i) != null) {
                    for (int j = 0; j < images.submenu.get(i).length; j++)
                        items_menu[i].items_submenu.get(j).set_image(images.submenu.get(i)[j]);
                }
            }
        }
    }

    private void set_acctions_to_menu() {
        MouseListener msl_menu = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ItemMenu item_current_enable = (ItemMenu) e.getSource();
                if (!item_current_enable.is_enable) {
                    item_menu_enable.disable();
                    item_current_enable.enable();
                    item_menu_enable = item_current_enable;
                    event.set_event(MenuEvent.MENU, item_menu_enable.index);
                    if (listener != null)
                        listener.changed_item_enable(event);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                ItemMenu item_current_captured = (ItemMenu) e.getSource();
                if (!item_current_captured.is_enable)
                    item_current_captured.setBackground(menu_focus);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ItemMenu item_current_exit = (ItemMenu) e.getSource();
                if (!item_current_exit.is_enable)
                    item_current_exit.setBackground(menu_disable);
            }
        };
        MouseListener msl_submenu = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ItemSubmenu item_current_enable = (ItemSubmenu) e.getSource();
                if (!item_current_enable.is_enable) {
                    item_submenu_enable.disable();
                    item_current_enable.enable();
                    item_submenu_enable = item_current_enable;
                    event.set_event(MenuEvent.SUBMENU, item_submenu_enable.index);
                    if (listener != null)
                        listener.changed_item_enable(event);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                ItemSubmenu item_current_captured = (ItemSubmenu) e.getSource();
                if (!item_current_captured.is_enable)
                    item_current_captured.setBackground(menu_focus);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ItemSubmenu item_current_exit = (ItemSubmenu) e.getSource();
                if (!item_current_exit.is_enable)
                    item_current_exit.setBackground(submenu_disable);
            }
        };

        // Agregar eventos a los items de menu y submenu
        for (ItemMenu im : items_menu) {
            im.addMouseListener(msl_menu);
            for (ItemSubmenu is : im.items_submenu)
                is.addMouseListener(msl_submenu);
        }
    }

    private class ItemMenu extends JLabel {
        private static final long serialVersionUID = 1L;
        private ArrayList<ItemSubmenu> items_submenu = new ArrayList<ItemSubmenu>();
        private boolean is_enable;
        public final int index;

        public ItemMenu(String str_name, int index) {
            super(str_name);
            this.index = index;
            setOpaque(true);
            setBackground(menu_disable);
            setBorder(new EmptyBorder(4, 8, 4, 8));
            is_enable = false;
        }

        public void set_image(ImageIcon imi) {
            if (imi != null) setIcon(imi);
        }

        public void addItems(String[] str) {
            if (str != null) {
                for (int i = 0; i < str.length; i++)
                    items_submenu.add(new ItemSubmenu(str[i], i));
            }
        }

        public void enable() {
            for (ItemSubmenu ism : items_submenu)
                ism.setVisible(true);
            setBackground(menu_enable);
            is_enable = true;
        }

        public void disable() {
            for (ItemSubmenu ism : items_submenu) {
                ism.setVisible(false);
                ism.disable();
            }
            setBackground(menu_disable);
            is_enable = false;
        }
    }

    private class ItemSubmenu extends JLabel {
        private static final long serialVersionUID = 1L;
        public final int index;
        private boolean is_enable;

        public ItemSubmenu(String str_name, int index) {
            super(str_name);
            this.index = index;
            is_enable = false;
            setOpaque(true);
            setVisible(false);
            setBorder(new EmptyBorder(4, 8, 4, 8));
            setBackground(submenu_disable);
        }

        public void set_image(ImageIcon imi) {
            if (imi != null) setIcon(imi);
        }

        public void enable() {
            setBackground(submenu_enable);
            is_enable = true;
        }

        public void disable() {
            setBackground(submenu_disable);
            is_enable = false;
        }
    }

    public static class NameItems {
        public final String[] menu;
        public final String[][] submenu;
    
        public NameItems(String[] menu, String[][] submenu) throws NullPointerException {
            if (menu == null) throw new NullPointerException("No se especificaron los items");
            this.menu = menu;
            this.submenu = submenu;
        }
    }

    public static class Images {
        public final ImageIcon menu[];
        public final ArrayList<ImageIcon[]> submenu = new ArrayList<ImageIcon[]>();
    
        public Images(String[] path_menu, String path_submenu[][]) {
            // Imagenes del Menu
            if (path_menu != null && path_menu.length > 0) {
                menu = new ImageIcon[path_menu.length];
                for (int i = 0; i < path_menu.length; i++)
                    if (path_menu[i].length() > 0)
                        menu[i] = Resource.get(path_menu[i]);
            } else
                menu = null;
    
            // Imagenes de los submenus
            if (path_submenu != null) {
                for (int i = 0; i < path_submenu.length; i++) {
                    String[] path = path_submenu[i];
                    ImageIcon[] imi = null;
                    if (path != null && path.length > 0) {
                        imi = new ImageIcon[path.length];
                        for (int j = 0; j < path.length; j++)
                            if (path[j].length() > 0)
                                imi[j] = Resource.get(path[j]);
                    }
                    submenu.add(imi);
                }
            }
        }
    }
}
