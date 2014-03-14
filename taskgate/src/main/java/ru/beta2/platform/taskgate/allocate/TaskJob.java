package ru.beta2.platform.taskgate.allocate;

import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * User: Inc
 * Date: 08.03.14
 * Time: 16:12
 */
public class TaskJob implements Job
{

    private final ClientSession session;
    private final ClientProducer producer;

    public TaskJob(ClientSession session, ClientProducer producer)
    {
        this.session = session;
        this.producer = producer;
    }

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException
    {
        ClientMessage msg = session.createMessage(true);

    }
}
