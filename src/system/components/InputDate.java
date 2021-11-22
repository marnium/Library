package system.components;

import system.resource.Resource;

import com.toedter.calendar.*;

import javax.swing.*;

public class InputDate extends Input {
    private static final long serialVersionUID = 1L;
    private JDateChooser date;

    public InputDate(String label) {
        date = new JDateChooser("dd/MM/yyyy", "##/##/####", '_');
        JTextField jtext = (JTextField)date.getDateEditor();
        layout_input(new JLabel(label), jtext, date, null);

        date.setIcon(Resource.get("components/calendar.png"));
        date.setFont(FontUse.serif_plain_14);
        date.getJCalendar().setWeekOfYearVisible(false);
        jtext.setToolTipText("DD/MM/AAAA");
    }

    public java.util.Date getDate() {
        return date.getDate();
    }

    public java.util.Calendar getCalendar() {
        return date.getCalendar();
    }

    public void cleanField() {
        ((JTextField)date.getDateEditor()).setText("");
    }
}