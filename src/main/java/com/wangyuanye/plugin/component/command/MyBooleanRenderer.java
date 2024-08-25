package com.wangyuanye.plugin.component.command;

import com.intellij.util.ui.JBUI;

import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author wangyuanye
 * @date 2024/8/21
 **/
public class MyBooleanRenderer extends JCheckBox implements TableCellRenderer, UIResource {
    private static final Border noFocusBorder = JBUI.Borders.empty(1);
    private JTable table;

    public MyBooleanRenderer(JTable table) {
        this.setHorizontalAlignment(0);
        this.setBorderPainted(true);
        this.table = table;
    }


    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.setForeground(table.getForeground());
        this.setBackground(table.getBackground());

        boolean selected = value != null && (Boolean) value;
        this.setSelected(selected);
        this.table.setValueAt(selected, row, column);
        if (hasFocus) {
            this.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        } else {
            this.setBorder(noFocusBorder);
        }
        setBorder(null);
        return this;
    }

    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleBooleanRenderer();
        }

        return this.accessibleContext;
    }

    class AccessibleBooleanRenderer extends JCheckBox.AccessibleJCheckBox {
        AccessibleBooleanRenderer() {
            super();
        }

        public AccessibleAction getAccessibleAction() {
            return null;
        }
    }
}
