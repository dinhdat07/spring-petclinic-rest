package org.springframework.samples.petclinic.iam.infra;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.iam.domain.User;

public interface UserRepository {

    void save(User user) throws DataAccessException;
}
