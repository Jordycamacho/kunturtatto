package com.example.kunturtatto.service;

import com.example.kunturtatto.model.User;

public interface IAuthenticationFacade {
    User getAuthenticatedUser();
}
