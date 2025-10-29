package org.springframework.samples.petclinic.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;

/**
 * Provides the shared Hazelcast instance used for Bucket4j rate limits.
 */
@Configuration
public class HazelcastConfiguration {

    private static final String CLUSTER_NAME = "petclinic-rate-limit";

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setClusterName(CLUSTER_NAME);

        // disable persistence / store
        MapStoreConfig mapStoreConfig = new MapStoreConfig().setEnabled(false);

        config.addMapConfig(new MapConfig("rateLimitVisits")
            .setTimeToLiveSeconds(300)
            .setMapStoreConfig(mapStoreConfig));
        config.addMapConfig(new MapConfig("rateLimitOwners")
            .setTimeToLiveSeconds(300)
            .setMapStoreConfig(mapStoreConfig));
        config.addMapConfig(new MapConfig("rateLimitApi")
            .setTimeToLiveSeconds(300)
            .setMapStoreConfig(mapStoreConfig));

        NetworkConfig networkConfig = config.getNetworkConfig();
        JoinConfig join = networkConfig.getJoin();
        // keep multicast on to avoid binding to specific IPs locally
        join.getTcpIpConfig().setEnabled(false);
        MulticastConfig multicastConfig = join.getMulticastConfig();
        multicastConfig.setEnabled(true);

        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    public HazelcastCacheManager hazelcastCacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }
}
