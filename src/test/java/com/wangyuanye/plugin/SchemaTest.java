package com.wangyuanye.plugin;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.wangyuanye.plugin.dao.SchemaDataSave;
import com.wangyuanye.plugin.dao.dto.MySchema;

/**
 * @author wangyuanye
 *  2024/8/27
 **/
public class SchemaTest extends BasePlatformTestCase {
    private SchemaDataSave schemaService;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        schemaService = ApplicationManager.getApplication().getService(SchemaDataSave.class);
    }

    @SuppressWarnings("all")
    public void testCRUDSchema(){
        String id = "222";
        // add
        MySchema schema = new MySchema(id, "test2", true);
        schemaService.addSchema(schema);
        // query
        MySchema query = schemaService.getById(id);
        query.setName("test2_edit");
        schemaService.updateSchema(query);
        // update
        MySchema updated = schemaService.getById(id);
        assertEquals("test2_edit", updated.getName());

        // delete
        schemaService.deleteSchema(id);
        MySchema delete = schemaService.getById(id);
        assertNull(delete);
    }

    public void testIdeaConfig(){
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        String shellPath = propertiesComponent.getValue("TerminalProjectOptionsProvider.shellPath");
        System.out.println("Shell Path: " + shellPath);
    }
}
