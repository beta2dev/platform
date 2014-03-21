package ru.beta2.platform.scheduler;

import org.apache.commons.lang.StringUtils;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.quartz.utils.Key;

/**
 * User: inc
 * Date: 21.03.14
 * Time: 12:02
 */
public abstract class ObjectDescriptor
{

    protected String name;
    protected String group;

    private String generatedName;

    public String getName()
    {
        return name;
    }

    public String getGroup()
    {
        return group;
    }

    public String getGroupSafe()
    {
        return StringUtils.isEmpty(group) ? Key.DEFAULT_GROUP : group;
    }

    public String getNameSafe()
    {
        if (StringUtils.isEmpty(name)) {
            if (generatedName == null) {
                generatedName = Key.createUniqueName(getGroupSafe());
            }
            return generatedName;
        }
        return name;
    }

}
