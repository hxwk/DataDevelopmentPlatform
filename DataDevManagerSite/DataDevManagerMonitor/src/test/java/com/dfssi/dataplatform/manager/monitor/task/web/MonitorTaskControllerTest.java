package com.dfssi.dataplatform.manager.monitor.task.web;

import com.dfssi.dataplatform.manager.monitor.ManagerMonitorApp;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.io.InputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ManagerMonitorApp.class)
@WebAppConfiguration
public class MonitorTaskControllerTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testListAllTasks() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/manager/monitor/task/listtasks/0/10").contentType(MediaType
                .APPLICATION_JSON_UTF8).param("name", "abc").accept(MediaType.APPLICATION_JSON)).andExpect
                (MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testListAllExecRecords() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/manager/monitor/task/listrecords/0/10").contentType(MediaType
                .APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status()
                .isOk()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetTask() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/manager/monitor/task/get/1213a").contentType(MediaType
                .APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status()
                .isOk()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testDeleteTask() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/manager/monitor/task/delete/1213").contentType(MediaType
                .APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status()
                .isOk()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testSaveTask() throws Exception {
        InputStream is = this.getClass().getResourceAsStream
                ("/com/dfssi/dataplatform/manager/monitor/task/interface/Save.json");
        String jsonStr = IOUtils.toString(is);
        JsonObject modelJsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();

        mvc.perform(MockMvcRequestBuilders.post("/manager/monitor/task/save").contentType(MediaType
                .APPLICATION_JSON_UTF8).content(modelJsonObject.toString()).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
    }
}
