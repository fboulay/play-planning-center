/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import play.Logger;
import play.cache.Cache;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;

/**
 *
 * @author florian
 */
public class DefaultInterceptors extends Controller{

    @Before
    static void printHttpInfos() {
        Cache.set("timeRequest-" + session.getId(), System.nanoTime());
    }

    @After
    static void printRequestTime() {
        long start = Cache.get("timeRequest-" + session.getId(), Long.class);
        Logger.debug("URL called : %s %s lasted %.5s s", request.method, request.domain + request.path, (double) (System.nanoTime() - start) / 1000000000.0d);
    }
}
