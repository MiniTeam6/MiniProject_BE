package com.miniproject.pantry.restend.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.miniproject.pantry.restend.model.event.EventType;
import com.miniproject.pantry.restend.model.order.OrderState;
import com.miniproject.pantry.restend.model.user.UserRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.EnumSet;

@RestController
@RequestMapping("/test")
public class RestDocumentApi {

    private final ObjectMapper mapper;

    public RestDocumentApi(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    @PostMapping("/sample")
    public void sample(@RequestBody @Valid SampleRequest dto) {

    }

    /**
     * 1. Test Controller(RestDocs 문서용)에 Member Status Enum API를 생성
     * 2. Test Controller에대한 테스트 코드 작성 -> Member Status Enum snippet 파일생성
     * 3. Member Status Enum snippet 기반으로 Document 작성
     */

    @GetMapping("/userRole")
    public ArrayNode getUserRole() {
        final ArrayNode arrayNode = mapper.createArrayNode();
        final EnumSet<UserRole> types = EnumSet.allOf(UserRole.class);

        for (final UserRole type : types) {
            final ObjectNode node = mapper.createObjectNode();
            node.put("MemberStatus", type.name());
            node.put("description", type.getDescription());
            arrayNode.add(node);
        }

        return arrayNode;
    }
    @GetMapping("/orderState")
    public ArrayNode getOrderState() {
        final ArrayNode arrayNode = mapper.createArrayNode();
        final EnumSet<OrderState> types = EnumSet.allOf(OrderState.class);

        for (final OrderState type : types) {
            final ObjectNode node = mapper.createObjectNode();
            node.put("OrderState", type.name());
            node.put("description", type.getState());
            arrayNode.add(node);
        }

        return arrayNode;
    }
    @GetMapping("/eventType")
    public ArrayNode getEventType() {
        final ArrayNode arrayNode = mapper.createArrayNode();
        final EnumSet<EventType> types = EnumSet.allOf(EventType.class);

        for (final EventType type : types) {
            final ObjectNode node = mapper.createObjectNode();
            node.put("OrderState", type.name());
            node.put("description", type.getType());
            arrayNode.add(node);
        }

        return arrayNode;
    }

    public static class SampleRequest {

        @NotEmpty
        private String name;

        @Email
        private String email;

        public SampleRequest(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }
}
