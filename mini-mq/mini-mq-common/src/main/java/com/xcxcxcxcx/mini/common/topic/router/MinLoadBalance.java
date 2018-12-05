package com.xcxcxcxcx.mini.common.topic.router;

import com.xcxcxcxcx.mini.api.connector.topic.router.LoadBalance;

import java.util.List;
import java.util.Set;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class MinLoadBalance implements LoadBalance {
    @Override
    public int choose(List<Integer> list, String... keys) {

        Set<Integer> keyIndex = hash(list.size(), keys);

        if(list == null || list.isEmpty()){
            throw new RuntimeException("no list to choose");
        }
        if(list.size() == 1){
            return 0;
        }

        int minIndex = 0;
        int min = list.get(0);
        for(int i = 0; i < list.size(); i++){
            if(keyIndex.contains(i) && list.get(i) < min){
                minIndex = i;
                min = list.get(i);
            }
        }
        if(min == 0){
            return -1;
        }

        return minIndex;
    }

    @Override
    public int choose(List<Integer> list) {
        if(list == null || list.isEmpty()){
            throw new RuntimeException("no list to choose");
        }
        if(list.size() == 1){
            return 0;
        }

        int minIndex = 0;
        int min = list.get(0);
        for(int i = 0; i < list.size(); i++){
            if(list.get(i) < min){
                minIndex = i;
                min = list.get(i);
            }
        }
        if(min == 0){
            return -1;
        }

        return minIndex;
    }
}
