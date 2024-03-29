package com.rozsa.demoapp.resources.mapper;

import com.rozsa.demoapp.domain.Owner;
import com.rozsa.demoapp.resources.dto.OwnerRequest;
import com.rozsa.demoapp.resources.dto.OwnerResponse;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface OwnerMapper {

    Owner from(OwnerRequest request);

    OwnerResponse toResponse(Owner owner);
}
