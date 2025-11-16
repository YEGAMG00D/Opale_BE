package yegam.opale_be.domain.preference.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.preference.entity.UserPreferenceVector;

@Repository
public interface UserPreferenceVectorRepository extends JpaRepository<UserPreferenceVector, Long> {

}
