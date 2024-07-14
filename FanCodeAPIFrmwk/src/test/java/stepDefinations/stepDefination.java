package stepDefinations;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.specification.RequestSpecification;
import pojo.ToDo;
import pojo.Users;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import resources.APIResources;
import resources.Utils;

public class stepDefination extends Utils {
	List<Users> users;
    List<ToDo> todos;

    @Given("User has the todo tasks")
    public void user_has_the_todo_tasks() throws IOException {
        users = getUsers();
        todos = getTodos();
    }

    @Given("User belongs to the city FanCode")
    public void user_belongs_to_the_city_FanCode() {
        users = users.stream()
                .filter(user -> user.getAddress().getGeo().getLat() > -40 && user.getAddress().getGeo().getLat() < 5
                        && user.getAddress().getGeo().getLng() > 5 && user.getAddress().getGeo().getLng() < 100)
                .collect(Collectors.toList());
        System.out.println("Users from FanCode city : ");
        users.forEach(user -> System.out.println(user.toString()));
    }

    @Then("User Completed task percentage should be greater than {int}%")
    public void user_completed_task_percentage_should_be_greater_than(Integer percentageThreshold) {
    	  List<Users> usersWithTasksCompletedAboveThreshold = users.stream()
                  .filter(user -> {
                      List<ToDo> userTodos = todos.stream()
                              .filter(todo -> todo.getUserId() == user.getId())
                              .collect(Collectors.toList());
                      long completedCount = userTodos.stream()
                              .filter(ToDo::isCompleted)
                              .count();
                      double completedPercentage = ((double) completedCount / userTodos.size()) * 100;
                      return completedPercentage > percentageThreshold;
                  })
                  .collect(Collectors.toList());

          // Logging users who meet the criteria
          System.out.println("Users from FanCode city with task completion > " + percentageThreshold + "%:");
          usersWithTasksCompletedAboveThreshold.forEach(user -> System.out.println(user.toString()));

          // Assertion can be added if needed
          assertTrue("No users found with task completion > " + percentageThreshold + "%", !usersWithTasksCompletedAboveThreshold.isEmpty());
      }
}
