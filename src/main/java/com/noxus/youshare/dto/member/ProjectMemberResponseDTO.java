package com.noxus.youshare.dto.member;

import com.noxus.youshare.entity.enums.InviteStatus;
import com.noxus.youshare.entity.enums.MemberRole;

import java.util.Date;
import java.util.UUID;

public record ProjectMemberResponseDTO(
    UUID userId,
    String username,
    MemberRole role,
    InviteStatus inviteStatus,
    Date joinedAt
) {}