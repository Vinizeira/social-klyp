package com.github.devlucasjava.socialklyp.application.mapper;

import com.github.devlucasjava.socialklyp.application.dto.request.auth.RegisterDTO;
import com.github.devlucasjava.socialklyp.application.dto.response.user.UserDTO;
import com.github.devlucasjava.socialklyp.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);
    User toEntity(RegisterDTO registerDTO);
}
