package in.ineuron.services.impl;

import in.ineuron.dto.GroupChatRequest;
import in.ineuron.dto.UserResponse;
import in.ineuron.exception.ChatNotFoundException;
import in.ineuron.exception.UserNotFoundException;
import in.ineuron.models.Chat;
import in.ineuron.models.User;
import in.ineuron.repositories.ChatRepository;
import in.ineuron.services.ChatService;
import in.ineuron.services.UserService;

import java.util.List;

public class ChatServiceImpl implements ChatService {

    private ChatRepository chatRepo;
    private UserService userService;

    public ChatServiceImpl(ChatRepository chatRepo, UserService userService) {
        this.chatRepo = chatRepo;
        this.userService = userService;
    }

    @Override
    public Chat createChat(Long reqUserId, Long participantId) throws UserNotFoundException {

        User reqUser = userService.fetchUserById(reqUserId);
        User participantUser = userService.fetchUserById(participantId);

        Chat chat = chatRepo.findSingleChatByUserIds(reqUserId, participantId);
        if(chat!=null){
            return chat;
        }

        Chat newChat = new Chat();
        newChat.setCreatedBy(reqUser);
        newChat.getUsers().add(reqUser);
        newChat.getUsers().add(participantUser);
        newChat.setIsGroup(false);

        return newChat;
    }

    @Override
    public Chat findChatById(Long chatId) throws ChatNotFoundException {
        return null;
    }

    @Override
    public List<Chat> findAllChatsByUserId(Long userId) throws UserNotFoundException {
        return null;
    }

    @Override
    public List<Chat> createGroup(GroupChatRequest req, Long reqUserId) throws UserNotFoundException {
        return null;
    }

    @Override
    public Chat addUserToGroup(Long chatId, Long userId) throws ChatNotFoundException, UserNotFoundException {
        return null;
    }

    @Override
    public Chat renameGroup(Long chatId, String newChatName, Long reqUserId) throws ChatNotFoundException, UserNotFoundException {
        return null;
    }

    @Override
    public Chat removeUserFromGroup(Long chatId, Long userId, Long reqUserId) throws ChatNotFoundException, UserNotFoundException {
        return null;
    }

    @Override
    public Chat deleteChat(Long chatId, Long userId) throws ChatNotFoundException, UserNotFoundException {
        return null;
    }
}
