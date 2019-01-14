package com.hugo.common.pushcenter.provider.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class RedisUtil {

    @Autowired
    RedisTemplate redisTemplate;

    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object get(String key) {
        try {
            return key == null ? null : redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete(String... key) {
        if (key == null || key.length == 0) {
            return;
        }
        try {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public long leftPush(String key, String value) {
        try {
            Long count = redisTemplate.opsForList().leftPush(key, value);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long removeAllValue(String key, String value) {
        try {
            Long count = redisTemplate.opsForList().remove(key, 0, value);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
