//package in.ineuron.restcontrollers;
//
//import in.ineuron.annotation.ValidateUser;
//import in.ineuron.dto.MessageRequest;
//import in.ineuron.models.Message;
//import in.ineuron.services.MessageService;
//import in.ineuron.services.TokenStorageService;
//import in.ineuron.services.UserService;
//import in.ineuron.utils.UserUtils;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.AllArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/api/message")
//@ValidateUser
//@AllArgsConstructor
//public class MessageController {
//
//    private MessageService messageService;
//    private UserService userService;
//    private UserUtils userUtils;
//    private TokenStorageService tokenService;
//
//    @PostMapping("/send")
//    public ResponseEntity<Message> sendMessageHandler(@RequestBody MessageRequest msgReq, HttpServletRequest request) {
//
//        Long reqUser = userUtils.getRequestingUserId(request);
//        Message message = messageService.sendMessage(msgReq);
//        return ResponseEntity.ok(message);
//    }
//
//    @GetMapping("/chat/{chat-id}")
//    public ResponseEntity<List<Message>> getChatsMessageHandler(@PathVariable("chat-id") Long chatId, HttpServletRequest request) {
//
//        Long reqUser = userUtils.getRequestingUserId(request);
//        List<Message> message = messageService.getChatsMessage(chatId,reqUser);
//        return ResponseEntity.ok(message);
//    }
//
//    @DeleteMapping("/{message-id}")
//    public ResponseEntity<String> deleteMessageHandler(@PathVariable("message-id") Long messageId, HttpServletRequest request) {
//
//        Long reqUser = userUtils.getRequestingUserId(request);
//        messageService.deleteMessage(messageId,reqUser);
//        return ResponseEntity.ok("Message deleted successfully...");
//    }
//
//
//
//}