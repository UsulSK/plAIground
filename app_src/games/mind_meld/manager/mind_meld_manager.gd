extends Game
class_name MindMeldManager

signal game_state_updated(chat_msgs: Array[Label])

var _mindMeltCtr := MindMeltCtr.new()


func get_game_name() -> String:
	return "mind melt"


func get_game_scene() -> String:
	return "res://games/mind_meld/frontend/setup/mind_meld_setup.tscn"


func quit_game() -> void:
	print("mm_mng_quit")
	self._mindMeltCtr.quit_game()


func start_game(nr_of_players: int) -> void:
	print("mm_mng_start")
	self._mindMeltCtr.start_game(nr_of_players)


func _init() -> void:
	self._mindMeltCtr.game_state_updated.connect(_on_game_state_updated)


func on_new_user_word(word: String) -> MgrTurnResult:
	print("mm_mng_new_word " + word)
	var human_player = self._mindMeltCtr.get_human_player()
	var turn_result = self._mindMeltCtr.play_turn(human_player, word)
	var mgr_turn_result = MgrTurnResult.new()
	mgr_turn_result.error_text = turn_result.error_text
	if !mgr_turn_result.error_text.is_empty():
		return mgr_turn_result
	
	# let other players guess
	for ai_player in self._mindMeltCtr.get_state().get_ai_players():
		var guess = await AiPlayer.make_guess(self._mindMeltCtr.get_state())
		self._mindMeltCtr.play_turn(ai_player, guess)
	
	return mgr_turn_result


# put all guesses in chat history
func _on_game_state_updated(state: MindMeltState) -> void:
	var labels: Array[Label] = []
	for turn in state.turns:
		for guess in turn.guesses:
			var player_guess_text := Label.new()
			
			if guess.player == state.human_player:
				player_guess_text.add_theme_color_override("font_color", Color.BLUE)
			else:
				player_guess_text.add_theme_color_override("font_color", Color.CYAN)
			player_guess_text.text = "[" + guess.player.name + "] " + guess.word
			labels.append(player_guess_text)
		var turn_delimiter_text := Label.new()
		turn_delimiter_text.text = ""
		labels.append(turn_delimiter_text)
	
	self.game_state_updated.emit(labels)
