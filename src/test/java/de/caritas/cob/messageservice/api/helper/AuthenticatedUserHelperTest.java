package de.caritas.cob.messageservice.api.helper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import de.caritas.cob.messageservice.api.authorization.Role;
import org.apache.commons.collections4.SetUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserHelperTest {

  @Test
  void isConsultant_Should_ReturnTrue_IfAuthenticatedUserIsConsultant() {

    AuthenticatedUser authenticatedUser = new AuthenticatedUser();
    authenticatedUser.setRoles(SetUtils.unmodifiableSet(Role.CONSULTANT.getRoleName()));

    boolean result = AuthenticatedUserHelper.isConsultant(authenticatedUser);

    assertThat(result, is(true));
  }

  @Test
  void isConsultant_Should_ReturnFalse_IfAuthenticatedUserIsNotConsultant() {

    AuthenticatedUser authenticatedUser = new AuthenticatedUser();
    authenticatedUser.setRoles(SetUtils.unmodifiableSet(Role.USER.getRoleName()));

    boolean result = AuthenticatedUserHelper.isConsultant(authenticatedUser);

    assertThat(result, is(false));
  }

}
