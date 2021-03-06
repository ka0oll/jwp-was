package handler;

import static org.assertj.core.api.Assertions.assertThat;

import db.DataBase;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.view.RedirectView;
import java.util.Arrays;
import model.User;
import org.junit.jupiter.api.Test;
import webserver.LocalHttpSessionStorage;

class UserLoginHandlerTest {

    @Test
    void login_success() {
        DataBase.addUser(new User("javajigi", "password", "", ""));
        HttpResponse httpResponse = login("javajigi", "password");

        HttpResponse expect = new HttpResponse(new RedirectView("/index.html"));
        expect.addCookie("logined", "true");

        assertThat(httpResponse).isEqualTo(expect);
    }

    @Test
    void login_fail() {
        HttpResponse httpResponse = login("javajigi", "password");

        HttpResponse expect = new HttpResponse(new RedirectView("/user/login_failed.html"));
        assertThat(httpResponse).isEqualTo(expect);
    }

    private HttpResponse login(String userId, String password) {
        String line = "POST /user/login HTTP/1.1";
        String body = String.format("userId=%s&password=%s", userId, password);

        String contentLength = String.format("Content-Length: %s", body.length());
        String contentType = "Content-Type: application/x-www-form-urlencoded";

        HttpRequest httpRequest = HttpRequest.of(
            line,
            Arrays.asList(contentLength, contentType),
            body,
            new LocalHttpSessionStorage()
        );

        return new UserHandler().login(httpRequest);
    }
}
