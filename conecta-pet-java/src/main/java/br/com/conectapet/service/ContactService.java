package br.com.conectapet.service;

import br.com.conectapet.dto.ContactDTOs;
import br.com.conectapet.model.Contact;
import br.com.conectapet.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final ContactRepository contactRepository;

    @Transactional
    public ContactDTOs.ContactResponse send(ContactDTOs.ContactRequest req) {
        var c = Contact.builder()
            .name(req.name().trim()).email(req.email().trim().toLowerCase())
            .phone(req.phone()).subject(req.subject()).message(req.message().trim()).build();
        log.info("Nova mensagem de: {}", req.email());
        return ContactDTOs.ContactResponse.from(contactRepository.save(c));
    }

    public List<ContactDTOs.ContactResponse> listAll() {
        return contactRepository.findAllByOrderByCreatedAtDesc()
            .stream().map(ContactDTOs.ContactResponse::from).toList();
    }

    @Transactional
    public void markAsRead(Long id) {
        contactRepository.findById(id).ifPresent(c -> { c.setRead(true); contactRepository.save(c); });
    }
}
