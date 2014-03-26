package ru.beta2.platform.taskgate.task;

/**
 * User: inc
 * Date: 24.03.14
 * Time: 12:12
 */
public class NameValuePair implements org.apache.http.NameValuePair
{


    private final String name;
    private final String value;

    public NameValuePair(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }
}
