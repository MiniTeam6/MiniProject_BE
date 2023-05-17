package com.miniproject.pantry.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
public class MyRestDoc {
    protected MockMvc mockMvc;
    protected RestDocumentationResultHandler documentImage;
    protected RestDocumentationResultHandler document;

    @BeforeEach
    private void setup(WebApplicationContext webApplicationContext,
                       RestDocumentationContextProvider restDocumentation) {
        this.documentImage = MockMvcRestDocumentation.document("{class-name}/{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestParts(
                        partWithName("signupInDTO")
                                .description("회원가입 폼")
                                .attributes(key("Content-Type").value(MediaType.APPLICATION_JSON_VALUE))
                                .attributes(key("constraints").value("50자 이하"))
                                .attributes(key("example").value("{\"username\":\"abc\",\"password\":\"1234\",\"email\":\"abc@test.com\",\"phone\":\"010-1234-5678\"}"))
                                .optional(),
                        partWithName("image")
                                .description("프로필 이미지 (JPEG, PNG 파일만)")
                                .attributes(key("contentTypes").value(MediaType.IMAGE_JPEG_VALUE + ", " + MediaType.IMAGE_PNG_VALUE))
                                .attributes(key("fileExtension").value("jpg, png"))
                                .optional()
                )
        );
        this.document = MockMvcRestDocumentation.document("{class-name}/{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
        );


        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo(document)
                .build();
    }
}