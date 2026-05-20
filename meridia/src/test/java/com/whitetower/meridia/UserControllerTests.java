package com.whitetower.meridia;

import com.whitetower.meridia.controller.UserController;
import com.whitetower.meridia.dto.UserDTO;
import com.whitetower.meridia.enumeration.ServiceResponseType;
import com.whitetower.meridia.model.User;
import com.whitetower.meridia.service.ServiceResponse;
import com.whitetower.meridia.service.UserService;
import com.whitetower.meridia.util.Security;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.whitetower.meridia.controller.UserController.wwwAuthenticateHeader;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class) // Only load the UserController
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private Security security;

    @Test
    void createUser() throws Exception {
        // Mock Response

        Mockito.when(userService.createUser(Mockito.isA(User.class)))
                .thenReturn(new ServiceResponse<>(ServiceResponseType.OK, new UserDTO("miguel_dev", "miguel@example.com", 20)));

        String requestJson = Files.readString(Path.of("src/test/resources/requests/userController/createUser.json"));
        String response = mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String expectedResponse = Files.readString(Path.of("src/test/resources/responses/userController/createUser.json"));

        JSONAssert.assertEquals(expectedResponse, response, JSONCompareMode.STRICT);

    }

    @Test
    void missingName() throws Exception {

        String requestJson = Files.readString(Path.of("src/test/resources/requests/userController/missingName.json"));
        String response = mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

        String expectedResponse = Files.readString(Path.of("src/test/resources/responses/userController/missingName.json"));

        JSONAssert.assertEquals(expectedResponse, response, JSONCompareMode.STRICT);
    }

    @Test
    void badFormatEmail1() throws Exception {

        String requestJson = Files.readString(Path.of("src/test/resources/requests/userController/badFormatEmail1.json"));
        String response = mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
        String expectedResponse = Files.readString(Path.of("src/test/resources/responses/userController/badFormatEmail.json"));

        JSONAssert.assertEquals(expectedResponse, response, JSONCompareMode.STRICT);
    }

    @Test
    void badFormatEmail2() throws Exception {

        String requestJson = Files.readString(Path.of("src/test/resources/requests/userController/badFormatEmail1.json"));
        String response = mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
        String expectedResponse = Files.readString(Path.of("src/test/resources/responses/userController/badFormatEmail.json"));

        JSONAssert.assertEquals(expectedResponse, response, JSONCompareMode.STRICT);
    }


    @Test
    void missingPassword() throws Exception {

        String requestJson = Files.readString(Path.of("src/test/resources/requests/userController/missingPassword.json"));
        String response = mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

        String expectedResponse = Files.readString(Path.of("src/test/resources/responses/userController/badFormatPassword.json"));

        JSONAssert.assertEquals(expectedResponse, response, JSONCompareMode.STRICT);
    }

    @Test
    void badFormatPassword1() throws Exception {

        String requestJson = Files.readString(Path.of("src/test/resources/requests/userController/badFormatPassword1.json"));
        String response = mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

        String expectedResponse = Files.readString(Path.of("src/test/resources/responses/userController/badFormatPassword.json"));

        JSONAssert.assertEquals(expectedResponse, response, JSONCompareMode.STRICT);
    }


    @Test
    void badFormatPassword2() throws Exception {

        String requestJson =Files.readString(Path.of("src/test/resources/requests/userController/badFormatPassword2.json"));
        String response = mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

        String expectedResponse = Files.readString(Path.of("src/test/resources/responses/userController/badFormatPassword.json"));

        JSONAssert.assertEquals(expectedResponse, response, JSONCompareMode.STRICT);
    }

    @Test
    void badFormatPassword3() throws Exception {

        String requestJson = Files.readString(Path.of("src/test/resources/requests/userController/badFormatPassword3.json"));
        String response = mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
        String expectedResponse = Files.readString(Path.of("src/test/resources/responses/userController/badFormatPassword.json"));

        JSONAssert.assertEquals(expectedResponse, response, JSONCompareMode.STRICT);
    }

    @Test
    void incorrectJWETokenPassed() throws Exception {


        MvcResult mvcResult = mockMvc.perform(delete(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();

        Assertions.assertEquals("Required cookie 'jwt-token' is not present.", mvcResult.getResponse().getErrorMessage());
        Assertions.assertEquals("", mvcResult.getResponse().getContentAsString());

        mvcResult = mockMvc.perform(delete(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).cookie(new Cookie("incorrect-token-name", "")))
                .andExpect(status().isBadRequest()).andReturn();

        Assertions.assertEquals("Required cookie 'jwt-token' is not present.", mvcResult.getResponse().getErrorMessage());
        Assertions.assertEquals("", mvcResult.getResponse().getContentAsString());

        mvcResult = mockMvc.perform(delete(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).cookie(new Cookie("jwt-token", "")))
                .andExpect(status().isUnauthorized()).andReturn();

        Assertions.assertEquals(wwwAuthenticateHeader().get(HttpHeaders.WWW_AUTHENTICATE), mvcResult.getResponse().getHeaders(HttpHeaders.WWW_AUTHENTICATE));
        Assertions.assertEquals("", mvcResult.getResponse().getContentAsString());

        mvcResult = mockMvc.perform(delete(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).cookie(new Cookie("jwt-token", "I am a hacker please arrest me")))
                .andExpect(status().isForbidden()).andReturn();
        
        Assertions.assertEquals(wwwAuthenticateHeader().get(HttpHeaders.WWW_AUTHENTICATE), mvcResult.getResponse().getHeaders(HttpHeaders.WWW_AUTHENTICATE));
        Assertions.assertEquals("", mvcResult.getResponse().getContentAsString());


    }

}
