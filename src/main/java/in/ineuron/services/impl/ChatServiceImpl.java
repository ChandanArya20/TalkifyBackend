package in.ineuron.services.impl;

import in.ineuron.dto.GroupChatRequest;
import in.ineuron.exception.ChatNotFoundException;
import in.ineuron.exception.UserNotFoundException;
import in.ineuron.models.Chat;
import in.ineuron.services.ChatService;

import java.util.List;

public class ChatServiceImpl implements ChatService {
    @Override
    public Chat createChat(Long reqUserId, Long participantId) {
        return null;
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
