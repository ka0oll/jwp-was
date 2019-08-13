package webserver.resolver.resource;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import webserver.request.HttpRequest;
import webserver.request.RequestLine;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by hspark on 2019-08-05.
 */
class ResourceResolverRegistrationTest {

    @ParameterizedTest(name = "suffixSet : {0}, classPath : {1}, successHttpRequest : {2}, failHttpRequest : {3}")
    @MethodSource("getTestSources")
    public void test_resolve(Set<String> suffixSet, String classPath, HttpRequest httpRequest, HttpRequest faultHttpRequest) {
        ResourceResolverRegistration resourceResolverRegistration = new ResourceResolverRegistration(suffixSet, classPath);
        assertThat(resourceResolverRegistration.isTarget(httpRequest.getPath())).isTrue();
        assertThat(resourceResolverRegistration.resolve(httpRequest)).isEqualTo(classPath + httpRequest.getPath());

        assertThat(resourceResolverRegistration.isTarget(faultHttpRequest.getPath())).isFalse();
        assertThatThrownBy(() -> resourceResolverRegistration.resolve(faultHttpRequest));

    }

    @Test
    public void test_suffix_지원안함() {
        ResourceResolverRegistration resourceResolverRegistration =
                new ResourceResolverRegistration(Sets.newHashSet(".css"), "/static");

        HttpRequest httpRequest = HttpRequest.builder().requestLine(RequestLine.parse("GET /css/bootstrap.js HTTP/1.1")).build();
        Assertions.assertThrows(IllegalArgumentException.class, () -> resourceResolverRegistration.resolve(httpRequest));
    }


    private static Stream<Arguments> getTestSources() {
        return Stream.of(
                Arguments.of(Sets.newHashSet(".css"), "/static", HttpRequest.builder().requestLine(RequestLine.parse("GET /css/bootstrap.min.css HTTP/1.1")).build(), HttpRequest.builder().requestLine(RequestLine.parse("GET /css/bootstrap.js HTTP/1.1")).build()),
                Arguments.of(Sets.newHashSet(".js"), "/static", HttpRequest.builder().requestLine(RequestLine.parse("GET /js/bootstrap.js HTTP/1.1")).build(), HttpRequest.builder().requestLine(RequestLine.parse("GET /index.html HTTP/1.1")).build()),
                Arguments.of(Sets.newHashSet(".html"), "/templates", HttpRequest.builder().requestLine(RequestLine.parse("GET /index.html HTTP/1.1")).build(), HttpRequest.builder().requestLine(RequestLine.parse("GET /css/bootstrap.js HTTP/1.1")).build())
        );
    }
}