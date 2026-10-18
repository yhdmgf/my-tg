package org.lunagram.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.lunagram.core.database.dao.ChatDao
import org.lunagram.core.database.dao.FolderDao
import org.lunagram.core.database.dao.MessageDao
import org.lunagram.core.database.dao.MessageHoleDao
import org.lunagram.core.database.dao.MessageTombstoneDao
import org.lunagram.core.database.dao.MetaDao
import org.lunagram.core.database.dao.PeerDao
import org.lunagram.core.database.dao.ProfileCommonDao
import org.lunagram.core.database.dao.ProfileMediaDao
import org.lunagram.core.database.dao.ProfileMembersDao
import org.lunagram.core.database.dao.ProfileTabsDao
import org.lunagram.core.database.dao.SponsorDao
import org.lunagram.core.database.dao.UpdateCursorDao
import org.lunagram.core.database.entity.ChatEntity
import org.lunagram.core.database.entity.FolderEntity
import org.lunagram.core.database.entity.MessageEntity
import org.lunagram.core.database.entity.MessageHoleEntity
import org.lunagram.core.database.entity.MetaEntity
import org.lunagram.core.database.entity.PeerEntity
import org.lunagram.core.database.entity.ProfileCommonEntity
import org.lunagram.core.database.entity.ProfileMediaEntity
import org.lunagram.core.database.entity.ProfileMemberEntity
import org.lunagram.core.database.entity.ProfileTabsEntity
import org.lunagram.core.database.entity.SponsorEntity
import org.lunagram.core.database.entity.UpdateBatchReceiptEntity
import org.lunagram.core.database.entity.UpdateCursorEntity

@Database(
    entities = [
        MetaEntity::class,
        ChatEntity::class,
        FolderEntity::class,
        MessageEntity::class,
        org.lunagram.core.database.entity.MessageTombstoneEntity::class,
        MessageHoleEntity::class,
        UpdateCursorEntity::class,
        UpdateBatchReceiptEntity::class,
        PeerEntity::class,
        ProfileTabsEntity::class,
        ProfileMemberEntity::class,
        ProfileMediaEntity::class,
        ProfileCommonEntity::class,
        SponsorEntity::class,
    ],
    version = 8,
    exportSchema = true,
)
abstract class MonogramDatabase : RoomDatabase() {
    abstract fun metaDao(): MetaDao
    abstract fun chatDao(): ChatDao
    abstract fun folderDao(): FolderDao
    abstract fun messageDao(): MessageDao
    abstract fun messageTombstoneDao(): MessageTombstoneDao
    abstract fun messageHoleDao(): MessageHoleDao
    abstract fun updateCursorDao(): UpdateCursorDao
    abstract fun peerDao(): PeerDao
    abstract fun profileTabsDao(): ProfileTabsDao
    abstract fun profileMembersDao(): ProfileMembersDao
    abstract fun profileMediaDao(): ProfileMediaDao
    abstract fun profileCommonDao(): ProfileCommonDao
    abstract fun sponsorDao(): SponsorDao
}
