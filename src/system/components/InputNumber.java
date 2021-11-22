package system.components;

import javax.swing.JLabel;

public class InputNumber extends Input {
    private static final long serialVersionUID = 1L;

    public InputNumber(String label, int cols, int length) {
        FieldNumber fn = new FieldNumber(cols, length);
        layout_input(new JLabel(label), fn, fn, null);
    }
}