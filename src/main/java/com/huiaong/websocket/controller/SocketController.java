package com.huiaong.websocket.controller;

import com.huiaong.websocket.store.SocketStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@RestController
public class SocketController {

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * 服务器指定用户进行推送,需要前端开通 var socket = new SockJS(host+'/myUrl' + '?token=1234');
     */
    @RequestMapping("/sendUser")
    public void sendUser(String hall) {
        log.info("hall = {} ,对其发送您好", hall);
        WebSocketSession webSocketSession = SocketStore.get(hall);
        if (webSocketSession != null) {
            /**
             * 主要防止broken pipe
             */
            template.convertAndSendToUser(hall, "/queue/sendUser", "您好");
        }

    }

    /**
     * 广播，服务器主动推给连接的客户端
     */
    @RequestMapping("/sendTopic")
    public void sendTopic(@RequestParam String imgPath) {
        log.info("发送给所有链接的客户端，对他们发：{}", imgPath);
        template.convertAndSend("/queue/sendTopic", imgPath);
    }

    /**
     * 客户端发消息，服务端接收
     *
     * @param message
     */
    // 相当于RequestMapping
    @MessageMapping("/sendServer")
    public void sendServer(String message) {
        log.info("message:{}", message);
    }

    /**
     * 客户端发消息，大家都接收，相当于直播说话
     *
     * @param message
     * @return
     */
    @MessageMapping("/sendAllUser")
    @SendTo("/queue/sendTopic")
    public String sendAllUser(String message) {
        // 也可以采用template方式
        return message;
    }

    /**
     * 点对点用户聊天，这边需要注意，由于前端传过来json数据，所以使用@RequestBody
     * 这边需要前端开通var socket = new SockJS(host+'/myUrl' + '?token=4567');   token为指定name
     * @param map
     */
    @MessageMapping("/sendMyUser")
    public void sendMyUser(@RequestBody Map<String, String> map) {
        log.info("map = {}", map);
        WebSocketSession webSocketSession = SocketStore.get(map.get("name"));
        if (webSocketSession != null) {
            log.info("sessionId = {}", webSocketSession.getId());
            template.convertAndSendToUser(map.get("name"), "/queue/sendUser", map.get("message"));
        }
    }

}
