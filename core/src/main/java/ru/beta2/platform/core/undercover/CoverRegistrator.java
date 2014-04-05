package ru.beta2.platform.core.undercover;

import org.picocontainer.Startable;
import ru.beta2.platform.core.util.HandlerRegistration;

import java.util.List;

/**
 * User: inc
 * Date: 01.04.14
 * Time: 23:24
 */
public class CoverRegistrator implements Startable
{

    private final UndercoverService undercover;
    private final CoverRegistrable[] covers;

    private HandlerRegistration[] registrations;

    public CoverRegistrator(UndercoverService undercover, CoverRegistrable... covers)
    {
        this.undercover = undercover;
        this.covers = covers;
    }

    @Override
    public void start()
    {
        registrations = new HandlerRegistration[covers.length];
        int i = 0;
        for (CoverRegistrable cover : covers) {
            registrations[i++] = undercover.registerCover(cover.getCoverPath(), cover.getCoverInterface(), cover);
        }
    }

    @Override
    public void stop()
    {
        for (HandlerRegistration r : registrations) {
            r.removeHandler();
        }
        registrations = null;
    }
}
