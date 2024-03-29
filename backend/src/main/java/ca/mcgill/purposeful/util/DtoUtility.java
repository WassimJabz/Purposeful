package ca.mcgill.purposeful.util;

import ca.mcgill.purposeful.dto.AppUserDto;
import ca.mcgill.purposeful.dto.IdeaDTO;
import ca.mcgill.purposeful.exception.GlobalException;
import ca.mcgill.purposeful.model.AppUser;
import ca.mcgill.purposeful.model.Idea;
import org.springframework.http.HttpStatus;

/** Utility class for converting entities to DTOs */
public class DtoUtility {

  /**
   * Converts a AppUser to a AppUserDto
   *
   * @param appUser - the AppUser to be converted
   * @return the converted AppUserDto
   * @author Siger Ma
   */
  public static AppUserDto convertToDto(AppUser appUser) {
    if (appUser == null) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "AppUser is null");
    }

    // Convert AppUser to AppUserDto
    AppUserDto appUserDto = new AppUserDto();
    appUserDto.setId(appUser.getId());
    appUserDto.setEmail(appUser.getEmail());
    appUserDto.setFirstname(appUser.getFirstname());
    appUserDto.setLastname(appUser.getLastname());
    appUserDto.setPassword("");
    return appUserDto;
  }

  /**
   * Converts a Idea to a IdeaDTO
   *
   * @param idea - the Idea to be converted
   * @return the converted IdeaDTO
   */
  public static IdeaDTO convertToDto(Idea idea) {
    if (idea == null) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Idea is null");
    }

    // Convert Idea to IdeaDTO
    IdeaDTO ideaDTO = new IdeaDTO(idea);
    return ideaDTO;
  }
}
