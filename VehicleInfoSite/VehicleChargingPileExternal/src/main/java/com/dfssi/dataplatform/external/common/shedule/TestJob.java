package com.dfssi.dataplatform.external.common.shedule;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/10/12 19:12
 */
public class TestJob implements ITask {
    public int exec() {
        System.out.println("11111111111");
        return 1;
    }
}
