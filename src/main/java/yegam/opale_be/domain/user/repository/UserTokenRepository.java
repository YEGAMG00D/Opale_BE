package yegam.opale_be.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yegam.opale_be.domain.user.entity.UserToken;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {


}

