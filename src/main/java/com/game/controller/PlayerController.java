package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping(value = "/rest/players")
    public ResponseEntity<List<Player>> getPlayersList(
            @RequestParam(required = false) Map<String, String> params
    ) {
        return new ResponseEntity<>(playerService.findAllPlayers(params), HttpStatus.OK);
    }

    @GetMapping(value = "/rest/players/count")
    public ResponseEntity<Integer> getPlayersCount(
            @RequestParam(required = false) Map<String, String> params
    ){
        return new ResponseEntity<>(playerService.findPlayersCount(params), HttpStatus.OK);
    }

    @PostMapping(value = "/rest/players")
    public ResponseEntity<?> createPlayers(@RequestBody(required = false) Map<String, String> params){
        return new ResponseEntity<>(playerService.createPlayer(params), HttpStatus.OK);
    }

    @GetMapping(value = "/rest/players/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable(name = "id") String id) {
        return new ResponseEntity<>(playerService.getPlayer(id), HttpStatus.OK);
    }

    @PostMapping(value = "/rest/players/{id}")
    public ResponseEntity<?> updatePlayer(@PathVariable(name = "id") String id,
                                               @RequestBody(required = false) Map<String, String> params) {
        return new ResponseEntity<>(playerService.updatePlayer(params, id), HttpStatus.OK);

    }

    @DeleteMapping(value = "/rest/players/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable(name = "id") String id) {
        playerService.deletePlayer(playerService.getPlayer(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
