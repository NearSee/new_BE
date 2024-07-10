package org.example.near.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.near.common.dto.LoginResponse;
import org.example.near.exception.ErrorCode;
import org.example.near.oauth.service.KakaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private  final KakaoService kakaoService;

    @ResponseBody
    @GetMapping("/login/oauth/kakao")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam String code, HttpServletRequest request){
        try{
            return  ResponseEntity.ok(kakaoService.kakaoLogin(code));
        }catch (NoSuchElementException e){
            throw new ResponseStatusException(ErrorCode.VALIDATION_REQUEST_HEADER_MISSING_EXCEPTION.getHttpStatus());
        }
    }

}
