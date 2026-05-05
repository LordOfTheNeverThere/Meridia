package com.whitetower.meridia;
import com.whitetower.meridia.controller.UserController;

import com.whitetower.meridia.dto.UserRegistrationDTO;
import com.whitetower.meridia.model.User;
import com.whitetower.meridia.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.Mockito.mockConstruction;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository db;

    @Test
    void createUser() throws Exception {

        String requestJson = Files.readString(Path.of("src/test/resources/requests/userController/createUser.json"));
        String response = mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String expectedResponse = Files.readString(Path.of("src/test/resources/responses/userController/createUser.json"));

        JSONAssert.assertEquals(expectedResponse, response, JSONCompareMode.STRICT);

        List<User> allUsers =  db.findAll();
        Assertions.assertEquals(1, allUsers.size());
    }

    @Test
    void creationOfDuplicateUser() throws Exception {

        String requestJson = Files.readString(Path.of("src/test/resources/requests/userController/createUser.json"));
        String response = mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String expectedResponse = Files.readString(Path.of("src/test/resources/responses/userController/createUser.json"));

        JSONAssert.assertEquals(expectedResponse, response, JSONCompareMode.STRICT);

        response = mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

        expectedResponse = Files.readString(Path.of("src/test/resources/responses/userController/entityAlreadyExists.json"));
        JSONAssert.assertEquals(expectedResponse, response, JSONCompareMode.STRICT);
    }

    @Test
    void creationOfDuplicateUserWithDBConstraintViolation() throws Exception {

        try (MockedConstruction<User> mocked = mockConstruction(User.class, (mock, context) -> {
        })) {
            String requestJson = Files.readString(Path.of("src/test/resources/requests/userController/createUser.json"));
            String response = mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                    .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
            String expectedResponse = Files.readString(Path.of("src/test/resources/responses/userController/invalidEntity.json"));

            JSONAssert.assertEquals(expectedResponse, response, JSONCompareMode.STRICT);
        }
    }

}
