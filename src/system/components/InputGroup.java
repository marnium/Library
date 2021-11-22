package system.components;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.TitledBorder;
import java.awt.Font;

public class InputGroup extends javax.swing.JPanel {
    private static final long serialVersionUID = 1L;
    public Input[] inputs;

    public InputGroup(Input[] inputs, boolean active_border, String title, Font font) {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        // Inicializar inputs
        this.inputs = inputs;

        // Activar borde, si corresponede
        if (active_border) {
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);
        }
        //Activar titulo, si corresponde
        if (title != null) {
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ColorUse.blue_main),
            title, TitledBorder.LEFT, TitledBorder.TOP, font));
        }

        // Diseñar el grupo
        // Grupo Horizontal -> Se ajustan a la longitud del comopente de mayor longitud
        GroupLayout.ParallelGroup hgroup = layout.createParallelGroup();
        for (Input input : inputs) {
            hgroup.addComponent(input);
        }
        layout.setHorizontalGroup(hgroup);

        // Grupo vertical -> La altura variable, pero el objeto Input impide el cambio
        // Se agrega un espacio de 4 a 12 entre cada input
        GroupLayout.SequentialGroup vgroup = layout.createSequentialGroup();
        for (Input input : inputs) {
            vgroup.addComponent(input).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 4, 12);
        }
        layout.setVerticalGroup(vgroup);
    }
}