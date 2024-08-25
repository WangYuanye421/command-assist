package com.wangyuanye.plugin.component.toolbar;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.ArrayUtil;
import com.wangyuanye.plugin.component.command.CmdTable;
import com.wangyuanye.plugin.dao.dto.CmdDataSave;
import com.wangyuanye.plugin.services.MyService;
import com.wangyuanye.plugin.services.MyServiceImpl;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;

/**
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class DeleteAction extends AnAction {
    private JTable table;

    public DeleteAction() {
    }

    public DeleteAction(JTable table) {
        super("delete", "delete data", AllIcons.General.Remove);
        this.table = table;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Map<Integer, String> idMap = new HashMap<>();
        String schemaId = "";
        for (int i = 0; i < table.getRowCount(); i++) {
            Boolean isSelected = (Boolean) table.getValueAt(i, 0);  // 获取第一列的值
            if (isSelected) {
                // 获取被勾选行的id
                idMap.put(i, (String) table.getValueAt(i, 4));
            }
        }
        if (!idMap.isEmpty()) {
            System.out.println("idMap.size: " + idMap.size());
            // 删除DB
            schemaId = (String) table.getValueAt(0, CmdTable.col_schema_id);
            CmdDataSave cmdService = IdeaApiUtil.getCmdService();
            cmdService.deleteCmdList(idMap.values().stream().toList());
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            // 移除table
            Set<Integer> indexSet = idMap.keySet();
            // 将 Set 转换为 List
            List<Integer> indexList = new ArrayList<>(indexSet);
            // 对 List 进行降序排序, 防止table索引混乱
            Collections.sort(indexList, Collections.reverseOrder());
            for (Integer id : indexList) {
                model.removeRow(id);
            }
            model.fireTableDataChanged();

            table.revalidate();
            table.repaint();
        }
    }
}
