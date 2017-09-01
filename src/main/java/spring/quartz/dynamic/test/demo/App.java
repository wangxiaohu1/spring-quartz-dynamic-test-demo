package spring.quartz.dynamic.test.demo;

import java.util.ArrayList;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws SchedulerException, InterruptedException
    {
        
        System.out.println( "Hello World!" );
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{
            "classpath:spring-quartz.xml"
        });
        Scheduler schedule = (Scheduler) context.getBean("schedulerFactoryBean");
//        Scheduler schedule = schedulerFactoryBean.getScheduler();
        //从数据库读取任务
        List<JobModel> jobList = getJobListFromDB();
        for(JobModel job:jobList){
            //获取触发器
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
            CronTrigger trigger = (CronTrigger) schedule.getTrigger(triggerKey);
            //创建jobdetail
            JobDetail jobDetail = JobBuilder.newJob(DemoJobDetail.class).withIdentity(job.getJobName(), job.getJobGroup()).build();
            jobDetail.getJobDataMap().put("userName", "demo1");//可以设置job的参数
            //根据cron表达式是生成cron对象
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
            //创建触发器
            trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup())
                    .withSchedule(cronScheduleBuilder).build();
            schedule.scheduleJob(jobDetail, trigger);
            System.out.println("add one schedule: " + triggerKey.getName());
        }
        Thread.sleep(10000000);
        
    }
    
    private static List<JobModel> getJobListFromDB(){
        List<JobModel> jobList = new ArrayList<>();
        for(int i=0;i<2;i++){
            JobModel job = new JobModel();
            job.setJobId(String.valueOf(i));
            job.setJobName(i+"-name");
            job.setJobGroup("demo");
            job.setState("1");
            job.setCronExpression("0/5 * * * * ?");
            jobList.add(job);
        }
        return jobList;
    }
}
