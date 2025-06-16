package com.budgetbuddy.personal_finance_tracker.mapper;

import com.budgetbuddy.personal_finance_tracker.dto.LoginResponse;
import com.budgetbuddy.personal_finance_tracker.dto.UserResponse;
import com.budgetbuddy.personal_finance_tracker.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginMapper {

    private final UserMapper userMapper;

    public LoginResponse toLoginResponse(String accessToken, String tokenType, User user) {
        UserResponse userResponse = userMapper.toResponse(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType(tokenType)
                .user(userResponse)
                .build();
    }
}
