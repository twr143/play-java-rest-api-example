package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import java.util.ArrayList;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {


  // gracefull shutdown
  public Result exit() {
//    System.exit(0);
    return Results.ok();
  }
  public Result index() {
        return ok(views.html.index.render(new ArrayList<>(),false));
    }

}
