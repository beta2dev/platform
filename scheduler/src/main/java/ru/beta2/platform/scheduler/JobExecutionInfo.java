package ru.beta2.platform.scheduler;

import com.mongodb.DBObject;
import org.bson.BasicBSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * User: inc
 * Date: 22.03.14
 * Time: 15:16
 */
public class JobExecutionInfo implements Serializable
{

    private ObjectKey jobKey;
    private ObjectKey triggerKey;

    private Date fireTime;
    private Date scheduledFireTime;
    private long jobRunTime;
    private int refireCount;

    private String fireInstanceId;
    private String exception;

    public JobExecutionInfo()
    {
    }

    public JobExecutionInfo(BasicBSONObject o)
    {
        jobKey = objectKey((DBObject) o.get("jobKey"));
        triggerKey = objectKey((DBObject) o.get("triggerKey"));

        fireTime = o.getDate("fireTime");
        scheduledFireTime = o.getDate("scheduledFireTime");
        jobRunTime = o.getLong("jobRunTime");
        refireCount = o.getInt("refireCount");

        fireInstanceId = o.getString("fireInstanceId");
        exception = o.getString("exception");
    }

    public ObjectKey getJobKey()
    {
        return jobKey;
    }

    public ObjectKey getTriggerKey()
    {
        return triggerKey;
    }

    public Date getFireTime()
    {
        return fireTime;
    }

    public Date getScheduledFireTime()
    {
        return scheduledFireTime;
    }

    public long getJobRunTime()
    {
        return jobRunTime;
    }

    public int getRefireCount()
    {
        return refireCount;
    }

    public String getFireInstanceId()
    {
        return fireInstanceId;
    }

    public String getException()
    {
        return exception;
    }

    private ObjectKey objectKey(DBObject o)
    {
        return new ObjectKey(
                String.valueOf(o.get("name")),
                String.valueOf(o.get("group"))
        );
    }

}
