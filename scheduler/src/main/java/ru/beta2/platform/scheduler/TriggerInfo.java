package ru.beta2.platform.scheduler;

import org.quartz.Trigger;

import java.util.Date;

/**
 * User: inc
 * Date: 21.03.14
 * Time: 20:53
 */
public class TriggerInfo extends TriggerDescriptor
{

    private Date nextFireTime;
    private Date previousFireTime;
    private Date finalFireTime;
    private String state;

    public TriggerInfo()
    {
        super();
    }

    public TriggerInfo(Trigger trigger)
    {
        super(trigger);
        nextFireTime = trigger.getNextFireTime();
        previousFireTime = trigger.getPreviousFireTime();
        finalFireTime = trigger.getFinalFireTime();
    }

    public Date getNextFireTime()
    {
        return nextFireTime;
    }

    public Date getPreviousFireTime()
    {
        return previousFireTime;
    }

    public Date getFinalFireTime()
    {
        return finalFireTime;
    }

    public String getState()
    {
        return state;
    }

    public void setState(Trigger.TriggerState state)
    {
        this.state = state.name();
    }
}
