package spring.quartz.dynamic.test.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class AppForGetAllJobTest 
{
    public static void main( String[] args ) throws SchedulerException, InterruptedException, ClassNotFoundException
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
            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(job.getJobClass());
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(job.getJobName(), job.getJobGroup()).build();
            jobDetail.getJobDataMap().put("userName", "demo1");//可以设置job的参数
            jobDetail.getJobDataMap().put("sleep", "5000");
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
        System.out.println("get all schedule job...");
        getAllScheduleJob(schedule);
        System.out.println("get all running job...");
        while(true){
            getAllRunningJob(schedule);
            Thread.sleep(200);
        }
       
        
    }
    //获取所有的计划中的job
    private static void getAllScheduleJob(Scheduler schedule) throws SchedulerException{
        GroupMatcher<JobKey> groupMatcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeySet = schedule.getJobKeys(groupMatcher);
        for(JobKey jobKey:jobKeySet){
            List<? extends Trigger> triggerList = schedule.getTriggersOfJob(jobKey);
            String jobName = jobKey.getName();
            String jobGroup = jobKey.getGroup();
            for(Trigger trigger :triggerList){
                Trigger.TriggerState triggerState = schedule.getTriggerState(trigger.getKey());
                String triggerStateName = triggerState.name();
                CronTrigger cronTrigger = (CronTrigger)trigger;
                String cronExpression = cronTrigger.getCronExpression();
                System.out.println("all schedule job: <"+jobName+"> " +"<"+jobGroup+"> " + "<"+cronExpression+">"+"<"+triggerStateName+">");
            }
        }
    }
    //获取所有正在运行的job
    private static void getAllRunningJob(Scheduler schedule) throws SchedulerException{
        List<JobExecutionContext> currentlyExecutingJobList = schedule.getCurrentlyExecutingJobs();
        for(JobExecutionContext jobExecutionContext:currentlyExecutingJobList){
            JobDetail jobDetail = jobExecutionContext.getJobDetail();
            JobKey jobKey = jobDetail.getKey();
            CronTrigger cronTrigger = (CronTrigger)jobExecutionContext.getTrigger();
            String jobName = jobKey.getName();
            String jobGroup = jobKey.getGroup();
            String cronExpression = cronTrigger.getCronExpression(); 
            TriggerKey triggerKey = cronTrigger.getKey();
            Trigger.TriggerState triggerState = schedule.getTriggerState(triggerKey);
            String triggerStateName = triggerState.name();
            System.out.println("all running job: <"+jobName+"> " +"<"+jobGroup+"> " + "<"+cronExpression+">"+"<"+triggerStateName+">");
        }
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
}
