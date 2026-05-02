package com.whitetower.meridia;

import com.whitetower.meridia.controller.UserController;
import com.whitetower.meridia.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class) // Only load the UserController
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void happyPath() throws Exception {

        String requestJson = Files.readString(Path.of("src/test/resources/requests/userController/happyPath.json"));
        String response = mockMvc.perform(post(UserController.API_USER_POST).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        Assertions.assertTrue(response.contains("id"));

        //TODO: Mock repository response
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

}
