package system.components;

import system.resource.Resource;

import java.awt.event.*;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class InputPassword extends Input {
    private static final long serialVersionUID = 1L;
    private final char echo_char;
    private ImageIcon[] images;
    private boolean is_visible_password = false;
    private JLabel action;

    public InputPassword(ImageIcon imi, String label, int cols, int length) {
        FieldPassword fp = new FieldPassword(cols, length);
        echo_char = fp.getEchoChar();

        //Mostrar y ocultar la contraseña
        images = new ImageIcon[2];
        images[0] = Resource.get("components/show_pass.png");
        images[1] = Resource.get("components/hide_pass.png");
        action = new JLabel(images[0]);
        action.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (is_visible_password) { //Ocultar contraseña
                    ((FieldPassword) field).setEchoChar(echo_char);
                    action.setIcon(images[0]);
                    is_visible_password = false;
                } else { //Mostrar contraseña
                    ((FieldPassword) field).setEchoChar((char)0);;
                    action.setIcon(images[1]);
                    is_visible_password = true;
                }
            }
        });

        if (imi != null)
            layout_input(new JLabel(imi), fp, fp, action);
        else
            layout_input(new JLabel(label), fp, fp, action);
    }
}