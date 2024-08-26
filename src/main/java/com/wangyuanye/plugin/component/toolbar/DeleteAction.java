package com.wangyuanye.plugin.component.toolbar;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.wangyuanye.plugin.component.command.CmdTable;
import com.wangyuanye.plugin.dao.dto.CmdDataSave;
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
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < table.getRowCount(); i++) {
            Boolean isSelected = (Boolean) model.getValueAt(i, CmdTable.col_check);  // 获取第一列的值
            if (isSelected) {
                // 获取被勾选行的id
                idMap.put(i, (String) model.getValueAt(i, CmdTable.col_cmd_id));
            }
        }
        if (!idMap.isEmpty()) {
            System.out.println("idMap.size: " + idMap.size());
            // 删除DB
            CmdDataSave cmdService = IdeaApiUtil.getCmdService();
            cmdService.deleteCmdList(idMap.values().stream().toList());
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
