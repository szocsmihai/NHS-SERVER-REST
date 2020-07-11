package ro.iteahome.nhs.backend.repository.nhs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.iteahome.nhs.backend.model.nhs.entity.Nurse;

import java.util.Optional;

@Repository
public interface NurseRepository extends JpaRepository<Nurse, Integer> {

    Optional<Nurse> findByEmail(String email);

    Optional<Nurse> findByCnp(String cnp);

    Nurse getByCnp(String cnp);

    void deleteById(int id);

    boolean existsByEmail(String email);

    boolean existsByCnp(String cnp);
}
