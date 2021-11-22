package system.components;

import javax.swing.JLabel;

public class InputTextArea extends Input {
    private static final long serialVersionUID = 1L;

    public InputTextArea(String label, int rows, int cols, int length) {
        FieldTextArea fta = new FieldTextArea(rows, cols, length);
        layout_input(new JLabel(label), fta, fta, null);
    }
}