package in.ineuron.restcontrollers;

import in.ineuron.annotation.ValidateUser;
import in.ineuron.dto.ChatResponse;
import in.ineuron.dto.GroupChatRequest;
import in.ineuron.models.Chat;
import in.ineuron.models.User;
import in.ineuron.services.ChatService;
import in.ineuron.services.TokenStorageService;
import in.ineuron.services.UserService;
import in.ineuron.utils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {

    private ChatService chatService;
    private UserService userService;
    private UserUtils userUtils;
    private TokenStorageService tokenService;


    @PostMapping("/single")
    public ResponseEntity<ChatResponse> createSingleChatHandler(@RequestParam Long participantId, @CookieValue("auth-token") String authToken) {
        System.out.println("From chat "+authToken);
        User user = userService.fetchUserByAuthToken(authToken);
        ChatResponse singleChat = chatService.createSingleChat(user.getId(), participantId);

        return ResponseEntity.ok(singleChat);
    }

    @GetMapping("/{chat-id}")
    public ResponseEntity<Chat> findChatByIdHandler(@PathVariable("chat-id") Long chatId, HttpServletRequest request) {

        Chat chat = chatService.findChatById(chatId);
        return ResponseEntity.ok(chat);
    }

    @GetMapping("/all-chat")
    public ResponseEntity<List<Chat>> findAllChatsByUserIdHandler(HttpServletRequest request) {

        Long userId = userUtils.getRequestingUserId(request);
        List<Chat> chats = chatService.findAllChatsByUserId(userId);

        return ResponseEntity.ok(chats);
    }

    @PostMapping("/create-group")
    public ResponseEntity<Chat> createGroupHandler(@RequestBody GroupChatRequest chatReq, HttpServletRequest request) {

        Long userId = userUtils.getRequestingUserId(request);
        Chat group = chatService.createGroup(chatReq, userId);

        return ResponseEntity.ok(group);
    }
    @PutMapping("/{chat-id}/add-user/{user-to-add-id}")
    public ResponseEntity<Chat> addUserToGroupHandler(@PathVariable("chat-id") Long chatId,
            @PathVariable("user-to-add-id") Long userToAddId,  HttpServletRequest request) {

        Long reqUserId = userUtils.getRequestingUserId(request);
        Chat chat = chatService.addUserToGroup(chatId,userToAddId,reqUserId);

        return ResponseEntity.ok(chat);
    }

    @PutMapping("/{chat-id}/remove-user/{user-to-remove-id}")
    public ResponseEntity<Chat> removeUserToGroupHandler(@PathVariable("chat-id") Long chatId,
                                                      @PathVariable("user-to-remove-id") Long userToRemoveId,  HttpServletRequest request) {

        Long reqUserId = userUtils.getRequestingUserId(request);
        Chat chat = chatService.removeUserFromGroup(chatId,userToRemoveId,reqUserId);

        return ResponseEntity.ok(chat);
    }

    @PutMapping("/{chat-id}/rename-group/{new-name}")
    public ResponseEntity<Chat> renameGroupHandler(@PathVariable("chat-id") Long chatId,
                                                      @PathVariable("new-name") String newGroupName,  HttpServletRequest request) {

        Long reqUserId = userUtils.getRequestingUserId(request);
        Chat chat = chatService.renameGroup(chatId,newGroupName, reqUserId);

        return ResponseEntity.ok(chat);
    }

    @DeleteMapping("/{chat-id}/delete-group")
    public ResponseEntity<String> renameGroupHandler(@PathVariable("chat-id") Long chatId, HttpServletRequest request) {

        Long userId = userUtils.getRequestingUserId(request);
        chatService.deleteChat(chatId, userId);

        return ResponseEntity.ok("Chat deleted successfully..");
    }








}
