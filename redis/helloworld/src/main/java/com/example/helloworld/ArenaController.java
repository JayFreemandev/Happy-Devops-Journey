package com.example.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * packageName    : com.example.helloworld
 * fileName       : ArenaController
 * author         : 정재윤
 * date           : 2023-07-30
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-07-30        정재윤       최초 생성
 */
@RestController
public class ArenaController {

    private RankingService rankingService;

    @Autowired
    public ArenaController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/setChamp")
    public Boolean setChamp(@RequestParam String name, @RequestParam int rate){
        return rankingService.setChampScore(name, rate);
    }

    @GetMapping("/getRank")
    public Long getRank(@RequestParam String name){
        return rankingService.getChampRanking(name);
    }

    @GetMapping("/getTopRank")
    public List<String> getRankList(@RequestParam int rate){
        return rankingService.getChampTopRanking(rate);
    }
}
