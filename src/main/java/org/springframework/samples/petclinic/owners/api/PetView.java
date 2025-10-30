package org.springframework.samples.petclinic.owners.api;


public record PetView (Integer id, String name, Integer ownerId, Integer typeId) {
	
}
