package de.caritas.cob.messageservice.api.authorization;

import static de.caritas.cob.messageservice.api.authorization.Authority.AuthorityValue.ANONYMOUS_DEFAULT;
import static de.caritas.cob.messageservice.api.authorization.Authority.AuthorityValue.CONSULTANT_DEFAULT;
import static de.caritas.cob.messageservice.api.authorization.Authority.AuthorityValue.TECHNICAL_DEFAULT;
import static de.caritas.cob.messageservice.api.authorization.Authority.AuthorityValue.USER_DEFAULT;
import static de.caritas.cob.messageservice.api.authorization.Authority.AuthorityValue.USE_FEEDBACK;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Definition of all authorities and of the role-authority-mapping.
 */
@Getter
@AllArgsConstructor
public enum Authority {

  ANONYMOUS(Role.ANONYMOUS.getRoleName(), singletonList(ANONYMOUS_DEFAULT)),
  USER(Role.USER.getRoleName(), singletonList(USER_DEFAULT)),
  CONSULTANT(Role.CONSULTANT.getRoleName(), singletonList(CONSULTANT_DEFAULT)),
  PEER_CONSULTANT(Role.PEER_CONSULTANT.getRoleName(), singletonList(USE_FEEDBACK)),
  TECHNICAL(Role.TECHNICAL.getRoleName(), singletonList(TECHNICAL_DEFAULT));

  private final String roleName;
  private final List<String> authorities;

  /**
   * Get all authorities for a specific role.
   *
   * @param userRole the user role
   * @return the related authorities
   */
  public static List<String> getAuthoritiesByUserRole(Role userRole) {
    var authorities = Stream.of(values())
        .filter(authority -> authority.getRoleName().equals(userRole.getRoleName())).toList();

    List<String> collect = authorities.stream().map(a -> a.getAuthorities())
        .flatMap(a -> a.stream()).collect(
            Collectors.toList());
    return authorities.isEmpty() ? emptyList() : collect;

  }

  public static Authority fromRoleName(String roleName) {
    return Stream.of(values())
        .filter(authority -> authority.roleName.equals(roleName))
        .findFirst()
        .orElse(null);
  }

  public static class AuthorityValue {

    private AuthorityValue() {
    }

    public static final String PREFIX = "AUTHORIZATION_";

    public static final String CONSULTANT_DEFAULT = PREFIX + "CONSULTANT_DEFAULT";
    public static final String USER_DEFAULT = PREFIX + "USER_DEFAULT";
    public static final String USE_FEEDBACK = PREFIX + "USE_FEEDBACK";
    public static final String TECHNICAL_DEFAULT = PREFIX + "TECHNICAL_DEFAULT";
    public static final String ANONYMOUS_DEFAULT = PREFIX + "ANONYMOUS_DEFAULT";

  }

}
