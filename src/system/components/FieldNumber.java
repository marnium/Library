package system.components;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class FieldNumber extends javax.swing.JTextField {
    private static final long serialVersionUID = 1L;
    private final int length_max;

    public FieldNumber(int cols, int length_max) {
        super(cols);
        this.length_max = length_max;
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
                char[] chars;
                if (str.length() > chars_max) {
                    String str_sub = str.substring(0, chars_max);
                    chars = str_sub.toCharArray();
                } else
                    chars = str.toCharArray();
                char[] digits = new char[chars.length];
                for (int i = 0; i < digits.length; i++)
                    if (Character.isDigit(chars[i]))
                        digits[i] = chars[i];
                super.insertString(offs, new String(digits).trim(), a);
            }
        }
    }
}