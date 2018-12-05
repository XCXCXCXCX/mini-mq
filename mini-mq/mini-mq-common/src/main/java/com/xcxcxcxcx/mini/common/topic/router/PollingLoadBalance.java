package com.xcxcxcxcx.mini.common.topic.router;

import com.xcxcxcxcx.mini.api.connector.topic.router.LoadBalance;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class PollingLoadBalance implements LoadBalance{

    private AtomicInteger currentIndex = new AtomicInteger(0);

    private AtomicInteger currentKeyIndex = new AtomicInteger(0);

    @Override
    public int choose(List<Integer> list, String... keys) {

        if(list == null || list.isEmpty()){
            throw new RuntimeException("no list to choose");
        }
        if(list.size() == 1){
            return 0;
        }

        Set<Integer> keyIndexs = new LinkedHashSet<>();
        for (String key: keys){
            keyIndexs.add(key.hashCode()&list.size());
        }
        if(currentKeyIndex.get() > keyIndexs.size() - 1){
            currentKeyIndex.set(0);
        }

        return (Integer)keyIndexs.toArray()[currentKeyIndex.getAndIncrement()];


    }

    @Override
    public int choose(List<Integer> list) {

        if(list == null || list.isEmpty()){
            throw new RuntimeException("no list to choose");
        }
        if(list.size() == 1){
            return 0;
        }

        if(currentIndex.get() > list.size() - 1){
            currentIndex.set(0);
        }

        return currentIndex.getAndIncrement();
    }

    public static void main(String[] args) {
        LoadBalance loadBalance = new RandomLoadBalance();

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(4);
        list.add(3);
        list.add(2);
        for(int i = 0; i < 10; i++){
            int choose = loadBalance.choose(list);
            System.out.println(choose + "," +list.toString());
        }
    }
}
