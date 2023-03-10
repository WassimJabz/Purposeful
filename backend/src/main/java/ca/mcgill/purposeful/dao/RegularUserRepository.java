package ca.mcgill.purposeful.dao;

import ca.mcgill.purposeful.model.RegularUser;
import org.springframework.data.repository.CrudRepository;

/** Repository for RegularUser */
public interface RegularUserRepository extends CrudRepository<RegularUser, Integer> {

  RegularUser findRegularUserById(String id);

  RegularUser findRegularUserByAppUserEmail(String email);

  RegularUser findRegularUserByAppUser_Id(String app_user_id);
}
