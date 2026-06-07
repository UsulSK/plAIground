class_name MindMeltState
extends RefCounted

enum STATE { 
	PLAYING, FINISHED
	}

var turns: Array[Turn] = []
var state: STATE = STATE.FINISHED
var players: Array[Player] = []
var human_player: Player


func reset_state() -> void:
	self.turns.clear()
	self.state = STATE.PLAYING
	self.players.clear()
	var firstTurn = Turn.new()
	self.turns.append(firstTurn)
	self.human_player = Player.new()
	self.human_player.name = "you"
	self.players.append(self.human_player)


func get_ai_players() -> Array[Player]:
	var ai_players: Array[Player] = players.filter(
		func(player: Player): return player != human_player
	)
	return ai_players

func _to_string() -> String:
	return (
		"[" 
		+ "state: " + STATE.keys()[self.state] 
		+ ", human player: " + str(self.human_player) 
		+ ", players: " + str(self.players) 
		+ ", turns: " + str(self.turns) 
		+ "]"
	)
