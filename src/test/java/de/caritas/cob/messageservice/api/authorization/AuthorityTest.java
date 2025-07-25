package de.caritas.cob.messageservice.api.authorization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.caritas.cob.messageservice.api.authorization.Authority.AuthorityValue;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorityTest {

  @Test
  void getAuthoritiesByRoleName_Should_ReturnCorrectRoles_ForKeycloakRoleConsultant() {

    List<String> result = Authority.getAuthoritiesByUserRole(Role.CONSULTANT);

    assertNotNull(result);
    assertTrue(result.contains(AuthorityValue.CONSULTANT_DEFAULT));
    assertEquals(1, result.size());

  }

  @Test
  void getAuthoritiesByRoleName_Should_ReturnCorrectRoles_ForKeycloakRolePeerConsultant() {

    List<String> result = Authority.getAuthoritiesByUserRole(Role.PEER_CONSULTANT);

    assertNotNull(result);
    assertTrue(result.contains(AuthorityValue.USE_FEEDBACK));
    assertEquals(1, result.size());

  }

  @Test
  void getAuthoritiesByRoleName_Should_ReturnCorrectRoles_ForKeycloakRoleUser() {

    List<String> result = Authority.getAuthoritiesByUserRole(Role.USER);

    assertNotNull(result);
    assertTrue(result.contains(AuthorityValue.USER_DEFAULT));
    assertEquals(1, result.size());

  }

  @Test
  void getAuthoritiesByRoleName_Should_ReturnCorrectRoles_ForKeycloakRoleTechnical() {

    List<String> result = Authority.getAuthoritiesByUserRole(Role.TECHNICAL);

    assertNotNull(result);
    assertTrue(result.contains(AuthorityValue.TECHNICAL_DEFAULT));
    assertEquals(1, result.size());

  }

}
