package com.wangyuanye.plugin.component.command;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 表头渲染器
 *
 * @author wangyuanye
 * @date 2024/8/20
 **/
public class CheckBoxHeaderRenderer extends JCheckBox implements TableCellRenderer, ItemListener {
    private boolean mousePressed = false;

    public CheckBoxHeaderRenderer(JTable table) {
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);
        this.addItemListener(this);

        // 鼠标事件
        JTableHeader header = table.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());
                if (column == 0) { // 如果点击的是第一列
                    boolean selected = !isSelected();
                    setSelected(selected); // 设置复选框状态
                    // 手动更新标题列
                    mousePressed = true;
                    // 手动更新表格中的所有行
                    for (int i = 0; i < table.getRowCount(); i++) {
                        table.setValueAt(selected, i, 0);
                    }
                    // 重新绘制表头
                    header.repaint();
                }
            }
        });
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (mousePressed) {
            setSelected(isSelected());
        }
        return this;
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        System.out.println("itemStateChanged: " + isSelected());
    }
}
