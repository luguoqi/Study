package com.yjl.quartz.controller;

import com.yjl.quartz.Schedule.QuartzJobFactory;
import com.yjl.quartz.Schedule.ScheduleJob;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/quartz")
public class QuartzController2 {
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @RequestMapping(value = "/quartzs", method = RequestMethod.GET)
    public String list(Model model) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        String[] groupNameLs = scheduler.getJobGroupNames();     //所有的group的name
        String[] jobNameLs = null;
        //一个group下可以有多个job
        List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
        for (int i = 0; i < groupNameLs.length; i++) {
            jobNameLs = scheduler.getJobNames(groupNameLs[i]);
            for (int j = 0; j < jobNameLs.length; j++) {
                Trigger[] ts = scheduler.getTriggersOfJob(jobNameLs[j], groupNameLs[i]);
                for (int k = 0; k < ts.length; k++) {
                    ScheduleJob job = new ScheduleJob();
                    job.setJobName(jobNameLs[j]);
                    job.setJobGroup(groupNameLs[i]);
                    job.setDesc("触发器：1111");
                    job.setCronExpression("表达式");
                    job.setDesc("触发器：" + ts[k].getFullName());
                    CronTrigger ex = (CronTrigger) ts[k];
                    job.setCronExpression(ex.getCronExpression());
                    int sta = scheduler.getTriggerState(ts[k].getName(), ts[k].getGroup());
                    if (sta == -1) {
                        job.setJobStatus("不存在");
                    } else if (sta == 0) {
                        job.setJobStatus("正常");
                    } else if (sta == 1) {
                        job.setJobStatus("暂停");
                    } else if (sta == 2) {
                        job.setJobStatus("完成");
                    } else if (sta == 3) {
                        job.setJobStatus("错误");
                    } else if (sta == 4) {
                        job.setJobStatus("阻塞");
                    } else {
                        job.setJobStatus("其他");
                    }
                    jobList.add(job);
                }
            }
        }
        model.addAttribute("allJobs", jobList);
        return "quartzs";
    }

    // 链接到add页面时是GET请求，会访问这段代码
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(@ModelAttribute("job") ScheduleJob job) {
        return "add";
    }
    // 在具体添加用户时，是post请求，就访问以下代码
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add2(ScheduleJob job) throws SchedulerException, ParseException {// 一定要紧跟Validate之后写验证结果类
        String seconds = job.getCronExpression();
        String cronExp = "0/" + seconds + " * * * * ?";
        job.setCronExpression(cronExp);
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
//		TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
        Trigger trigger2 = scheduler.getTrigger(job.getJobName(), job.getJobGroup());
        // 获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
        CronTrigger trigger = (CronTrigger) trigger2;
        // 不存在，创建一个
        if (null == trigger) {
            JobDetail jobD = new JobDetail(job.getJobName(), job.getJobGroup(), QuartzJobFactory.class);
            jobD.getJobDataMap().put("scheduleJob", job);
            // 表达式调度构建器
//			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
            CronExpression ex = new CronExpression(job.getCronExpression());
            // 按新的cronExpression表达式构建一个新的trigger
//			trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup())
//					.withSchedule(scheduleBuilder).build();
            trigger = new CronTrigger(job.getJobName(), job.getJobGroup());
            trigger.setCronExpression(ex);
            scheduler.scheduleJob(jobD, trigger);
        } else {
            // Trigger已存在，那么更新相应的定时设置
            // 表达式调度构建器
            CronExpression ex = new CronExpression(job.getCronExpression());
            // 按新的cronExpression表达式重新构建trigger
            trigger.setCronExpression(ex);
            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(job.getJobName(), job.getJobGroup(), trigger);
        }
        return "redirect:/quartz/quartzs";
    }
    @RequestMapping(value = "/{jobGroup}/{jobName}/stop", method = RequestMethod.GET)
    public String stop(@PathVariable String jobGroup, @PathVariable String jobName) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
//		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        scheduler.pauseJob(jobName, jobGroup);
        return "redirect:/quartz/quartzs";
    }

    @RequestMapping(value = "/{jobGroup}/{jobName}/reStart", method = RequestMethod.GET)
    public String reStart(@PathVariable String jobGroup, @PathVariable String jobName) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
//		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        scheduler.resumeJob(jobName, jobGroup);
        return "redirect:/quartz/quartzs";
    }

    @RequestMapping(value = "/{jobGroup}/{jobName}/startNow", method = RequestMethod.GET)
    public String startNow(@PathVariable String jobGroup, @PathVariable String jobName) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
//		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        scheduler.triggerJob(jobName, jobGroup);
        return "redirect:/quartz/quartzs";
    }

    @RequestMapping(value = "/{jobGroup}/{jobName}/del", method = RequestMethod.GET)
    public String del(@PathVariable String jobGroup, @PathVariable String jobName) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
//		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        scheduler.deleteJob(jobName, jobGroup);
        return "redirect:/quartz/quartzs";
    }

    @RequestMapping(value = "/{jobGroup}/{jobName}/oneSecond", method = RequestMethod.GET)
    public String oneSecond(@PathVariable String jobGroup, @PathVariable String jobName) throws SchedulerException, ParseException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(jobName, jobGroup);
        //获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
        //表达式调度构建器
//	    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0/1 * * * * ?");
        CronExpression ex = new CronExpression("0/1 * * * * ?");
        //按新的cronExpression表达式重新构建trigger
        trigger.setCronExpression(ex);
        //按新的trigger重新设置job执行
        scheduler.rescheduleJob(jobName, jobGroup, trigger);
        return "redirect:/quartz/quartzs";
    }

    @RequestMapping(value = "/{jobGroup}/{jobName}/fiveSeconds", method = RequestMethod.GET)
    public String fiveSeconds(@PathVariable String jobGroup, @PathVariable String jobName) throws SchedulerException, ParseException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(jobName, jobGroup);
        //表达式调度构建器
        CronExpression ex = new CronExpression("0/5 * * * * ?");
        //按新的cronExpression表达式重新构建trigger
        trigger.setCronExpression(ex);
        //按新的trigger重新设置job执行
        scheduler.rescheduleJob(jobName, jobGroup, trigger);
        return "redirect:/quartz/quartzs";
    }

    public SchedulerFactoryBean getSchedulerFactoryBean() {
        return schedulerFactoryBean;
    }

    public void setSchedulerFactoryBean(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

}
