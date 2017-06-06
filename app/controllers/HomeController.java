package controllers;

import play.mvc.*;

import java.util.ArrayList;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    public Result index() {
        return ok(views.html.index.render(new ArrayList<>(),false));
    }

}
