package controllers;

import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author florian
 */
@With(DefaultInterceptors.class)
public class About extends Controller {

    public static void index() {
        render();
    }
}
