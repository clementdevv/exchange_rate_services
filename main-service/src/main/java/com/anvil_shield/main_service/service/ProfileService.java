package com.anvil_shield.main_service.service;

import com.anvil_shield.main_service.io.ProfileRequest;
import com.anvil_shield.main_service.io.ProfileResponse;

public interface ProfileService {
    ProfileResponse createProfile(ProfileRequest request);

    ProfileResponse getProfile(String email);
}
