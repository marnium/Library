package system.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

public abstract class Input extends JPanel {
    private static final long serialVersionUID = 1L;
    public JTextComponent field = null;

    protected void layout_input(JLabel jlabel, JTextComponent jtext, JComponent jcomp, JLabel act) {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        field = jtext;

        jlabel.setFont(FontUse.sansserif_bold_16);
        field.setFont(FontUse.serif_plain_14);
        Border border = BorderFactory.createLineBorder(ColorUse.green_main);
        Border margin = new EmptyBorder(4, 4, 4, 4);
        field.setBorder(new CompoundBorder(border, margin));

        GroupLayout.SequentialGroup hgroup = layout.createSequentialGroup();
        hgroup.addComponent(jlabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jcomp);
        GroupLayout.ParallelGroup vgroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        vgroup.addComponent(jlabel).addComponent(jcomp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
            GroupLayout.PREFERRED_SIZE);
        
        if (act != null) {
            hgroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(act);
            vgroup.addComponent(act);
        }

        layout.setHorizontalGroup(hgroup);
        layout.setVerticalGroup(vgroup);
    }
}