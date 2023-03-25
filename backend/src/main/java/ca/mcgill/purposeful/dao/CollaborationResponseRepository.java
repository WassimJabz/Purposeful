package ca.mcgill.purposeful.dao;

import ca.mcgill.purposeful.model.CollaborationRequest;
import ca.mcgill.purposeful.model.CollaborationResponse;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

/** Repository for CollaborationResponse */
public interface CollaborationResponseRepository
    extends CrudRepository<CollaborationResponse, Integer> {

  CollaborationResponse findCollaborationResponseById(String id);
}