package com.example.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * packageName    : com.example.helloworld
 * fileName       : CoffeeController
 * author         : 정재윤
 * date           : 2023-07-30
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-07-30        정재윤       최초 생성
 */
@RestController
public class CoffeeController {

    private StringRedisTemplate redisTemplate;

    @Autowired
    public CoffeeController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello world!";
    }

    @GetMapping("/setCoffee")
    public String setCoffee(@RequestParam String name){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("coffee", name);
        return "saved";
    }

    @GetMapping("/getCoffee")
    public String setCoffee(){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get("coffee");
    }

    @GetMapping("/login")
    public String login(HttpSession session, @RequestParam String name){
        session.setAttribute("name", name);
        return "saved";
    }

    @GetMapping("/myName")
    public String myName(HttpSession session){
        String myName = (String)session.getAttribute("name");
        return myName;
    }
}
