package ca.mcgill.purposeful.features;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * Class that allows all other cucumber classes to be run There was an attempt to randomize the
 * order in which step definition files were executed, but it was not successful due to the fact
 * that @CucumberOptions takes precedence over the build.gradle file, and there is no way to define
 * the order using @CucumberOptions. Furthermore, since step definition files are independent of
 * each other, making their order random would not serve any useful purpose. Therefore, it was
 * decided not to randomize the order of execution of step definition files to avoid introducing
 * unnecessary complexity and unpredictability into the testing process.
 *
 * @author Shidan Javaheri
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {"pretty", "html:target/cucumber-report.html"},
    features = {"src/test/resources"},
    extraGlue = "io.tpd.springbootcucumber.bagcommons")
// annotations to configure spring boot tests with a fake client, and to let us autowire objects
// into the cucumber class
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
public class RunCucumberTest {}
