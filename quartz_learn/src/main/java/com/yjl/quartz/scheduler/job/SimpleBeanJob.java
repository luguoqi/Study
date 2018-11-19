package com.yjl.quartz.scheduler.job;


import com.yjl.quartz.scheduler.job.support.SimpleBean;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;


public class SimpleBeanJob extends QuartzJobBean
{

    private SimpleBean simpleBean;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException
    {
        this.simpleBean.execute();        
    }

    public void setSimpleBean(SimpleBean simpleBean)
    {
        this.simpleBean = simpleBean;
    }
}
