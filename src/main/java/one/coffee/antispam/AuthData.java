package one.coffee.antispam;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthData {
    public static final String ACCESS_TOKEN = "access_token";
    public static final String TOKEN_TYPE = "token_type";
    public static final String EXPIRES_IN = "expires_in";
    public static final String SCOPE = "scope";
    @JsonProperty(ACCESS_TOKEN)
    private String accessToken;
    @JsonProperty(TOKEN_TYPE)
    private String tokenType;
    @JsonProperty(EXPIRES_IN)
    private Instant expiresIn;
    @JsonProperty(SCOPE)
    private String scope;

    @JsonCreator
    public AuthData(@JsonProperty("access_token") String accessToken,
                    @JsonProperty("token_type") String tokenType,
                    @JsonProperty("expires_in") int expiresIn,
                    @JsonProperty("scope") String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = Instant.now().plus(expiresIn, ChronoUnit.SECONDS);
        this.scope = scope;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Instant getExpiresIn() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthData that = (AuthData) o;

        if (expiresIn != that.expiresIn) return false;
        if (!Objects.equals(accessToken, that.accessToken)) return false;
        if (!Objects.equals(tokenType, that.tokenType)) return false;
        return Objects.equals(scope, that.scope);
    }

    @Override
    public int hashCode() {
        int result = accessToken != null ? accessToken.hashCode() : 0;
        result = 31 * result + (tokenType != null ? tokenType.hashCode() : 0);
        result = 31 * result + expiresIn.hashCode();
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AuthRequest{" +
                "accessToken='" + accessToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", scope='" + scope + '\'' +
                '}';
    }
}
