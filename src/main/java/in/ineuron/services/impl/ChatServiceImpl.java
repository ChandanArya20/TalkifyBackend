package in.ineuron.services.impl;

import ch.qos.logback.core.util.DelayStrategy;
import in.ineuron.dto.GroupChatRequest;
import in.ineuron.dto.UserResponse;
import in.ineuron.exception.ChatNotFoundException;
import in.ineuron.exception.UserNotAuthorizedException;
import in.ineuron.exception.UserNotFoundException;
import in.ineuron.models.Chat;
import in.ineuron.models.User;
import in.ineuron.repositories.ChatRepository;
import in.ineuron.services.ChatService;
import in.ineuron.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private ChatRepository chatRepo;
    private UserService userService;

    public ChatServiceImpl(ChatRepository chatRepo, UserService userService) {
        this.chatRepo = chatRepo;
        this.userService = userService;
    }

    @Override
    public Chat createSingleChat(Long reqUserId, Long participantId) {

        User reqUser = userService.fetchUserById(reqUserId);
        User participantUser = userService.fetchUserById(participantId);

        Chat chat = chatRepo.findSingleChatByUserIds(reqUserId, participantId);
        if (chat != null) {
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
    public Chat findChatById(Long chatId) {
        return chatRepo.findById(chatId).orElseThrow(
                () -> new ChatNotFoundException("Chat not found with id " + chatId)
        );
    }

    @Override
    public List<Chat> findAllChatsByUserId(Long userId) {
        User user = userService.fetchUserById(userId);
        return chatRepo.findChatByUsersContaining(user);
    }

    @Override
    public Chat createGroup(GroupChatRequest req, Long reqUserId) {
        Chat group = new Chat();
        User createdBy = userService.fetchUserById(reqUserId);

        group.setIsGroup(true);
        group.setChatName(req.getGroupName());
        group.setChatImage(req.getGroupImage());
        group.getAdmins().add(createdBy);
        group.setCreatedBy(createdBy);

        for (Long userId : req.getUserIds()) {
            User user = new User();
            user.setId(userId);
            group.getUsers().add(user);
        }
        return group;
    }

    @Override
    public Chat addUserToGroup(Long chatId, Long userId, Long reqUserId) {
        Chat chat = findChatById(chatId);
        User reqUser = userService.fetchUserById(reqUserId);
        User user = userService.fetchUserById(userId);

        if (chat.getAdmins().contains(reqUser)) {
            chat.getAdmins().add(user);
            return chat;
        } else {
            throw new UserNotFoundException("Only admins are allowed to delete the group chat");
        }
    }

    @Override
    public Chat renameGroup(Long chatId, String newGroupName, Long reqUserId) {
        Chat chat = findChatById(chatId);
        User reqUser = userService.fetchUserById(reqUserId);

        if (chat.getUsers().contains(reqUser)) {
            chat.setChatName(newGroupName);
            return chatRepo.save(chat);
        } else {
            throw new UserNotAuthorizedException("Requested user is not member of group");
        }

    }

    @Override
    public Chat removeUserFromGroup(Long chatId, Long userId, Long reqUserId) {

        Chat chat = findChatById(chatId);
        User reqUser = userService.fetchUserById(reqUserId);
        User user = userService.fetchUserById(userId);

        if (chat.getAdmins().contains(reqUser)) {
            chat.getAdmins().remove(user);
        } else if (chat.getUsers().contains(reqUser)) {
            if (userId.equals(reqUserId)) {
                chat.getUsers().remove(user);
            } else {
                throw new UserNotAuthorizedException("You can't remove another user");
            }
        } else {
            throw new UserNotAuthorizedException("User not found in the chat");
        }
        return chat;
    }

    @Override
    public void deleteChat(Long chatId, Long userId) {

        Chat chat = findChatById(chatId);
        User user = userService.fetchUserById(userId);

        if (chat.getIsGroup() && !chat.getAdmins().contains(user)) {
            throw new UserNotAuthorizedException("Only admins are allowed to delete the group chat");
        }

        if (!chat.getUsers().contains(user)) {
            throw new UserNotFoundException("User not found in the chat");
        }
        chatRepo.delete(chat);
    }
}
