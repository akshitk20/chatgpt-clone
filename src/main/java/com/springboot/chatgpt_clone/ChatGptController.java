package com.springboot.chatgpt_clone;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ChatGptController {

    private final ChatClient chatClient;

    public ChatGptController(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new PromptChatMemoryAdvisor(new InMemoryChatMemory())) // advisor -> intercepts the request and modifies it. this is used to preserve the state of request
                .build();
    }

    @GetMapping("")
    public String home() {
        return "index";
    }

    @HxRequest
    @PostMapping("/api/chat")
    public HtmxResponse generate(@RequestParam String message, Model model) {
        System.out.println("user message" + message);
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();
        model.addAttribute("response", response);
        model.addAttribute("message", message);
        return HtmxResponse.builder()
                .view("response::responseFragment")
                .view("recent-message-list :: messageFragment")
                .build();
    }
}
