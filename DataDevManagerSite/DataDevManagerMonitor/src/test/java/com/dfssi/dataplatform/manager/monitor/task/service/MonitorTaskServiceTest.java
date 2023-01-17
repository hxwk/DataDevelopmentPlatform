package com.dfssi.dataplatform.manager.monitor.task.service;

import com.dfssi.dataplatform.manager.monitor.ManagerMonitorApp;
import com.dfssi.dataplatform.manager.monitor.task.entity.MonitorTaskEntity;
import com.dfssi.dataplatform.manager.monitor.task.entity.MonitorTaskExecRecordEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ManagerMonitorApp.class)
@WebAppConfiguration
public class MonitorTaskServiceTest {

    @Autowired
    private MonitorTaskService deployService;

    @Test
    public void testListTasks() throws Exception {
        MonitorTaskEntity mte = deployService.getTask("1213");
        System.out.println(mte.getName());
    }

    @Test
    public void testListExecRecords() throws Exception {
        List<MonitorTaskExecRecordEntity> mte = deployService.getExecRecords(0, 10, "1213", null, null);
        System.out.println(mte.size());
    }

    @Test
    public void testStartOozieTask() throws Exception {
        deployService.startTask("20180125103444000000");
    }
}
