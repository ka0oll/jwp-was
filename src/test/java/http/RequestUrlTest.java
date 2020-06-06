package http;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RequestUrlTest {

    @Test
    void parsePath(){
        RequestUrl requestUrl = RequestUrl.of("/test/index.html");
        assertThat(requestUrl).isEqualTo(RequestUrl.of("/test/index.html", ""));
    }
}
