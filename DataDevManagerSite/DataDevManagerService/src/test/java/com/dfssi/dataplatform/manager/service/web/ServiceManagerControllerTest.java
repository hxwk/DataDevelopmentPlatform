package com.dfssi.dataplatform.manager.service.web;

import com.dfssi.dataplatform.manager.service.app.ServiceManagerApp;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ServiceManagerApp.class)
@WebAppConfiguration
public class ServiceManagerControllerTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testListAllModels() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/manager/analysis/listmodels/0/10").contentType(MediaType
                .APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status()
                .isOk()).andDo(MockMvcResultHandlers.print());
    }
}
