package de.qf0xb.qticket.auth;

import de.qf0xb.qticket.auth.model.account.AuthAccountEntity;
import de.qf0xb.qticket.auth.model.rbac.RoleEntity;
import de.qf0xb.qticket.auth.repository.RoleEntityRepository;
import de.qf0xb.qticket.auth.service.AuthAccountService;
import de.qf0xb.qticket.auth.service.AuthService;
import de.qf0xb.qticket.auth.service.RbacService;
import de.qf0xb.qticket.auth.service.RoleService;
import de.qf0xb.qticket.security.rbac.AppPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Set;
import java.util.UUID;

@Slf4j
@SpringBootApplication(scanBasePackages = "de.qf0xb.qticket")
public class AuthServiceApplication implements CommandLineRunner {
  static void main(String[] args) {
    SpringApplication.run(AuthServiceApplication.class, args);
  }

  private final RoleService roleService;
  private final AuthAccountService authAccountService;
  private final RbacService rbacService;

  public AuthServiceApplication(RoleService roleService, AuthAccountService authAccountService, RbacService rbacService) {
    this.roleService = roleService;
    this.authAccountService = authAccountService;
    this.rbacService = rbacService;
  }

  @Override
  public void run(String... args) throws Exception {
    log.info("Generating roles...");
    RoleEntity userRole = roleService.createRole("USER");
    userRole = roleService.addPermissionToRole("USER", AppPermission.ROLE_CREATE);

    log.info("Generated role: " + userRole.toString());

    RoleEntity moderatorRole = new RoleEntity();
    moderatorRole.setName("MODERATOR");
    moderatorRole.setDescription("Moderator role");
    moderatorRole.setAppPermission(Set.of(AppPermission.ROLE_DELETE));
    moderatorRole.setParent(userRole);
    moderatorRole = roleService.createRole(moderatorRole);

    RoleEntity adminRole = new RoleEntity();
    adminRole.setName("ADMIN");
    adminRole.setDescription("ADMIN role");
    adminRole.setAppPermission(Set.of(AppPermission.USER_SET_ROLE));
    adminRole.setParent(moderatorRole);
    adminRole = roleService.createRole(adminRole);

    log.info("Generated roles: {}", roleService.getAllRoles().toString());

    log.info("User-perms: {}", roleService.getPermissionsOfRole("USER").toString());
    log.info("Mod-perms: {}", roleService.getPermissionsOfRole("MODERATOR").toString());
    log.info("Admin-perms: {}", roleService.getPermissionsOfRole(adminRole).toString());


    AuthAccountEntity authAccount = authAccountService.createAccount("test@test.com", "test", "test", UUID.randomUUID());
    authAccount= authAccountService.setEmailVerified(authAccount.getUsername(), true);
    log.info("Account: {}", authAccount.toString());

    authAccount = authAccountService.addRoleToUser(authAccount, moderatorRole);
    log.info("Account roles: {}", authAccountService.getRolesOfUser(authAccount).toString());


    log.info("Account permissions: {}", rbacService.getPermissionsOfUser(authAccount));
  }
}
