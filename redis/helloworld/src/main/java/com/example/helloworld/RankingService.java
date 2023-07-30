package com.example.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * packageName    : com.example.helloworld
 * fileName       : RankingService
 * author         : 정재윤
 * date           : 2023-07-30
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-07-30        정재윤       최초 생성
 */
@Service
public class RankingService {

    private final String KEY = "arenaBoard";

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RankingService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    public boolean setChampScore(String name, int rate){
        ZSetOperations zSetOperations = stringRedisTemplate.opsForZSet();
        zSetOperations.add(KEY, name, rate);
        return true;
    }

    public Long getChampRanking(String name){
        ZSetOperations zSetOperations = stringRedisTemplate.opsForZSet();
        Long rank = zSetOperations.rank(KEY, name);
        return rank;
    }

    public List<String> getChampTopRanking(int limit){
        ZSetOperations zSetOperations = stringRedisTemplate.opsForZSet();
        Set<String> rankingList = zSetOperations.reverseRange(KEY, 0,limit-1);
        return new ArrayList<>(rankingList);
    }

}
