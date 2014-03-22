package ru.beta2.platform.scheduler;

import org.apache.commons.configuration.Configuration;

import java.util.TimeZone;

/**
 * User: inc
 * Date: 22.03.14
 * Time: 13:54
 */
public class SchedulerConfig
{

    private final Configuration cfg;

    public SchedulerConfig(Configuration cfg)
    {
        this.cfg = cfg;
    }

    public TimeZone getDefaultTriggerTimeZone()
    {
        String tz = cfg.getString("defaultTriggerTimeZone", null);
        return tz != null ? TimeZone.getTimeZone(tz) : null;
    }

    public JobExecutionMonitorConfig getJobExecutionMonitorConfig()
    {
        return new JobExecutionMonitorConfig(cfg.subset("jobExecutionMonitor"));
    }

}
