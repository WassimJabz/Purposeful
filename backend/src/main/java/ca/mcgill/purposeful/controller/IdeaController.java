package ca.mcgill.purposeful.controller;

import ca.mcgill.purposeful.dto.IdeaDTO;
import ca.mcgill.purposeful.dto.IdeaRequestDTO;
import ca.mcgill.purposeful.dto.SearchFilterDTO;
import ca.mcgill.purposeful.exception.GlobalException;
import ca.mcgill.purposeful.model.Idea;
import ca.mcgill.purposeful.service.AppUserService;
import ca.mcgill.purposeful.service.IdeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** API for demonstrating how permissions work for access to endpoints */
@RestController
@RequestMapping({"api/idea", "api/idea/"})
public class IdeaController {

  @Autowired IdeaService ideaService;

  @Autowired AppUserService appUserService;

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
    // Check if the user is authorized
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String requestEmail = authentication.getName();
    String ownerEmail = ideaService.getIdeaById(id).getUser().getAppUser().getEmail();
    if (!requestEmail.equals(ownerEmail) && !authentication.getAuthorities().contains("Owner") && !authentication.getAuthorities().contains("Moderator")) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "User not authorized");
    }
    // call service layer
    ideaService.removeIdeaById(id);
    // return response status with confirmation message
    return new ResponseEntity<String>("Idea successfully deleted", HttpStatus.OK);
  }

    /**
   * Remove an idea by its id
   *
   * @return a response entity with a list of ideas and the HttpStatus
   * @author Ramin Akhavan
   */
    @GetMapping({"/user", "/user/"})
  @PreAuthorize("hasAnyAuthority('User', 'Moderator', 'Owner')")
  public ResponseEntity<List<IdeaDTO>> getUserCreatedIdeas() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "User is not authenticated.");
    }
    String requestEmail = authentication.getName();

    List<Idea> createdIdeas = ideaService.getCreatedIdeas(requestEmail);

    List<IdeaDTO> ideaDTOs = IdeaDTO.convertToDto(createdIdeas);

    return ResponseEntity.status(HttpStatus.OK).body(IdeaDTO.convertToDto(createdIdeas));
  }
}
