package dk.trustworks.personalassistant.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by hans on 29/09/15.
 */
public class CacheHandler {

    private static CacheHandler instance;

    private final Cache<String, List> listCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000).recordStats()
            .build();
    private final Cache<String, Map> mapCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(100).recordStats()
            .build();

    private CacheHandler() {
    }

    public static CacheHandler createCacheHandler() {
        return ((instance!=null)?instance:(instance = new CacheHandler()));
    }

    public Cache<String, List> getListCache() {
        return listCache;
    }

    public Cache<String, Map> getMapCache() {
        return mapCache;
    }
}
