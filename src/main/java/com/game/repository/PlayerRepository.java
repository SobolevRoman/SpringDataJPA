package com.game.repository;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("select p from Player p where " +
            "(:name is null or p.name like %:name%) and " +
            "(:title is null or p.title like %:title%) and " +
            "(:race is null or p.race = :race) and " +
            "(:profession is null or p.profession = :profession) and " +
            "(:after is null or p.birthday >= :after) and " +
            "(:before is null or p.birthday <= :before) and " +
            "(:banned is null or p.banned = :banned) and " +
            "(:minExperience is null or p.experience >= :minExperience) and " +
            "(:maxExperience is null or p.experience <= :maxExperience) and " +
            "(:minLevel is null or p.level >= :minLevel) and " +
            "(:maxLevel is null or p.level <= :maxLevel) ")
    List<Player> getAllPlayersWithFilters(@Param("name") String name,
                                          @Param("title") String title,
                                          @Param("race") Race race,
                                          @Param("profession") Profession profession,
                                          @Param("after") Date after,
                                          @Param("before") Date before,
                                          @Param("banned") Boolean banned,
                                          @Param("minExperience") Integer minExperience,
                                          @Param("maxExperience") Integer maxExperience,
                                          @Param("minLevel") Integer minLevel,
                                          @Param("maxLevel") Integer maxLevel,
                                          Pageable pageable
    );

    /* reuse the method List<Player> getAllPlayersWithFilters(....)
    *  for the find Players count with @Param Pageable - null
    *  or use query with method  Integer getCountPlayersWithFilters(....)
    *  When this method is called (Integer getCountPlayersWithFilters(....)),
    *  the transaction can be left unattractive;to do so,
    *  you can change the propagation mode to
    *  @Transactional(propagation = Propagation.NEVER)
    * */
/*
    @Query("select count(p) from Player p where " +
            "(:name is null or p.name like %:name%) and " +
            "(:title is null or p.title like %:title%) and " +
            "(:race is null or p.race = :race) and " +
            "(:profession is null or p.profession = :profession) and " +
            "(:after is null or p.birthday >= :after) and " +
            "(:before is null or p.birthday <= :before) and " +
            "(:banned is null or p.banned = :banned) and " +
            "(:minExperience is null or p.experience >= :minExperience) and " +
            "(:maxExperience is null or p.experience <= :maxExperience) and " +
            "(:minLevel is null or p.level >= :minLevel) and " +
            "(:maxLevel is null or p.level <= :maxLevel) ")
    Integer getCountPlayersWithFilters(@Param("name") String name,
                                       @Param("title") String title,
                                       @Param("race") Race race,
                                       @Param("profession") Profession profession,
                                       @Param("after") Date after,
                                       @Param("before") Date before,
                                       @Param("banned") Boolean banned,
                                       @Param("minExperience") Integer minExperience,
                                       @Param("maxExperience") Integer maxExperience,
                                       @Param("minLevel") Integer minLevel,
                                       @Param("maxLevel") Integer maxLevel);
*/

}
