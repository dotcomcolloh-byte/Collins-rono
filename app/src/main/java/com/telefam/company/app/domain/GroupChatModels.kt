package com.telefam.company.app.domain

enum class GroupRole(val wireName: String) {
    OWNER("OWNER"),
    ADMIN("ADMIN"),
    MODERATOR("MODERATOR"),
    MEMBER("MEMBER");

    val isPrivileged: Boolean get() = this == OWNER || this == ADMIN || this == MODERATOR
    val isAdminOrOwner: Boolean get() = this == OWNER || this == ADMIN

    companion object {
        fun fromWire(wire: String?): GroupRole {
            return entries.firstOrNull { it.wireName.equals(wire, ignoreCase = true) } ?: MEMBER
        }
    }
}

enum class GroupType(val wireName: String) {
    PRIVATE("PRIVATE"),
    PUBLIC("PUBLIC");

    companion object {
        fun fromWire(wire: String?): GroupType {
            return entries.firstOrNull { it.wireName.equals(wire, ignoreCase = true) } ?: PRIVATE
        }
    }
}

enum class WhoCanOption(val wireName: String) {
    ALL_MEMBERS("ALL_MEMBERS"),
    ONLY_ADMINS("ONLY_ADMINS");

    companion object {
        fun fromWire(wire: String?): WhoCanOption {
            return entries.firstOrNull { it.wireName.equals(wire, ignoreCase = true) } ?: ALL_MEMBERS
        }
    }
}

enum class GroupProfileMediaTab {
    MEDIA,
    FILES,
    LINKS,
    VOICE,
    GIFS,
    POLLS
}

data class GroupMemberItem(
    val memberId: String,
    val groupId: String,
    val userId: String,
    val fullName: String,
    val username: String,
    val avatarUrl: String? = null,
    val role: GroupRole = GroupRole.MEMBER,
    val isOnline: Boolean = false,
    val lastSeenMs: Long? = null,
    val isMuted: Boolean = false,
    val isApproved: Boolean = true,
    val joinedAtMs: Long = System.currentTimeMillis()
)

data class GroupPermissionsSnapshot(
    val whoCanSendMessages: WhoCanOption = WhoCanOption.ALL_MEMBERS,
    val whoCanAddMembers: WhoCanOption = WhoCanOption.ALL_MEMBERS,
    val whoCanPinMessages: WhoCanOption = WhoCanOption.ONLY_ADMINS,
    val whoCanShareMessage: WhoCanOption = WhoCanOption.ALL_MEMBERS,
    val allowCopyMessages: Boolean = true,
    val allowScreenshots: Boolean = true,
    val allowDownloadMedia: Boolean = true,
    val requiresJoinApproval: Boolean = false
) {
    val enabledCount: Int
        get() {
            var count = 0
            if (whoCanSendMessages == WhoCanOption.ALL_MEMBERS) count++
            if (whoCanAddMembers == WhoCanOption.ALL_MEMBERS) count++
            if (whoCanPinMessages == WhoCanOption.ALL_MEMBERS) count++
            if (whoCanShareMessage == WhoCanOption.ALL_MEMBERS) count++
            if (allowCopyMessages) count++
            if (allowScreenshots) count++
            if (allowDownloadMedia) count++
            if (!requiresJoinApproval) count++
            return count
        }
}

data class GroupJoinRequestItem(
    val requestId: String,
    val groupId: String,
    val userId: String,
    val fullName: String,
    val username: String,
    val avatarUrl: String? = null,
    val requestedAtMs: Long = System.currentTimeMillis()
)

data class GroupAuditLogItem(
    val logId: String,
    val groupId: String,
    val actorUserId: String,
    val actorName: String,
    val actionText: String,
    val timestampMs: Long = System.currentTimeMillis()
)

data class GroupMediaPreviewItem(
    val id: String,
    val title: String,
    val url: String,
    val count: Int,
    val isVideo: Boolean = false
)

data class GroupProfileUiModel(
    val groupId: String,
    val groupName: String,
    val groupDescription: String,
    val avatarUrl: String? = null,
    val groupType: GroupType = GroupType.PUBLIC,
    val inviteLink: String = "",
    val memberCount: Int = 1,
    val onlineCount: Int = 1,
    val myRole: GroupRole = GroupRole.MEMBER,
    val isMuted: Boolean = false,
    val isHistoryVisible: Boolean = true,
    val permissions: GroupPermissionsSnapshot = GroupPermissionsSnapshot(),
    val members: List<GroupMemberItem> = emptyList(),
    val pendingApprovals: List<GroupJoinRequestItem> = emptyList(),
    val auditLogs: List<GroupAuditLogItem> = emptyList(),
    val bannedCount: Int = 0,
    val mediaItems: List<GroupMediaPreviewItem> = emptyList()
)

data class ContactsDiscoverableUser(
    val userId: String,
    val displayName: String,
    val username: String,
    val avatarUrl: String? = null
)

