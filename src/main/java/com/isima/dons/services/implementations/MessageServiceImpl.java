package com.isima.dons.services.implementations;

import com.isima.dons.entities.Message;
import com.isima.dons.entities.User;
import com.isima.dons.repositories.MessageRepository;
import com.isima.dons.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> getConversation(Long sender, Long receiver) {
        List<Message> messages = messageRepository.findMessagesBetweenUsers(sender, receiver);

        return messages;
    }

    @Override
    public List<User> getConversationsByUserId(Long userId) {
        // Récupérer tous les messages où l'utilisateur est soit l'expéditeur soit le
        // destinataire
        return messageRepository.findAllUsersInvolvedWithSender(userId);
    }

    @Override
    public Message getMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
    }

    @Override
    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public Message updateMessage(Long id, Message updatedMessage) {
        Optional<Message> messageOptional = messageRepository.findById(id);

        if (messageOptional.isPresent()) {
            Message existingMessage = messageOptional.get();
            existingMessage.setMessage(updatedMessage.getMessage());
            existingMessage.setSender(updatedMessage.getSender());
            existingMessage.setReciever(updatedMessage.getReciever());
            return messageRepository.save(existingMessage);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
    }

    @Override
    public void deleteMessage(Long id) {
        if (messageRepository.existsById(id)) {
            messageRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
    }
}
