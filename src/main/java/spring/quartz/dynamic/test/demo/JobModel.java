package spring.quartz.dynamic.test.demo;
/**
 * 存放任务的对象
 * 
 * @author wangxiaohu
 * @version $Id: ScheduleModel.java, v 0.1 2017年8月30日 上午9:45:54 wangxiaohu Exp $
 */
public class JobModel {
    private String jobId;
    private String jobName;
    private String jobGroup;
    private String cronExpression;
    private String state;
    private String desc;
    public String getJobId() {
        return jobId;
    }
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public String getJobGroup() {
        return jobGroup;
    }
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }
    public String getCronExpression() {
        return cronExpression;
    }
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
}
