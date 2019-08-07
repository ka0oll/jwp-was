package webserver.http.request.handler;

import webserver.http.request.HttpMethod;
import webserver.http.request.HttpRequest;
import webserver.http.request.controller.Controller;
import webserver.http.request.controller.ControllerType;
import webserver.http.response.HttpResponse;
import webserver.http.response.view.ModelAndView;
import webserver.http.response.view.TemplateResourceViewRenderer;
import webserver.http.response.view.ViewRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author : yusik
 * @date : 2019-08-06
 */
public class MappedRequestHandler implements RequestHandler {

    private final String prefix;
    private static Map<String, ControllerType> mappingControllerCache = new HashMap<>();

    static {
        for (ControllerType type : ControllerType.values()) {
            mappingControllerCache.put(type.getUrl(), type);
        }
    }

    public MappedRequestHandler(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public ViewRenderer handle(HttpRequest httpRequest, HttpResponse httpResponse) {

        String url = httpRequest.getPath();
        ModelAndView modelAndView = new ModelAndView("redirect::/error");

        ControllerType controllerType = mappingControllerCache.get(url);

        if (controllerType != null) {
            // check login
            Controller controller = controllerType.getController();
            if (controllerType.isAllowAll() || isLogin(httpRequest)) {
                // service logic
                modelAndView = controller.process(httpRequest, httpResponse);
            }
        }

        // redirect
        if (modelAndView.isRedirect()) {
            httpResponse.sendRedirect(modelAndView.getRedirectUrl());
        }

        return new TemplateResourceViewRenderer(httpResponse, modelAndView);
    }

    private boolean isLogin(HttpRequest httpRequest) {
        return Optional.ofNullable(httpRequest.getCookie())
                .map(cookie -> cookie.contains("logined=true"))
                .orElse(false);
    }
}