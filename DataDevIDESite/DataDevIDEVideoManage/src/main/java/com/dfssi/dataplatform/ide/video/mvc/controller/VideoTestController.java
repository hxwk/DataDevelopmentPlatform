package com.dfssi.dataplatform.ide.video.mvc.controller;

import com.dfssi.dataplatform.ide.video.mvc.entity.Greeting;
import com.dfssi.dataplatform.ide.video.mvc.service.impl.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by yanghs on 2018/8/23.
 * 调试kafka的类，未使用到
 */
@RestController
public class VideoTestController {

    private static final String TEMPLATE = "Hello, %s!";

    @Autowired
    private KafkaService kafkaService;


    @RequestMapping("/kafka")
    public String greeting() {
        String topic="test1";
        String key="abc";
        String data="123123";
        return kafkaService.sendKafka(topic,key,data);
    }

    /**
     * 请求视频列表
     * @param name
     * @return
     */
    @RequestMapping("/greeting")
    public HttpEntity<Greeting> greeting(
            @RequestParam(value = "name", required = false, defaultValue = "World") String name) {

        Greeting greeting = new Greeting(String.format(TEMPLATE, name));
        greeting.add(linkTo(methodOn(VideoTestController.class).showVideo()).withSelfRel());

        return new ResponseEntity<>(greeting, HttpStatus.OK);
    }

    /**
     * 读取视频流返回页面
     */
    @RequestMapping("/showVideo")
    public HttpEntity<String> showVideo(){
        return new ResponseEntity<>("", HttpStatus.OK);
    }
}
