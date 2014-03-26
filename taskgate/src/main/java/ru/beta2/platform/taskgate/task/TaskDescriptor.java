package ru.beta2.platform.taskgate.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * User: inc
 * Date: 24.03.14
 * Time: 11:32
 */
public class TaskDescriptor
{

    private static final String PARAM_PREFIX = "param.";
    private static final int PARAM_PREFIX_LENGTH = PARAM_PREFIX.length();

    private static final String TASK_PREFIX = "task.";
//    private static final int TASK_PREFIX_LENGTH = TASK_PREFIX.length();

    private static final String BODY_PREFIX = "body.";
//    private static final int BODY_PREFIX_LENGTH = BODY_PREFIX.length();

    private final TaskDataAdapter adapter;

    private Collection<NameValuePair> parameters;

    public TaskDescriptor(TaskDataAdapter adapter)
    {
        this.adapter = adapter;
    }

    public String getTaskId()
    {
        return adapter.getStringProperty(TASK_PREFIX + "id");
    }

    public String getTaskClass()
    {
        return adapter.getStringProperty(TASK_PREFIX + "class");
    }

    public String getTaskMethod()
    {
        return adapter.getStringProperty(TASK_PREFIX + "method");
    }

    public String getTaskTarget()
    {
        return adapter.getStringProperty(TASK_PREFIX + "target");
    }

    public String getBodyMimeType()
    {
        return adapter.getStringProperty((BODY_PREFIX + "mimeType"));
    }

    public String getBodyCharset()
    {
        return adapter.getStringProperty(BODY_PREFIX + "charset");
    }

    public boolean hasParameters()
    {
        return !getParameters().isEmpty();
    }

    public Collection<NameValuePair> getParameters()
    {
        if (parameters == null) {
            for (CharSequence p : adapter.getPropertyNames()) {
                if (p.length() > PARAM_PREFIX_LENGTH && PARAM_PREFIX.equals(p.subSequence(0, PARAM_PREFIX_LENGTH))) {
                    if (parameters == null) {
                        parameters = new ArrayList<NameValuePair>();
                    }
                    parameters.add(new NameValuePair(p.subSequence(PARAM_PREFIX_LENGTH, p.length()).toString(), adapter.getStringProperty(p.toString())));
                }
            }
            if (parameters == null) {
                parameters = Collections.emptyList();
            }
        }
        return parameters;
    }

    public void copyTo(TaskDataAdapter to)
    {
        for (CharSequence p : adapter.getPropertyNames()) {
            String key = p.toString();
            if (isTaskRelatedProperty(key)) {
                to.putStringProperty(key, adapter.getStringProperty(key));
            }
        }
    }

    private boolean isTaskRelatedProperty(String key)
    {
        return key.startsWith(PARAM_PREFIX) || key.startsWith(TASK_PREFIX) || key.startsWith(BODY_PREFIX);
    }

}
