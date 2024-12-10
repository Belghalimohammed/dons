package com.isima.dons.services;

import com.isima.dons.entities.Message;
import com.isima.dons.entities.User;

import java.util.List;

public interface MessageService {

    List<Message> getAllMessages();

    List<Message> getConversation(Long sender, Long receiver);

    List<User> getConversationsByUserId(Long userId);

    Message getMessageById(Long id);

    Message createMessage(Message message);

    Message updateMessage(Long id, Message updatedMessage);

    void deleteMessage(Long id);
}
