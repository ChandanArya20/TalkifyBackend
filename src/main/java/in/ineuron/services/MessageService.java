package in.ineuron.services;

import in.ineuron.dto.MessageRequest;
import in.ineuron.models.Message;
import org.apache.catalina.LifecycleState;

import java.util.List;

public interface MessageService {

    public Message sendMessage(MessageRequest messReq);
    public List<Message> getChatsMessage(Long chatId, Long reqUserId);
    public Message findMessageById(Long messageId);
    public void deleteMessage(Long messageId, Long reqUserId);

}
