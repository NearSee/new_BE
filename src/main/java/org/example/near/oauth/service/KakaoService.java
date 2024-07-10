package org.example.near.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.near.common.dto.LoginResponse;
import org.example.near.domain.User.User;
import org.example.near.domain.User.UserRepository;
import org.example.near.domain.User.UserRole;
import org.example.near.oauth.token.AuthTokens;
import org.example.near.oauth.token.AuthTokensGenerator;
import org.example.near.oauth.token.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoService{
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;
    private final AuthTokensGenerator authTokensGenerator;

    @Value("${KAKAO_CLIENT_ID}")
    private String clientID;

    @Value("${KAKAO_REDIRECT_URL}")
    private String redirectUrl;

    public LoginResponse kakaoLogin(String code){

        String accessToken = getAccessToken(code, redirectUrl);

        HashMap<String, Object> userInfo = getKakaoUserInfo(accessToken);

        LoginResponse kakaoUserResponse = kakaoUserLogin(userInfo);

        return  kakaoUserResponse;
    }


    // 1. 프론트에서 넘겨받은 인가코드 -> accesstoken
    public String getAccessToken(String code, String redirectUrl){
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientID);
        body.add("redirect_uri", redirectUrl);
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonNode.get("access_token").asText(); //토큰 전송
    }

    //2. accesstoken으로 카카오 API 호출
    private HashMap<String, Object> getKakaoUserInfo(String accessToken) {
        HashMap<String, Object> userInfo= new HashMap<String,Object>();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // responseBody에 있는 정보를 꺼냄
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("properties").get("nickname").asText();

        userInfo.put("id",id);
        userInfo.put("email",email);
        userInfo.put("nickname",nickname);

        return userInfo;
    }

    // 3. 카카오ID로 회원가입 & 로그인 처리
    public LoginResponse kakaoUserLogin(HashMap<String, Object> userInfo) {
        Long id = Long.valueOf(userInfo.get("id").toString());
        String kakaoEmail = userInfo.get("email").toString();
        String nickName = userInfo.get("nickname").toString();

        // 카카오 ID로 조회
        Optional<User> optionalKakaoUser = userRepository.findById(id);

        User kakaoUser;
        if (optionalKakaoUser.isPresent()) {
            kakaoUser = (User) optionalKakaoUser.get();
        } else {
            kakaoUser = new User();
            kakaoUser.setUserId(id);
            kakaoUser.setName(nickName);
            kakaoUser.setEmail(kakaoEmail);
            kakaoUser.setRole(UserRole.STUDENT); // 새로운 사용자의 기본 역할은 Student로 지정

            userRepository.save(kakaoUser);
        }

        // JWT 토큰 생성
        AuthTokens token = authTokensGenerator.generate(id.toString());

        return new LoginResponse(id, nickName, kakaoEmail, token);
    }

}
