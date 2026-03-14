package de.qf0xb.qticket.user.mapper;

import de.qf0xb.qticket.user.model.UserEntity;
import de.qf0xb.qticket.user.model.UserEntityAuditInfo;
import de.qf0xb.qticket.user.model.UserEntityStatusInfo;
import de.qf0xb.qticket.user.v1.api.model.UserAuditInfo;
import de.qf0xb.qticket.user.v1.api.model.UserInfo;
import de.qf0xb.qticket.user.v1.api.model.UserStatusInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", expression = "java(entity.getId() != null ? entity.getId().toString() : null)")
    @Mapping(target = "status", source = "userStatusInfo")
    @Mapping(target = "audit", source = "userAuditInfo")
    UserInfo toUserInfo(UserEntity entity);

    @Mapping(target = "lockedBy", expression = "java(status.getLockedBy() != null ? status.getLockedBy().getId().toString() : null)")
    UserStatusInfo toUserStatusInfo(UserEntityStatusInfo status);

    @Mapping(target = "createdBy", expression = "java(audit.getCreatedBy() != null ? audit.getCreatedBy().getId().toString() : null)")
    @Mapping(target = "updatedBy", expression = "java(audit.getLastModifiedBy() != null ? audit.getLastModifiedBy().getId().toString() : null)")
    @Mapping(target = "updatedAt", source = "lastModifiedAt")
    UserAuditInfo toUserAuditInfo(UserEntityAuditInfo audit);

    // Helper used automatically for Instant -> OffsetDateTime
    default OffsetDateTime map(Instant value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}
