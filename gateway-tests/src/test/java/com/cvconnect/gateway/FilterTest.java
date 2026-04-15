package com.cvconnect.gateway;

import com.cvconnect.config.Filter;
import com.cvconnect.dto.Response;
import com.cvconnect.dto.VerifyResponse;
import com.cvconnect.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * ============================================================
 * FILE: FilterTest.java
 * MODULE: api-gateway
 * PURPOSE: Unit test cho Global Security Filter
 * 
 * BAO PHỦ CÁC LUỒNG CẤP 2 (Branch Coverage):
 *   - Path public (swagger, auth, v.v.) -> bypass filter.
 *   - Path private, thiếu Authorization header -> 401.
 *   - Path private, Authorization hợp lệ -> proceed.
 *   - Path private, token sai -> unauthenticated trả về lỗi tương ứng.
 *   - AuthService lỗi -> 500.
 * ============================================================
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Filter - Unit Tests (C2 Branch Coverage)")
class FilterTest {

    @Mock
    private AuthService authService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private HttpHeaders headers;

    @InjectMocks
    private Filter filter;

    @BeforeEach
    void setUp() {
        lenient().when(exchange.getRequest()).thenReturn(request);
        lenient().when(exchange.getResponse()).thenReturn(response);
        lenient().when(request.getHeaders()).thenReturn(headers);
        lenient().when(response.getHeaders()).thenReturn(new HttpHeaders());
        lenient().when(response.bufferFactory()).thenReturn(new DefaultDataBufferFactory());
    }

    @Test
    @DisplayName("TC-GW-001: Bypass file khi truy cập public endpoint (swagger)")
    void TC_GW_001_publicEndpointBypass() {
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/v3/api-docs"));
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
        verify(chain).filter(exchange);
        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("TC-GW-002: Lỗi 401 khi truy cập private endpoint mà không có Header")
    void TC_GW_002_missingAuthHeader() throws JsonProcessingException {
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/core/job-ad/manage"));
        when(headers.get(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        
        // Mock unauthenticated behavior
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(response.writeWith(any())).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("TC-GW-003: Token hợp lệ -> Chế độ bình thường")
    void TC_GW_003_validTokenSuccess() {
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/core/job-ad/manage"));
        when(headers.get(HttpHeaders.AUTHORIZATION)).thenReturn(List.of("Bearer valid-token"));
        
        VerifyResponse verifyData = new VerifyResponse();
        verifyData.setIsValid(true);
        Response<VerifyResponse> authResponse = Response.<VerifyResponse>builder().data(verifyData).build();
        
        when(authService.verify("valid-token")).thenReturn(Mono.just(authResponse));
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
        verify(chain).filter(exchange);
    }

    @Test
    @DisplayName("TC-GW-004: Token không hợp lệ -> Trả về lỗi từ VerifyResponse")
    void TC_GW_004_invalidToken() throws JsonProcessingException {
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/core/job-ad/manage"));
        when(headers.get(HttpHeaders.AUTHORIZATION)).thenReturn(List.of("Bearer invalid-token"));
        
        VerifyResponse verifyData = new VerifyResponse();
        verifyData.setIsValid(false);
        verifyData.setMessage("Token expired");
        verifyData.setStatus(HttpStatus.FORBIDDEN);
        verifyData.setCode(403);
        Response<VerifyResponse> authResponse = Response.<VerifyResponse>builder().data(verifyData).build();
        
        when(authService.verify("invalid-token")).thenReturn(Mono.just(authResponse));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(response.writeWith(any())).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("TC-GW-005: AuthService ném lỗi -> Trả về 500")
    void TC_GW_005_authServiceError() throws JsonProcessingException {
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/core/job-ad/manage"));
        when(headers.get(HttpHeaders.AUTHORIZATION)).thenReturn(List.of("Bearer token"));
        
        when(authService.verify(anyString())).thenReturn(Mono.error(new RuntimeException("Connection lost")));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(response.writeWith(any())).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
        verify(response).setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
