package org.springframework.samples.petclinic;

import org.springframework.context.annotation.ComponentScan;
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
@ComponentScan(basePackageClasses = {
    OwnerMapper.class,
    PetMapper.class,
    PetTypeMapper.class,
    SpecialtyMapper.class,
    VetMapper.class,
    UserMapper.class,
    VisitMapper.class
})
public class MapStructTestConfiguration {
}

