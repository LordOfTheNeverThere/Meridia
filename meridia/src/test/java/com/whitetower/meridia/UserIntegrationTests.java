package com.whitetower.meridia;
import com.whitetower.meridia.controller.UserController;

import com.whitetower.meridia.dto.UserRegistrationDTO;
import com.whitetower.meridia.model.User;
import com.whitetower.meridia.repository.UserRepository;
import com.whitetower.meridia.util.Security;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

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

    @Autowired
    private Security security;

    @Autowired
    private Flyway flyway;

    @AfterEach
    void cleanDatabase(){
        flyway.clean();
        flyway.migrate();
    }

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

    @Test
    void signIn() throws Exception {
        //Create User
        String createRequest = Files.readString(Path.of("src/test/resources/requests/userController/createUser.json"));
        mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(createRequest)).andExpect(status().isCreated());

        // Login
        String requestJson = Files.readString(Path.of("src/test/resources/requests/userController/userLogin.json"));
        String header = mockMvc.perform(post(UserController.API_SIGN_IN).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk()).andReturn().getResponse().getHeader(HttpHeaders.SET_COOKIE);
        Assertions.assertTrue(null != header);
        Assertions.assertTrue(header.contains("jwt-token="));
        Assertions.assertTrue(header.contains("Path=/"));
        Assertions.assertTrue(header.contains("Max-Age=1800"));
        Assertions.assertTrue(header.contains("Expires="));
        Assertions.assertTrue(header.contains("Secure"));
        Assertions.assertTrue(header.contains("HttpOnly"));
        Assertions.assertTrue(header.contains("SameSite=Strict"));

        String[] fieldsInHeader = header.split(";");
        String token = fieldsInHeader[0].split("=")[1];
        Assertions.assertEquals(2, token.chars().filter(c -> (char) c == '.').count());

        Assertions.assertTrue(security.validateJwtToken(token));
    }


    @Test
    void userLoginTokenWasTampered() throws Exception {
        //Create User
        String createRequest = Files.readString(Path.of("src/test/resources/requests/userController/createUser.json"));
        mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(createRequest)).andExpect(status().isCreated());

        // Login
        String requestJson = Files.readString(Path.of("src/test/resources/requests/userController/userLogin.json"));
        String header = mockMvc.perform(post(UserController.API_SIGN_IN).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk()).andReturn().getResponse().getHeader(HttpHeaders.SET_COOKIE);
        Assertions.assertTrue(null != header);
        Assertions.assertTrue(header.contains("jwt-token="));

        String[] fieldsInHeader = header.split(";");
        String token = fieldsInHeader[0].split("=")[1];
        Assertions.assertEquals(2, token.chars().filter(c -> (char) c == '.').count());
        token = token.replace(".ey", ".ye");
        Assertions.assertFalse(security.validateJwtToken(token));
    }

    @Test
    void userLoginIncorrectPassword() throws Exception {
        //Create User
        String createRequest = Files.readString(Path.of("src/test/resources/requests/userController/createUser.json"));
        mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(createRequest)).andExpect(status().isCreated());

        // Login
        String requestJson = Files.readString(Path.of("src/test/resources/requests/userController/userLoginIncorrectPassword.json"));
        String response = mockMvc.perform(post(UserController.API_SIGN_IN).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();

        String expectedResponse = Files.readString(Path.of("src/test/resources/responses/userController/entityNotFound.json"));
        JSONAssert.assertEquals(expectedResponse, response, JSONCompareMode.STRICT);
    }


    @Test
    void userLoginIncorrectEmail() throws Exception {
        //Create User
        String createRequest = Files.readString(Path.of("src/test/resources/requests/userController/createUser.json"));
        mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(createRequest)).andExpect(status().isCreated());

        // Login
        String requestJson = Files.readString(Path.of("src/test/resources/requests/userController/userLoginIncorrectEmail.json"));
        String response = mockMvc.perform(post(UserController.API_SIGN_IN).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();

        String expectedResponse = Files.readString(Path.of("src/test/resources/responses/userController/entityNotFound.json"));
        JSONAssert.assertEquals(expectedResponse, response, JSONCompareMode.STRICT);
    }
}
