package org.springframework.samples.petclinic.owners.app.owner;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.common.EntityFinder;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.owners.infra.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile({"jdbc", "jpa", "spring-data-jpa"})
@Transactional(readOnly = true)
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerServiceImpl(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public Owner findById(int id) throws DataAccessException {
        return EntityFinder.findOrNull(() -> ownerRepository.findById(id));
    }

    @Override
    public Collection<Owner> findAll() throws DataAccessException {
        return ownerRepository.findAll();
    }

    @Override
    public Collection<Owner> findByLastName(String lastName) throws DataAccessException {
        return ownerRepository.findByLastName(lastName);
    }

    @Override
    @Transactional
    public void save(Owner owner) throws DataAccessException {
        ownerRepository.save(owner);
    }

    @Override
    @Transactional
    public void delete(Owner owner) throws DataAccessException {
        ownerRepository.delete(owner);
    }
}
