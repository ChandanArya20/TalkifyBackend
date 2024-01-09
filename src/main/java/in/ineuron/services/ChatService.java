package in.ineuron.services;

import in.ineuron.dto.GroupChatRequest;
import in.ineuron.exception.ChatNotFoundException;
import in.ineuron.exception.UserNotFoundException;
import in.ineuron.models.Chat;

import java.util.List;

public interface ChatService {

    public Chat createChat(Long reqUserId, Long participantId) throws UserNotFoundException;
    public Chat findChatById(Long chatId) throws ChatNotFoundException;
    public List<Chat> findAllChatsByUserId(Long userId) throws UserNotFoundException;
    public List<Chat> createGroup(GroupChatRequest req, Long reqUserId) throws UserNotFoundException;
    public Chat addUserToGroup(Long chatId, Long userId) throws ChatNotFoundException, UserNotFoundException;
    public Chat renameGroup(Long chatId, String newChatName, Long reqUserId) throws ChatNotFoundException, UserNotFoundException;
    public Chat removeUserFromGroup(Long chatId, Long userId, Long reqUserId) throws ChatNotFoundException, UserNotFoundException;
    public Chat deleteChat(Long chatId, Long userId) throws ChatNotFoundException, UserNotFoundException;

}
