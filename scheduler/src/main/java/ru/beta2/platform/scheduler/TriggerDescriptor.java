package ru.beta2.platform.scheduler;

import org.quartz.*;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

/**
 * User: inc
 * Date: 21.03.14
 * Time: 0:51
 */
public class TriggerDescriptor extends ObjectDescriptor implements Serializable
{

    private int priority;
    private int misfireInstruction;

    private String description;

    private Date startTime;
    private Date endTime;

    private String schedule;

    public TriggerDescriptor()
    {
    }

    public TriggerDescriptor(Trigger trigger)
    {
        name = trigger.getKey().getName();
        group = trigger.getKey().getGroup();

        priority = trigger.getPriority();
        misfireInstruction = trigger.getMisfireInstruction();

        description = trigger.getDescription();

        startTime = trigger.getStartTime();
        endTime = trigger.getEndTime();

        if (trigger instanceof CronTrigger) {
            schedule = ((CronTrigger) trigger).getCronExpression();
        }
    }

    public int getPriority()
    {
        return priority;
    }

    public int getMisfireInstruction()
    {
        return misfireInstruction;
    }

    public String getDescription()
    {
        return description;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public String getSchedule()
    {
        return schedule;
    }

    public void setSchedule(String schedule)
    {
        this.schedule = schedule;
    }

    public Trigger toTrigger(JobKey forJob, TimeZone tz)
    {
        TriggerBuilder<Trigger> b = TriggerBuilder.newTrigger()
                .withIdentity(getNameSafe(), getGroupSafe())
                .withDescription(description)
                .withPriority(priority)
                .forJob(forJob)
                ;
        if (startTime != null) {
            b.startAt(startTime);
        }
        if (endTime != null) {
            b.endAt(endTime);
        }

        CronScheduleBuilder sb = CronScheduleBuilder.cronSchedule(schedule);
        if (tz != null) {
            sb.inTimeZone(tz);
        }
        switch (misfireInstruction) {
            case CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING:
                sb.withMisfireHandlingInstructionDoNothing();
                break;
            case CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW:
                sb.withMisfireHandlingInstructionFireAndProceed();
                break;
            case CronTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY:
                sb.withMisfireHandlingInstructionIgnoreMisfires();
                break;
        }
        b.withSchedule(sb);

        return b.build();
    }

    @Override
    public String toString()
    {
        return "TriggerDescriptor{" +
                "priority=" + priority +
                ", misfireInstruction=" + misfireInstruction +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", schedule='" + schedule + '\'' +
                '}';
    }
}
