package com.wangyuanye.plugin.component;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.*;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.wangyuanye.plugin.dao.CmdDataSave;
import com.wangyuanye.plugin.dao.SchemaDataSave;
import com.wangyuanye.plugin.dao.dto.MySchema;
import com.wangyuanye.plugin.toolWindow.MyToolWindowFactory;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import com.wangyuanye.plugin.util.MessagesUtil;
import com.wangyuanye.plugin.util.MyTableUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangyuanye
 * @date 2024/8/28
 **/
public class SchemaTab implements Disposable {
    private static final Logger logger = Logger.getInstance(SchemaTab.class);
    public static final String TAB_NAME = MessagesUtil.getMessage("schema.tab.name");
    private MySchemaModel schemaModel;
    private List<MySchema> schemaList;
    private JBTable schemaTable;
    // 加载数据
    private CmdDataSave cmdService;
    private SchemaDataSave schemaService;

    public SchemaTab() {
        // 加载数据
        cmdService = IdeaApiUtil.getCmdService();
        schemaService = IdeaApiUtil.getSchemaService();
        schemaList = new ArrayList<>(schemaService.list());
        schemaModel = new MySchemaModel(schemaList);
        schemaTable = new JBTable(schemaModel);
        schemaTable.setShowGrid(false);
        schemaTable.setFocusable(false);
        schemaTable.setFocusable(false);
        schemaTable.getTableHeader().setReorderingAllowed(false);// 禁止列拖动
        MyTableUtil.setEmptyText(schemaTable);

    }


    public TabInfo buildSchemaTab(JBTabs jbTabs, ActionSchemaComboBox combobox) {
        schemaList = new ArrayList<>(schemaService.list());
        schemaModel.setSchemaList(schemaList);
        // Column "name"
        TableColumn columnName = schemaTable.getColumnModel().getColumn(0);
        JTableHeader tableHeader = schemaTable.getTableHeader();
        FontMetrics headerFontMetrics = tableHeader.getFontMetrics(tableHeader.getFont());
        int nameColumnWidth = headerFontMetrics.stringWidth(schemaTable.getColumnName(0) + JBUIScale.scale(20));
        columnName.setPreferredWidth(nameColumnWidth);
        columnName.setMinWidth(nameColumnWidth);

        // Column "default"
        TableColumn isDefault = schemaTable.getColumnModel().getColumn(1);
        int remarkColumnWidth = headerFontMetrics.stringWidth(schemaTable.getColumnName(1)) + JBUIScale.scale(20);
        isDefault.setPreferredWidth(remarkColumnWidth);
        isDefault.setMinWidth(remarkColumnWidth);
        ToolWindow toolWindow = ToolWindowManager.getInstance(IdeaApiUtil.getProject()).getToolWindow(MyToolWindowFactory.myToolWindowId);
        JComponent toolWindowComponent = toolWindow.getComponent();

        JPanel schemasPanel = new JPanel(new BorderLayout());
        schemasPanel.add(ToolbarDecorator.createDecorator(schemaTable)
                .setAddAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton button) {
                        stopEditing();
                        MySchema schemaAdd = new MySchema("", false);
                        DialogMySchema dialog = new DialogMySchema(toolWindowComponent, schemaAdd, -1, schemaList);
                        IdeaApiUtil.setRelatedLocation(dialog);
                        if (!dialog.showAndGet()) {
                            return;
                        }
                        logger.info("schema add. schema:" + schemaAdd.toString());
                        schemaList.add(schemaAdd);
                        schemaService.addSchema(schemaAdd);// db
                        int index = schemaList.size() - 1;
                        schemaModel.fireTableRowsInserted(index, index);
                        schemaTable.getSelectionModel().setSelectionInterval(index, index);
                        schemaTable.scrollRectToVisible(schemaTable.getCellRect(index, 0, true));
                        combobox.initComboBoxData(schemaList, true);
                    }
                })
                .setEditAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton button) {
                        editSelectedSchema(combobox);
                    }
                })
                .setRemoveAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton button) {
                        stopEditing();
                        int selectedIndex = schemaTable.getSelectedRow();
                        if (selectedIndex < 0 || selectedIndex >= schemaModel.getRowCount()) {
                            return;
                        }
                        MySchema schemaToBeRemoved = schemaList.get(selectedIndex);
                        logger.info("schema remove. schema:" + schemaToBeRemoved.toString());
                        schemaService.deleteSchema(schemaToBeRemoved.getId());
                        cmdService.deleteCmd(schemaToBeRemoved.getId());// db移除
                        TableUtil.removeSelectedItems(schemaTable);// 表移除
                        combobox.initComboBoxData(schemaList, true);// 下拉框移除
                    }
                }).addExtraAction(new ActionCloseSchemaTab(jbTabs))
                .disableUpDownActions().createPanel(), BorderLayout.CENTER);

        // double-click
        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(@NotNull MouseEvent e) {
                editSelectedSchema(combobox);
                return true;
            }
        }.installOn(schemaTable);
        return new TabInfo(schemasPanel).setText(SchemaTab.TAB_NAME);
    }


    private void editSelectedSchema(ActionSchemaComboBox combobox) {
        stopEditing();
        int selectedIndex = schemaTable.getSelectedRow();
        if (selectedIndex < 0 || selectedIndex >= schemaModel.getRowCount()) {
            return;
        }
        MySchema sourceSchema = schemaList.get(selectedIndex);
        MySchema schemaEdit = sourceSchema.clone();
        DialogMySchema dialog = new DialogMySchema(schemaTable, schemaEdit, selectedIndex, schemaList);
        IdeaApiUtil.setRelatedLocation(dialog);
        dialog.setTitle(MessagesUtil.getMessage("schema.dialog.edit.title"));
        if (!dialog.showAndGet()) {
            return;
        }
        logger.info("schema edit. schema:" + schemaEdit.toString());
        schemaList.set(selectedIndex, schemaEdit);
        schemaService.updateSchema(schemaEdit);// db
        schemaModel.fireTableRowsUpdated(selectedIndex, selectedIndex);
        schemaTable.getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
        combobox.initComboBoxData(schemaList, true);
    }

    protected void stopEditing() {
        if (schemaTable.isEditing()) {
            TableCellEditor editor = schemaTable.getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
        }
        if (schemaTable.isEditing()) {
            TableCellEditor editor = schemaTable.getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
        }
    }

    @Override
    public void dispose() {
        System.out.println("应用关闭，执行清理");
    }
}
