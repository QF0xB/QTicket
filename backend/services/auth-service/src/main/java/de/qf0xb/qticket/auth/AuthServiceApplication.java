package de.qf0xb.qticket.auth;

import de.qf0xb.qticket.auth.model.account.jpa.AuthAccountEntity;
import de.qf0xb.qticket.auth.model.rbac.RoleEntity;
import de.qf0xb.qticket.auth.service.AuthAccountService;
import de.qf0xb.qticket.auth.service.RbacService;
import de.qf0xb.qticket.auth.service.RoleService;
import de.qf0xb.qticket.security.rbac.AppPermission;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@NullMarked
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

    log.info("Generated role: {}", userRole.toString());

    roleService.createRole("MODERATOR");
    roleService.addPermissionsToRole("MODERATOR", AppPermission.USER_SEARCH);
    RoleEntity moderatorRole = roleService.setRoleParent("MODERATOR", userRole.getName());
    log.info("Generated role: {}", moderatorRole.toString());

    roleService.createRole("ADMIN");

    roleService.setRoleParent("ADMIN", moderatorRole.getName());
    RoleEntity adminRole = roleService.addPermissionsToRole("ADMIN", AppPermission.USER_CREATE);

    log.info("Generated roles: {}", roleService.getAllRoles());

    log.info("User-perms: {}", roleService.getPermissionsOfRole("USER"));
    log.info("Mod-perms: {}", roleService.getPermissionsOfRole("MODERATOR"));
    log.info("Admin-perms: {}", roleService.getPermissionsOfRole(adminRole.getName()));


    AuthAccountEntity authAccount = authAccountService.createAccount("test@test.com", "test", "test", UUID.randomUUID());
    authAccount= authAccountService.setEmailVerified(authAccount.getUsername(), true);
    log.info("Account: {}", authAccount);

    authAccount = authAccountService.addRoleToUser(authAccount.getUsername(), adminRole.getName());
    log.info("Account roles: {}", authAccountService.getRolesOfUser(authAccount.getUsername()).toString());


    log.info("Account permissions: {}", rbacService.getPermissionsOfUser(authAccount.getUsername()).toString());

    AuthAccountEntity authAccount2 = authAccountService.createAccount("test2@test.com", "test2", "test2", authAccount.getUserId());
    authAccountService.setEmailVerified(authAccount2.getUsername(), true);
  }
}
