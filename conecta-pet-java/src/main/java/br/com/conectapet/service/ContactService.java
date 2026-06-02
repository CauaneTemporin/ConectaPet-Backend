package br.com.conectapet.service;

import br.com.conectapet.config.TenantContext;
import br.com.conectapet.dto.ContactDTOs;
import br.com.conectapet.model.Contact;
import br.com.conectapet.repository.ContactRepository;
import br.com.conectapet.repository.OngRepository;
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
    private final OngRepository     ongRepository;

    @Transactional
    public ContactDTOs.ContactResponse send(ContactDTOs.ContactRequest req) {
        var builder = Contact.builder()
            .name(req.name().trim()).email(req.email().trim().toLowerCase())
            .phone(req.phone()).subject(req.subject()).message(req.message().trim());

        Long ongId = TenantContext.get();
        if (ongId != null) {
            ongRepository.findById(ongId).ifPresent(builder::ong);
        }

        log.info("Nova mensagem de: {}", req.email());
        return ContactDTOs.ContactResponse.from(contactRepository.save(builder.build()));
    }

    public List<ContactDTOs.ContactResponse> listAll() {
        Long ongId = TenantContext.get();
        List<Contact> contacts = ongId != null
            ? contactRepository.findByOngIdOrderByCreatedAtDesc(ongId)
            : contactRepository.findAllByOrderByCreatedAtDesc();
        return contacts.stream().map(ContactDTOs.ContactResponse::from).toList();
    }

    @Transactional
    public void markAsRead(Long id) {
        contactRepository.findById(id).ifPresent(c -> { c.setRead(true); contactRepository.save(c); });
    }
}
