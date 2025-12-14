package com.securetaskapp.services

import com.securetaskapp.dto.AuthResponse
import com.securetaskapp.dto.LoginRequest
import com.securetaskapp.dto.RegisterRequest
import com.securetaskapp.dto.UserResponse
import com.securetaskapp.model.User
import com.securetaskapp.repository.UserRepository
import com.securetaskapp.security.JwtTokenProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@ExtendWith(MockitoExtension::class)
class AuthServiceImplTest {

    @Mock lateinit var userRepository: UserRepository
    @Mock lateinit var passwordEncoder: PasswordEncoder
    @Mock lateinit var jwtTokenProvider: JwtTokenProvider

    private lateinit var authService: AuthServiceImpl

    @BeforeEach
    fun setUp() {
        authService = AuthServiceImpl(userRepository, passwordEncoder, jwtTokenProvider)
    }

    @Test
    fun `register with new email should save user and return UserResponse`() {
        val request = RegisterRequest().apply {
            email = "test@example.com"
            password = "password123"
            name = "Test User"
        }

        given(userRepository.existsByEmail(request.email)).willReturn(false)
        given(passwordEncoder.encode(request.password)).willReturn("encoded-pass")

        // User is a Java class with setters only (id has NO setter)
        val savedUser = User().apply {
            email = request.email
            name = request.name
            passwordHash = "encoded-pass"
            roles = "ROLE_USER"
        }

        given(userRepository.save(any(User::class.java))).willReturn(savedUser)

        val response: UserResponse = authService.register(request)

        assertEquals(savedUser.email, response.email)
        assertEquals(savedUser.name, response.name)
        assertEquals(savedUser.roles, response.roles)

        // BaseEntity.id has no setter → is ALWAYS null in unit tests
        assertNull(response.id)

        verify(userRepository).existsByEmail(request.email)
        verify(userRepository).save(any(User::class.java))
    }

    @Test
    fun `register with existing email should throw IllegalArgumentException`() {
        val request = RegisterRequest().apply {
            email = "taken@example.com"
            password = "password123"
            name = "Test User"
        }

        given(userRepository.existsByEmail(request.email)).willReturn(true)

        assertThrows<IllegalArgumentException> {
            authService.register(request)
        }
    }

    @Test
    fun `login with correct credentials should return AuthResponse with token`() {
        val user = User().apply {
            email = "test@example.com"
            name = "Test User"
            passwordHash = "encoded-pass"
            roles = "ROLE_USER"
        }

        val request = LoginRequest().apply {
            email = user.email
            password = "raw-pass"
        }

        given(userRepository.findByEmail(request.email)).willReturn(Optional.of(user))
        given(passwordEncoder.matches("raw-pass", "encoded-pass")).willReturn(true)
        given(jwtTokenProvider.generateToken(null, "ROLE_USER"))
            .willReturn("fake-jwt-token")

        val response: AuthResponse = authService.login(request)

        assertEquals("fake-jwt-token", response.token)
        assertNotNull(response.expiresAt)

        verify(jwtTokenProvider).generateToken(null, "ROLE_USER")
    }

    @Test
    fun `login with wrong password should throw BadCredentialsException`() {
        val user = User().apply {
            email = "test@example.com"
            name = "Test User"
            passwordHash = "encoded-pass"
            roles = "ROLE_USER"
        }

        val request = LoginRequest().apply {
            email = user.email
            password = "wrong-pass"
        }

        given(userRepository.findByEmail(request.email)).willReturn(Optional.of(user))
        given(passwordEncoder.matches("wrong-pass", "encoded-pass")).willReturn(false)

        assertThrows<BadCredentialsException> {
            authService.login(request)
        }
    }
}
