package spring.quartz.dynamic.test.demo;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
/**
 * demo job
 * 
 * @author wangxiaohu
 * @version $Id: DemoJobDetail.java, v 0.1 2017年8月29日 下午1:50:54 wangxiaohu Exp $
 */
public class DemoJobDetail implements Job{
    //这里就是因为有上文中的AutowiredInternalBeanOfJobFactory才可以使用@Autowired注解
    @Autowired
    private DemoService demoService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("DemoJobDetail.run..");
        demoService.printNowTime();
    }
}
