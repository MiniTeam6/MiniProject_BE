package com.miniproject.pantry.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@DisplayName("Enum API")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class RestDocument extends MyRestDoc {

    @Test
    public void userRole() throws Exception {
        mockMvc.perform(
                get("/test/userRole")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
        ;
    }
    @Test
    public void orderState() throws Exception {
        mockMvc.perform(
                        get("/test/orderState")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
        ;
    }
    @Test
    public void eventType() throws Exception {
        mockMvc.perform(
                        get("/test/eventType")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
        ;
    }
}
