package pro.documentum.demo.isis.integtests.specs;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;


/**
 * Runs scenarios in all <tt>.feature</tt> files (this package and any subpackages). 
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        format = {
                "html:target/cucumber-html-report"
                ,"json:target/cucumber.json"
        },
        glue={"classpath:pro.documentum.demo.isis.integtests.specglue"},
        strict = true,
        tags = { "~@backlog", "~@ignore" })
public class RunSpecs {
    // intentionally empty 
}
