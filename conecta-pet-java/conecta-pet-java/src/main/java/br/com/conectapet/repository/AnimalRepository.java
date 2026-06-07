package br.com.conectapet.repository;

import br.com.conectapet.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    List<Animal> findByStatus(Animal.AnimalStatus status);

    @Query("""
        SELECT a FROM Animal a
        WHERE (:species IS NULL OR a.species = :species)
          AND (:size    IS NULL OR a.size    = :size)
          AND (:status  IS NULL OR a.status  = :status)
          AND (:q       IS NULL
               OR LOWER(a.name)        LIKE LOWER(CONCAT('%',:q,'%'))
               OR LOWER(a.breed)       LIKE LOWER(CONCAT('%',:q,'%'))
               OR LOWER(a.description) LIKE LOWER(CONCAT('%',:q,'%')))
        ORDER BY a.createdAt DESC
    """)
    List<Animal> search(
        @Param("species") Animal.Species species,
        @Param("size")    Animal.Size size,
        @Param("status")  Animal.AnimalStatus status,
        @Param("q")       String q
    );

    long countByStatus(Animal.AnimalStatus status);

    @Query("""
        SELECT a FROM Animal a
        WHERE a.ong.id = :ongId
          AND (:species IS NULL OR a.species = :species)
          AND (:size    IS NULL OR a.size    = :size)
          AND (:status  IS NULL OR a.status  = :status)
          AND (:q       IS NULL
               OR LOWER(a.name)        LIKE LOWER(CONCAT('%',:q,'%'))
               OR LOWER(a.breed)       LIKE LOWER(CONCAT('%',:q,'%'))
               OR LOWER(a.description) LIKE LOWER(CONCAT('%',:q,'%')))
        ORDER BY a.createdAt DESC
    """)
    List<Animal> searchByOng(
        @Param("ongId")   Long ongId,
        @Param("species") Animal.Species species,
        @Param("size")    Animal.Size size,
        @Param("status")  Animal.AnimalStatus status,
        @Param("q")       String q
    );

    long countByOngId(Long ongId);
    long countByOngIdAndStatus(Long ongId, Animal.AnimalStatus status);
}
