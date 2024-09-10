package com.wangyuanye.plugin;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.externalSystem.autoimport.ProjectSettingsTracker;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectCoreUtil;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.impl.ProjectMacrosUtil;
import com.intellij.testFramework.ProjectExtension;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.wangyuanye.plugin.dao.SchemaDataSave;
import com.wangyuanye.plugin.dao.dto.MySchema;

import java.util.Map;
import java.util.Properties;

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

    }
}
