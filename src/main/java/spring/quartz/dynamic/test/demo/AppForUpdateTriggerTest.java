package spring.quartz.dynamic.test.demo;

import java.util.ArrayList;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class AppForUpdateTriggerTest 
{
    public static void main( String[] args ) throws SchedulerException, InterruptedException, ClassNotFoundException
    {
        
        System.out.println( "Hello World!" );
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{
            "classpath:spring-quartz.xml"
        });
        Scheduler schedule = (Scheduler) context.getBean("schedulerFactoryBean");
        //从数据库读取任务
        List<JobModel> jobList = getJobListFromDB();
        for(JobModel job:jobList){
            //获取触发器
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
            CronTrigger trigger = (CronTrigger) schedule.getTrigger(triggerKey);
            //创建jobdetail
            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(job.getJobClass());
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(job.getJobName(), job.getJobGroup()).build();
            jobDetail.getJobDataMap().put("userName", "demo1");//可以设置job的参数
            //根据cron表达式是生成cron对象
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
            //创建触发器
            trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup())
                    .withSchedule(cronScheduleBuilder).build();
            try {
                schedule.scheduleJob(jobDetail, trigger);
                
            } catch (ObjectAlreadyExistsException e) {
                System.out.println("schedule has exists...");
            }
            System.out.println("add one schedule: " + triggerKey.getName());
        }
        
        //暂停15s钟后修改任务执行时间
        Thread.sleep(15000);
        System.out.println("update job...");
        List<JobModel> updateTimeJobList = getUpdateTimeJobListFromDB();
        for(JobModel job:updateTimeJobList){
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
            CronTrigger oldTrigger = (CronTrigger)schedule.getTrigger(triggerKey);
            if(!oldTrigger.getCronExpression().equals(job.getCronExpression())){
                CronTrigger newTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(
                    CronScheduleBuilder.cronSchedule(job.getCronExpression())).build();
                schedule.rescheduleJob(triggerKey, newTrigger);
                System.out.println("update "+triggerKey + " cron [" + oldTrigger.getCronExpression() +"] to [" + newTrigger.getCronExpression() + "]");
            }
            
        }
        Thread.sleep(10000000);
        
    }
    
    private static List<JobModel> getJobListFromDB(){
        List<JobModel> jobList = new ArrayList<>();
        for(int i=0;i<1;i++){
            JobModel job = new JobModel();
            job.setJobId(String.valueOf(i));
            job.setJobName(i+"-name");
            job.setJobClass(DemoJobDetail.class.getName());
            job.setJobGroup("demo");
            job.setState("1");
            job.setCronExpression("0/5 * * * * ?");
            jobList.add(job);
        }
        return jobList;
    }
    
    private static List<JobModel> getUpdateTimeJobListFromDB(){
        List<JobModel> jobList = getJobListFromDB();
        for(JobModel job:jobList){
            job.setCronExpression("0/10 * * * * ?");
        }
        return jobList;
        
    }
}
