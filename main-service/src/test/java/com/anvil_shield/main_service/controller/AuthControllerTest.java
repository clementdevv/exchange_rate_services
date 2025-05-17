package com.anvil_shield.main_service.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.anvil_shield.main_service.io.AuthRequest;
import com.anvil_shield.main_service.io.AuthResponse;
import com.anvil_shield.main_service.io.ProfileRequest;
import com.anvil_shield.main_service.io.ProfileResponse;
import com.anvil_shield.main_service.service.AppUserDetailsService;
import com.anvil_shield.main_service.service.ProfileService;
import com.anvil_shield.main_service.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // This is to disable Spring security
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private ProfileService profileService;

    @Test
    public void testRegister_Success() throws Exception {
        ProfileRequest request = new ProfileRequest("test@example.com", "John", "Doe", "password");
        ProfileResponse response = new ProfileResponse(1L, "test@example.com", "John", "Doe");

        when(profileService.createProfile(any(ProfileRequest.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"password\":\"password\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void testLogin_Success() throws Exception {
        AuthRequest request = new AuthRequest("test@example.com", "password");
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(appUserDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("mocked-jwt-token");
        when(profileService.getProfile("test@example.com")).thenReturn(new ProfileResponse(1L, "test@example.com", "John", "Doe"));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("jwt"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void testLogin_Failure_BadCredentials() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"wrong@example.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true));
    }
}
