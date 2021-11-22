package system.components;

import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class FieldTextArea extends JTextArea {
    private static final long serialVersionUID = 1L;
    private final int length_max;

    public FieldTextArea(int rows, int cols, int length) {
        super(rows, cols);
        length_max = length;
    }

    @Override
    protected Document createDefaultModel() {
        return new CountChars();
    }

    private class CountChars extends PlainDocument {
        private static final long serialVersionUID = 1L;

        @Override
        public void insertString(int offs, String str, AttributeSet a) 
        throws BadLocationException {
            if (str != null && getLength() < length_max) {
                int chars_max = length_max - getLength();
                if (str.length() <= chars_max)
                    super.insertString(offs, str, a);
                else
                    super.insertString(offs, str.substring(0, chars_max), a);
            }
        }
    }
}