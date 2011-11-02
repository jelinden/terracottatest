package fi.example.terracottatest;

import junit.framework.Assert;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.TerracottaClientConfiguration;
import net.sf.ehcache.config.TerracottaConfiguration;
import java.io.Serializable;

public class TerracottaTest {

    Logger log = Logger.getLogger(TerracottaTest.class);
    private static final String SERVER = "localhost:9510";

    private CacheManager cacheManager;
    private Cache cache;
    
    String cacheName;
    private final Serializable keyA = "a";
    private final Serializable keyB = "b";
    private final Serializable keyC = "c";

    @Before
    public void setUp(){
        Configuration configuration = new Configuration();
        TerracottaClientConfiguration terracottaConfig = new TerracottaClientConfiguration();
        terracottaConfig.setUrl(SERVER);
        configuration.addTerracottaConfig(terracottaConfig);

        int maxElementsInMemory = 1;
        int maxElementsOnDisk = 0;
        long timeToIdleSeconds = 15;
        long timeToLiveSeconds = 15;
        String cacheName = "TEST_CACHE";
        TerracottaConfiguration terraConfig = new TerracottaConfiguration();
        terraConfig.setClustered(true);
        CacheConfiguration testCache = new CacheConfiguration(cacheName, maxElementsInMemory).statistics(true)
                .terracotta(terraConfig)
                .logging(true)
                .maxElementsOnDisk(maxElementsOnDisk)
                .timeToIdleSeconds(timeToIdleSeconds)
                .timeToLiveSeconds(timeToLiveSeconds)
                .diskPersistent(false);
        configuration.addCache(testCache);
        configuration.addDefaultCache(new CacheConfiguration("default", 1000));

        cacheManager = new CacheManager(configuration);
        cache = cacheManager.getCache(cacheName);
    }
    
    @Test
    public void test() {
        cache.put(new Element(keyA, keyA));
        cache.put(new Element(keyB, keyB));
        
        log.info(cache.get(keyA).getObjectValue());
        log.info(cache.get(keyB).getObjectValue());
        Assert.assertEquals(2, cache.getSize());
        Assert.assertNotNull(cache.get(keyB));
    }
}