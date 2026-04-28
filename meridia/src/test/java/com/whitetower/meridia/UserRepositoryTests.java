package com.whitetower.meridia;

import com.whitetower.meridia.model.User;
import com.whitetower.meridia.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // cleans and migtates DB before each method and @Sql annotation
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Sql("/testData/addUserWith0SizeAvailable.sql")
    void findUsersWithNoSizeAvailable() {

        List<User> results = userRepository.findAllUsersWithSizeAvailable(0);
        assertEquals(1, results.size());
        assertEquals(0, results.getFirst().getSizeAvailable());
    }
}
