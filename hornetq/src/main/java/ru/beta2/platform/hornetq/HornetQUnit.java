package ru.beta2.platform.hornetq;

import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.deployers.impl.FileConfigurationParser;
import org.hornetq.core.journal.impl.AIOSequentialFileFactory;
import org.hornetq.core.server.JournalType;
import org.hornetq.utils.XMLUtil;
import org.picocontainer.MutablePicoContainer;
import org.w3c.dom.Element;
import ru.beta2.platform.core.assembly.AssemblyUnit;
import ru.beta2.platform.core.assembly.PicoContainerFactory;
import ru.beta2.platform.core.config.ConfigService;

import static org.picocontainer.Characteristics.CACHE;

/**
 * User: Inc
 * Date: 20.02.14
 * Time: 19:20
 */
public class HornetQUnit extends AssemblyUnit
{

    public HornetQUnit(PicoContainerFactory containerFactory, ConfigService configService)
    {
        super(containerFactory, configService);
    }

    @Override
    protected String getConfigName()
    {
        return "hornetq";
    }

    @Override
    protected void populatePico(MutablePicoContainer pico) throws Exception
    {
        pico.addComponent(createHornetQConfiguration());
        pico.as(CACHE).addAdapter(new HornetQServerComponent());
    }

    private org.hornetq.core.config.Configuration createHornetQConfiguration() throws Exception
    {
        ConfigurationImpl cfg = new ConfigurationImpl();

        String xml = getConfigValue().trim();
        if (xml.isEmpty()) {
            JournalType journalType = AIOSequentialFileFactory.isSupported() ? JournalType.ASYNCIO : JournalType.NIO;
            log.debug("Use default configuration with detected journalType={}", journalType);
            cfg.setJournalType(journalType);
            return cfg;
        }

        log.debug("Parse XML configuration");
        Element e = XMLUtil.stringToElement(XMLUtil.replaceSystemProps(xml));

        FileConfigurationParser parser = new FileConfigurationParser();
        // https://jira.jboss.org/browse/HORNETQ-478 - We only want to validate AIO when
        //     starting the server
        //     and we don't want to do it when deploying hornetq-queues.xml which uses the same parser and XML format
        parser.setValidateAIO(true);
        parser.parseMainConfig(e, cfg);

        return cfg;
    }
}
