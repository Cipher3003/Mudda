/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : MediaRepository
 * Author  : Vikas Kumar
 * Created : 16-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.media;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    @Query("SELECT m.mediaKey FROM Media m WHERE m.ownerId = :ownerId AND m.ownerType = :mediaOwner")
    List<String> findMediaKeysByOwnerIdAndOwnerType(Long ownerId, MediaOwner mediaOwner);

    List<MediaProjection> findByOwnerIdInAndOwnerType(List<Long> issueIds, MediaOwner mediaOwner);

    Optional<Media> findByPublicId(String publicId);

    @Query("SELECT m FROM Media m WHERE m.status = 'FAILED' OR (m.status = 'UPLOADING' AND m.createdAt < :threshold)")
    Stream<Media> findExpiredOrFailed(Instant threshold);

    @Modifying
    @Query("UPDATE Media m SET m.status = 'FAILED' WHERE m.publicId = :publicId")
    int markFailed(String publicId);

    @Modifying
    @Query("UPDATE Media m SET m.ownerType = :mediaOwner, m.ownerId = :id WHERE m.publicId IN :mediaKeys")
    int updateOwner(MediaOwner mediaOwner, long id, List<String> mediaKeys);

    @Modifying
    @Query("UPDATE Media m SET m.status = 'FAILED' WHERE m.ownerId = :ownerId AND m.ownerType = :mediaOwner")
    int markFailedByOwnerIdAndOwnerType(Long ownerId, MediaOwner mediaOwner);
}
