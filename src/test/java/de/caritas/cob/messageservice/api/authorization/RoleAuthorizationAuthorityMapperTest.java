package de.caritas.cob.messageservice.api.authorization;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class RoleAuthorizationAuthorityMapperTest {

  private final RoleAuthorizationAuthorityMapper roleAuthorizationAuthorityMapper =
      new RoleAuthorizationAuthorityMapper();

  @Test
  void mapAuthorities_Should_returnGrantedConsultantAuthority_When_authorityConsultant() {
    List<GrantedAuthority> grantedAuthorities = Stream.of("consultant")
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    Collection<? extends GrantedAuthority> mappedAuthorities = this.roleAuthorizationAuthorityMapper
        .mapAuthorities(grantedAuthorities);

    assertThat(mappedAuthorities).hasSize(1);
    List<String> authorities = mappedAuthorities.stream()
        .map(grantedAuthority -> grantedAuthority.getAuthority()).toList();
    assertThat(authorities).containsAll(Authority.CONSULTANT.getAuthorities());
  }

  @Test
  void mapAuthorities_Should_returnGrantedTechnicalAuthority_When_authoritiesContainsTechnical() {
    List<GrantedAuthority> grantedAuthorities = Stream.of("a", "v", "technical", "c")
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    Collection<? extends GrantedAuthority> mappedAuthorities = this.roleAuthorizationAuthorityMapper
        .mapAuthorities(grantedAuthorities);

    assertThat(mappedAuthorities).hasSize(1);
    List<String> authorities = mappedAuthorities.stream()
        .map(grantedAuthority -> grantedAuthority.getAuthority()).toList();
    assertThat(authorities).containsAll(Authority.TECHNICAL.getAuthorities());

  }

  @Test
  void mapAuthorities_Should_returnEmptyCollection_When_authorityIsEmpty() {
    Collection<? extends GrantedAuthority> mappedAuthorities = this.roleAuthorizationAuthorityMapper
        .mapAuthorities(emptyList());

    assertThat(mappedAuthorities).isEmpty();
  }

  @Test
  void mapAuthorities_Should_returnEmptyCollection_When_authoritiesAreNotProvided() {
    List<GrantedAuthority> grantedAuthorities = Stream.of("a", "v", "b", "c")
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    Collection<? extends GrantedAuthority> mappedAuthorities = this.roleAuthorizationAuthorityMapper
        .mapAuthorities(grantedAuthorities);

    assertThat(mappedAuthorities).isEmpty();
  }

}
