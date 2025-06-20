package com.skillbox.redisdemo;

import java.util.*;

import static java.lang.System.out;

public class RedisTest {

    private static final int USERS = 20;

    private static final int SLEEP = 1000;

    private static void log(int Users) {
        String log = String.format("- На главной странице показываем пользователя %s", Users);
        out.println(log);
    }

    private static void paid(int Users) {
        String paid = String.format("> Пользователь %s оплатил платную услугу", Users);
        out.println(paid);
    }

    public static void main(String[] args) throws InterruptedException {

        RedisStorage redis = new RedisStorage();
        redis.init();

        for (int userId = 1; userId <= USERS; userId++) {
            redis.logSite(userId);
        }

        while (true) {
            int userFirstHalf = new Random().nextInt(USERS/2); // Выбор места с 0 по 9 показа первого оплаченного пользователя в первой половине Seta
            int userLastHalf = new Random().nextInt(USERS/2) + USERS/2; // Выбор места с 10 по 19 показа второго оплаченного пользователя во второй половине Seta
            int userPaidFirst; // Id первого оплаченного пользователя
            int userPaidSecond; // Id второго оплаченного пользователя
            boolean falseFirst = false;
            boolean falseSecond = false;
            userPaidFirst = new Random().nextInt(USERS) + 1; // Выбор первого оплаченного пользователя
            userPaidSecond = new Random().nextInt(USERS) + 1; // Выбор второго оплаченного пользователя
            if (userPaidSecond == userPaidFirst) { //Повторный выбор второго оплаченного пользователя, если он такой же, как и первый
                userPaidSecond = new Random().nextInt(USERS) + 1;
            }
            if (userFirstHalf <= redis.users.rank(String.valueOf(userPaidFirst))) { // Удаление первого оплаченного пользователя, если
                redis.users.remove(String.valueOf(userPaidFirst)); // место его показа меньше или такое же, как место его нахождения
                falseFirst = true;
            }
            if (userLastHalf <= redis.users.rank(String.valueOf(userPaidSecond))) { // Удаление второго оплаченного пользователя, если
                redis.users.remove(String.valueOf(userPaidSecond)); // место его показа меньше или такое же, как место его нахождения
                falseSecond = true;
            }
            for (String userId : redis.getUsers()) {
                int userRank = redis.users.rank(userId); // Место пользователя в Setе
                if (userRank == userFirstHalf) {
                    paid(userPaidFirst);
                    log(userPaidFirst);
                }
                if (userRank == userLastHalf) {
                    paid(userPaidSecond);
                    log(userPaidSecond);
                }
                out.println("- На главной странице показываем пользователя " + userId);
            }
            if (!falseFirst) {
                redis.users.remove(String.valueOf(userPaidFirst));
            }
            if (!falseSecond) {
                redis.users.remove(String.valueOf(userPaidSecond));
            }
            redis.logSite(userPaidFirst);
            redis.logSite(userPaidSecond);
            out.println(" ");
            Thread.sleep(SLEEP);
        }
    }
}