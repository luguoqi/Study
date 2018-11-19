package com.yjl.quartz.scheduler;

import org.joda.time.Duration;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.MethodInvoker;
import static com.yjl.quartz.scheduler.Constants.APPLICATION_CONTEXT_KEY;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;


public class BaseDynamicScheduler implements InitializingBean
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDynamicScheduler.class);

    private static final String TARGET_BEAN_NAME_KEY = "targetBean";
    private static final String ARGUMENTS_KEY = "arguments";
    private static final String METHOD_NAME_KEY = "method";

    private final Scheduler scheduler;

    public BaseDynamicScheduler(final Scheduler newScheduler)
    {
        this.scheduler = newScheduler;
    }

    public void afterPropertiesSet()
    {
        Assert.notNull(this.scheduler, "Scheduler must be set.");
    }


    /**
     * 调度执行
     * @param jobName
     * @param group
     * @param cronExpression
     * @param invocationDetail
     */
    public void scheduleInvocation(final String jobName, final String group, final String cronExpression,
        final InvocationDetail invocationDetail)
    {
        schedule(createDynamicJobDetail(invocationDetail, jobName, group), buildCronTrigger(jobName, group, cronExpression));
    }
    public void scheduleInvocation(final String jobName, final String group, final Date when,
                                   final InvocationDetail invocationDetail)
    {
        schedule(createDynamicJobDetail(invocationDetail, jobName, group), buildExactTimeTrigger(jobName, group, when));
    }



    /**
     * 构建trriger
     * @param jobName
     * @param group
     * @param cronExpression
     * @return
     */
    private Trigger buildCronTrigger(final String jobName, final String group, final String cronExpression)
    {
        CronTrigger trigger;
        try
        {
            trigger = new CronTrigger(jobName, group, jobName, group, cronExpression);
        }
        catch (ParseException e)
        {
            throw new IllegalArgumentException("Invalid cronExpression " + cronExpression, e);
        }
        return trigger;
    }


    /**
     * 构建时间trigger
     * @param jobName
     * @param group
     * @param when
     * @return
     */
    private SimpleTrigger buildExactTimeTrigger(final String jobName, final String group, final Date when)
    {
        SimpleTrigger trigger = new SimpleTrigger(jobName, group, when);
        trigger.setJobName(jobName);
        trigger.setJobGroup(group);
        return trigger;
    }


    /**
     * 按时间间隔调度
     * @param jobName
     * @param group
     * @param repeateInterval
     * @param invocationDetail
     */
    public void scheduleWithInterval(final String jobName, final String group, final Duration repeateInterval,
        final InvocationDetail invocationDetail)
    {
        SimpleTrigger trigger = buildRepeatingInterval(jobName, group, repeateInterval);
        schedule(createDynamicJobDetail(invocationDetail, jobName, group), trigger);
    }


    /**
     * 构建重复间隔trigger
     * @param jobName
     * @param group
     * @param repeateInterval
     * @return
     */
    private SimpleTrigger buildRepeatingInterval(final String jobName, final String group,
        final Duration repeateInterval)
    {
        SimpleTrigger trigger = new SimpleTrigger(jobName, group, new Date());
        trigger.setRepeatInterval(repeateInterval.getMillis());
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setJobName(jobName);
        trigger.setJobGroup(group);
        return trigger;
    }


    /**
     * 创建动态job
     * @param invocationDetail
     * @param jobName
     * @param group
     * @return
     */
    private JobDetail createDynamicJobDetail(final InvocationDetail invocationDetail, final String jobName, final String group)
    {
        JobDetail detail = new JobDetail(jobName, group, MethodInvocatingScheduledJob.class);
        setJobArguments(invocationDetail, detail);
        setJobToAutoDelete(detail);
        return detail;
    }

    /**
     * 设置job参数
     * @param invocationDetail
     * @param detail
     */
    private void setJobArguments(final InvocationDetail invocationDetail, final JobDetail detail)
    {
        detail.getJobDataMap().put(TARGET_BEAN_NAME_KEY, invocationDetail.getTargetBeanName());
        detail.getJobDataMap().put(METHOD_NAME_KEY, invocationDetail.getTargetMethod());
        detail.getJobDataMap().put(ARGUMENTS_KEY, invocationDetail.getMethodArgs());
    }

    /**
     * 设置job自动删除
     * @param detail
     */
    private void setJobToAutoDelete(final JobDetail detail)
    {
        detail.setDurability(false);
    }


    /**
     * 【开始调度】
     * @param job
     * @param trigger
     */
    public void schedule(final JobDetail job, final Trigger trigger)
    {
        if (isJobExists(job))
        {
            rescheduleJob(job, trigger);
        }
        else
        {
            doScheduleJob(job, trigger);
        }
    }


    /**
     * 【job是否存在】
     * @param job
     * @return
     */
    public boolean isJobExists(final JobDetail job)
    {
        try
        {
            return this.scheduler.getJobDetail(job.getName(), job.getGroup()) != null;
        }
        catch (SchedulerException e)
        {
            throw new IllegalStateException("Failed to find Job.", e);
        }
    }


    /**
     * 【调度job】
     * @param job
     * @param trigger
     */
    private void doScheduleJob(final JobDetail job, final Trigger trigger)
    {
        try
        {
            this.scheduler.scheduleJob(job, trigger);
        }
        catch (SchedulerException e)
        {
            throw new IllegalStateException("Failed to schedule the Job.", e);
        }
    }

    /**
     * 【刷新job】
     * @param job
     * @param trigger
     */
    private void rescheduleJob(final JobDetail job, final Trigger trigger)
    {
        try
        {
            this.scheduler.rescheduleJob(trigger.getName(), job.getGroup(), trigger);
        }
        catch (SchedulerException e)
        {
            throw new IllegalStateException("Failed to reschedule the Job.", e);
        }
    }


    /**
     * 【删除job】
     * @param jobName
     * @param group
     */
    public void deleteJob(final String jobName, final String group)
    {
        try
        {
            this.scheduler.unscheduleJob(jobName, group);
            this.scheduler.deleteJob(jobName, group);
            this.scheduler.removeTriggerListener(jobName);
        }
        catch (SchedulerException e)
        {
            throw new IllegalStateException("Failed to delete the Job.", e);
        }
    }


    /**
     * 动态job子类
     */
    public static class MethodInvocatingScheduledJob implements Job
    {

        public void execute(final JobExecutionContext context) throws JobExecutionException
        {
            try
            {
                JobDataMap data = jobData(context);
                invokeMethod(targetBean(context, data), method(data), arguments(data));
            }
            catch (Exception e)
            {
                LOGGER.error(e.getMessage());
                throw new JobExecutionException(e);
            }
        }

        private JobDataMap jobData(final JobExecutionContext context)
        {
            return context.getJobDetail().getJobDataMap();
        }

        private Object targetBean(JobExecutionContext context, JobDataMap data) throws Exception {
            return applicationContext(context).getBean(data.getString(TARGET_BEAN_NAME_KEY));
        }

        private String method(final JobDataMap data)
        {
            return data.getString(METHOD_NAME_KEY);
        }

        private Object[] arguments(final JobDataMap data)
        {
            return (Object[]) data.get(ARGUMENTS_KEY);
        }

        private ApplicationContext applicationContext(JobExecutionContext context) throws Exception {
            ApplicationContext appCtx = (ApplicationContext)context.getScheduler()
                                                                   .getContext()
                                                                   .get(APPLICATION_CONTEXT_KEY);

            if (appCtx == null) {
                throw new JobExecutionException("Application context unavailable to scheduler for key '"
                                                    + APPLICATION_CONTEXT_KEY + "'");
            }
            return appCtx;
        }

        private void invokeMethod(final Object target, final String method, final Object[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException
        {
            MethodInvoker inv = new ArgumentConvertingMethodInvoker();

            inv.setTargetObject(target);
            inv.setTargetMethod(method);
            inv.setArguments(args);
            inv.prepare();
            inv.invoke();
        }
    }


    /**
     * 执行详情子类
     */
    public static class InvocationDetail
    {
        private String targetBeanName;
        private String targetMethod;
        private List<?> methodArgs;

        public InvocationDetail(final String newTargetBean, final String newTargetMethod, final List<?> newMethodArgs)
        {
            this.targetBeanName = newTargetBean;
            this.targetMethod = newTargetMethod;
            this.methodArgs = newMethodArgs;
        }

        public String getTargetBeanName()
        {
            return targetBeanName;
        }

        public String getTargetMethod()
        {
            return targetMethod;
        }

        public Object[] getMethodArgs()
        {
            return methodArgs.toArray();
        }
    }
}
