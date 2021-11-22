package system.components;

import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class InputText extends Input {
    private static final long serialVersionUID = 1L;

    public InputText(ImageIcon imi, String label,int cols, int length) {
        FieldText ft = new FieldText(cols, length);
        if (imi != null)
            layout_input(new JLabel(imi), ft, ft, null);
        else
            layout_input(new JLabel(label), ft, ft, null);
    }
}