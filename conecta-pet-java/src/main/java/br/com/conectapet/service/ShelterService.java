package br.com.conectapet.service;

import br.com.conectapet.dto.ShelterDTOs;
import br.com.conectapet.model.Shelter;
import br.com.conectapet.repository.ShelterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelterService {

    private final ShelterRepository shelterRepository;

    public List<ShelterDTOs.ShelterResponse> listAll() {
        return shelterRepository.findAllByOrderByNameAsc()
            .stream().map(ShelterDTOs.ShelterResponse::from).toList();
    }

    @Transactional
    public ShelterDTOs.ShelterResponse create(ShelterDTOs.ShelterRequest req) {
        var s = Shelter.builder().name(req.name().trim()).city(req.city())
            .address(req.address()).phone(req.phone()).email(req.email()).build();
        return ShelterDTOs.ShelterResponse.from(shelterRepository.save(s));
    }
}
