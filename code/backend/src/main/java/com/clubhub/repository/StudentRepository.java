import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Customer, Long> {
    // Specific 
    Student findById(Long id); // inherited from CrudRepository
    Student findByEmail(String email);
    // Many results
    List<Student> findByLastName(String lastName);
}
