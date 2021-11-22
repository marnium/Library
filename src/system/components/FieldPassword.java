package system.components;

import javax.swing.JPasswordField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class FieldPassword extends JPasswordField {
    private static final long serialVersionUID = 1L;
    private final int length_max;

    public FieldPassword(int cols, int length) {
        super(cols);
        this.length_max = length;
        ((AbstractDocument)getDocument()).setDocumentFilter(new CountChars());
    }

    private class CountChars extends DocumentFilter {
        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offset,
            String text, AttributeSet attrs) throws BadLocationException {
            if (text != null && fb.getDocument().getLength() < length_max) {
                int chars_max = length_max - fb.getDocument().getLength();
                if (text.length() <= chars_max)
                    super.insertString(fb, offset, text, attrs);
                else
                    super.insertString(fb, offset, text.substring(0, chars_max), attrs);
            }
        }
        
        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text != null && fb.getDocument().getLength() < length_max) {
                int chars_max = length_max - fb.getDocument().getLength();
                if (text.length() <= chars_max)
                    super.replace(fb, offset, length, text, attrs);
                else
                    super.replace(fb, offset, length, text.substring(0, chars_max), attrs);
            }
        }
    }
}