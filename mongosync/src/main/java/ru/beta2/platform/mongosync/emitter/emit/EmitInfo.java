package ru.beta2.platform.mongosync.emitter.emit;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: inc
 * Date: 04.04.14
 * Time: 23:13
 */
public class EmitInfo
{

    public static class ChangeResult
    {
        private final Set<String> added;
        private final Set<String> removed;
        public ChangeResult(Set<String> added, Set<String> removed)
        {
            this.added = added;
            this.removed = removed;
        }
        public Set<String> getAdded()
        {
            return added;
        }

        public Set<String> getRemoved()
        {
            return removed;
        }

        public boolean hasChanges()
        {
            return CollectionUtils.isNotEmpty(added) || CollectionUtils.isNotEmpty(removed);
        }
    }

    private String address;
    private Set<String> namespaces;

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public Set<String> getNamespaces()
    {
        return namespaces;
    }

    public void setNamespaces(Set<String> namespaces)
    {
        this.namespaces = namespaces;
    }

    public Set<String> addNamespaces(Collection<String> toAdd)
    {
        initNamespaces();
        HashSet<String> added = new HashSet<String>();
        for (String ns : toAdd) {
            if (this.namespaces.add(ns)) {
                added.add(ns);
            }
        }
        return added;
    }

    public Set<String> removeNamespaces(Collection<String> toRemove)
    {
        if (this.namespaces == null) {
            return Collections.emptySet();
        }
        HashSet<String> removed = new HashSet<String>();
        for (String ns : toRemove) {
            if (this.namespaces.remove(ns)) {
                removed.add(ns);
            }
        }
        return removed;
    }

    public ChangeResult changeNamespaces(Collection<String> namespaces)
    {
        initNamespaces();
        return new ChangeResult(
                addNamespaces(namespaces),
                removeNamespaces(CollectionUtils.subtract(this.namespaces, namespaces))
        );
    }

    public boolean hasNamespaces()
    {
        return CollectionUtils.isNotEmpty(namespaces);
    }

    @Override
    public String toString()
    {
        return "EmitInfo{" +
                "address='" + address + '\'' +
                ", namespaces=" + namespaces +
                '}';
    }

    private void initNamespaces()
    {
        if (this.namespaces == null) {
            this.namespaces = new HashSet<String>();
        }
    }
}
