package com.xcxcxcxcx.mini.common.topic.router;

import com.xcxcxcxcx.mini.api.connector.topic.router.LoadBalance;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class RandomLoadBalance implements LoadBalance{

    private Random random = new Random();

    @Override
    public int choose(List<Integer> list, String... keys) {

        if(list == null || list.isEmpty()){
            throw new RuntimeException("no list to choose");
        }
        if(list.size() == 1){
            return 0;
        }

        int randomKeyIndex = random.nextInt(keys.length);

        return keys[randomKeyIndex].hashCode()&list.size();
    }

    @Override
    public int choose(List<Integer> list) {

        if(list == null || list.isEmpty()){
            throw new RuntimeException("no list to choose");
        }
        if(list.size() == 1){
            return 0;
        }

        int index = random.nextInt(list.size());

        return index;
    }
}
