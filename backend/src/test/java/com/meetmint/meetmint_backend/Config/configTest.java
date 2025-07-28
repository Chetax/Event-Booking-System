
package com.meetmint.meetmint_backend.Config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class configTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void shouldAllowAccessToLoginEndpoint() throws Exception {
            mockMvc.perform(post("/api/users/login"))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldBlockAccessToProtectedEndpoint() throws Exception {
            mockMvc.perform(get("/api/secure-data"))
                    .andExpect(status().isUnauthorized());
        }

}
