package com.MindMitra.Rolebased.Controller;


import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @GetMapping
    public String chat(Principal principal) {
        return "Welcome " + principal.getName() ;
    }
}