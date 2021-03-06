package handler;

import static org.assertj.core.api.Assertions.assertThat;

import db.DataBase;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.view.RedirectView;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import model.User;
import org.junit.jupiter.api.Test;
import webserver.LocalHttpSessionStorage;

class UserCreateHandlerTest {

    private Handler handler = httpRequest -> new UserHandler().create(httpRequest);

    @Test
    void create() throws IOException {
        HttpRequest httpRequest = getCreateHttpRequest();

        handler.handle(httpRequest);

        User user = DataBase.findUserById("javajigi");
        assertThat(user.getUserId()).isEqualTo("javajigi");
    }

    @Test
    void create_redirect() throws IOException {
        HttpRequest httpRequest = getCreateHttpRequest();

        HttpResponse httpResponse = handler.handle(httpRequest);

        assertThat(httpResponse).isEqualTo(new HttpResponse(new RedirectView("/index.html")));
    }

    private HttpRequest getCreateHttpRequest() throws IOException {
        String line = "POST /user/create HTTP/1.1";
        String header = "Content-Length: 91\nContent-Type: application/x-www-form-urlencoded";
        String body = "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net";

        String raw = line + "\n" + header + "\n\n" + body;
        InputStream in = new ByteArrayInputStream(raw.getBytes());

        return HttpRequest.from(in, new LocalHttpSessionStorage());
    }
}
