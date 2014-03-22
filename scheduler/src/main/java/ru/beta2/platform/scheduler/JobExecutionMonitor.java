package ru.beta2.platform.scheduler;

import com.mongodb.*;
import org.bson.BasicBSONObject;
import org.bson.types.ObjectId;
import org.picocontainer.Startable;
import org.quartz.*;
import org.quartz.utils.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.util.LifecycleException;

import java.util.ArrayList;
import java.util.List;

/**
 * User: inc
 * Date: 22.03.14
 * Time: 14:14
 */
public class JobExecutionMonitor implements Startable
{

    private static final String JOB_LISTENER_NAME = JobExecutionMonitor.class.getName();

    private final Logger log = LoggerFactory.getLogger(JobExecutionMonitor.class);

    private final JobExecutionMonitorConfig cfg;
    private final Scheduler scheduler;
    private final MongoClient mongo;

    private DBCollection coll;

    public JobExecutionMonitor(JobExecutionMonitorConfig cfg, Scheduler scheduler, MongoClient mongo)
    {
        this.cfg = cfg;
        this.scheduler = scheduler;
        this.mongo = mongo;
    }

    @Override
    public void start()
    {
        log.trace("Start JobExecutionMonitor");

        if (cfg.isEnabled()) {
            log.debug("Job execution monitoring is ENABLED");

            coll = mongo.getDB(cfg.getDbName()).getCollection(cfg.getCollectionName());

            BasicDBObject indx = new BasicDBObject();
            indx.put("jobKey.group", 1);
            indx.put("jobKey.name", 1);
            coll.ensureIndex(indx);

            try {
                log.trace("Add job listener");
                scheduler.getListenerManager().addJobListener(new JobMonitor());
            }
            catch (SchedulerException e) {
                log.error("Error add job listener", e);
                throw new LifecycleException("Error add job listener", e);
            }
        }
    }

    @Override
    public void stop()
    {
        log.trace("Stop JobExecutionMonitor");
        if (cfg.isEnabled()) {
            try {
                log.trace("Remove job listener");
                scheduler.getListenerManager().removeJobListener(JOB_LISTENER_NAME);
            }
            catch (SchedulerException e) {
                log.error("Error remove job listener", e);
                throw new LifecycleException("Error remove job listener", e);
            }

            coll = null;
        }
    }

    public List<JobExecutionInfo> getJobExecutions(ObjectKey jobKey, Integer limit)
    {
        log.trace("Get job executions: jobKey={}, limit={}", jobKey, limit);

        BasicDBObject ref = new BasicDBObject();
        if (jobKey != null) {
            ref.put("jobKey.group", jobKey.getGroup());
            ref.put("jobKey.name", jobKey.getName());
        }
        DBCursor c = coll.find(ref).sort(new BasicDBObject("_id", -1));
        if (limit != null) {
            c.limit(limit);
        }

        List<JobExecutionInfo> result = new ArrayList<JobExecutionInfo>();
        for (DBObject o : c) {
            result.add(new JobExecutionInfo((BasicBSONObject) o));
        }
        return result;
    }

    private class JobMonitor implements JobListener
    {

        @Override
        public String getName()
        {
            return JOB_LISTENER_NAME;
        }

        @Override
        public void jobToBeExecuted(JobExecutionContext context)
        {
            if (log.isTraceEnabled()) {
                log.trace("Job to be executed: job={}, trigger={}",
                        context.getJobDetail().getKey(), context.getTrigger().getKey());
            }
        }

        @Override
        public void jobExecutionVetoed(JobExecutionContext context)
        {
            if (log.isTraceEnabled()) {
                log.trace("Job executed vetoed: job={}, trigger={}",
                        context.getJobDetail().getKey(), context.getTrigger().getKey());
            }
        }

        @Override
        public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException)
        {
            if (log.isTraceEnabled()) {
                log.trace("Job was executed: job={}, trigger={}",
                        context.getJobDetail().getKey(), context.getTrigger().getKey());
            }

            BasicDBObject o = new BasicDBObject();
            o.put("jobKey", keyToDBObject(context.getJobDetail().getKey()));
            o.put("triggerKey", keyToDBObject(context.getTrigger().getKey()));
            o.put("fireTime", context.getFireTime());
            o.put("scheduledFireTime", context.getScheduledFireTime());
            o.put("jobRunTime", context.getJobRunTime());
            o.put("refireCount", context.getRefireCount());

            if (context.getFireInstanceId() != null) {
                o.put("fireInstanceId", context.getFireInstanceId());
            }
            if (jobException != null) {
                o.put("exception", jobException.getMessage());
            }

            coll.save(o);
        }

        private BasicDBObject keyToDBObject(Key key)
        {
            BasicDBObject o = new BasicDBObject();
            o.put("name", key.getName());
            o.put("group", key.getGroup());
            return o;

        }
    }

}
