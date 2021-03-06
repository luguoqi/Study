package com.yjl.quartz.scheduler.job.support;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class SimpleBean implements Serializable
{
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleBean.class);

    public void execute()
    {
        LOGGER.info("Simple job bean executed");
    }
}
