package com.abik.nowme.module.nowme.service;

import com.abik.nowme.module.nowme.dto.CreateNowmeDto;
import com.abik.nowme.module.nowme.entity.Nowme;
import com.abik.nowme.module.nowme.repository.NowmeRepository;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NowmeService {

    private final NowmeRepository nowmeRepository;
    private final UserRepository userRepository;

    public Long createNowme(Long userId, CreateNowmeDto createNowmeDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        Nowme nowme = new Nowme();

        nowme.setUser(user);
        nowme.setImage(createNowmeDto.getImage());
        nowme.setDescription(createNowmeDto.getDescription());

        nowmeRepository.save(nowme);

        return nowme.getId();
    }
}