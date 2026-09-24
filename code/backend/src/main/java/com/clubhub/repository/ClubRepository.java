import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ClubRepository extends CrudRepository<Club, Long> {
    // return a specific club
    Club findByID(Long clubID);
    // return all clubs matching major
}
