package com.codelab.users.services;

import com.codelab.users.dto.UserResponse;
import com.codelab.users.entities.User;
import com.codelab.users.repositories.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserResponse>getAllUsers(
            int page,
            int size,
            String sortBy,
            boolean ascending
    ){
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        var resultPage = userRepository.findAll(pageable);
        return new PageImpl<UserResponse>(
                resultPage.getContent().stream().map(User::toUserResponse).collect(Collectors.toList()),
                resultPage.getPageable(),
                resultPage.getTotalElements())
        ;
    }
}
