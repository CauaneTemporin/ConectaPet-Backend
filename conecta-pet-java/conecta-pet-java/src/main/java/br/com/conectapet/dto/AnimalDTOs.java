package br.com.conectapet.dto;

import br.com.conectapet.model.Animal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AnimalDTOs {

    public record AnimalRequest(
        @NotBlank(message = "Nome é obrigatório") String name,
        @NotNull(message = "Espécie é obrigatória") Animal.Species species,
        String breed, Animal.Gender gender, Double ageYears, Animal.Size size,
        String description, String temperament,
        Boolean vaccinated, Boolean neutered,
        Animal.AnimalStatus status, String photoUrl, Long shelterId
    ) {}

    public record AnimalUpdateRequest(
        String name, Animal.Species species, String breed, Animal.Gender gender,
        Double ageYears, Animal.Size size, String description, String temperament,
        Boolean vaccinated, Boolean neutered, Animal.AnimalStatus status, String photoUrl
    ) {}

    public record AnimalResponse(
        Long id, String name, String species, String breed, String gender,
        Double ageYears, String size, String description, String temperament,
        Boolean vaccinated, Boolean neutered, String status, String photoUrl,
        String shelterName, LocalDateTime createdAt
    ) {
        public static AnimalResponse from(Animal a) {
            return new AnimalResponse(
                a.getId(), a.getName(),
                a.getSpecies() != null ? a.getSpecies().name().toLowerCase() : null,
                a.getBreed(),
                a.getGender()  != null ? a.getGender().name().toLowerCase()  : null,
                a.getAgeYears(),
                a.getSize()    != null ? a.getSize().name().toLowerCase()     : null,
                a.getDescription(), a.getTemperament(),
                a.getVaccinated(), a.getNeutered(),
                a.getStatus()  != null ? a.getStatus().name().toLowerCase()   : null,
                a.getPhotoUrl(),
                a.getShelter() != null ? a.getShelter().getName()            : null,
                a.getCreatedAt()
            );
        }
    }
}
