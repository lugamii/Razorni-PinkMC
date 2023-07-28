package dev.razorni.core.database.redis;

import dev.razorni.core.Core;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisHandler {

    private final Core core;
    
    public RedisHandler(Core core) {
        this.core = core;

        try {
            this.core.setLocalJedisPool(new JedisPool(new JedisPoolConfig(), "redis-14912.c278.us-east-1-4.ec2.cloud.redislabs.com", 14912, 20000, "pusitekurac123", 0));
        } catch (Exception var6) {
            this.core.setLocalJedisPool(null);
            var6.printStackTrace();
            this.core.getLogger().warning("Couldn't connect to a Redis instance at localhost" + ".");
        }
        try {
            this.core.setBackboneJedisPool(new JedisPool(new JedisPoolConfig(), "redis-14912.c278.us-east-1-4.ec2.cloud.redislabs.com", 14912, 20000, "pusitekurac123", 0));
        } catch (Exception var5) {
            this.core.setBackboneJedisPool(null);
            var5.printStackTrace();
            this.core.getLogger().warning("Couldn't connect to a Backbone Redis instance at localhost" + ".");
        }
    }
}
