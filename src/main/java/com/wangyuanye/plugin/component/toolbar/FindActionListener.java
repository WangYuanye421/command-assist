package com.wangyuanye.plugin.component.toolbar;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class FindActionListener implements DocumentListener {

    @Override
    public void insertUpdate(DocumentEvent e) {
        onSearchChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        onSearchChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        onSearchChanged();
    }

    private void onSearchChanged() {
        // 处理搜索逻辑
        // 比如可以在这里过滤一个表或列表中的项
        System.out.println("onSearchChanged");
    }
}
