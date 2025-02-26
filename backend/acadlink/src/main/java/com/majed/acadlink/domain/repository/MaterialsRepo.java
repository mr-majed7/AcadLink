package com.majed.acadlink.domain.repository;

import com.majed.acadlink.domain.entitie.Materials;
import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.enums.Privacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MaterialsRepo extends JpaRepository<Materials, UUID> {
    List<Materials> findByFolderIdAndType(UUID id, MaterialType type);

    List<Materials> findByType(MaterialType type);

    // Search for public materials
    @Query("SELECT m FROM Materials m WHERE m.privacy = :privacy " +
            "AND LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Materials> searchPublicMaterials(@Param("keyword") String keyword,
                                          @Param("privacy") Privacy privacy);

    // Search institutional materials
    @Query("SELECT m FROM Materials m " +
            "JOIN m.folder f JOIN f.user u " +
            "WHERE m.privacy = :privacy " +
            "AND LOWER(REPLACE(u.institute, ' ', '')) = LOWER(REPLACE(:institute, ' ', '')) " +
            "AND LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Materials> searchInstitutionalMaterials(@Param("keyword") String keyword,
                                                 @Param("institute") String institute,
                                                 @Param("privacy") Privacy privacy);

    // Search peer-shared materials
    @Query("SELECT m FROM Materials m " +
            "JOIN Peers p ON (p.user1.id = :userId OR p.user2.id = :userId) " +
            "WHERE m.privacy = :privacy AND p.status = 'ACCEPTED' " +
            "AND LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Materials> searchPeerMaterials(@Param("keyword") String keyword,
                                        @Param("userId") UUID userId,
                                        @Param("privacy") Privacy privacy);

    // Find all materials given userId and privacy is PUBLIC, PEERS or INSTITUTIONAL
    @Query("SELECT m FROM Materials m " +
            "JOIN m.folder f JOIN f.user u " +
            "LEFT JOIN Peers p ON (p.user1.id = :userId OR p.user2.id = :userId) " +
            "WHERE (m.privacy = com.majed.acadlink.enums.Privacy.PUBLIC OR " +
            "(m.privacy = com.majed.acadlink.enums.Privacy.PEERS AND p.status = 'ACCEPTED') OR " +
            "(m.privacy = com.majed.acadlink.enums.Privacy.INSTITUTIONAL AND LOWER(REPLACE(u.institute, ' ', '')) = LOWER(REPLACE(:institute, ' ', '')))) " +
            "AND u.id = :userId")
    List<Materials> findAllByUserIdAndPrivacy(@Param("userId") UUID userId,
                                              @Param("institute") String institute);
}
