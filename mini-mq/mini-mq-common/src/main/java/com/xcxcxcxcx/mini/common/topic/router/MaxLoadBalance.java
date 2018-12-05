package com.xcxcxcxcx.mini.common.topic.router;

import com.xcxcxcxcx.mini.api.connector.topic.router.LoadBalance;

import java.util.*;

/**
 *
 * 最大负载
 * @author XCXCXCXCX
 * @since 1.0
 */
public class MaxLoadBalance implements LoadBalance{

    @Override
    public int choose(List<Integer> list, String... keys) {

        Set<Integer> keyIndex = hash(list.size(), keys);

        if(list == null || list.isEmpty()){
            throw new RuntimeException("no list to choose");
        }
        if(list.size() == 1){
            return 0;
        }

        int maxIndex = 0;
        int max = list.get(0);
        for(int i = 0; i < list.size(); i++){
            if(keyIndex.contains(i) && list.get(i) > max){
                maxIndex = i;
                max = list.get(i);
            }
        }
        if(max == 0){
            return -1;
        }

        return maxIndex;
    }

    @Override
    public int choose(List<Integer> list) {
        if(list == null || list.isEmpty()){
            throw new RuntimeException("no list to choose");
        }
        if(list.size() == 1){
            return 0;
        }

        int maxIndex = 0;
        int max = list.get(0);
        for(int i = 0; i < list.size(); i++){
            if(list.get(i) > max){
                maxIndex = i;
                max = list.get(i);
            }
        }
        if(max == 0){
            return -1;
        }

        return maxIndex;
    }

}
