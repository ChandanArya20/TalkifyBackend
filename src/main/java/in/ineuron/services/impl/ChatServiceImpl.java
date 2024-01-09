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

import java.util.List;
import java.util.Optional;

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
    public Chat findChatById(Long chatId) throws ChatNotFoundException {
        return chatRepo.findById(chatId).orElseThrow(
                () -> new ChatNotFoundException("Chat not found with id " + chatId)
        );
    }

    @Override
    public List<Chat> findAllChatsByUserId(Long userId) throws UserNotFoundException {
        User user = userService.fetchUserById(userId);
        return chatRepo.findChatByUsersContaining(user);
    }

    @Override
    public Chat createGroup(GroupChatRequest req, Long reqUserId) throws UserNotFoundException {
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
    public Chat addUserToGroup(Long chatId, Long reqUserId, Long userId) throws ChatNotFoundException, UserNotFoundException {
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
    public Chat renameGroup(Long chatId, String newGroupName, Long reqUserId) throws ChatNotFoundException, UserNotFoundException, UserNotAuthorizedException {
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
    public Chat removeUserFromGroup(Long chatId, Long userId, Long reqUserId) throws ChatNotFoundException, UserNotAuthorizedException, UserNotFoundException {

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
    public void deleteChat(Long chatId, Long userId) throws ChatNotFoundException, UserNotFoundException, UserNotAuthorizedException {

        Chat chat = findChatById(chatId);
        User user = userService.fetchUserById(userId);

        if (!chat.getUsers().contains(user)) {
            throw new UserNotFoundException("User not found in the chat");
        }

        if (chat.getIsGroup() && !chat.getAdmins().contains(user)) {
            throw new UserNotAuthorizedException("Only admins are allowed to delete the group chat");
        }
        chatRepo.delete(chat);
    }
}
