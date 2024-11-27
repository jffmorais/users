package com.codelab.users.services;

import com.codelab.users.dto.UserResponse;
import com.codelab.users.entities.User;
import com.codelab.users.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PagedModel<UserResponse>getAllUsers(
            int page,
            int size,
            String sortBy,
            boolean ascending
    ){
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> resultPage = userRepository.findAll(pageable);
        return PagedModel.of(
                resultPage.getContent().stream().map(User::toUserResponse).collect(Collectors.toList()),
                new PagedModel.PageMetadata(
                        resultPage.getSize(),
                        resultPage.getNumber(),
                        resultPage.getTotalElements()
                )
        );
    }

    public UserResponse getUser(String id){
        User userRecovered = userRepository.findByUserId(UUID.fromString(id)).orElseThrow(()->
            new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        UUID uuid = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!userRecovered.getUserId().equals(uuid) && !userRecovered.getManager().getUserId().equals(uuid)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return userRecovered.toUserResponse();
    }

}
