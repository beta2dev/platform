package ru.beta2.platform.mongosync.emitter.emit;

import org.bson.types.ObjectId;
import org.jongo.marshall.jackson.oid.Id;
import ru.beta2.platform.core.undercover.CoverRegistrable;
import ru.beta2.platform.core.util.HandlerRegistration;

import java.util.Collection;

/**
 * User: inc
 * Date: 04.04.14
 * Time: 23:15
 */
public class EmitManager implements EmitManagerCover, CoverRegistrable
{

    private EmitListener listener;

    public HandlerRegistration setListener(EmitListener listener)
    {
        if (listener != null) {
            throw new IllegalStateException("EmitListener already assigned");
        }
        this.listener = listener;
        return new HandlerRegistration()
        {
            @Override
            public void removeHandler()
            {
                EmitManager.this.listener = null;
            }
        };
    }

    @Override
    public Collection<EmitInfo> getEmits()
    {
        return null;  // todo !!! implement
    }

    @Override
    public void startEmit(String address, String... namespaces)
    {
        // todo !!! implement
    }

    @Override
    public void stopEmitAll(String address)
    {
        // todo !!! implement
    }

    @Override
    public void stopEmit(String address, String... namespaces)
    {
        // todo !!! implement
    }

    @Override
    public void changeEmit(String address, String... namespaces)
    {
        // todo !!! implement
    }

    @Override
    public String getCoverPath()
    {
        return "/mongosync-emitter-emitmanager";
    }

    @Override
    public Class getCoverInterface()
    {
        return EmitManagerCover.class;
    }
}

class EmitRecord extends EmitInfo
{
    @Id
    private ObjectId id;

    public ObjectId getId()
    {
        return id;
    }

    public void setId(ObjectId id)
    {
        this.id = id;
    }
}