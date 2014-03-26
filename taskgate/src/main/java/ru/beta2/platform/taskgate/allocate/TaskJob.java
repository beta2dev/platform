package ru.beta2.platform.taskgate.allocate;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.taskgate.task.ClientMessageTaskAdapter;
import ru.beta2.platform.taskgate.task.JobDataMapTaskAdapter;
import ru.beta2.platform.taskgate.task.TaskDescriptor;

/**
 * User: Inc
 * Date: 08.03.14
 * Time: 16:12
 */
public class TaskJob implements Job
{

    private final Logger log = LoggerFactory.getLogger(TaskJob.class);

    private final AllocatorConfig cfg;
    private final MessageProducer producer;

    public TaskJob(AllocatorConfig cfg, MessageProducer producer)
    {
        this.cfg = cfg;
        this.producer = producer;
    }

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException
    {
        JobDetail job = ctx.getJobDetail();
        log.debug("Execute task job {}", job.getKey());

        TaskDescriptor descriptor = new TaskDescriptor(new JobDataMapTaskAdapter(ctx.getMergedJobDataMap()));

        String targetName = descriptor.getTaskTarget();
        TargetConfig targetConfig = targetName != null ? cfg.getTarget(targetName) : cfg.getDefaultTarget();
        log.debug("Task target detection: targetName={}, targetConfig={}", targetName, targetConfig);

        if (targetConfig == null) {
            log.error("Target not found for job {}, unschedule job", job.getKey());
            JobExecutionException ex = new JobExecutionException("Target not found for job");
            ex.unscheduleAllTriggers();
            throw ex;
        }

        log.trace("Create message");
        ClientMessage msg = producer.createMessage();
        descriptor.copyTo(new ClientMessageTaskAdapter(msg));
        try {
            log.debug("Send task message: address={}, message={}", targetConfig.getAddress(), msg);
            producer.send(targetConfig.getAddress(), msg);
        }
        catch (HornetQException e) {
            log.error("Error send message", e);
            throw new JobExecutionException("Error send message", e);
        }
    }
}
