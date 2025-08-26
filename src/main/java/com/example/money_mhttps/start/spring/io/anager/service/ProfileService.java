package com.example.money_mhttps.start.spring.io.anager.service;

import com.example.money_mhttps.start.spring.io.anager.dto.AuthDTO;
import com.example.money_mhttps.start.spring.io.anager.dto.ProfileDTO;
import com.example.money_mhttps.start.spring.io.anager.entity.ProfileEntity;
import com.example.money_mhttps.start.spring.io.anager.repositoty.ProfileRepository;
import com.example.money_mhttps.start.spring.io.anager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ProfileRepository profileRepository;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Value("${app.activation.url}")
    private String activationURL;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        // Validate input
        if (profileDTO.getPassword() == null || profileDTO.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        if (profileRepository.findByEmail(profileDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists. Please use a different one.");
        }


        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile.setIsActive(false); // new accounts inactive until activated
        newProfile = profileRepository.save(newProfile);

        // Send activation email (safe)
        try {
            String activationLink = activationURL + "/activate?token=" + newProfile.getActivationToken();
            String subject = "Activate your Money Manager account";
            String body = "Click the link to activate your account: " + activationLink;
            emailService.sendEmail(newProfile.getEmail(), subject, body);
        } catch (Exception e) {
            System.err.println("Failed to send activation email: " + e.getMessage());
        }

        return toDTO(newProfile);
    }


    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String activateToken) {
        return profileRepository.findByActivationToken(activateToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }

    public boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + authentication.getName()));
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentUser = null;
        if (email == null) {
            currentUser = getCurrentProfile();
        } else {
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
        }

        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword())
            );
            //String token=jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                    "token", jwtUtil.generateToken(authDTO.getEmail()),
                    "user", getPublicProfile(authDTO.getEmail())
            );

        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password", e);
        }
    }
}



