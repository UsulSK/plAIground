class_name MindMeltCtr
extends RefCounted

signal game_state_updated(state: MindMeltState)


var _mind_melt_state := MindMeltState.new()


func get_human_player() -> Player:
	return self._mind_melt_state.human_player


func get_state() -> MindMeltState:
	return self._mind_melt_state


func start_game(nr_of_players: int) -> void:
	print("ctr_start_game " + str(nr_of_players))
	if self._mind_melt_state.state == MindMeltState.STATE.PLAYING:
		printerr("game_already_playing")
		return
	self._mind_melt_state.reset_state()
	
	# create players
	var available_names: Array[String] = []
	# "res://" targets your project root directory. Change the prefix if this script lives deep in a subfolder.
	var file_path := "res://games/mind_meld/game_logic/control/assets/player_names.txt" 
	if FileAccess.file_exists(file_path):
		var file := FileAccess.open(file_path, FileAccess.READ)
		if file:
			while file.get_position() < file.get_length():
				var line_name := file.get_line().strip_edges()
				if not line_name.is_empty():
					available_names.append(line_name)
			file.close()
	else:
		printerr("ctr_start_game_player_names_error: names file not found at " + file_path)
	available_names.shuffle()
	for i in range(nr_of_players - 1):
		var other_player = Player.new()
		var name_index := i % available_names.size()
		other_player.name = available_names[name_index]
		print("ctr_start_created_player " + str(other_player))
		self._mind_melt_state.players.append(other_player)
	
	self._mind_melt_state.state = MindMeltState.STATE.PLAYING
	self.game_state_updated.emit(self._mind_melt_state)


func quit_game() -> void:
	print("ctr_quit_game")
	self._mind_melt_state.reset_state()
	self.game_state_updated.emit(self._mind_melt_state)


func play_turn(player: Player, word: String) -> TurnResult:
	print("ctr_player_turn: " + str(player) + ", " + str(word))
	var turn_result = TurnResult.new()
	
	# check if game is playing
	if self._mind_melt_state.state != MindMeltState.STATE.PLAYING:
		printerr("ctr_turn_but_no_running_game")
		turn_result.error_text = "no game is playing"
		return turn_result
	
	# check if player has already guessed in this turn
	var last_turn = self._mind_melt_state.turns.back()
	for guess in last_turn.guesses:
		if guess.player == player:
			printerr("ctr_player_already_guessed: " + str(player))
			turn_result.error_text = "player has already guessed this turn"
			return turn_result
	
	# check if word is valid
	var regex := RegEx.new()
	regex.compile("^[A-Za-z]+$")
	var result := regex.search(word)
	if not result:
		var error_text = "'" + word + "' is not a valid English word! Only letters A-Z and a-z allowed."
		printerr("ctr_bad_word: " + word)
		turn_result.error_text = error_text
		return turn_result
	
	# all good: update the state
	
	var new_guess = Guess.new()
	new_guess.player = player
	new_guess.word = word.to_lower()
	last_turn.guesses.append(new_guess)
	
	# check if all players guessed
	if last_turn.guesses.size() == self._mind_melt_state.players.size():
		# create new turn
		var new_turn = Turn.new()
		self._mind_melt_state.turns.append(new_turn)
	self.game_state_updated.emit(self._mind_melt_state)
	
	return turn_result
