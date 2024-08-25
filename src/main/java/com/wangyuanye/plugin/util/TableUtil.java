package com.wangyuanye.plugin.util;

import com.intellij.ide.ui.UISettings;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import com.wangyuanye.plugin.component.command.CheckBoxHeaderRenderer;
import com.wangyuanye.plugin.component.command.CmdTable;
import com.wangyuanye.plugin.component.command.MyBooleanRenderer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

/**
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class TableUtil {
    private static final Border myNoFocusBorder = JBUI.Borders.empty(1);
    public static final int CHECKBOX_COL_WIDTH = 35; // checkbox列宽度
    public static final int OPERATION_COL_WIDTH = 60; // 操作列列宽度
    public static final int CELL_WIDTH = 70;//列宽度
    public static final int CELL_WIDTH_MIN = 60;//列宽度
    public static final int CELL_HEIGHT = 28;//行高
    public static final int FONT_SIZE = 14;
    // 手动更新标题列
    boolean mousePressed = false;


    // 通用cell设置
    public static void cellSetting(JBTable table, Boolean firstIsCheckbox) {
        if (firstIsCheckbox) {
            table.getColumnModel().getColumn(CmdTable.col_check).setCellRenderer(new MyBooleanRenderer(table));
            table.getColumnModel().getColumn(CmdTable.col_check).setCellEditor(JBTable.createBooleanEditor());
            ;
        }

        // 通用单元格渲染设置
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                if (firstIsCheckbox) {
                    if (column == CmdTable.col_check) {
                        JCheckBox checkBox = new JCheckBox();
                        checkBox.setSelected(isSelected);
                        checkBox.addItemListener(e -> {

                        });
                        boolean selected = value != null && (Boolean) value;
                        checkBox.setSelected(selected);
                        table.setValueAt(selected, row, column);
                        if (hasFocus) {
                            this.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
                        } else {
                            this.setBorder(myNoFocusBorder);
                        }
                        return checkBox;
                    }
                }

                setForeground(table.getForeground());// 取消选中行效果
                setBackground(table.getBackground());
                setHorizontalAlignment(JLabel.CENTER);// 居中
                setVerticalAlignment(JLabel.CENTER);// 居中
                setBorder(null);// 禁用焦点框
                table.setSelectionBackground(table.getBackground());// 背景色不变
                table.setRowHeight(CELL_HEIGHT); // 根据需要调整行高
                // 设置单元格自动换行
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    ((JLabel) c).setVerticalAlignment(TOP);
                    ((JLabel) c).setHorizontalAlignment(CENTER);
                    ((JLabel) c).setText("<html>" + ((JLabel) c).getText().replace("\n", "<br>") + "</html>");
                }
                return c;
            }
        });
    }

    // 通用表头渲染设置
    public static void headerSetting(JBTable table, Boolean firstIsCheckbox) {
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setReorderingAllowed(false);// 禁用标题行的拖动
        tableHeader.setBorder(null);// 禁用焦点框
        table.setSelectionBackground(table.getBackground());// 背景色不变
        tableHeader.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                // 获取原始的渲染器组件
                setHorizontalAlignment(SwingConstants.CENTER);// 居中
                UISettings uiSettings = UISettings.getInstance();
                Font font = new Font(IdeaApiUtil.getUiFont(), Font.BOLD, IdeaApiUtil.getUiFontSize() + 1);
                setFont(font);// 和IDEA保持一致,大小+1
                return component;
            }
        });

        TableColumnModel columnModel = table.getColumnModel();
        if (firstIsCheckbox) {
            // 渲染行为
            columnModel.getColumn(CmdTable.col_check).setHeaderRenderer(new CheckBoxHeaderRenderer(table));
            columnModel.getColumn(CmdTable.col_check).setCellRenderer(new MyBooleanRenderer(table));
            columnModel.getColumn(CmdTable.col_check).setCellEditor(JBTable.createBooleanEditor());
        }
        // 宽度设置
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            if (firstIsCheckbox) {
                if (i == CmdTable.col_check) {
                    column.setMinWidth(CHECKBOX_COL_WIDTH);
                    column.setMaxWidth(CHECKBOX_COL_WIDTH);
                } else if (i == columnModel.getColumnCount() - 1) {
                    column.setMinWidth(OPERATION_COL_WIDTH);
                    column.setMaxWidth(OPERATION_COL_WIDTH);
                } else {
                    column.setPreferredWidth(CELL_WIDTH);
                    column.setMinWidth(CELL_WIDTH_MIN);
                }
            } else {
                column.setPreferredWidth(CELL_WIDTH);
                column.setMinWidth(CELL_WIDTH_MIN);
            }
        }
    }
}
