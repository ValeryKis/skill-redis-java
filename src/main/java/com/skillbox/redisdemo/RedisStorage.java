package com.skillbox.redisdemo;

import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.config.Config;

import java.util.Date;

import static java.lang.System.out;

public class RedisStorage {

    // Объект для работы с Redis
    private RedissonClient redisson;

    // Объект для работы с Sorted Set'ом
    public RScoredSortedSet<String> users;

    private final static String KEY = "USERS";

    public double getTs() {
        return (double) new Date().getTime() / 1000;
    }

    public RScoredSortedSet<String> getUsers() {
        return users;
    }

    void init() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        try {
            redisson = Redisson.create(config);
        } catch (RedisConnectionException Exc) {
            out.println("Не удалось подключиться к Redis");
            out.println(Exc.getMessage());
        }
        // Объект для работы с ключами
        RKeys rKeys = redisson.getKeys();
        users = redisson.getScoredSortedSet(KEY);
        rKeys.delete(KEY);
    }

    // Фиксирует посещение пользователем страницы
    void logSite(int user_id)
    {
        //ZADD ONLINE_USERS
        users.add(getTs(), String.valueOf(user_id));
    }
}