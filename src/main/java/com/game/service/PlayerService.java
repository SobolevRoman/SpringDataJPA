package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional(readOnly = true)
    public Integer findPlayersCount(Map<String, String> params){
        return findAllPlayersBySupportMethod(params, null).size();
    }

    @Transactional(readOnly = true)
    public List<Player> findAllPlayers(Map<String, String> params){
        Integer pageNumber = params.get("pageNumber") != null ? Integer.parseInt(params.get("pageNumber")) : 0;
        Integer pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize")) : 3;
        String order = params.get("order") != null ? params.get("order") : PlayerOrder.ID.getFieldName();
        return findAllPlayersBySupportMethod(params, PageRequest.of(pageNumber, pageSize, Sort.by(order)));
    }

    @Transactional(readOnly = true)
    public Player getPlayer(String id) {
        if (!validateParamId(id)){
            throw new InvalidParamException();
        }
        Optional<Player> optionalPlayer = playerRepository.findById(Long.parseLong(id));
        if (!optionalPlayer.isPresent()){
            throw new NotFoundEntityException();
        }
        return optionalPlayer.get();
    }


    public Player createPlayer(Map<String, String> params){
        if (params.size() == 0){
            throw new InvalidParamException();
        }
         Player newPlayer = new Player();
         updatePlayerMainFields(params, newPlayer);
         setUpLevelAndNextLevel(newPlayer);
         if (params.get("banned") != null){
             setParamBanned(params, newPlayer);
         }
         playerRepository.save(newPlayer);
         return newPlayer;
    }


    public Player updatePlayer(Map<String, String> params, String id){
        Player playerDB = getPlayer(id);
        updatePlayerMainFields(params, playerDB);
        setParamBanned(params, playerDB);
        setUpLevelAndNextLevel(playerDB);
        playerRepository.save(playerDB);
        return playerDB;
    }

    public void deletePlayer(Player player) {
        playerRepository.delete(player);
    }


    private List<Player> findAllPlayersBySupportMethod(Map<String, String> params, Pageable pageable){
        return playerRepository.getAllPlayersWithFilters(
                params.get("name") != null ? params.get("name") : null,
                params.get("title") != null ? params.get("title") : null,
                params.get("race") != null ? Race.valueOf(params.get("race")) : null,
                params.get("profession") != null ? Profession.valueOf(params.get("profession")) : null,
                params.get("after") != null ? new Date(Long.parseLong(params.get("after"))) : null,
                params.get("before") != null ? new Date(Long.parseLong(params.get("before"))) : null,
                params.get("banned") != null ? Boolean.valueOf(params.get("banned")) : null,
                params.get("minExperience") != null ? Integer.valueOf(params.get("minExperience")) : null,
                params.get("maxExperience") != null ? Integer.valueOf(params.get("maxExperience")) : null,
                params.get("minLevel") != null ? Integer.valueOf(params.get("minLevel")) : null,
                params.get("maxLevel") != null ? Integer.valueOf(params.get("maxLevel")) : null,
                pageable
        );
    }

    private boolean validateParamId(String id){
        try {
            return Long.parseLong(id) > 0;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private void updatePlayerMainFields(Map<String, String> params, Player playerDB){
        /* update name * */
        if (params.get("name") != null){
            if (params.get("name").isEmpty() || params.get("name").length() > 12){
                throw new InvalidParamException();
            }
            playerDB.setName(params.get("name"));
        }
        /* update title */
        if (params.get("title") != null && !params.get("title").isEmpty()){
            if (params.get("title").length() > 30){
                throw new InvalidParamException();
            }else {
                playerDB.setTitle(params.get("title"));
            }
        }
        /* update Race*/
        if (params.get("race") != null) {
            Race race;
            try {
                race = Race.valueOf(params.get("race"));
            } catch (IllegalArgumentException e) {
                throw new InvalidParamException();
            }
            playerDB.setRace(race);

        }
        /* update Profession*/
        if (params.get("profession") != null){
            Profession profession;
            try {
                profession = Profession.valueOf(params.get("profession"));
            } catch (IllegalArgumentException e) {
                throw new InvalidParamException();
            }
            playerDB.setProfession(profession);
        }
        /* update Date*/
        if (params.get("birthday") != null){
            validateParamId(params.get("birthday"));
            Long birthday = Long.parseLong(params.get("birthday"));
            if (birthday <
                    (new GregorianCalendar(2000, Calendar.JANUARY, 1).getTimeInMillis())
                || (birthday >
                    (new GregorianCalendar(3001, Calendar.JANUARY, 1).getTimeInMillis()))){
                throw new InvalidParamException();
            }
            playerDB.setBirthday(new Date(birthday));
        }
        /* update experience */
        if (params.get("experience") != null) {
            Integer exp;
            try {
                exp = Integer.parseInt(params.get("experience"));
            } catch (NumberFormatException e) {
                throw new InvalidParamException();
            }
            if (exp < 0 || exp > 10_000_000) {
                throw new InvalidParamException();
            }
            playerDB.setExperience(exp);
        }
    }

    private void setParamBanned(Map<String, String> params, Player playerDB){
        if (params.get("banned") != null){
            if (!"true".equals(params.get("banned"))){
                if (!"false".equals(params.get("banned"))){
                    throw new InvalidParamException();
                }
            }
            playerDB.setBanned(Boolean.parseBoolean(params.get("banned")));
        }
    }

    private void setUpLevelAndNextLevel(Player player){
        player.setLevel((int)((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100));
        player.setUntilNextLevel(50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience());
    }

}
