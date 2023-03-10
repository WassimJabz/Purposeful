package ca.mcgill.purposeful.service;

import ca.mcgill.purposeful.dao.*;
import ca.mcgill.purposeful.exception.GlobalException;
import ca.mcgill.purposeful.model.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

/** Service functions of the Idea class */
@Service
public class IdeaService {

  /*
   * CRUD repos
   */

  @Autowired IdeaRepository ideaRepository;

  @Autowired DomainRepository domainRepository;

  @Autowired TechnologyRepository technologyRepository;

  @Autowired TopicRepository topicRepository;

  @Autowired URLRepository urlRepository;

  @Autowired RegularUserRepository regularUserRepository;

  /*
   * Service functions
   */

  /**
   * Get an idea by its UUID
   *
   * @param uuid UUID of the idea
   * @return The {@link Idea} with the given UUID
   * @author Wassim Jabbour
   */
  @Transactional
  public Idea getIdeaById(String uuid) {

    if (uuid == null || uuid.isEmpty()) {
      throw new GlobalException(
          HttpStatus.BAD_REQUEST, "Please enter a valid UUID. UUID cannot be empty.");
    }

    Idea idea = ideaRepository.findIdeaById(uuid);

    if (idea == null) {
      throw new GlobalException(
          HttpStatus.BAD_REQUEST, "Idea with UUID " + uuid + " does not exist.");
    }

    return idea;
  }

  // TODO: For in a future sprint, we will implement a recommendations engine to
  // sort the ideas!

  /**
   * Get all ideas with a set of domain names, topic names, and technology names. For now, we can
   * just return all ideas upon a (null, null, null) call. Currently just sorts from newest to
   * oldest.
   *
   * @param domainNames The list of domain names that the idea must have one of (null if no filter)
   * @param topicNames The list of topic names that the idea must have one of (null if no filter)
   * @param techNames The list of technology names that the idea must have one of (null if no
   *     filter)
   * @return A list of ideas that match all the criteria
   * @author Wassim Jabbour
   */
  @Transactional
  public List<Idea> getIdeasByAllCriteria(
      List<String> domainNames, List<String> topicNames, List<String> techNames) {

    // Retrieve all ideas
    Iterable<Idea> allIdeas = ideaRepository.findAll();

    // Check whether the request was successfull
    if (allIdeas == null) {
      throw new GlobalException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not retrieve ideas.");
    }

    // Convert the iterable object to a list
    List<Idea> allIdeasList = new ArrayList<>();
    allIdeas.forEach(allIdeasList::add);

    // Filter out private ideas from the list of all ideas
    for(Idea idea : allIdeas) {
      if(idea.isPrivate()) {
        allIdeasList.remove(idea);
      }
    }

    // Create a list to hold the filtered ideas
    List<Idea> filteredIdeas = new ArrayList<>();

    // Filter by all criteria
    for (Idea idea : allIdeasList) {

      // 1) Check whether the idea contains 1 of the required domains
      // We do this by checking if the required domain list contains at least 1 of the
      // idea's
      // domains
      // The following boolean will be set to true if the required domain list
      // contains at least 1
      // domain of our idea
      boolean contains = false;
      if (domainNames == null) { // No criteria based on domain
        contains = true;
      } else {
        for (Domain ideaDomain : idea.getDomains()) {
          // If null, no requirement on domain, so we skip this check
          if (domainNames.contains(ideaDomain.getName())) {
            contains = true;
            break;
          }
        }
      }
      if (!contains) {
        continue; // Skip the other checks if the idea does not contain the required domain
      }

      // 2) Check whether the idea contains 1 of the required topics
      contains = false; // Variable reuse
      if (topicNames == null) { // No filter on topic name
        contains = true;
      } else {
        for (Topic ideaTopic : idea.getTopics()) {
          if (topicNames.contains(ideaTopic.getName())) {
            contains = true;
            break;
          }
        }
      }
      if (!contains) {
        continue; // Skip the other checks if the idea does not contain the required topic
      }

      // 3) Check whether the idea contains 1 of the required technologies
      contains = false; // Variable reuse
      if (techNames == null) { // No filter on tech name
        contains = true;
      } else {
        for (Technology ideaTech : idea.getTechs()) {
          if (techNames.contains(ideaTech.getName())) {
            contains = true;
            break;
          }
        }
      }
      if (!contains) {
        continue; // Skip the other checks if the idea does not contain the required technology
      }

      // If we reach this point, the idea matches all the criteria
      filteredIdeas.add(idea);
    }

    // Check whether any ideas match the criteria
    if (filteredIdeas.isEmpty()) {
      throw new GlobalException(
          HttpStatus.NOT_FOUND,
          "No ideas match the given criteria. Please try again with different criteria.");
    }

    // Sort the ideas from newest to oldest
    // We flip the order so that the newest (bigger date) comes first
    filteredIdeas.sort((idea1, idea2) -> idea2.getDate().compareTo(idea1.getDate()));

    // Return the list of ideas otherwise
    return filteredIdeas;
  }

  /**
   * Method to create an idea
   *
   * @return The newly created {@link Idea}
   * @author Adam Kazma
   */
  @Transactional
  public Idea createIdea(
      String title,
      String purpose,
      String description,
      boolean isPaid,
      boolean inProgress,
      boolean isPrivate,
      List<String> domainIds,
      List<String> techIds,
      List<String> topicIds,
      List<String> imgUrlIds,
      String iconUrlId,
      String regularUsername) {
    // Check parameters are not empty
    checkEmptyAttributeViolation(title);
    if (title.length() > 100) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Idea titles cannot exceed 100 characters");
    }
    checkEmptyAttributeViolation(description);
    checkEmptyAttributeViolation(purpose);

    // Check to see if all given objects exist
    if (domainIds.size() == 0
        || domainIds == null
        || (domainIds.size() == 1 && (domainIds.get(0) == null || domainIds.get(0).isEmpty()))) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "You must specify at least 1 domain");
    }
    Set<Domain> domains = checkDomains(domainIds);
    Set<Technology> techs = checkTechs(techIds);
    if (topicIds == null
        || topicIds.size() == 0
        || (topicIds.size() == 1 && (topicIds.get(0) == null || topicIds.get(0).isEmpty()))) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "You must specify at least 1 topic");
    }
    Set<Topic> topics = checkTopics(topicIds);
    List<URL> imgUrls = checkImgURLS(imgUrlIds);
    if (iconUrlId == null || iconUrlId.isEmpty()) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "An idea icon is required");
    }
    URL iconUrl = checkURL(iconUrlId);
    RegularUser user = regularUserRepository.findRegularUserByAppUserEmail(regularUsername);
    Idea idea = new Idea();
    idea.setDate(Date.from(Instant.now()));
    idea.setTitle(title);
    idea.setPurpose(purpose);
    idea.setDescription(description);
    idea.setPaid(isPaid);
    idea.setInProgress(inProgress);
    idea.setPrivate(isPrivate);
    idea.setDomains(domains);
    idea.setTechs(techs);
    idea.setTopics(topics);
    idea.setIconUrl(iconUrl);
    idea.setSupportingImageUrls(imgUrls);
    idea.setUser(user);

    // Save to repository
    ideaRepository.save(idea);

    return idea;
  }

  /**
   * Modify an idea based on id
   *
   * @param id id
   * @param title title
   * @param purpose purpose
   * @param descriptions description
   * @param isPaid paid or not paid idea
   * @param inProgress status of progress
   * @param isPrivate privacy of idea
   * @param domainIds domain Ids of domains
   * @param techIds tech Ids of idea
   * @param topicIds topic Ids of idea
   * @param imgUrlIds image url Ids of idea
   * @param iconUrlId icon url Ids of idea
   * @author Ramin Akhavan
   * @throws GlobalException if necessary field are left empty or if an object does not exist
   */
  @Transactional
  public Idea modifyIdea(
      String id,
      String title,
      String purpose,
      String descriptions,
      boolean isPaid,
      boolean inProgress,
      boolean isPrivate,
      List<String> domainIds,
      List<String> techIds,
      List<String> topicIds,
      List<String> imgUrlIds,
      String iconUrlId) {

    // Retrieve idea (we assume that no user can access an idea they don't own
    // because of frontend)
    Idea idea = getIdeaById(id);

    // Check to make sure essential fields are not empty
    checkEmptyAttributeViolation(title);
    checkEmptyAttributeViolation(purpose);
    checkEmptyAttributeViolation(descriptions);

    // Check to see if all objects exists
    Set<Domain> domains = checkDomains(domainIds);
    Set<Technology> techs = checkTechs(techIds);
    Set<Topic> topics = checkTopics(topicIds);
    List<URL> imgUrls = checkImgURLS(imgUrlIds);
    URL iconUrl = checkURL(iconUrlId);

    // Check to see if it is necessary to change boolean fields
    if (idea.isPaid() != isPaid) {
      idea.setPaid(isPaid);
    }
    if (idea.isInProgress() != inProgress) {
      idea.setInProgress(inProgress);
    }
    if (idea.isPrivate() != isPrivate) {
      idea.setPrivate(isPrivate);
    }

    // Change all remaining attributes
    if (title != null) {
      idea.setTitle(title);
    }
    if (descriptions != null) {
      idea.setDescription(descriptions);
    }
    if (purpose != null) {
      idea.setPurpose(purpose);
    }

    if (domainIds != null) {
      idea.setDomains(domains);
    }
    if (techIds != null) {
      idea.setTechs(techs);
    }
    if (topicIds != null) {
      idea.setTopics(topics);
    }
    if (imgUrlIds != null) {
      idea.setSupportingImageUrls(imgUrls);
    }
    idea.setIconUrl(iconUrl);

    // Save updated idea in the repository
    ideaRepository.save(idea);
    return idea;
  }

  /**
   * Check to make sure a necessary field is not empty
   *
   * @throws GlobalException if necessary field is left empty
   * @author Ramin Akhavan
   */
  public void checkEmptyAttributeViolation(String newValue) {
    if (newValue != null) {
      if (newValue.equalsIgnoreCase("")) {
        throw new GlobalException(HttpStatus.BAD_REQUEST, "Necessary fields have been left empty");
      }
    }
  }

  /**
   * Check to make sure all domains of an idea exist
   *
   * @throws GlobalException if an object does not exist
   * @author Ramin Akhavan
   */
  public Set<Domain> checkDomains(List<String> domainIds) {
    Domain domain = null;
    Set<Domain> domains = new HashSet<Domain>();
    if (domainIds != null) {
      for (String id : domainIds) {
        domain = domainRepository.findDomainById(id);
        if (domain == null) {
          throw new GlobalException(
              HttpStatus.BAD_REQUEST,
              "You are attempting to link your idea to an object that does not exist");
        }
        domains.add(domain);
      }
    }
    return domains;
  }

  /**
   * Check to make sure all technologies of an idea exist
   *
   * @throws GlobalException if an object does not exist
   * @author Ramin Akhavan
   */
  public Set<Technology> checkTechs(List<String> techIds) {
    Technology tech = null;
    Set<Technology> techs = new HashSet<Technology>();
    if (techIds != null) {
      for (String id : techIds) {
        tech = technologyRepository.findTechnologyById(id);
        if (tech == null) {
          throw new GlobalException(
              HttpStatus.BAD_REQUEST,
              "You are attempting to link your idea to an object that does not exist");
        }
        techs.add(tech);
      }
    }
    return techs;
  }

  /**
   * Check to make sure all topics of an idea exist
   *
   * @throws GlobalException if an object does not exist
   * @author Ramin Akhavan
   */
  public Set<Topic> checkTopics(List<String> topicIds) {
    Topic topic = null;
    Set<Topic> topics = new HashSet<Topic>();
    if (topicIds != null) {
      for (String id : topicIds) {
        topic = topicRepository.findTopicById(id);
        if (topic == null) {
          throw new GlobalException(
              HttpStatus.BAD_REQUEST,
              "You are attempting to link your idea to an object that does not exist");
        }
        topics.add(topic);
      }
    }
    return topics;
  }

  /**
   * Check to make sure all image urls exist
   *
   * @throws GlobalException if an object does not exist
   * @author Ramin Akhavan
   */
  public List<URL> checkImgURLS(List<String> imgUrlIds) {
    List<URL> urls = new ArrayList<URL>();
    if (imgUrlIds != null) {
      for (String id : imgUrlIds) {
        URL urlCheck = checkURL(id);
        if (urlCheck != null) {
          urls.add(urlCheck);
        }
      }
    }
    return urls;
  }

  /**
   * Check to make sure a url exists
   *
   * @throws GlobalException if an object does not exist
   * @author Ramin Akhavan
   */
  public URL checkURL(String urlId) {
    URL url = null;
    if (urlId != null) {
      url = urlRepository.findURLById(urlId);
      if (url == null) {
        throw new GlobalException(
            HttpStatus.BAD_REQUEST,
            "You are attempting to link your idea to an object that does not exist");
      }
    }
    return url;
  }

  /**
   * Remove a posted idea from the system alongside its reaction and URLs
   *
   * @param uuid the idea's uuid
   * @author Athmane Benarous
   */
  @Transactional
  public void removeIdeaById(String uuid) {
    // validate idea by getting it
    this.getIdeaById(uuid);

    // remove idea
    ideaRepository.deleteById(uuid);
  }
}
