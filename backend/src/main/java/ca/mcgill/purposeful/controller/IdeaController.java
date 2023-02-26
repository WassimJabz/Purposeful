package ca.mcgill.purposeful.controller;

import ca.mcgill.purposeful.dto.IdeaDTO;
import ca.mcgill.purposeful.dto.IdeaRequestDTO;
import ca.mcgill.purposeful.dto.SearchFilterDTO;
import ca.mcgill.purposeful.exception.GlobalException;
import ca.mcgill.purposeful.model.Idea;
import ca.mcgill.purposeful.model.RegularUser;
import ca.mcgill.purposeful.service.IdeaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API for demonstrating how permissions work for access to endpoints
 */
@RestController
@RequestMapping({"api/idea", "api/idea/"})
public class IdeaController {

  @Autowired
  IdeaService ideaService;

  @GetMapping("{id}")
  @PreAuthorize("hasAnyAuthority('User', 'Moderator', 'Owner')")
  public ResponseEntity<IdeaDTO> getIdeaById(@PathVariable String id) {
    Idea idea = ideaService.getIdeaById(id);
    return ResponseEntity.status(HttpStatus.OK).body(new IdeaDTO(idea));
  }

  /**
   * Filter ideas by topics, domains, and techs. Results are ordered by date.
   *
   * @return A list of idea DTOs that matches the filters
   * @author Wassim Jabbour
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthority('User', 'Moderator', 'Owner')")
  public ResponseEntity<List<IdeaDTO>> filterIdeas(@RequestBody SearchFilterDTO searchFilterDTO) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            IdeaDTO.convertToDto(
                ideaService.getIdeasByAllCriteria(
                    searchFilterDTO.getDomains(),
                    searchFilterDTO.getTopics(),
                    searchFilterDTO.getTechnologies())));
  }

  /**
   * This method creates an idea
   *
   * @return created idea
   * @throws GlobalException if user is not authenticated or ideaDTO is null
   * @author Adam Kazma
   */
  @PostMapping(value = {"/create", "/create/"})
  @PreAuthorize("hasAuthority('User')")
  public ResponseEntity<IdeaRequestDTO> createIdea(@RequestBody IdeaRequestDTO ideaDTO)
      throws GlobalException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "User is not authenticated.");
    }
    if (ideaDTO == null) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "ideaDTO is null.");
    }

    Idea createdIdea =
        ideaService.createIdea(
            ideaDTO.getTitle(),
            ideaDTO.getPurpose(),
            ideaDTO.getDescription(),
            ideaDTO.getIsPaid(),
            ideaDTO.getInProgress(),
            ideaDTO.getIsPrivate(),
            ideaDTO.getDomainIds(),
            ideaDTO.getTechIds(),
            ideaDTO.getTopicIds(),
            ideaDTO.getImgUrlIds(),
            ideaDTO.getIconUrlId(),
            auth.getName());

    IdeaRequestDTO createdIdeaDTO = new IdeaRequestDTO(createdIdea);

    return ResponseEntity.status(HttpStatus.OK).body(createdIdeaDTO);
  }

  /**
   * This method modifies an idea
   *
   * @return update idea
   * @throws GlobalException if the ideaDTO is null
   * @author Ramin Akhavan
   */
  @PutMapping(
      value = {"/edit", "/edit/"},
      consumes = "application/json",
      produces = "application/json")
  @PreAuthorize("hasAnyAuthority('User', 'Moderator', 'Owner')")
  public ResponseEntity<IdeaRequestDTO> modifyIdea(@RequestBody IdeaRequestDTO ideaDTO)
      throws GlobalException {
    // Unpack the DTO
    if (ideaDTO == null) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "ideaDTO is null");
    }

    Idea modifiedIdea =
        ideaService.modifyIdea(
            ideaDTO.getId(),
            ideaDTO.getTitle(),
            ideaDTO.getPurpose(),
            ideaDTO.getDescription(),
            ideaDTO.getIsPaid(),
            ideaDTO.getInProgress(),
            ideaDTO.getIsPrivate(),
            ideaDTO.getDomainIds(),
            ideaDTO.getTechIds(),
            ideaDTO.getTopicIds(),
            ideaDTO.getImgUrlIds(),
            ideaDTO.getIconUrlId());
    IdeaRequestDTO modifiedIdeaDTO = new IdeaRequestDTO(modifiedIdea);

    return ResponseEntity.status(HttpStatus.OK).body(modifiedIdeaDTO);
  }

  /**
   * Remove an idea by its id
   *
   * @param id the idea's id
   * @return a response entity with a message instance and the HttpStatus
   * @author Athmane Benarous
   */
  @DeleteMapping({"/{id}", "/{id}/"})
  @PreAuthorize("hasAnyAuthority('User', 'Moderator', 'Owner')")
  public ResponseEntity<String> removeIdea(@PathVariable String id) {
    // authenticate user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String email = authentication.getName();

    // check if user is the owner of the idea
    RegularUser owner = ideaService.getIdeaById(id).getUser();

    if (!owner.getAppUser().getEmail().equals(email)) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "User not authorized");
    }

    // call service layer
    ideaService.removeIdeaById(id);
    // return response status with confirmation message
    return new ResponseEntity<String>("Idea successfully deleted", HttpStatus.OK);
  }
}
