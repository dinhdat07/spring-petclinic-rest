package org.springframework.samples.petclinic;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.catalog.mapper.PetTypeMapper;
import org.springframework.samples.petclinic.catalog.mapper.SpecialtyMapper;
import org.springframework.samples.petclinic.iam.mapper.UserMapper;
import org.springframework.samples.petclinic.owners.mapper.OwnerMapper;
import org.springframework.samples.petclinic.owners.mapper.PetMapper;
import org.springframework.samples.petclinic.vets.mapper.VetMapper;
import org.springframework.samples.petclinic.visits.mapper.VisitMapper;

/**
 * Registers MapStruct-generated mapper implementations for slice tests.
 */
@Configuration
public class MapStructTestConfiguration {

    @Bean
    PetMapper petMapper() {
        return Mappers.getMapper(PetMapper.class);
    }

    @Bean
    OwnerMapper ownerMapper() {
        return Mappers.getMapper(OwnerMapper.class);
    }

    @Bean
    PetTypeMapper petTypeMapper() {
        return Mappers.getMapper(PetTypeMapper.class);
    }

    @Bean
    SpecialtyMapper specialtyMapper() {
        return Mappers.getMapper(SpecialtyMapper.class);
    }

    @Bean
    VetMapper vetMapper() {
        return Mappers.getMapper(VetMapper.class);
    }

    @Bean
    UserMapper userMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

    @Bean
    VisitMapper visitMapper() {
        return Mappers.getMapper(VisitMapper.class);
    }
}
